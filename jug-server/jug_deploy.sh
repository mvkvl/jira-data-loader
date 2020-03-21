#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
USER=mkantur

cd ${DIR}/..                                                                                           && \
mvn clean package                                                                                      && \
cp target/jira-data-loader.jar ${DIR}/loader/lib                                                       && \
cd ${DIR}                                                                                              && \
tar cvfz jug.tar.gz docker loader                                                                      && \
scp jug.tar.gz jug:~                                                                                   && \
ssh jug 'sudo mv jug.tar.gz /opt && cd /opt && sudo tar xfz jug.tar.gz && sudo rm jug.tar.gz'          && \
ssh jug 'cd /opt/docker/mongodb && ./start.sh'                                                         && \
ssh jug 'sudo echo "0/5 * * * * ${USER} /opt/loader/bin/load recent > /opt/loader/log/last-run.log" > /etc/cron.d/jira-data-loader'     && \
ssh jug "sudo chown -R ${USER} /opt/loader"                                                            && \
rm jug.tar.gz                                                                                          && \
echo "DONE!"
