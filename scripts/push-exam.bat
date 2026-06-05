@echo off
REM Quick commit + push during the exam
REM Usage:  scripts\push-exam.bat "Task 1: employee management"
REM    or:  scripts\push-exam.bat   (prompts for message)
cd /d "%~dp0.."

where git >nul 2>&1
if errorlevel 1 (
  echo ERROR: Git is not installed or not in PATH.
  exit /b 1
)

if not exist .git (
  echo ERROR: Git not initialized. Run first:  scripts\setup-github.bat
  exit /b 1
)

git remote get-url origin >nul 2>&1
if errorlevel 1 (
  echo ERROR: No GitHub remote. Run first:  scripts\setup-github.bat
  exit /b 1
)

set MSG=%*
if "%MSG%"=="" (
  set /p MSG=Commit message: 
)
if "%MSG%"=="" (
  echo ERROR: Commit message required.
  exit /b 1
)

echo.
echo Staging all changes...
git add -A

git diff --cached --quiet
if not errorlevel 1 (
  git diff --quiet
  if not errorlevel 1 (
    echo Nothing to commit.
    exit /b 0
  )
)

echo Committing...
git commit -m "%MSG%"
if errorlevel 1 (
  echo Commit failed.
  exit /b 1
)

for /f %%b in ('git rev-parse --abbrev-ref HEAD') do set BRANCH=%%b
echo Pushing to origin/%BRANCH% ...
git push -u origin %BRANCH%
if errorlevel 1 (
  echo.
  echo Push failed. Check: internet, GitHub login, repo exists.
  echo First time? You may need: git push -u origin main
  exit /b 1
)

echo.
echo Done. Changes pushed to GitHub.
echo.
