#!/bin/bash -e

POSTGIS_HOSTNAME=${POSTGIS_HOSTNAME:-${POSTGRES_DB_HOSTNAME}}
POSTGIS_USER=${POSTGIS_USER:-${POSTGRES_DB_USERNAME}}
POSTGIS_PASSWORD=${POSTGIS_PASSWORD:-${POSTGRES_DB_PASSWORD}}

case "$1" in
  -* | "" | start | startup )
    cp -R --update=none /opt/geoserver/data_tmpl/* "${GEOSERVER_DATA_DIR}"

    if [ -n "$GEOSERVER_GLOWROOT_ENABLED" ] && [ "$GEOSERVER_GLOWROOT_ENABLED" != "false" ] && [ "$GEOSERVER_GLOWROOT_ENABLED" != "0" ]; then
      JAVA_OPTS="$JAVA_OPTS -javaagent:/opt/glowroot/glowroot.jar"
      if [ -n "$GLOWROOT_PORT" ]; then
        JAVA_OPTS="$JAVA_OPTS -Dglowroot.agent.port=${GEOSERVER_GLOWROOT_PORT:-4001}"
      fi
    fi
    if [ "${OTEL_JAVAAGENT_ENABLED,,}" = "true" ]; then
      OTEL_RESOURCE_ATTRIBUTES="service.namespace=$OTEL_SERVICE_NAMESPACE,service.instance.id=$OTEL_SERVICE_INSTANCE_ID,service.version=$OTEL_SERVICE_VERSION,$OTEL_RESOURCE_ATTRIBUTES"
      echo "---------------------------------"
      echo "OpenTelemetry environment variables :"
      env | grep "^OTEL_" | sort
      echo "---------------------------------"
      JAVA_OPTS="$JAVA_OPTS -javaagent:/opt/opentelemetry-javaagent/opentelemetry-javaagent.jar"
    fi

    export JAVA_OPTS="$JAVA_OPTS -Djetty.http.port=${GEOSERVER_PORT:-8090}"
    commande=( /opt/geoserver/bin/startup.sh )
    if [[ "$(id -u)" == "0" ]]; then
      commande=( setpriv --reuid=geoserver --regid=geoserver --init-groups "${commande[@]}" )
    fi
    echo "---------------------------------"
    echo "Starting with JVM options :"
    echo "JAVA_OPTS:       $JAVA_OPTS"
    echo "---------------------------------"
    exec "${commande[@]}" "$@"
    ;;
  load-data )
    exec /usr/local/bin/load-geoserver-data.sh "${@:2}"
    ;;
  * )
    exec "$@"
    ;;
esac
