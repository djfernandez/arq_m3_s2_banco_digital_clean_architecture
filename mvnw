#!/bin/sh
set -e

BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
PROPS_FILE="$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"

if [ ! -f "$PROPS_FILE" ]; then
  echo "Error: Missing $PROPS_FILE" >&2
  exit 1
fi

DISTRIBUTION_URL=$(grep '^distributionUrl=' "$PROPS_FILE" | cut -d= -f2-)

if [ -z "$DISTRIBUTION_URL" ]; then
  echo "Error: distributionUrl was not found in $PROPS_FILE" >&2
  exit 1
fi

ZIP_NAME=$(basename "$DISTRIBUTION_URL")
VERSION_DIR=${ZIP_NAME%-bin.zip}
MVNW_REPO="${HOME}/.m2/wrapper/dists"
MAVEN_HOME="${MVNW_REPO}/${VERSION_DIR}"
MVN_CMD="${MAVEN_HOME}/bin/mvn"

if [ ! -x "$MVN_CMD" ]; then
  mkdir -p "$MVNW_REPO"
  TMP_ZIP="${TMPDIR:-/tmp}/${ZIP_NAME}"

  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$DISTRIBUTION_URL" -o "$TMP_ZIP"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$DISTRIBUTION_URL" -O "$TMP_ZIP"
  else
    echo "Error: curl or wget is required to download Maven." >&2
    exit 1
  fi

  unzip -q -o "$TMP_ZIP" -d "$MVNW_REPO"
fi

exec "$MVN_CMD" "$@"