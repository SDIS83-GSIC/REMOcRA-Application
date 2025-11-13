FROM apache/hop:2.15.0


USER hop

# Permet de lancer HOP avec un utilisateur arbitraire (docker run --user)
# TODO: cibler plus précisément les répertoires dans lesquels HOP va écrire
RUN find /opt/hop -type d -exec chmod a=rwxt {} +

# Copier le fichier variables.json template
COPY variables.json /opt/hop/variables.json

COPY remocra.json /files/metadata/rdbms/remocra.json

RUN mkdir -p /files/tasks

# Copier l'entrypoint personnalisé et le rendre exécutable
COPY --chmod=a+x entrypoint.sh /entrypoint.sh

ENV HOP_SERVER_PORT=8060

ENV HOP_PROJECT_FOLDER=/files/tasks
ENV HOP_SERVER_METADATA_FOLDER=/files/metadata

ENTRYPOINT [ "/entrypoint.sh" ]
CMD ["hop"]
