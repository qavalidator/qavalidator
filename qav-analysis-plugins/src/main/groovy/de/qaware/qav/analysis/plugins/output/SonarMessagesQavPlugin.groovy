package de.qaware.qav.analysis.plugins.output

import de.qaware.qav.analysis.plugins.base.BasePlugin
import de.qaware.qav.analysis.plugins.output.impl.SonarLogUtil
import de.qaware.qav.doc.QavCommand
import de.qaware.qav.doc.QavPluginDoc
import de.qaware.qav.analysis.dsl.model.Analysis

/**
 * Handling of message for Sonar analysis.
 *
 * @author QAware GmbH
 */
@QavPluginDoc(name = "SonarMessagesQavPlugin",
        description = """
                QAvalidator may be used as part of a SonarQube analysis  (see <<SonarQube>>).
                It writes a log file for SonarQube; each architecture violation is written on one line.
                A SonarQube Plugin can read that file and report the number of violation as part of the SonarQube
                Quality Contract.
                The file is located in the directory defined as `outputDir` (via command line, or in the Maven POM,
                or in the analysis file).
                """)
class SonarMessagesQavPlugin extends BasePlugin {

    @Override
    void apply(Analysis analysis) {
        super.apply(analysis)

        analysis.register("sonarError", this.&sonarError)
        analysis.register("sonarWarn", this.&sonarWarn)

    }

    /**
     * writes the given message as Sonar Error. See {@link SonarLogUtil#error(java.lang.String)}.
     *
     * @param msg the message
     */
    @QavCommand(name = "sonarError",
            description = "writes the given message as Sonar Error.",
            parameters = @QavCommand.Param(name = "msg", description = "the message")
    )
    static void sonarError(String msg) {
        SonarLogUtil.error(msg);
    }

    /**
     * writes the given message as Sonar Warning. See {@link SonarLogUtil#warn(java.lang.String)}.
     *
     * @param msg the message
     */
    @QavCommand(name = "sonarWarn",
            description = "writes the given message as Sonar Warning.",
            parameters = @QavCommand.Param(name = "msg", description = "the message")
    )
    static void sonarWarn(String msg) {
        SonarLogUtil.warn(msg);
    }
}
