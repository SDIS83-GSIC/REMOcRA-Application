#!/bin/bash

TMP_USERNAME=tmp_user
TMP_PASSWORD=

KEYCLOAK_URL=http://localhost:8080

SUPER_USERNAME="Upper_admin"
SUPER_PASSWORD=

#1. Créer un compte super-admin temporaire
docker compose run --rm \
    -e KEYCLOAK_URL="$KEYCLOAK_URL" \
    -e USERNAME="$TMP_USERNAME" \
    -e PASSWORD="$TMP_PASSWORD" \
    keycloak bootstrap-admin user --username:env USERNAME --password:env PASSWORD --no-prompt

#2. Création du compte
docker compose run --rm \
    -e KEYCLOAK_URL="$KEYCLOAK_URL" \
    -e KEYCLOAK_USER="$TMP_USERNAME" \
    -e KEYCLOAK_PASSWORD="$TMP_PASSWORD" \
    keycloak kcadm.sh create users -r master -s username="$SUPER_USERNAME" -s enabled=true

#3. Init du mot de passe
docker compose run --rm \
    -e KEYCLOAK_URL="$KEYCLOAK_URL" \
    -e KEYCLOAK_USER="$TMP_USERNAME" \
    -e KEYCLOAK_PASSWORD="$TMP_PASSWORD" \
    keycloak kcadm.sh set-password -r master --username "$SUPER_USERNAME" --new-password "$SUPER_PASSWORD"

#4. Affectation du rôle admin
docker compose run --rm \
    -e KEYCLOAK_URL="$KEYCLOAK_URL" \
    -e KEYCLOAK_USER="$TMP_USERNAME" \
    -e KEYCLOAK_PASSWORD="$TMP_PASSWORD" \
    keycloak kcadm.sh add-roles -r master --uusername "$SUPER_USERNAME" --rolename admin

exit
# Ce exit est la pour signifier que la suite est à exécuter dans un second temps, après vérification que le compte admin
# créer soit fonctionnelle.

#5. Suppression du compte temporaire
# /!\ Bien vérifié que le compte créer avant fonctionne
USER_ID=$(docker compose run --rm \
    -e KEYCLOAK_URL="$KEYCLOAK_URL" \
    -e KEYCLOAK_USER="$TMP_USERNAME" \
    -e KEYCLOAK_PASSWORD="$TMP_PASSWORD" \
    keycloak kcadm.sh get users -r master -q username="$TMP_USERNAME" --fields id --format csv --noquotes) && \
docker compose run --rm \
    -e KEYCLOAK_URL="$KEYCLOAK_URL" \
    -e KEYCLOAK_USER="$SUPER_USERNAME" \
    -e KEYCLOAK_PASSWORD="$SUPER_PASSWORD" \
    keycloak kcadm.sh delete "users/$USER_ID" -r master
