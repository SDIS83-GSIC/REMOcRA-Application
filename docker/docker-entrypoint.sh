#!/bin/bash

case "$1" in
  # Argument direct, ou sous-commandes connues gérées par l’application
  -* | serve | migrate-db | validate-db | info-db | repair-db )
    if [ -n "$GLOWROOT_ENABLED" ] && [ "$GLOWROOT_ENABLED" != "false" ] && [ "$GLOWROOT_ENABLED" != "0" ]; then
      JAVA_OPTS="$JAVA_OPTS -javaagent:/opt/glowroot/glowroot.jar"
      if [ -n "$GLOWROOT_PORT" ]; then
        JAVA_OPTS="$JAVA_OPTS -Dglowroot.agent.port=$GLOWROOT_PORT"
      fi
    fi

    # shellcheck disable=SC2206 # pour GC_OPTS et JAVA_OPTS
    commande=( java $GC_OPTS $JAVA_OPTS -cp "/opt/remocra/classes:/opt/remocra/libs/*" remocra.cli.Main )
    if [[ "$(id -u)" == "0" ]]; then
      commande=( setpriv --reuid=remocra --regid=remocra --init-groups "${commande[@]}" )
    fi
    exec "${commande[@]}" "$@"
    ;;
  * )
    exec "$@"
    ;;
esac
