@echo off
REM Create temp directory if it doesn't exist
if not exist temp mkdir temp

REM Copy all Java files directly to temp folder (flattening structure)
for /r src %%f in (*.java) do copy "%%f" temp\

REM Check if any Java files exist in temp before compiling
if not exist temp\*.java (
    echo No Java files found in temp folder.
    exit /b 1
)

REM Compile all Java files in the temp folder
javac -d bin -cp "lib/*" temp\*.java

REM Cleanup temp folder
rmdir /s /q temp

echo Compilation finished.