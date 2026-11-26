package vn.edu.utex.admin.web;

import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import vn.edu.utex.admin.model.*;
import vn.edu.utex.admin.repository.*;
import vn.edu.utex.admin.service.*;
import vn.edu.utex.admin.web.form.*;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {
  private final CategoryRepository repository;
  private final CategoryService service;

  public CategoryController(CategoryRepository repository, CategoryService service) {
    this.repository = repository;
    this.service = service;
  }

  @GetMapping
  String list(
      @RequestParam(defaultValue = "") String q,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      Model model) {
    String keyword = Queries.keyword(q);
    model.addAttribute("q", keyword);
    model.addAttribute(
        "result",
        Queries.page(
            page,
            size,
            p ->
                repository.findAll(Queries.<Category>contains(keyword, "name", "description"), p)));
    return "admin/categories/list";
  }

  @GetMapping("/new")
  String create(Model model) {
    model.addAttribute("form", new CategoryForm());
    model.addAttribute("editing", false);
    return "admin/categories/form";
  }

  @GetMapping("/{id}/edit")
  String edit(@PathVariable Long id, Model model) {
    model.addAttribute("form", service.form(id));
    model.addAttribute("editing", true);
    model.addAttribute("recordId", id);
    return "admin/categories/form";
  }

  @PostMapping
  String create(
      @Valid @ModelAttribute("form") CategoryForm form,
      BindingResult errors,
      Model model,
      RedirectAttributes flash,
      Principal principal) {
    return save(null, form, errors, model, flash, principal);
  }

  @PostMapping("/{id}")
  String update(
      @PathVariable Long id,
      @Valid @ModelAttribute("form") CategoryForm form,
      BindingResult errors,
      Model model,
      RedirectAttributes flash,
      Principal principal) {
    return save(id, form, errors, model, flash, principal);
  }

  private String save(
      Long id,
      CategoryForm form,
      BindingResult errors,
      Model model,
      RedirectAttributes flash,
      Principal principal) {
    model.addAttribute("editing", id != null);
    model.addAttribute("recordId", id);
    if (errors.hasErrors()) return "admin/categories/form";
    try {
      service.save(id, form);
    } catch (FormProblem e) {
      errors.rejectValue(e.getField(), "invalid", e.getMessage());
      return "admin/categories/form";
    } catch (DataIntegrityViolationException e) {
      errors.reject("duplicate", "Dữ liệu bị trùng. Kiểm tra lại tên hoặc email.");
      return "admin/categories/form";
    }
    flash.addFlashAttribute("success", id == null ? "Đã thêm danh mục." : "Đã cập nhật danh mục.");
    return "redirect:/admin/categories";
  }

  @PostMapping("/{id}/delete")
  String delete(
      @PathVariable Long id,
      @RequestParam(defaultValue = "") String q,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      Principal principal,
      RedirectAttributes flash) {
    try {
      service.delete(id);
      flash.addFlashAttribute("success", "Đã xóa danh mục.");
    } catch (FormProblem e) {
      flash.addFlashAttribute("error", e.getMessage());
    } catch (DataIntegrityViolationException e) {
      flash.addFlashAttribute("error", "Không thể xóa vì dữ liệu đang được sử dụng.");
    }
    return "redirect:"
        + UriComponentsBuilder.fromPath("/admin/categories")
            .queryParam("q", Queries.keyword(q))
            .queryParam("page", Math.max(page, 0))
            .queryParam("size", size)
            .build()
            .encode()
            .toUriString();
  }
}
