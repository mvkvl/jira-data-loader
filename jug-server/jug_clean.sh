#!/usr/bin/env bash

ssh jug "/opt/docker/clean.sh"
ssh jug 'cd /opt && sudo rm -rf docker*'
#ssh jug 'cd /opt && sudo rm -rf loader/{bin,config,lib,log}'
ssh jug 'cd /opt && sudo rm -rf loader'
ssh jug 'sudo rm /etc/cron.d/jira-data-loader'
rm jug.tar.gz
