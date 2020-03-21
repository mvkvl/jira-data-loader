#!/usr/bin/env bash

ssh jug "/opt/docker/clean.sh"
ssh jug 'cd /opt && sudo rm -rf docker*'
ssh jug 'cd /opt && sudo rm -rf loader'
ssh jug 'sudo rm /etc/cron.d/jira-data-load'
rm jug.tar.gz
