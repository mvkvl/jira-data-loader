#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
USER=loader

cd ${DIR}/..                                                                                           && \
mvn clean package                                                                                      && \
mkdir -p ${DIR}/loader/lib                                                                             && \
cp target/jira-data-loader.jar ${DIR}/loader/lib                                                       && \
cd ${DIR}                                                                                              && \
tar cvfz jug.tar.gz docker loader jira-data-loader.cron                                                && \
scp jug.tar.gz jug:~                                                                                   && \
ssh jug 'sudo mv jug.tar.gz /opt && cd /opt && sudo tar xfz jug.tar.gz && sudo rm jug.tar.gz'          && \
ssh jug "sudo chown -R ${USER} /opt/loader && sudo chown -R ${USER} /opt/docker"                       && \
ssh jug "sudo chmod -R a+w /opt/docker/mongodb/data"                                                   && \
ssh jug 'cd /opt/docker/mongodb && source /opt/loader/env/env && ./start.sh'                           && \
ssh jug 'sudo mv /opt/jira-data-loader.cron /etc/cron.d/jira-data-loader'                              && \
ssh jug 'sudo chown root:root /etc/cron.d/jira-data-loader'                                            && \
rm jug.tar.gz                                                                                          && \
echo "DONE!"
