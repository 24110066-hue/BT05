package vn.edu.utex.admin.service;

import java.util.Locale;
import java.util.function.Function;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

public final class Queries {
  private Queries() {}

  public static String keyword(String q) {
    return q == null ? "" : q.strip().substring(0, Math.min(100, q.strip().length()));
  }

  public static <T> Specification<T> contains(String q, String... fields) {
    return (root, query, cb) -> {
      String pattern =
          "%"
              + keyword(q)
                  .toLowerCase(Locale.ROOT)
                  .replace("\\", "\\\\")
                  .replace("%", "\\%")
                  .replace("_", "\\_")
              + "%";
      var predicates =
          java.util.Arrays.stream(fields)
              .map(f -> cb.like(cb.lower(root.get(f)), pattern, '\\'))
              .toArray(jakarta.persistence.criteria.Predicate[]::new);
      return cb.or(predicates);
    };
  }

  public static <T> Page<T> page(int page, int size, Function<Pageable, Page<T>> loader) {
    int safeSize =
        switch (size) {
          case 5, 10, 20 -> size;
          default -> 5;
        };
    Page<T> result =
        loader.apply(PageRequest.of(Math.max(0, page), safeSize, Sort.by("id").descending()));
    if (result.getTotalPages() > 0 && result.getNumber() >= result.getTotalPages())
      return loader.apply(
          PageRequest.of(result.getTotalPages() - 1, safeSize, Sort.by("id").descending()));
    return result;
  }
}
