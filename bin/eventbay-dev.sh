#!/usr/bin/env bash
set -e

if [[ -z "${EVENTBAY_HOME}" ]]; then
  echo "EVENTBAY_HOME environment variable is not set!"
  exit 255
fi

cd $EVENTBAY_HOME && docker compose \
    -f .docker/docker-compose.development.yml \
    --project-name eventbay \
    --project-directory . $@
