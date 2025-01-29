ARG GLOWROOT_VERSION=0.14.2
FROM docker-registry.priv.atolcd.com/glowroot:$GLOWROOT_VERSION AS glowroot

# On passe par un stage intermédiaire parce qu'on n'a ni 'unzip' ni 'jar' dans l'image java:17-jre
FROM docker-registry.priv.atolcd.com/buildtools/base:jammy AS build

ARG GEOSERVER_VERSION=2.26.2
ARG GEOSERVER_SHA256=2b839e3069dccca5d597e9f1c9df6e9ee09da04a7217b8f7eb85717b8f443b1a
ARG GEOSERVER_QUERYLAYER_SHA256=2ae691016b73017785f16ffc5d80f794406461ea7b95161a802c5801855abfa0

ENV GEOSERVER_HOME=/opt/geoserver

SHELL [ "/bin/bash", "-c" ]
RUN mkdir -p /opt/geoserver \
    # Installation Geoserver
    && curl -fsSL -o /tmp/geoserver.zip "https://downloads.sourceforge.net/project/geoserver/GeoServer/${GEOSERVER_VERSION}/geoserver-${GEOSERVER_VERSION}-bin.zip" \
    && sha256sum -c - <<<"$GEOSERVER_SHA256 /tmp/geoserver.zip" \
    && unzip /tmp/geoserver.zip -x 'data_dir/*' -d /opt/geoserver \
    && mkdir -p /opt/geoserver/data_tmpl/security \
    # Le security/config.xml définit les filtres de sécurité, notamment sur l'API REST
    # Si on laisse Geoserver le créer à la volée au démarrage, l'API REST sera inaccessible jusqu'à redémarrer le service
    # On conserve donc le fichier et on le copie, si nécessaire, dans le GEOSERVER_DATA_DIR au démarrage
    && unzip -j /tmp/geoserver.zip data_dir/security/config.xml -d /opt/geoserver/data_tmpl/security \
    && rm -f /tmp/geoserver.zip \
    # Installation du plugin Geoserver querylayer
    && curl -fsSL -o /tmp/geoserver-querylayer-plugin.zip "https://downloads.sourceforge.net/project/geoserver/GeoServer/${GEOSERVER_VERSION}/extensions/geoserver-${GEOSERVER_VERSION}-querylayer-plugin.zip" \
    && sha256sum -c - <<<"$GEOSERVER_QUERYLAYER_SHA256 /tmp/geoserver-querylayer-plugin.zip" \
    && unzip /tmp/geoserver-querylayer-plugin.zip '*.jar' -d /opt/geoserver/webapps/geoserver/WEB-INF/lib \
    && rm -f /tmp/geoserver-querylayer-plugin.zip \
    # Finalisation
    && chmod -R a+rX /opt/geoserver

# Stage intermédiaire pour le dev local (ne contient pas de données)
FROM docker-registry.priv.atolcd.com/java:17-jre AS dev

RUN groupadd --system --non-unique --gid 1000 geoserver \
    && useradd --no-log-init --system --no-create-home --home "/opt/geoserver" --non-unique --uid 1000 --gid geoserver geoserver

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

COPY --link --from=build /opt/geoserver /opt/geoserver

COPY --link entrypoint.sh /

RUN mkdir -p /var/opt/geoserver && chmod -R a=rwxt /var/opt/geoserver

EXPOSE 8080
#VOLUME /var/opt/geoserver
#VOLUME /var/opt/glowroot

ENV GEOSERVER_HOME=/opt/geoserver
ENV GEOSERVER_DATA_DIR=/var/opt/geoserver

USER geoserver
ENTRYPOINT [ "/entrypoint.sh" ]
CMD [ "startup" ]

# Stage final
FROM dev

COPY --link load-data.sh /usr/local/bin/load-geoserver-data.sh
COPY --link data/ /data
