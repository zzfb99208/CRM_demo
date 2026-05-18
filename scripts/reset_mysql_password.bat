@echo off
:: MySQL Root Password Reset
:: Right-click and "Run as Administrator"

set MYSQL_BIN=C:\Program Files\MySQL\MySQL Server 8.0\bin
set MYSQL_INI=C:\ProgramData\MySQL\MySQL Server 8.0\my.ini

echo ============================================
echo   MySQL Root Password Reset
echo ============================================
echo.

echo [1/4] Stopping MySQL service...
net stop MYSQL80
timeout /t 2 /nobreak >nul

echo [2/4] Starting mysqld in background (skip-grant-tables)...
:: Use start /MIN to minimize the window, mysqld runs in background
start "MySQL_Reset" /MIN "%MYSQL_BIN%\mysqld.exe" --defaults-file="%MYSQL_INI%" --skip-grant-tables --shared-memory --skip-networking=0 --port=3306

echo Waiting for mysqld to be ready...
timeout /t 10 /nobreak >nul

:: Check if mysqld is actually running
tasklist /FI "IMAGENAME eq mysqld.exe" 2>nul | find /I "mysqld" >nul
if %errorlevel% neq 0 (
    echo ERROR: mysqld did not start! Check the MySQL_Reset window for errors.
    echo The window might show a permission error. Close it and try again.
    pause
    exit /b 1
)
echo mysqld is running.

echo [3/4] Resetting root password via shared memory...
"%MYSQL_BIN%\mysql" -u root --protocol=memory -e "FLUSH PRIVILEGES; ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';" 2>&1
if %errorlevel% equ 0 (
    echo Password reset SUCCESS!
) else (
    echo.
    echo ERROR: Password reset failed.
    echo The MySQL_Reset window may show what went wrong.
    echo.
)

echo [4/4] Cleaning up and restarting service...
taskkill /F /IM mysqld.exe >nul 2>&1
timeout /t 3 /nobreak >nul
net start MYSQL80
timeout /t 5 /nobreak >nul

echo.
echo ============================================
echo Testing new password (root/root)...
"%MYSQL_BIN%\mysql" -u root -proot -e "SELECT 'SUCCESS - Password is now: root' AS Result;"
echo ============================================
pause
