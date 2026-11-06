# Ejecuta las pruebas automáticas del backend (HU-11) y guarda la salida como evidencia.
# Uso, desde la raíz del repositorio en PowerShell:  .\scripts\ejecutar-pruebas.ps1
$raiz = Split-Path -Parent $PSScriptRoot
$fecha = Get-Date -Format 'yyyy-MM-dd'
$salida = Join-Path $raiz "evidencias\semana-14\${fecha}_mvn-test.txt"

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host 'No se encontró Maven (mvn). Instálalo o ejecuta las pruebas desde IntelliJ (clic derecho en backend > Run All Tests).'
    exit 1
}

Push-Location (Join-Path $raiz 'backend')
try {
    "Ejecución: $(Get-Date -Format 'yyyy-MM-dd HH:mm')  |  Equipo: $env:COMPUTERNAME" | Out-File -FilePath $salida -Encoding utf8
    mvn -B test 2>&1 | Tee-Object -FilePath $salida -Append
    $codigo = $LASTEXITCODE
} finally {
    Pop-Location
}

if ($codigo -eq 0) { Write-Host "`nPruebas aprobadas. Salida guardada en $salida" }
else { Write-Host "`nHay pruebas fallidas (código $codigo). Revisa $salida y la carpeta backend\target\surefire-reports." }
exit $codigo
