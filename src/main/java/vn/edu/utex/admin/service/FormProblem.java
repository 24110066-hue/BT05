package vn.edu.utex.admin.service;

public class FormProblem extends RuntimeException {
  private final String field;

  public FormProblem(String field, String message) {
    super(message);
    this.field = field;
  }

  public String getField() {
    return field;
  }
}
