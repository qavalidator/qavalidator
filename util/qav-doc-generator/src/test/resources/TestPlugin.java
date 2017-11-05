import de.qaware.qav.doc.QavCommand;
import de.qaware.qav.doc.QavPluginDoc;

/**
 * @author QAware GmbH
 */
@QavPluginDoc(
        name = "TestPlugin",
        description = "This is a test plugin"
)
public class TestPlugin {

    @QavCommand(name = "testCmd1", description = "Desc1")
    public void testCmd1() {

    }
}
