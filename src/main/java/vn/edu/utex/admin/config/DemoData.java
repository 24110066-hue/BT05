package vn.edu.utex.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.edu.utex.admin.model.*;
import vn.edu.utex.admin.repository.*;

@Component
public class DemoData implements CommandLineRunner {
  private final UserRepository users;
  private final CategoryRepository categories;
  private final PasswordEncoder encoder;
  private final boolean enabled;

  public DemoData(
      UserRepository u,
      CategoryRepository c,
      PasswordEncoder p,
      @Value("${app.seed-demo:true}") boolean enabled) {
    users = u;
    categories = c;
    encoder = p;
    this.enabled = enabled;
  }

  @Override
  public void run(String... args) {
    if (!enabled) return;
    if (users.count() == 0) {
      add("admin", "admin@example.com", "Quản trị viên", Role.ADMIN, "Admin@123");
      for (int i = 1; i <= 12; i++)
        add("user" + i, "user" + i + "@example.com", "Người dùng mẫu " + i, Role.USER, "User@123");
    }
    if (categories.count() == 0)
      for (String name :
          new String[] {
            "Áo thun",
            "Áo sơ mi",
            "Áo khoác",
            "Quần jeans",
            "Quần short",
            "Quần tây",
            "Váy",
            "Giày thể thao",
            "Dép",
            "Túi xách",
            "Mũ",
            "Phụ kiện"
          }) {
        Category c = new Category();
        c.setName(name);
        c.setDescription("Danh mục " + name.toLowerCase());
        c.setEnabled(true);
        categories.save(c);
      }
  }

  private void add(String name, String email, String full, Role role, String password) {
    AppUser u = new AppUser();
    u.setUsername(name);
    u.setEmail(email);
    u.setFullName(full);
    u.setPasswordHash(encoder.encode(password));
    u.setRole(role);
    u.setEnabled(true);
    users.save(u);
  }
}
