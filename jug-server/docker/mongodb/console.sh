#!/usr/bin/env bash

docker exec -it mongodb mongo                       \
            --username "${MONGODB_USER}"            \
            --password "${MONGODB_PASSWORD}"        \
            --authenticationDatabase jira_data      \
            --authenticationMechanism SCRAM-SHA-256
