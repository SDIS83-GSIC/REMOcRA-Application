#!/bin/bash

# On lève une erreur si les variables ne sont pas définies
: "${POSTGRES_DB_NAME:?}"
: "${POSTGRES_DB_USERNAME:?}"
: "${POSTGRES_DB_PASSWORD:?}"
: "${POSTGRES_DB_HOSTNAME:?}"
: "${POSTGRES_DB_PORT:?}"

# On copie le fichier template en remplaçant les placeholders
sed -e "s|#DB_NAME|${POSTGRES_DB_NAME}|g" \
    -e "s|#DB_USERNAME|${POSTGRES_DB_USERNAME}|g" \
    -e "s|#DB_PASSWORD|${POSTGRES_DB_PASSWORD}|g" \
    -e "s|#DB_HOSTNAME|${POSTGRES_DB_HOSTNAME}|g" \
    -e "s|#DB_PORT|${POSTGRES_DB_PORT}|g" \
    /opt/hop/variables.json \
    > /tmp/variables.json



# On s'assure que le fichier est dans la liste, même si la variable est définie au runtime
export HOP_ENVIRONMENT_CONFIG_FILE_NAME_PATHS="/tmp/variables.json,${HOP_ENVIRONMENT_CONFIG_FILE_NAME_PATHS}"

case "$1" in
  hop )
    exec /opt/hop/run.sh "${@:1}"
    ;;
  * )
    exec "$@"
    ;;
esac