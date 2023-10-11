#!/bin/sh

nodeName=Nexxus_1.0.0_$(cat /proc/self/cgroup | grep docker | sed s/\\//\\n/g | tail -1)

export AJSC_HOME=/opt/att/ajsc
export AJSC_CONFIG_HOME=${AJSC_HOME}/config
source ${AJSC_CONFIG_HOME}/run.source
