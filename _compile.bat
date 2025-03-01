@echo off
REM Create temp directory if it doesn't exist
if not exist temp mkdir temp

REM Copy all Java files to temp folder
xcopy /s /y src\*.java temp\

REM Compile all Java files
javac -d bin -cp lib/*; temp\*.java

REM Cleanup temp folder
rmdir /s /q temp

echo Compilation finished.