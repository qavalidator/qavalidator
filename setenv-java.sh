#!/bin/bash
#
# Set env variables to choose the correct JDK, and to make the scripts in itest work.
# Works on MacOS.
#
# Source this into the current shell.

JAVA_VERSION=1.8

export JAVA_HOME=$(/usr/libexec/java_home -v $JAVA_VERSION)
echo "Using Java:"
java -version

export QAV_HOME=$(dirname "$(pwd)")
echo "QAV_HOME is: $QAV_HOME"
