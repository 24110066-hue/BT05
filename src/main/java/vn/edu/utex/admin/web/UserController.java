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
@RequestMapping("/admin/users")
public class UserController {
  private final UserRepository repository;
  private final UserService service;

  public UserController(UserRepository repository, UserService service) {
    this.repository = repository;
    this.service = service;
  }

  @ModelAttribute("roles")
  Role[] roles() {
    return Role.values();
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
                repository.findAll(
                    Queries.<AppUser>contains(keyword, "username", "email", "fullName"), p)));
    return "admin/users/list";
  }

  @GetMapping("/new")
  String create(Model model) {
    model.addAttribute("form", new UserForm());
    model.addAttribute("editing", false);
    return "admin/users/form";
  }

  @GetMapping("/{id}/edit")
  String edit(@PathVariable Long id, Model model) {
    model.addAttribute("form", service.form(id));
    model.addAttribute("editing", true);
    model.addAttribute("recordId", id);
    return "admin/users/form";
  }

  @PostMapping
  String create(
      @Valid @ModelAttribute("form") UserForm form,
      BindingResult errors,
      Model model,
      RedirectAttributes flash,
      Principal principal) {
    return save(null, form, errors, model, flash, principal);
  }

  @PostMapping("/{id}")
  String update(
      @PathVariable Long id,
      @Valid @ModelAttribute("form") UserForm form,
      BindingResult errors,
      Model model,
      RedirectAttributes flash,
      Principal principal) {
    return save(id, form, errors, model, flash, principal);
  }

  private String save(
      Long id,
      UserForm form,
      BindingResult errors,
      Model model,
      RedirectAttributes flash,
      Principal principal) {
    model.addAttribute("editing", id != null);
    model.addAttribute("recordId", id);
    if (errors.hasErrors()) return "admin/users/form";
    try {
      service.save(id, form, principal.getName());
    } catch (FormProblem e) {
      errors.rejectValue(e.getField(), "invalid", e.getMessage());
      return "admin/users/form";
    } catch (DataIntegrityViolationException e) {
      errors.reject("duplicate", "Dữ liệu bị trùng. Kiểm tra lại tên hoặc email.");
      return "admin/users/form";
    }
    flash.addFlashAttribute(
        "success", id == null ? "Đã thêm tài khoản." : "Đã cập nhật tài khoản.");
    return "redirect:/admin/users";
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
      service.delete(id, principal.getName());
      flash.addFlashAttribute("success", "Đã xóa tài khoản.");
    } catch (FormProblem e) {
      flash.addFlashAttribute("error", e.getMessage());
    } catch (DataIntegrityViolationException e) {
      flash.addFlashAttribute("error", "Không thể xóa vì dữ liệu đang được sử dụng.");
    }
    return "redirect:"
        + UriComponentsBuilder.fromPath("/admin/users")
            .queryParam("q", Queries.keyword(q))
            .queryParam("page", Math.max(page, 0))
            .queryParam("size", size)
            .build()
            .encode()
            .toUriString();
  }
}
