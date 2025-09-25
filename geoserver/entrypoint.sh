#!/bin/bash -e

case "$1" in
  -* | "" | start | startup )
    cp -R --update=none /opt/geoserver/data_tmpl/* "${GEOSERVER_DATA_DIR}"

    if [ -n "$GEOSERVER_GLOWROOT_ENABLED" ] && [ "$GEOSERVER_GLOWROOT_ENABLED" != "false" ] && [ "$GEOSERVER_GLOWROOT_ENABLED" != "0" ]; then
      JAVA_OPTS="$JAVA_OPTS -javaagent:/opt/glowroot/glowroot.jar"
      if [ -n "$GLOWROOT_PORT" ]; then
        JAVA_OPTS="$JAVA_OPTS -Dglowroot.agent.port=${GEOSERVER_GLOWROOT_PORT:-4001}"
      fi
    fi

    export JAVA_OPTS="$JAVA_OPTS -Djetty.http.port=${GEOSERVER_PORT:-8090}"
    commande=( /opt/geoserver/bin/startup.sh )
    if [[ "$(id -u)" == "0" ]]; then
      commande=( setpriv --reuid=geoserver --regid=geoserver --init-groups "${commande[@]}" )
    fi
    exec "${commande[@]}" "$@"
    ;;
  load-data )
    exec /usr/local/bin/load-geoserver-data.sh "${@:2}"
    ;;
  * )
    exec "$@"
    ;;
esac
