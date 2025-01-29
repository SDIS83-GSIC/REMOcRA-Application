#!/bin/bash -e

# Vérifie la présence des variables d'environnement
: "${GEOSERVER_URL:?}"
: "${GEOSERVER_USER:?}"
: "${GEOSERVER_PASSWORD:?}"
: "${POSTGIS_HOSTNAME:?}"
: "${POSTGIS_USER:?}"
: "${POSTGIS_PASSWORD:?}"

# Valeurs par défaut des variables d'environnement optionnelles
: "${POSTGIS_PORT:=5432}"
: "${POSTGIS_DB:=remocra}"
: "${DATA_DIR:=/data}"
: "${GEOSERVER_AVAILABILITYCHECK_ENABLED:=true}"
: "${GEOSERVER_AVAILABILITYCHECK_TIMEOUT:=120s}"
: "${GEOSERVER_AVAILABILITYCHECK_RETRYDELAY:=2s}"

# Réexporte les variables réassignées (valeurs par défaut) qui ne sont pas utilisées directement par le script
export POSTGIS_PORT POSTGIS_DB GEOSERVER_AVAILABILITYCHECK_RETRYDELAY


if [[ "$GEOSERVER_AVAILABILITYCHECK_ENABLED" == "true" ]]; then
    echo Attend "${GEOSERVER_AVAILABILITYCHECK_TIMEOUT}" que Geoserver soit disponible
    # URL tirée de l'image officielle docker.osgeo.org/geoserver
    # https://github.com/geoserver/docker/blob/ac3e1262221f96eb28de4ad6d1a4a7d85a68a30b/startup.sh#L39
    # shellcheck disable=SC2016 # on veut que le 'sh -c' évalue les variables
    timeout "${GEOSERVER_AVAILABILITYCHECK_TIMEOUT}" sh -c 'until curl -u "${GEOSERVER_USER}:${GEOSERVER_PASSWORD}" -fsS "${GEOSERVER_URL%/}/web/wicket/resource/org.geoserver.web.GeoServerBasePage/img/logo.png" >/dev/null 2>&1; do sleep "${GEOSERVER_AVAILABILITYCHECK_RETRYDELAY}"; done'
fi

echo Crée le workspace remocra
curl -fsS -u "${GEOSERVER_USER}:${GEOSERVER_PASSWORD}" "${GEOSERVER_URL%/}/rest/workspaces" --json '{"workspace":{"name":"remocra"}}' >/dev/null

echo Crée le datastore postgis
curl -fsS -u "${GEOSERVER_USER}:${GEOSERVER_PASSWORD}" "${GEOSERVER_URL%/}/rest/workspaces/remocra/datastores" >/dev/null \
    --variable %POSTGIS_HOSTNAME \
    --variable %POSTGIS_PORT \
    --variable %POSTGIS_USER \
    --variable %POSTGIS_PASSWORD \
    --variable %POSTGIS_DB \
    --expand-json "$(<"${DATA_DIR%/}/postgis-store.json")"
