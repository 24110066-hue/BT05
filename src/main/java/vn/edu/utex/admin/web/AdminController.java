package vn.edu.utex.admin.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.edu.utex.admin.model.Role;
import vn.edu.utex.admin.repository.*;

@Controller
public class AdminController {
  private final UserRepository users;
  private final CategoryRepository categories;

  public AdminController(UserRepository users, CategoryRepository categories) {
    this.users = users;
    this.categories = categories;
  }

  @GetMapping({"/admin", "/admin/"})
  String dashboard(Model model) {
    model.addAttribute("userCount", users.count());
    model.addAttribute("categoryCount", categories.count());
    model.addAttribute("adminCount", users.countByRole(Role.ADMIN));
    return "admin/dashboard";
  }
}
