#!/usr/bin/env bash

. /opt/loader/env/env

docker exec -it mongodb mongo                       \
            --username "${MONGODB_USER}"            \
            --password "${MONGODB_PASSWORD}"        \
            --authenticationDatabase jira_data      \
            --authenticationMechanism SCRAM-SHA-256
