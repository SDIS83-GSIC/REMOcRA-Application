FROM docker-registry.priv.atolcd.com/java:21-jre

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

# Documente le port exposé par l'application
EXPOSE 8881
# Documente les répertoires de données (notamment accédés en écriture)
# VOLUME /srv/remocra/

# Quelques variables d'environnement pour permettre une certaine configurabilité au runtime
ENV GC_OPTS="-XX:+DisableExplicitGC"
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
# Entrypoint
USER remocra
ENTRYPOINT [ "/opt/remocra/docker-entrypoint.sh" ]
CMD [ "serve" ]
