ARG GLOWROOT_VERSION=0.14.2
FROM docker-registry.priv.atolcd.com/glowroot:$GLOWROOT_VERSION AS glowroot

FROM docker-registry.priv.atolcd.com/java:21-jre

ARG REMOCRA_VERSION=DEV

# Version temporaire, en attendant que hadolint supporte le flag "--exclude"
# https://github.com/hadolint/hadolint/issues/1029
RUN --mount=type=bind,from=glowroot,target=/mnt/glowroot \
    cp -a /mnt/glowroot/* / \
    && rm /opt/glowroot/lib/*-osx.jar /opt/glowroot/lib/*-windows.jar
# La syntaxe "docker/dockerfile:1.7-labs" ajoute le support du flag "--exclude"
# mais ce n'est pas valide pour hadolint.
#COPY --link --from=glowroot \
#     --exclude=opt/glowroot/lib/*-osx.jar \
#     --exclude=opt/glowroot/lib/*-windows.jar \
#     / /

# Crée un utilisateur applicatif et son groupe dédié
RUN groupadd --system --non-unique --gid 2000 remocra \
 && useradd --no-log-init --system --no-create-home --home /opt/remocra --non-unique --uid 2000 --gid remocra remocra
WORKDIR /opt/remocra

COPY docker/docker-entrypoint.sh ./

# 1. Third-party libs (préparées préalablement par le build)
COPY build/docker/libs/ libs/
# 2. Resources (y compris la configuration de référence)
COPY build/docker/resources/ classes/
# 3. Classes
COPY build/docker/classes/ classes/
# 4. Frontend
COPY frontend/build/parceljs/ www/

# Le fichier est lu par remocra.app.AppSettings au lancement de l'application
COPY <<EOF classes/REMOCRA_VERSION
${REMOCRA_VERSION}
EOF

# Documente le port exposé par l'application
EXPOSE 8881
# Documente les répertoires de données (notamment accédés en écriture)
# VOLUME /var/lib/remocra/
# VOLUME /var/opt/glowroot/

# Quelques variables d'environnement pour permettre une certaine configurabilité au runtime
ENV GC_OPTS="-XX:+DisableExplicitGC"
ENV GC_LOG_OPTS=""
ENV JAVA_OPTS=""
ENV JAVA_MEM_OPTS="-XX:MaxRAMPercentage=75"
# Entrypoint
USER remocra
ENTRYPOINT [ "/opt/remocra/docker-entrypoint.sh" ]
CMD [ "serve" ]
