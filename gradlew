#!/usr/bin/env sh

# Simplified Gradle wrapper bootstrapper for the demo project.
# If the standard wrapper JAR is missing (common in minimal templates),
# this script will try to download the Gradle distribution configured in
# gradle/wrapper/gradle-wrapper.properties and extract the wrapper JAR
# before delegating to it.

set -e

APP_BASE_DIR=$(cd "$(dirname "$0")" && pwd)
WRAPPER_JAR="$APP_BASE_DIR/gradle/wrapper/gradle-wrapper.jar"
PROPERTIES_FILE="$APP_BASE_DIR/gradle/wrapper/gradle-wrapper.properties"

# Attempt to bootstrap the wrapper JAR if it doesn't exist yet
if [ ! -f "$WRAPPER_JAR" ]; then
  if [ ! -f "$PROPERTIES_FILE" ]; then
    echo "Missing $PROPERTIES_FILE; cannot determine distribution URL." >&2
    exit 1
  fi

  DISTRIBUTION_URL=$(grep distributionUrl "$PROPERTIES_FILE" | cut -d'=' -f2)
  if [ -z "$DISTRIBUTION_URL" ]; then
    echo "Unable to read distributionUrl from $PROPERTIES_FILE." >&2
    exit 1
  fi

  TMP_DIR=$(mktemp -d)
  ZIP_PATH="$TMP_DIR/gradle.zip"
  echo "gradle-wrapper.jar not found. Attempting to download distribution..." >&2
  if command -v curl >/dev/null 2>&1; then
    curl -L "$DISTRIBUTION_URL" -o "$ZIP_PATH"
  elif command -v wget >/dev/null 2>&1; then
    wget "$DISTRIBUTION_URL" -O "$ZIP_PATH"
  else
    echo "Neither curl nor wget is available to download Gradle." >&2
    exit 1
  fi

  unzip -j "$ZIP_PATH" "*/lib/gradle-wrapper-*.jar" -d "$APP_BASE_DIR/gradle/wrapper" >/dev/null
  rm -rf "$TMP_DIR"
fi

if [ ! -f "$WRAPPER_JAR" ]; then
  echo "gradle-wrapper.jar is still missing. Please download the Gradle distribution manually." >&2
  exit 1
fi

exec java -Dorg.gradle.appname="gradlew" -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
