#!/bin/bash

case "$1" in
  # Argument direct, ou sous-commandes connues gérées par l’application
  -* | serve | migrate-db | validate-db | info-db | repair-db )
    if [ -n "$GLOWROOT_ENABLED" ] && [ "$GLOWROOT_ENABLED" != "false" ] && [ "$GLOWROOT_ENABLED" != "0" ]; then
      JAVA_OPTS="$JAVA_OPTS -javaagent:/opt/glowroot/glowroot.jar"
      if [ -n "$GLOWROOT_PORT" ]; then
        JAVA_OPTS="$JAVA_OPTS -Dglowroot.agent.port=${GLOWROOT_PORT:-4000}"
      fi
    fi
    if [ "${OTEL_JAVAAGENT_ENABLED,,}" = "true" ]; then
      OTEL_RESOURCE_ATTRIBUTES="service.namespace=$OTEL_SERVICE_NAMESPACE,service.instance.id=$OTEL_SERVICE_INSTANCE_ID,service.version=$OTEL_SERVICE_VERSION,$OTEL_RESOURCE_ATTRIBUTES"
      echo "---------------------------------"
      echo "OpenTelemetry environment variables :"
      env | grep "^OTEL_" | sort
      echo "---------------------------------"
      OTEL_OPTS="-javaagent:/opt/opentelemetry-javaagent/opentelemetry-javaagent.jar"
    fi
     # shellcheck disable=SC2206 # pour GC_OPTS, GC_LOG_OPTS, JAVA_OPTS, JAVA_MEM_OPTS et OTEL_OPTS
    commande=( java $GC_OPTS $GC_LOG_OPTS $JAVA_OPTS $JAVA_MEM_OPTS $OTEL_OPTS -cp "/opt/remocra/classes:/opt/remocra/libs/*" remocra.cli.Main )
    if [[ "$(id -u)" == "0" ]]; then
      commande=( setpriv --reuid=remocra --regid=remocra --init-groups "${commande[@]}" )
    fi
    echo "---------------------------------"
    echo "Starting with JVM options :"
    echo "GC_OPTS:         $GC_OPTS"
    echo "GC_LOG_OPTS:     $GC_LOG_OPTS"
    echo "JAVA_OPTS:       $JAVA_OPTS"
    echo "JAVA_MEM_OPTS:   $JAVA_MEM_OPTS"
    echo "OTEL_OPTS:       $OTEL_OPTS"
    echo "---------------------------------"
    exec "${commande[@]}" "$@"
    ;;
  * )
    exec "$@"
    ;;
esac
