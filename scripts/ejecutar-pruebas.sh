#!/usr/bin/env bash
# Ejecuta las pruebas automáticas del backend (HU-11) y guarda la salida como evidencia.
# Uso, desde la raíz del repositorio:  bash scripts/ejecutar-pruebas.sh
set -o pipefail
raiz="$(cd "$(dirname "$0")/.." && pwd)"
salida="$raiz/evidencias/semana-14/$(date +%F)_mvn-test.txt"
command -v mvn >/dev/null || { echo "No se encontró Maven (mvn)."; exit 1; }
echo "Ejecución: $(date '+%F %H:%M')  |  Equipo: $(hostname)" > "$salida"
(cd "$raiz/backend" && mvn -B test) 2>&1 | tee -a "$salida"
codigo=$?
echo; [ $codigo -eq 0 ] && echo "Pruebas aprobadas. Salida en $salida" || echo "Hay pruebas fallidas (código $codigo). Revisa $salida."
exit $codigo
