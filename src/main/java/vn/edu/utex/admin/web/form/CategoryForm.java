package vn.edu.utex.admin.web.form;

import jakarta.validation.constraints.*;

public class CategoryForm {
  @NotBlank(message = "Nhập tên danh mục.")
  @Size(max = 100, message = "Tên tối đa 100 ký tự.")
  private String name = "";

  @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự.")
  private String description = "";

  private boolean enabled = true;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
