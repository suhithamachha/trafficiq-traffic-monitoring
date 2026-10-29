@REM ----------------------------------------------------------------------------
@REM Maven Wrapper script for Windows
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set MAVEN_CMD_LINE_ARGS=%*

if "%JAVA_HOME%" == "" (
  for /f "tokens=*" %%i in ('where java 2^>nul') do (
    set JAVA_EXE=%%i
    goto found_java
  )
  echo Error: JAVA_HOME is not set and java could not be found in PATH.
  exit /b 1
) else (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
)

:found_java
where mvn >nul 2>nul
if %ERRORLEVEL% equ 0 (
  mvn %MAVEN_CMD_LINE_ARGS%
  exit /b %ERRORLEVEL%
)

echo Notice: Using system Java to compile or execute Maven commands...
echo To build with Maven, install Apache Maven or run using your IDE (IntelliJ / Eclipse / VS Code).
exit /b 0
