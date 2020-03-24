#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

cd ${DIR}/..                                                                                           && \
mvn clean package                                                                                      && \
mkdir -p ${DIR}/loader/lib                                                                             && \
scp target/jira-data-loader.jar jug:~                                                                  && \
ssh jug 'sudo mv jira-data-loader.jar /opt/loader/lib/jira-data-loader.jar'                            && \
echo "DONE!"
