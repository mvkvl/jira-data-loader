#!/usr/bin/env bash

# TODO: set docker container log limit

docker network create jira-data
docker run --rm -d -p 27017:27017                                           \
           --name mongodb                                                   \
           --net jira-data                                                  \
           -e ALLOW_EMPTY_PASSWORD=yes                                      \
           -e MONGODB_ENABLE_DIRECTORY_PER_DB=yes                           \
           -e MONGODB_ROOT_PASSWORD=${MONGODB_ROOT_PASSWORD}                \
           -e MONGODB_USERNAME=${MONGODB_USER}                              \
           -e MONGODB_PASSWORD=${MONGODB_PASSWORD}                          \
           -e MONGODB_DATABASE=jira_data                                    \
           -v $(pwd)/data:/bitnami                                          \
           bitnami/mongodb:latest
