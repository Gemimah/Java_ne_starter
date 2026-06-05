@echo off
REM One-time setup: link this project to GitHub (Java_ne_starter)
cd /d "%~dp0.."

where git >nul 2>&1
if errorlevel 1 (
  echo ERROR: Git is not installed or not in PATH.
  exit /b 1
)

if not exist .git (
  echo Initializing git repository...
  git init
  git branch -M main
)

set /p GITHUB_USER=Enter your GitHub username: 
if "%GITHUB_USER%"=="" (
  echo ERROR: Username required.
  exit /b 1
)

set REMOTE=https://github.com/%GITHUB_USER%/Java_ne_starter.git
git remote remove origin 2>nul
git remote add origin %REMOTE%
echo.
echo Remote set to: %REMOTE%
echo.
echo Make sure the repo "Java_ne_starter" exists on GitHub before pushing.
echo Then run:  scripts\push-exam.bat "your commit message"
echo.
