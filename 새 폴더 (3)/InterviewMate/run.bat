@echo off
chcp 65001 > nul
cd /d "%~dp0"
set "JAVA="

rem 1) PATH 에 java 가 있으면 그대로 사용
where java >nul 2>nul && set "JAVA=java"

rem 2) IntelliJ 가 내려받은 JDK (사용자\.jdks)
if not defined JAVA for /d %%D in ("%USERPROFILE%\.jdks\*") do if exist "%%D\bin\java.exe" set "JAVA=%%D\bin\java.exe"

rem 3) 자주 쓰는 JDK 설치 위치
if not defined JAVA for /d %%D in ("%ProgramFiles%\Java\*") do if exist "%%D\bin\java.exe" set "JAVA=%%D\bin\java.exe"
if not defined JAVA for /d %%D in ("%ProgramFiles%\Eclipse Adoptium\*") do if exist "%%D\bin\java.exe" set "JAVA=%%D\bin\java.exe"
if not defined JAVA for /d %%D in ("%ProgramFiles%\Microsoft\*") do if exist "%%D\bin\java.exe" set "JAVA=%%D\bin\java.exe"
if not defined JAVA for /d %%D in ("%ProgramFiles%\Zulu\*") do if exist "%%D\bin\java.exe" set "JAVA=%%D\bin\java.exe"

rem 4) IntelliJ 에 들어 있는 자바 (jbr)
if not defined JAVA for /d %%D in ("%ProgramFiles%\JetBrains\*") do if exist "%%D\jbr\bin\java.exe" set "JAVA=%%D\jbr\bin\java.exe"
if not defined JAVA for /d %%D in ("%LOCALAPPDATA%\Programs\*") do if exist "%%D\jbr\bin\java.exe" set "JAVA=%%D\jbr\bin\java.exe"

if not defined JAVA (
  echo [!] 이 컴퓨터에서 자바를 찾지 못했어요.
  echo     IntelliJ에서 App.java 를 실행하거나, https://adoptium.net 에서 JDK를 설치해 주세요.
  pause
  exit /b 1
)

echo 사용하는 자바: %JAVA%
"%JAVA%" -jar InterviewMate.jar
if errorlevel 1 (
  echo.
  echo [!] 실행에 실패했어요. 위 오류 메시지를 캡처해서 보여 주세요.
  pause
)
