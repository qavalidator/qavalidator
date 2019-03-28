#!/bin/bash
#
# run integration tests

QAV_HOME=${QAV_HOME:-v:/codebase/qavalidator}
QAV_ITEST_DIR=$QAV_HOME/itest
QAV_APP_DIR=$QAV_HOME/dist/qav-app

JAR=$(ls $QAV_APP_DIR/build/libs/qav-app-*.jar | grep -v "javadoc.jar" | grep -v "sources.jar")
OUTPUT_DIR=$QAV_ITEST_DIR/itest-results

param="$1"
graph="$OUTPUT_DIR/results-$param/dependencyGraph.json"

if [ ! -f "$graph" ] ; then
    echo "Graph not found: $graph"
    exit 1
fi

java -jar $JAR --graph="$graph"

