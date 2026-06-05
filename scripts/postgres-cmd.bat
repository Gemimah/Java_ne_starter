@echo off
REM Open psql on exam_db. First run in CMD:
REM   set PGPASSWORD=your_postgres_password
if "%PGPASSWORD%"=="" (
  echo ERROR: Run first: set PGPASSWORD=your_postgres_password
  exit /b 1
)
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -d exam_db
