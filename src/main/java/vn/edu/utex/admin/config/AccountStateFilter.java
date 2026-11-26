package vn.edu.utex.admin.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.edu.utex.admin.repository.UserRepository;

/** Revoke an existing login when an admin disables, deletes, renames or changes its role. */
public class AccountStateFilter extends OncePerRequestFilter {
  private final UserRepository users;

  public AccountStateFilter(UserRepository users) {
    this.users = users;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
      var user = users.findByUsernameIgnoreCase(auth.getName());
      boolean valid =
          user.isPresent()
              && user.get().isEnabled()
              && auth.getAuthorities().stream()
                  .anyMatch(a -> a.getAuthority().equals("ROLE_" + user.get().getRole().name()));
      if (!valid) {
        var session = request.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
        response.sendRedirect(request.getContextPath() + "/login?changed");
        return;
      }
    }
    chain.doFilter(request, response);
  }
}
