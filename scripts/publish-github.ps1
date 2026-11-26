param([string]$RepoName = "springboot4-jsp-category-user", [switch]$Public)
$ErrorActionPreference = "Stop"
Set-Location (Split-Path $PSScriptRoot -Parent)
foreach ($exe in @("git", "gh")) {
 if (-not (Get-Command $exe -ErrorAction SilentlyContinue)) { throw "Can cai $exe truoc. Xem README.md." }
}
gh auth status
if ($LASTEXITCODE -ne 0) {
 gh auth login --hostname github.com --web --git-protocol https
 if ($LASTEXITCODE -ne 0) { throw "Chua dang nhap GitHub." }
}
$login = gh api user --jq .login
if ($LASTEXITCODE -ne 0) { throw "Khong doc duoc tai khoan GitHub." }
if (-not (Test-Path .git)) {
 git init -b main
 if ($LASTEXITCODE -ne 0) { throw "Khong tao duoc Git repository." }
}
# Local author settings are needed only when the machine has no Git author configured.
if (-not (git config user.name)) { git config user.name $login }
if (-not (git config user.email)) {
 $userId = gh api user --jq .id
 if ($LASTEXITCODE -ne 0) { throw "Khong doc duoc GitHub user ID." }
 git config user.email "$userId+$login@users.noreply.github.com"
}
git add .
git diff --cached --quiet
if ($LASTEXITCODE -ne 0) {
 git commit -m "Implement Spring Boot 4 JSP auth and admin Category User CRUD"
 if ($LASTEXITCODE -ne 0) { throw "Commit that bai." }
}
$remotes = @(git remote)
if ($remotes -contains "origin") { throw "Da co remote origin. Dung git push de cap nhat; khong tao repository trung." }
$visibility = if ($Public) { "--public" } else { "--private" }
gh repo create "$login/$RepoName" $visibility --source . --remote origin --push
if ($LASTEXITCODE -ne 0) { throw "Tao repository/push that bai. Kiem tra ten repository va quyen GitHub." }
Write-Host "Link GitHub: https://github.com/$login/$RepoName"
