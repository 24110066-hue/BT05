package vn.edu.utex.admin.web.form;

import jakarta.validation.constraints.*;
import vn.edu.utex.admin.model.Role;

public class UserForm {
  @NotBlank(message = "Nhập tên đăng nhập.")
  @Pattern(regexp = "[a-zA-Z0-9_.-]{3,40}", message = "Tên đăng nhập gồm 3–40 chữ, số hoặc _ . -")
  private String username = "";

  @NotBlank(message = "Nhập email.")
  @Email(message = "Email không hợp lệ.")
  @Size(max = 120)
  private String email = "";

  @NotBlank(message = "Nhập họ tên.")
  @Size(max = 100)
  private String fullName = "";

  @Size(max = 72, message = "Mật khẩu tối đa 72 ký tự.")
  private String password = "";

  @NotNull(message = "Chọn vai trò.")
  private Role role = Role.USER;

  private boolean enabled = true;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
