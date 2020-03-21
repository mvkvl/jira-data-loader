#!/usr/bin/env bash

docker stop $(docker ps -qa)
docker container rm $(docker container ls -qa)
docker system prune -f -a --volumes
