
:: Mock Server Simple - Ejecutar desde CMD (no PowerShell)
@echo off
echo 🚀 Iniciando Mock Server Java...
echo.

:: Buscar Gson en el proyecto
set GSON_JAR=
for /r . %%f in (gson-*.jar) do set GSON_JAR=%%f

:: Si no encuentra Gson, usar una ruta común
if not defined GSON_JAR (
    echo Buscando Gson en Gradle cache...
    for /r "%USERPROFILE%\.gradle" %%f in (gson-*.jar) do set GSON_JAR=%%f
)

:: Crear directorio build si no existe
if not exist build mkdir build

echo Compilando MockServer...
javac -cp "%GSON_JAR%" -d build app\src\main\java\com\android\excuses404\mock\MockServer.java

if %errorlevel% neq 0 (
    echo ❌ Error de compilacion. Intentando sin Gson...
    javac -d build app\src\main\java\com\android\excuses404\mock\MockServer.java
    if %errorlevel% neq 0 (
        echo ❌ Error critico de compilacion
        pause
        exit /b 1
    )
)

echo ✅ Compilacion exitosa
echo Ejecutando Mock Server...
java -cp "build;%GSON_JAR%" com.android.excuses404.mock.MockServer

pause
