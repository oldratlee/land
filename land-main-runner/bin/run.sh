#!/bin/bash
#
# @Function
#   Run Main Class
# 
# 

cd $(dirname $(readlink -f $0))
BASE=$(pwd)

source common.sh

checkRootUser

mainClass="$1"
shift 1

echoCmdLineThenRun "$(findJavaBin)" ${JAVA_OPTS} -cp "${classPath}" "$mainClass" "$@"
