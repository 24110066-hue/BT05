package vn.edu.utex.admin.web.form;

import jakarta.validation.constraints.*;

public class RegisterForm {
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

  @NotBlank(message = "Nhập mật khẩu.")
  @Size(min = 8, max = 72, message = "Mật khẩu dài 8–72 ký tự.")
  private String password = "";

  @NotBlank(message = "Nhập lại mật khẩu.")
  private String confirmPassword = "";

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

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }
}
