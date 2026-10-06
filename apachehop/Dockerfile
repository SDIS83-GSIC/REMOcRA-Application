FROM apache/hop:2.16.0

# Installer en tant que root
USER root

# Installe jq pour le script d'entrypoint
## TODO: cibler plus précisément les répertoires dans lesquels HOP va écrire
RUN apk add --no-cache jq=1.8.2-r0 && \
    find /opt/hop -type d -exec chmod a=rwxt {} +

# Copier le fichier variables.json template
COPY variables.json /opt/hop/variables.json

COPY remocra.json /files/metadata/rdbms/remocra.json

COPY local.json /files/metadata/pipeline-run-configuration/local.json

RUN mkdir -p /files/tasks

# Copier l'entrypoint personnalisé et le rendre exécutable
COPY --chmod=a+x entrypoint.sh /entrypoint.sh

# Changer d'utilisateur en dernier
USER hop

ENV HOP_SERVER_PORT=8060

ENV HOP_PROJECT_FOLDER=/files/tasks
ENV HOP_SERVER_METADATA_FOLDER=/files/metadata

ENTRYPOINT [ "/entrypoint.sh" ]
CMD ["hop"]
