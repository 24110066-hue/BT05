# Spring Boot 4 · JSP/JSTL · Category & User Admin

Bài tập đăng ký, đăng nhập, đăng xuất và CRUD Category/User dành cho ADMIN, có tìm kiếm và phân trang cho cả hai bảng.

## 1. Công nghệ

- Java 21, **Spring Boot 4.0.0**, Maven Wrapper 3.9.11.
- Spring MVC, Spring Security, Spring Data JPA, Jakarta Validation.
- Giao diện **JSP/JSTL**, URI JSTL `jakarta.tags.core`, CSS cục bộ.
- H2 lưu trên đĩa để chạy thử ngay; profile MySQL cho MySQL 8+.
- Đóng gói executable **WAR** để JSP chạy với Tomcat nhúng; không đổi sang JAR.

## 2. Chạy nhanh trên Windows (VS Code)

1. Giải nén toàn bộ ZIP, ví dụ `D:\springboot4-jsp-category-user`. Không mở và chạy trực tiếp bên trong ZIP.
2. Cài **JDK 21** (Temurin hoặc Microsoft OpenJDK). Mở Terminal mới, chạy `java -version` và `javac -version`, cả hai cần là 21. Nếu có nhiều Java, đặt `JAVA_HOME` trỏ đến thư mục JDK 21.
3. Trong VS Code chọn **File → Open Folder**, mở thư mục có `pom.xml`, `mvnw.cmd` và `src`.
4. Bản ZIP có WAR dựng sẵn: nhấp đúp **`scripts\run-demo.bat`**. Hoặc chạy trong Terminal:

```powershell
java -jar .\dist\category-user-admin.war
```

5. Chờ log `Started AdminApplication`, mở **http://localhost:8080**.
6. Dừng server bằng **Ctrl+C**. Giữ Terminal mở khi sử dụng ứng dụng.

Nếu tải mã nguồn từ GitHub (không có thư mục `dist`), chạy:

```powershell
.\mvnw.cmd spring-boot:run
```

Maven Wrapper tự tải Maven và thư viện ở lần đầu, cần Internet. Không cần cài Maven riêng hay Tomcat riêng. Mã nguồn được biên dịch khi chạy lệnh này.

Trên Linux/macOS:

```bash
chmod +x mvnw scripts/run-demo.sh
./mvnw spring-boot:run
```

**Lưu ý khi sửa code:** `run-demo.bat` ưu tiên WAR trong `dist`. Sau khi sửa, dùng `mvnw.cmd spring-boot:run` để chạy code mới; hoặc chạy `scripts\build.bat` để dựng lại WAR.

## 3. Tài khoản mẫu

| Vai trò | Tên đăng nhập | Mật khẩu |
| --- | --- | --- |
| ADMIN | `admin` | `Admin@123` |
| USER | `user1` đến `user12` | `User@123` |

Lần khởi động đầu tạo 12 danh mục quần áo/phụ kiện và 13 tài khoản, đủ để kiểm tra phân trang. Chỉ tạo dữ liệu mẫu khi bảng tương ứng rỗng; không ghi đè dữ liệu đã chỉnh sửa. Đây là tài khoản demo công khai trong mã nguồn, nên đổi mật khẩu nếu dùng ngoài phạm vi bài tập.

Dữ liệu H2 nằm ở `data/category-user.mv.db` tính từ thư mục chạy lệnh. Khởi động từ thư mục gốc dự án để giữ đúng vị trí dữ liệu. Tắt ứng dụng rồi mở lại vẫn còn dữ liệu. Để làm lại dữ liệu mẫu: dừng server, sao lưu nếu cần, xóa thư mục `data`, chạy lại.

## 4. Kiểm tra chức năng theo thứ tự

| Bước | Thao tác | Kết quả mong đợi |
| --- | --- | --- |
| 1 | Khi chưa đăng nhập, mở `/admin/users` | Chuyển về trang đăng nhập |
| 2 | Đăng ký ở `/register`, nhập mật khẩu tối thiểu 8 ký tự và xác nhận đúng | Tạo tài khoản USER, chuyển về đăng nhập |
| 3 | Đăng ký bằng tên hoặc email đã dùng / xác nhận mật khẩu sai | Hiện thông báo, không tạo bản ghi |
| 4 | Đăng nhập bằng tài khoản vừa tạo | Hiện trang chủ và thông tin tài khoản |
| 5 | Với USER, nhập URL `/admin` hoặc `/admin/categories` | HTTP 403, không có quyền quản trị |
| 6 | Nhấn Đăng xuất | Quay về đăng nhập, phiên đăng nhập kết thúc |
| 7 | Đăng nhập `admin` / `Admin@123`, chọn Tổng quan | Hiện số Category/User/ADMIN |
| 8 | Category → Thêm danh mục, nhập `Áo demo`, mô tả rồi Lưu | Danh mục xuất hiện trong danh sách |
| 9 | Tìm `Áo demo`, sửa thành `Áo demo mới` | Tìm kiếm và cập nhật đúng |
| 10 | Thử thêm tên danh mục trùng / để trống tên | Báo lỗi và không lưu |
| 11 | Đặt lại tìm kiếm, chọn 5/10/20 dòng, chuyển trang | Đúng số dòng và tổng số trang |
| 12 | Xóa danh mục vừa tạo và xác nhận | Bản ghi bị xóa |
| 13 | User → Thêm tài khoản, nhập username/email/họ tên/mật khẩu/vai trò | Tạo tài khoản; mật khẩu lưu dưới dạng BCrypt |
| 14 | Tìm tài khoản theo username, email hoặc họ tên | Chỉ hiện tài khoản phù hợp |
| 15 | Sửa tài khoản, để trống mật khẩu | Giữ nguyên mật khẩu hiện tại |
| 16 | Sửa mật khẩu, vai trò hoặc khóa tài khoản thử | Lưu thay đổi; tài khoản bị khóa không đăng nhập được |
| 17 | Chuyển trang trong User; xóa tài khoản thử | Phân trang và xóa hoạt động |
| 18 | Thử tự xóa / tự khóa / tự hạ quyền admin đang đăng nhập | Bị chặn, giữ quyền quản trị |
| 19 | Tìm từ khóa không tồn tại | Hiện trạng thái không có kết quả |
| 20 | Đăng xuất và truy cập lại URL quản trị | Yêu cầu đăng nhập lại |

Để kiểm tra việc khóa tài khoản đang đăng nhập, dùng cửa sổ ẩn danh đăng nhập USER và cửa sổ thường đăng nhập ADMIN. ADMIN khóa USER; lần truy cập kế tiếp trong cửa sổ USER sẽ bị yêu cầu đăng nhập lại. Đổi vai trò, đổi username hoặc xóa tài khoản cũng kết thúc quyền truy cập cũ ở lần yêu cầu kế tiếp.

## 5. URL chính

| URL | Quyền | Chức năng |
| --- | --- | --- |
| `/register` | Công khai | Đăng ký |
| `/login` | Công khai | Đăng nhập |
| `/logout` | POST, có CSRF | Đăng xuất qua nút trên giao diện |
| `/home` | Đã đăng nhập | Thông tin tài khoản |
| `/admin` | ADMIN | Tổng quan |
| `/admin/categories` | ADMIN | Xem, tìm kiếm, phân trang Category |
| `/admin/categories/new` | ADMIN | Thêm Category |
| `/admin/categories/{id}/edit` | ADMIN | Sửa Category |
| `/admin/users` | ADMIN | Xem, tìm kiếm, phân trang User |
| `/admin/users/new` | ADMIN | Thêm User |
| `/admin/users/{id}/edit` | ADMIN | Sửa User |

Mọi thao tác thêm, sửa, xóa và đăng xuất dùng POST có CSRF. Tìm kiếm giữ từ khóa khi chuyển trang; số trang không hợp lệ được đưa về trang hợp lệ. JSP escape dữ liệu nhập để tránh chèn HTML. Mật khẩu không được hiển thị trong bảng User hay trả lại ở form sửa. Đăng ký không thể tự chọn ADMIN.

## 6. Dùng MySQL (tùy chọn)

1. Mở MySQL 8+, tạo database bằng `database/schema-mysql.sql` hoặc cho phép ứng dụng tự tạo qua JDBC URL mặc định.
2. Mở PowerShell tại thư mục gốc dự án:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="mat_khau_mysql_cua_ban"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=mysql"
```

Hoặc chạy WAR:

```powershell
java -jar .\dist\category-user-admin.war --spring.profiles.active=mysql
```

Profile `mysql` thay hoàn toàn database H2. Dữ liệu H2 không tự chuyển sang MySQL. Có thể đặt `DB_URL` để đổi host/port/database. Không ghi mật khẩu thật vào Git. Nếu không muốn tài khoản mẫu, đặt `$env:SEED_DEMO="false"` trước khi khởi động; khi đó cần tự chuẩn bị tài khoản admin trong môi trường của bạn.

## 7. Build và kiểm thử

```powershell
.\mvnw.cmd clean verify
java -jar .\target\category-user-admin.war
```

`ApplicationIntegrationTest` chạy Tomcat thật ở cổng ngẫu nhiên, render JSP thật và dùng database H2 riêng trong bộ nhớ. Kiểm tra đăng ký, BCrypt, đăng nhập sai/đúng, logout, quyền USER/ADMIN, CSRF, CRUD, tìm kiếm, phân trang, dữ liệu tiếng Việt/HTML, giữ mật khẩu khi để trống, tự bảo vệ admin và thu hồi phiên của tài khoản bị khóa. Không làm thay đổi database demo.

## 8. Tạo repository GitHub và commit

Repository nên chứa mã nguồn, `pom.xml`, Maven Wrapper, README, SQL và test. `.gitignore` bỏ qua `target`, `dist`, `data`, log, IDE và `.env`. Không đưa database hay mật khẩu MySQL thật lên GitHub.

Cách nhanh nếu có **Git** và **GitHub CLI** (lệnh `gh`):

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\publish-github.ps1 -Public
```

Script sẽ mở đăng nhập GitHub nếu cần, tạo Git repository ở máy, commit và tạo repository mới `springboot4-jsp-category-user`, rồi push. Cờ `-Public` cho phép giảng viên xem mà không cần cấp quyền. Bỏ cờ này để tạo repository private. Nếu tên đã tồn tại, đổi tên:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\publish-github.ps1 -RepoName "baitap-springboot4-category-user" -Public
```

Cách thủ công:

1. Đăng nhập GitHub, bấm **+ → New repository**, đặt tên, chọn Public để giảng viên truy cập. Để repository trống (không tạo README/.gitignore/license từ giao diện).
2. Tại thư mục dự án chạy các lệnh dưới đây, thay `TEN_GITHUB` bằng tài khoản của bạn:

```powershell
git init -b main
git add .
git commit -m "Implement Spring Boot 4 JSP auth and admin CRUD"
git remote add origin https://github.com/TEN_GITHUB/springboot4-jsp-category-user.git
git push -u origin main
```

Nếu Git báo thiếu danh tính tác giả, chạy `git config user.name "Ten cua ban"` và `git config user.email "email_github_cua_ban"` rồi commit lại. Không nhập mật khẩu/token vào mã nguồn.

## 9. Nộp UTExLMS

1. Mở đúng học phần và bài tập.
2. Chọn **Add submission / Thêm bài nộp**.
3. Dán link repository GitHub thực tế vào ô **Online text / Văn bản trực tuyến**, ví dụ `https://github.com/TEN_GITHUB/springboot4-jsp-category-user`.
4. **Save changes / Lưu thay đổi**; nếu bài có bước **Submit assignment / Nộp bài**, bấm tiếp và xác nhận.
5. Kiểm tra trạng thái **Submitted for grading / Đã nộp để chấm** hoặc trạng thái tương ứng của học phần; mở lại link để bảo đảm giảng viên truy cập được.

Loại ô nộp phụ thuộc cấu hình của giảng viên. Nếu chỉ cho tải tệp, nộp tệp chứa đường dẫn theo hướng dẫn của môn học. README này không phải bằng chứng đã tạo repository hay đã nộp LMS; chỉ kết quả trực tiếp trên GitHub/LMS mới xác nhận được hai việc đó.

## 10. Cấu trúc mã nguồn

```text
src/main/java/vn/edu/utex/admin/
  config/         Security, thu hồi quyền cũ, dữ liệu demo
  model/          AppUser, Category, Role
  repository/     Spring Data JPA
  service/        Quy tắc nghiệp vụ, xử lý tìm kiếm/phân trang
  web/            Controller MVC và form validation
src/main/webapp/WEB-INF/views/   JSP
src/main/webapp/WEB-INF/tags/    JSTL pagination tag
src/main/resources/static/css/  Giao diện
src/test/                       Kiểm thử tích hợp HTTP
scripts/                        Chạy, build và đưa mã lên GitHub
```

## 11. Lỗi thường gặp

- **Cổng 8080 đã được dùng:** dừng ứng dụng cũ bằng Ctrl+C, hoặc chạy `java -jar .\dist\category-user-admin.war --server.port=8081` rồi mở `http://localhost:8081`.
- **Java không tìm thấy / UnsupportedClassVersionError / release version 21 not supported:** kiểm tra `JAVA_HOME`, `java -version`, `javac -version`, `mvnw.cmd -v`; dùng JDK 21 và mở Terminal mới.
- **Database may be already in use:** không chạy hai phiên demo cùng lúc trên cùng thư mục dữ liệu.
- **Maven tải thư viện lỗi:** kiểm tra Internet/proxy. Lần đầu có thể mất vài phút. Bản WAR trong ZIP có sẵn thư viện để chạy mà không cần tải Maven.
- **MySQL Access denied:** kiểm tra `DB_USERNAME`, `DB_PASSWORD`, cổng MySQL và quyền tạo database.
- **Sửa code nhưng không thay đổi:** dùng `mvnw.cmd spring-boot:run`, hoặc build lại trước khi chạy WAR.
- **Đóng trang rồi mở vẫn đăng nhập:** cookie phiên còn hiệu lực; dùng nút Đăng xuất để kết thúc phiên.
