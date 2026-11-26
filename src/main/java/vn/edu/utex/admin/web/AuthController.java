package vn.edu.utex.admin.web;

import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.utex.admin.repository.*;
import vn.edu.utex.admin.service.*;
import vn.edu.utex.admin.web.form.RegisterForm;

@Controller
public class AuthController {
  private final UserService service;
  private final UserRepository users;

  public AuthController(UserService service, UserRepository users) {
    this.service = service;
    this.users = users;
  }

  @GetMapping("/")
  String index(Principal principal) {
    return principal == null ? "redirect:/login" : "redirect:/home";
  }

  @GetMapping("/login")
  String login(Principal principal) {
    return principal == null ? "auth/login" : "redirect:/home";
  }

  @GetMapping("/register")
  String register(Model model) {
    model.addAttribute("form", new RegisterForm());
    return "auth/register";
  }

  @PostMapping("/register")
  String register(
      @Valid @ModelAttribute("form") RegisterForm form,
      BindingResult errors,
      RedirectAttributes flash) {
    if (errors.hasErrors()) return "auth/register";
    try {
      service.register(form);
    } catch (FormProblem e) {
      errors.rejectValue(e.getField(), "invalid", e.getMessage());
      return "auth/register";
    } catch (DataIntegrityViolationException e) {
      errors.reject("duplicate", "Tên đăng nhập hoặc email đã tồn tại.");
      return "auth/register";
    }
    flash.addFlashAttribute("success", "Đăng ký thành công. Hãy đăng nhập bằng tài khoản vừa tạo.");
    return "redirect:/login";
  }

  @GetMapping("/home")
  String home(Principal principal, Model model) {
    model.addAttribute(
        "account", users.findByUsernameIgnoreCase(principal.getName()).orElseThrow());
    return "home";
  }

  @RequestMapping("/forbidden")
  String forbidden(jakarta.servlet.http.HttpServletResponse response) {
    response.setStatus(403);
    return "forbidden";
  }
}
