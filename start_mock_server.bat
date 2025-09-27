@echo off
echo 🚀 Compilando y ejecutando Mock Server Java...
echo.

cd /d "c:\Users\Kaibor\Desktop\desarollo 1\android"

REM Crear directorio para las clases compiladas
if not exist "mock_build" mkdir mock_build

REM Buscar el JAR de Gson en el proyecto Gradle
for /r "app\build\intermediates" %%f in (gson-*.jar) do set GSON_JAR=%%f

REM Si no encuentra Gson en build, buscar en cache de Gradle
if not defined GSON_JAR (
    for /r "%USERPROFILE%\.gradle\caches" %%f in (gson-*.jar) do set GSON_JAR=%%f
)

REM Si aún no encuentra Gson, usar una versión online (requiere internet)
if not defined GSON_JAR (
    echo ⚠️  No se encontró Gson localmente, descargando...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.9/gson-2.8.9.jar' -OutFile 'gson-2.8.9.jar'"
    set GSON_JAR=gson-2.8.9.jar
)

echo 📦 Usando Gson: %GSON_JAR%
echo.

REM Compilar el MockServer
echo 🔨 Compilando MockServer...
javac -cp "%GSON_JAR%" -d mock_build app\src\main\java\com\android\excuses404\mock\MockServer.java

if errorlevel 1 (
    echo ❌ Error al compilar MockServer
    pause
    exit /b 1
)

echo ✅ Compilación exitosa
echo.

REM Ejecutar el MockServer
echo 🌐 Iniciando Mock Server...
java -cp "mock_build;%GSON_JAR%" com.android.excuses404.mock.MockServer

pause
