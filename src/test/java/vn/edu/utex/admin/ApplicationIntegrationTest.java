package vn.edu.utex.admin;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.utex.admin.model.*;
import vn.edu.utex.admin.repository.*;
import vn.edu.utex.admin.service.*;
import vn.edu.utex.admin.web.form.*;

/** Exercises real embedded Tomcat, compiled JSPs, CSRF, sessions and the database. */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "spring.datasource.url=jdbc:h2:mem:integration;DB_CLOSE_DELAY=-1",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "app.seed-demo=false"
    })
class ApplicationIntegrationTest {
  @LocalServerPort int port;
  @Autowired UserRepository users;
  @Autowired CategoryRepository categories;
  @Autowired UserService service;
  @Autowired PasswordEncoder encoder;
  HttpClient client;

  @BeforeEach
  void setup() {
    categories.deleteAll();
    users.deleteAll();
    account("admin", "admin@test.local", Role.ADMIN);
    account("member", "member@test.local", Role.USER);
    client = newClient();
  }

  HttpClient newClient() {
    return HttpClient.newBuilder()
        .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
        .followRedirects(HttpClient.Redirect.NEVER)
        .build();
  }

  void account(String name, String email, Role role) {
    AppUser u = new AppUser();
    u.setUsername(name);
    u.setEmail(email);
    u.setFullName(name);
    u.setRole(role);
    u.setEnabled(true);
    u.setPasswordHash(encoder.encode("Password@123"));
    users.save(u);
  }

  HttpResponse<String> get(String path) throws Exception {
    return get(client, path);
  }

  HttpResponse<String> get(HttpClient c, String path) throws Exception {
    return c.send(
        HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(),
        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
  }

  String token(String html) {
    Matcher m = Pattern.compile("<input[^>]*name=\"_csrf\"[^>]*value=\"([^\"]+)\"").matcher(html);
    if (!m.find())
      throw new AssertionError(
          "CSRF token missing from rendered JSP: "
              + html.substring(0, Math.min(500, html.length())));
    return m.group(1);
  }

  HttpResponse<String> post(HttpClient c, String path, Map<String, String> fields)
      throws Exception {
    String body =
        fields.entrySet().stream()
            .map(
                e ->
                    URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)
                        + "="
                        + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
            .collect(java.util.stream.Collectors.joining("&"));
    return c.send(
        HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build(),
        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
  }

  HttpResponse<String> submit(String view, String path, Map<String, String> fields)
      throws Exception {
    var f = new HashMap<>(fields);
    f.put("_csrf", token(get(view).body()));
    return post(client, path, f);
  }

  void login(HttpClient c, String username) throws Exception {
    String csrf = token(get(c, "/login").body());
    var response =
        post(c, "/login", Map.of("username", username, "password", "Password@123", "_csrf", csrf));
    assertThat(response.statusCode()).isEqualTo(302);
    assertThat(response.headers().firstValue("Location").orElse("")).endsWith("/home");
  }

  Map<String, String> category(String name) {
    return Map.of("name", name, "description", "Mô tả thử nghiệm", "enabled", "true");
  }

  Map<String, String> user(
      String username, String email, String password, String role, boolean enabled) {
    return Map.of(
        "username",
        username,
        "email",
        email,
        "fullName",
        "Họ tên thử",
        "password",
        password,
        "role",
        role,
        "enabled",
        Boolean.toString(enabled));
  }

  @Test
  void anonymousAndCsrfProtection() throws Exception {
    assertThat(get("/admin/users").statusCode()).isEqualTo(302);
    assertThat(post(client, "/register", Map.of("username", "intruder")).statusCode())
        .isEqualTo(403);
    assertThat(users.existsByUsernameIgnoreCase("intruder")).isFalse();
  }

  @Test
  void registrationValidatesHashesAndAlwaysCreatesUser() throws Exception {
    var fields =
        new HashMap<>(
            Map.of(
                "username",
                "newuser",
                "email",
                "new@test.local",
                "fullName",
                "Nguyễn Văn A",
                "password",
                "Password@123",
                "confirmPassword",
                "different"));
    assertThat(submit("/register", "/register", fields).body())
        .contains("Mật khẩu nhập lại không khớp");
    fields.put("confirmPassword", "Password@123");
    fields.put("role", "ADMIN");
    assertThat(submit("/register", "/register", fields).statusCode()).isEqualTo(302);
    var saved = users.findByUsernameIgnoreCase("NEWUSER").orElseThrow();
    assertThat(saved.getRole()).isEqualTo(Role.USER);
    assertThat(saved.getFullName()).isEqualTo("Nguyễn Văn A");
    assertThat(saved.getPasswordHash()).isNotEqualTo("Password@123");
    assertThat(encoder.matches("Password@123", saved.getPasswordHash())).isTrue();
    assertThat(submit("/register", "/register", fields).body())
        .contains("Tên đăng nhập đã tồn tại");
    fields.put("username", "otheruser");
    assertThat(submit("/register", "/register", fields).body()).contains("Email đã được sử dụng");
  }

  @Test
  void loginFailureUserForbiddenAndLogout() throws Exception {
    assertThat(
            submit("/login", "/login", Map.of("username", "member", "password", "wrong"))
                .headers()
                .firstValue("Location")
                .orElse(""))
        .endsWith("/login?error");
    login(client, "member");
    assertThat(get("/home").body()).contains("member@test.local");
    var denied = get("/admin/users");
    assertThat(denied.statusCode()).isEqualTo(403);
    assertThat(denied.body()).contains("Không có quyền truy cập");
    assertThat(submit("/home", "/logout", Map.of()).statusCode()).isEqualTo(302);
    assertThat(get("/home").statusCode()).isEqualTo(302);
  }

  @Test
  void categoryCrudSearchPaginationAndEscaping() throws Exception {
    login(client, "admin");
    for (int i = 0; i < 12; i++)
      assertThat(
              submit("/admin/categories/new", "/admin/categories", category("Nhóm " + i))
                  .statusCode())
          .isEqualTo(302);
    String list = get("/admin/categories?size=5&page=1").body();
    assertThat(list).contains("12 kết quả", "Trang 2/3");
    assertThat(get("/admin/categories?q=KHONG_TIM_THAY").body())
        .contains("Không có kết quả phù hợp");
    assertThat(get("/admin/categories?q=%25").body()).contains("0 kết quả");
    assertThat(get("/admin/categories?q=%5C").statusCode()).isEqualTo(200);
    assertThat(get("/admin/categories?page=999999&size=5").body()).contains("Trang 3/3");
    assertThat(get("/admin/categories?page=-2&size=1000").body()).contains("Trang 1/3");
    var id = categories.findAll().getFirst().getId();
    assertThat(
            submit(
                    "/admin/categories/" + id + "/edit",
                    "/admin/categories/" + id,
                    category("Áo <script>alert(1)</script>"))
                .statusCode())
        .isEqualTo(302);
    String searched =
        get("/admin/categories?q=" + URLEncoder.encode("Áo", StandardCharsets.UTF_8)).body();
    assertThat(searched)
        .contains("1 kết quả", "&lt;script&gt;")
        .doesNotContain("<script>alert(1)</script>");
    assertThat(submit("/admin/categories/new", "/admin/categories", category("Nhóm 1")).body())
        .contains("Tên danh mục đã tồn tại");
    assertThat(
            submit(
                    "/admin/categories",
                    "/admin/categories/" + id + "/delete",
                    Map.of("page", "2", "size", "5"))
                .statusCode())
        .isEqualTo(302);
    assertThat(categories.existsById(id)).isFalse();
    assertThat(get("/admin/categories/" + id + "/edit").statusCode()).isEqualTo(404);
  }

  @Test
  void userCrudSearchValidationAndSelfProtection() throws Exception {
    login(client, "admin");
    assertThat(get("/admin").body()).contains("Tổng quan quản trị");
    assertThat(
            submit(
                    "/admin/users/new",
                    "/admin/users",
                    user("testuser", "test@test.local", "", "USER", true))
                .body())
        .contains("Mật khẩu cần ít nhất");
    assertThat(
            submit(
                    "/admin/users/new",
                    "/admin/users",
                    user("testuser", "test@test.local", "Password@123", "USER", true))
                .statusCode())
        .isEqualTo(302);
    var id = users.findByUsernameIgnoreCase("testuser").orElseThrow().getId();
    String hash = users.findById(id).orElseThrow().getPasswordHash();
    assertThat(get("/admin/users?q=test%40test.local").body()).contains("1 kết quả", "testuser");
    assertThat(
            submit(
                    "/admin/users/" + id + "/edit",
                    "/admin/users/" + id,
                    user("testuser", "changed@test.local", "", "ADMIN", true))
                .statusCode())
        .isEqualTo(302);
    assertThat(users.findById(id).orElseThrow().getPasswordHash()).isEqualTo(hash);
    assertThat(users.findById(id).orElseThrow().getRole()).isEqualTo(Role.ADMIN);
    Long adminId = users.findByUsernameIgnoreCase("admin").orElseThrow().getId();
    assertThat(
            submit(
                    "/admin/users/" + adminId + "/edit",
                    "/admin/users/" + adminId,
                    user("admin", "admin@test.local", "", "USER", false))
                .body())
        .contains("Bạn không thể tự khóa");
    assertThat(submit("/admin/users", "/admin/users/" + adminId + "/delete", Map.of()).statusCode())
        .isEqualTo(302);
    assertThat(users.existsById(adminId)).isTrue();
    assertThat(submit("/admin/users", "/admin/users/" + id + "/delete", Map.of()).statusCode())
        .isEqualTo(302);
    assertThat(users.existsById(id)).isFalse();
  }

  @Test
  void disabledOrDemotedAccountLosesExistingSession() throws Exception {
    HttpClient member = newClient();
    login(member, "member");
    login(client, "admin");
    Long id = users.findByUsernameIgnoreCase("member").orElseThrow().getId();
    assertThat(
            submit(
                    "/admin/users/" + id + "/edit",
                    "/admin/users/" + id,
                    user("member", "member@test.local", "", "USER", false))
                .statusCode())
        .isEqualTo(302);
    assertThat(get(member, "/home").headers().firstValue("Location").orElse(""))
        .endsWith("/login?changed");
    String csrf = token(get(member, "/login").body());
    assertThat(
            post(
                    member,
                    "/login",
                    Map.of("username", "member", "password", "Password@123", "_csrf", csrf))
                .headers()
                .firstValue("Location")
                .orElse(""))
        .endsWith("/login?error");
  }

  @Test
  void userPaginationAndDemotionRevokeAdminAccess() throws Exception {
    for (int i = 1; i <= 9; i++) account("sample" + i, "sample" + i + "@test.local", Role.USER);
    login(client, "admin");
    assertThat(get("/admin/users?size=5&page=1").body()).contains("11 kết quả", "Trang 2/3");
    assertThat(get("/admin/users?page=999&size=5").body()).contains("Trang 3/3");
    assertThat(get("/admin/users?q=SAMPLE9").body()).contains("1 kết quả", "sample9");
    assertThat(get("/admin/users?q=missing").body()).contains("0 kết quả");
    account("secondadmin", "second@test.local", Role.ADMIN);
    HttpClient second = newClient();
    login(second, "secondadmin");
    Long id = users.findByUsernameIgnoreCase("secondadmin").orElseThrow().getId();
    assertThat(
            submit(
                    "/admin/users/" + id + "/edit",
                    "/admin/users/" + id,
                    user("secondadmin", "second@test.local", "", "USER", true))
                .statusCode())
        .isEqualTo(302);
    assertThat(get(second, "/admin/users").headers().firstValue("Location").orElse(""))
        .endsWith("/login?changed");
  }
}
