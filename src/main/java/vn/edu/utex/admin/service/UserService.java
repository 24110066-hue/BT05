package vn.edu.utex.admin.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.edu.utex.admin.model.*;
import vn.edu.utex.admin.repository.UserRepository;
import vn.edu.utex.admin.web.form.*;

@Service
public class UserService {
  private final UserRepository users;
  private final PasswordEncoder encoder;

  public UserService(UserRepository users, PasswordEncoder encoder) {
    this.users = users;
    this.encoder = encoder;
  }

  public AppUser get(Long id) {
    return users
        .findById(id)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản"));
  }

  private void unique(String username, String email, Long id) {
    if (id == null
        ? users.existsByUsernameIgnoreCase(username)
        : users.existsByUsernameIgnoreCaseAndIdNot(username, id))
      throw new FormProblem("username", "Tên đăng nhập đã tồn tại.");
    if (id == null
        ? users.existsByEmailIgnoreCase(email)
        : users.existsByEmailIgnoreCaseAndIdNot(email, id))
      throw new FormProblem("email", "Email đã được sử dụng.");
  }

  private void password(String value) {
    if (value == null || value.length() < 8 || value.getBytes(StandardCharsets.UTF_8).length > 72)
      throw new FormProblem("password", "Mật khẩu cần ít nhất 8 ký tự và tối đa 72 byte UTF-8.");
  }

  @Transactional
  public AppUser register(RegisterForm f) {
    if (!f.getPassword().equals(f.getConfirmPassword()))
      throw new FormProblem("confirmPassword", "Mật khẩu nhập lại không khớp.");
    password(f.getPassword());
    unique(f.getUsername().strip(), f.getEmail().strip(), null);
    AppUser u = new AppUser();
    u.setUsername(f.getUsername().strip().toLowerCase(Locale.ROOT));
    u.setEmail(f.getEmail().strip().toLowerCase(Locale.ROOT));
    u.setFullName(f.getFullName().strip());
    u.setPasswordHash(encoder.encode(f.getPassword()));
    u.setRole(Role.USER);
    u.setEnabled(true);
    return users.saveAndFlush(u);
  }

  @Transactional
  public AppUser save(Long id, UserForm f, String actor) {
    AppUser u = id == null ? new AppUser() : get(id);
    unique(f.getUsername().strip(), f.getEmail().strip(), id);
    if (id != null
        && u.getUsername().equalsIgnoreCase(actor)
        && (f.getRole() != Role.ADMIN || !f.isEnabled()))
      throw new FormProblem(
          "role", "Bạn không thể tự khóa hoặc hạ quyền tài khoản đang đăng nhập.");
    if (id == null || (f.getPassword() != null && !f.getPassword().isEmpty())) {
      password(f.getPassword());
      u.setPasswordHash(encoder.encode(f.getPassword()));
    }
    u.setUsername(f.getUsername().strip().toLowerCase(Locale.ROOT));
    u.setEmail(f.getEmail().strip().toLowerCase(Locale.ROOT));
    u.setFullName(f.getFullName().strip());
    u.setRole(f.getRole());
    u.setEnabled(f.isEnabled());
    return users.saveAndFlush(u);
  }

  @Transactional
  public void delete(Long id, String actor) {
    AppUser u = get(id);
    if (u.getUsername().equalsIgnoreCase(actor))
      throw new FormProblem("", "Bạn không thể xóa tài khoản đang đăng nhập.");
    users.delete(u);
    users.flush();
  }

  public UserForm form(Long id) {
    AppUser u = get(id);
    UserForm f = new UserForm();
    f.setUsername(u.getUsername());
    f.setEmail(u.getEmail());
    f.setFullName(u.getFullName());
    f.setRole(u.getRole());
    f.setEnabled(u.isEnabled());
    return f;
  }
}
