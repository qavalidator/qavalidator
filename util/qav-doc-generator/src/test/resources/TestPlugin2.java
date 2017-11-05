import de.qaware.qav.doc.QavCommand;
import de.qaware.qav.doc.QavPluginDoc;

/**
 * @author QAware GmbH
 */
@QavPluginDoc(
        name = "TestPlugin2",
        description = "This is a test plugin"
)
public class TestPlugin2 {

    @QavCommand(
            name = "cmd",
            description = "Desc1",
            parameters = {
                    @QavCommand.Param(name = "p1", description = "parameter 1"),
                    @QavCommand.Param(name = "p2", description = "parameter 2")
            },
            result = "The result.")
    public String testCmd1(String p1, String p2) {
        return null;
    }

    /**
     * Note that this is the same command name as above.
     *
     * @param p1 parameter 1
     * @param p2 parameter 2
     * @param p3 parameter 3
     */
    @QavCommand(
            name = "cmd",
            description = "Desc2",
            parameters = {
                    @QavCommand.Param(name = "p1", description = "parameter 1"),
                    @QavCommand.Param(name = "p2", description = "parameter 2"),
                    @QavCommand.Param(name = "p3", description = "parameter 3")
            })
    public void testCmd2(String p1, String p2, String p3) {
    }

}
