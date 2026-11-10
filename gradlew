#!/bin/bash
GRADLE_HOME="$HOME/.gradle/wrapper/dists/gradle-8.11.1-bin/gradle-8.11.1"
exec "$GRADLE_HOME/bin/gradle" "$@"
