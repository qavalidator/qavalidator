#!/bin/bash
#
# run integration tests

QAV_HOME=v:/codebase/qavalidator
QAV_ITEST_DIR=$QAV_HOME/itest
QAV_APP_DIR=$QAV_HOME/dist/qav-app

JAR=$(ls $QAV_APP_DIR/build/libs/qav-app-*.jar | grep -v "javadoc.jar" | grep -v "sources.jar")

export NEO4J_URI=bolt://localhost
export NEO4J_USERNAME=neo4j
export NEO4J_PASSWORD=secret

while [[ -n "$1" ]] ; do
    param="$1"
    OUTPUT_DIR="$QAV_ITEST_DIR/itest-results/results-$param"

    case "$param" in
        qav-runner)
            cd $QAV_HOME/qav-runner
            java -jar $JAR --outputDir="$OUTPUT_DIR" build/classes/main
            ;;

        qav-app)
            cd $QAV_APP_DIR
            java -jar $JAR --analysis="$QAV_APP_DIR/src/qa/qav-app/qav-app_analysis.groovy" --outputDir="$OUTPUT_DIR"
            ;;

        merge)
            java -jar $JAR --analysis="analysis/merge_test.groovy" --outputDir="$OUTPUT_DIR"
            ;;

        *)
            echo "Wrong parameter: $param"
            ;;
    esac
    shift
done
