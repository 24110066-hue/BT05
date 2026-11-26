@echo off
setlocal
chcp 65001 >nul
cd /d "%~dp0.."
where java >nul 2>nul
if errorlevel 1 (
 echo Chua tim thay Java. Hay cai JDK 21 va mo lai Terminal.
 pause
 exit /b 1
)
if exist "dist\category-user-admin.war" (
 echo Dang chay: http://localhost:8080
 echo Admin: admin / Admin@123
 java -jar "dist\category-user-admin.war"
) else (
 echo Dang tai thu vien va chay du an. Lan dau can Internet.
 call mvnw.cmd spring-boot:run
)
pause
