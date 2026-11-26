package vn.edu.utex.admin.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.edu.utex.admin.model.Category;
import vn.edu.utex.admin.repository.CategoryRepository;
import vn.edu.utex.admin.web.form.CategoryForm;

@Service
public class CategoryService {
  private final CategoryRepository categories;

  public CategoryService(CategoryRepository categories) {
    this.categories = categories;
  }

  public Category get(Long id) {
    return categories
        .findById(id)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục"));
  }

  @Transactional
  public void save(Long id, CategoryForm f) {
    String name = f.getName().strip();
    if (id == null
        ? categories.existsByNameIgnoreCase(name)
        : categories.existsByNameIgnoreCaseAndIdNot(name, id))
      throw new FormProblem("name", "Tên danh mục đã tồn tại.");
    Category c = id == null ? new Category() : get(id);
    c.setName(name);
    c.setDescription(f.getDescription().strip());
    c.setEnabled(f.isEnabled());
    categories.saveAndFlush(c);
  }

  @Transactional
  public void delete(Long id) {
    categories.delete(get(id));
    categories.flush();
  }

  public CategoryForm form(Long id) {
    Category c = get(id);
    CategoryForm f = new CategoryForm();
    f.setName(c.getName());
    f.setDescription(c.getDescription());
    f.setEnabled(c.isEnabled());
    return f;
  }
}
