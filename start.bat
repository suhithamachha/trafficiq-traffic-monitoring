@echo off
title TRAFFICIQ - System Launcher
cls

echo ================================================================
echo             TRAFFICIQ - FULL-STACK SYSTEM LAUNCHER             
echo    Real-Time Traffic Monitoring & Intelligent Route Database    
echo ================================================================
echo.
echo Select an option to run:
echo.
echo  [1] Open Frontend Website in Default Web Browser (Zero Setup)
echo  [2] Start Local Web Server (Node.js - Port 5500)
echo  [3] Start Python Intelligence Service (FastAPI - Port 8000)
echo  [4] Start Java Spring Boot Backend (Port 8080)
echo  [5] Launch Full System (Browser + Local Server)
echo  [6] Exit
echo.
set /p choice="Enter your choice [1-6]: "

if "%choice%"=="1" goto open_browser
if "%choice%"=="2" goto start_node
if "%choice%"=="3" goto start_python
if "%choice%"=="4" goto start_java
if "%choice%"=="5" goto start_full
if "%choice%"=="6" goto exit

echo Invalid choice.
pause
exit /b

:open_browser
echo Opening frontend in your default browser...
start "" "%~dp0frontend\index.html"
echo Launched!
pause
exit /b

:start_node
echo Starting Node.js Web Server on http://localhost:5500...
node "%~dp0server.js" 5500
pause
exit /b

:start_python
echo Starting Python FastAPI Service on port 8000...
cd /d "%~dp0python-service"
if not exist "venv" (
    echo Creating virtual environment...
    python -m venv venv
    call venv\Scripts\activate.bat
    echo Installing dependencies...
    pip install -r requirements.txt
) else (
    call venv\Scripts\activate.bat
)
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
pause
exit /b

:start_java
echo Starting Java Spring Boot Backend on port 8080...
cd /d "%~dp0java-backend"
where mvn >nul 2>nul
if %ERRORLEVEL% equ 0 (
    mvn spring-boot:run
) else (
    echo Maven not found in PATH. Please install Apache Maven or open java-backend in your IDE.
)
pause
exit /b

:start_full
echo Launching TrafficiQ Web Server and opening browser...
start "" node "%~dp0server.js" 5500
timeout /t 2 >nul
start http://localhost:5500
echo Running on http://localhost:5500
pause
exit /b

:exit
exit /b
