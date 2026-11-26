@echo off
setlocal
cd /d "%~dp0.."
call mvnw.cmd clean verify
if errorlevel 1 (
 pause
 exit /b 1
)
if not exist dist mkdir dist
copy /Y target\category-user-admin.war dist\category-user-admin.war >nul
 echo Build thanh cong. Chay scripts\run-demo.bat de mo ung dung.
pause
