#!/bin/bash

# On lève une erreur si les variables obligatoires ne sont pas définies
: "${POSTGRES_DB_NAME:?}"
: "${POSTGRES_DB_USERNAME:?}"
: "${POSTGRES_DB_PASSWORD:?}"
: "${POSTGRES_DB_HOSTNAME:?}"
: "${POSTGRES_DB_PORT:?}"
: "${CLIENT_SECRET_APACHE_HOP}"

# Utilise jq pour remplacer les variables avec échappement JSON automatique
# Les caractères spéciaux (", &, /, etc.) sont gérés correctement
jq \
  --arg db_name "$POSTGRES_DB_NAME" \
  --arg db_username "$POSTGRES_DB_USERNAME" \
  --arg db_password "$POSTGRES_DB_PASSWORD" \
  --arg db_hostname "$POSTGRES_DB_HOSTNAME" \
  --arg db_port "$POSTGRES_DB_PORT" \
  --arg sig_hostname "${SIG_HOSTNAME}" \
  --arg sig_vendor "${SIG_DATABASE_VENDOR}" \
  --arg sig_dbname "${SIG_DBNAME}" \
  --arg sig_username "${SIG_USERNAME}" \
  --arg sig_password "${SIG_PASSWORD}" \
  --arg sig_port "${SIG_PORT_NUMBER}" \
  --arg client_secret "$CLIENT_SECRET_APACHE_HOP" \
  '.variables[0].value = $db_name |
   .variables[1].value = $db_username |
   .variables[2].value = $db_password |
   .variables[3].value = $db_hostname |
   .variables[4].value = $db_port |
   .variables[5].value = $sig_hostname |
   .variables[6].value = $sig_vendor |
   .variables[7].value = $sig_dbname |
   .variables[8].value = $sig_username |
   .variables[9].value = $sig_password |
   .variables[10].value = $sig_port |
   .variables[11].value = $client_secret' \
  /opt/hop/variables.json > /tmp/variables.json

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