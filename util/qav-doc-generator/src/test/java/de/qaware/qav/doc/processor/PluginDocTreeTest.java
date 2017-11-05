package de.qaware.qav.doc.processor;

import de.qaware.qav.doc.model.CommandDoc;
import de.qaware.qav.doc.model.PluginDoc;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link PluginDocTree}.
 *
 * @author QAware GmbH
 */
public class PluginDocTreeTest {

    private PluginDocTree pluginDocTree;

    // Mocks:
    private PluginDoc pluginDoc1;
    private PluginDoc pluginDoc2;

    private CommandDoc qavCommand1;

    @Before
    public void setup() {
        pluginDocTree = new PluginDocTree();

        pluginDoc1 = new PluginDoc();
        pluginDoc1.setName("plugin1");
        pluginDoc1.setDescription("plugin1 description");

        pluginDoc2 = new PluginDoc();
        pluginDoc2.setName("plugin2");
        pluginDoc2.setDescription("plugin2 description");

        qavCommand1 = new CommandDoc();
        qavCommand1.setName("cmd1");
    }

    @Test
    public void testAddPlugin() {
        assertThat(pluginDocTree.getPlugin("MyPlugin"), nullValue());
        pluginDocTree.addPlugin("MyPlugin", pluginDoc1);
        assertThat(pluginDocTree.getPlugin("MyPlugin"), is(pluginDoc1));
    }

    @Test
    public void testGetPluginNames() {
        pluginDocTree.addPlugin("pluginDoc1", pluginDoc1);
        pluginDocTree.addPlugin("pluginDoc2", pluginDoc2);

        assertThat(pluginDocTree.getPluginNames(), containsInAnyOrder("pluginDoc1", "pluginDoc2"));
    }

    @Test
    public void testAddPluginsMoreThanOnce() {
        pluginDocTree.addPlugin("pluginDoc1", pluginDoc1);
        pluginDocTree.addPlugin("pluginDoc1", pluginDoc2);

        assertThat(pluginDocTree.getPluginNames(), containsInAnyOrder("pluginDoc1"));

        assertThat(pluginDocTree.getPlugin("pluginDoc1"), is(pluginDoc2));
    }

    @Test
    public void testAddCommand() {
        pluginDocTree.addPlugin("pluginDoc1", pluginDoc1);
        pluginDocTree.addCommand("pluginDoc1", qavCommand1);

        List<CommandDoc> commands = pluginDocTree.getCommands("pluginDoc1");
        assertThat(commands.size(), is(1));
        assertThat(commands.get(0), is(qavCommand1));
    }

    @Test
    public void testAddCommand2() {
        pluginDocTree.addPlugin("pluginDoc1", pluginDoc1);
        pluginDocTree.addCommand("not-existing-plugin", qavCommand1);

        List<CommandDoc> commands = pluginDocTree.getCommands("pluginDoc1");
        assertThat(commands, notNullValue());
        assertThat(commands, hasSize(0));

        commands = pluginDocTree.getCommands("not-existing-plugin");
        assertThat(commands.size(), is(1));
        assertThat(commands.get(0), is(qavCommand1));
    }
}