package de.qaware.qav.doc.processor;

import com.google.common.collect.Lists;
import de.qaware.qav.doc.model.CommandDoc;
import de.qaware.qav.doc.model.ParameterDoc;
import de.qaware.qav.doc.model.PluginDoc;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link DocGenerator}.
 *
 * @author QAware GmbH
 */
public class DocGeneratorTest {

    // Object under test:
    private PluginDocTree pluginDocTree;

    // Mocks
    private PluginDoc pluginDoc2;
    private DocFileWriter writerMock;
    private DocGenerator docGenerator;

    @Before
    public void setup() {
        pluginDocTree = new PluginDocTree();

        PluginDoc pluginDoc1 = new PluginDoc();
        pluginDoc1.setName("plugin1");
        pluginDoc1.setDescription("plugin1 description");

        pluginDoc2 = new PluginDoc();
        pluginDoc2.setName("plugin2");
        pluginDoc2.setDescription("plugin2 description");

        CommandDoc qavCommand1 = new CommandDoc();
        qavCommand1.setName("cmd1");
        qavCommand1.setDescription("cmd1 description");
        qavCommand1.setResult("");

        ParameterDoc param = new ParameterDoc();
        param.setName("p1");
        param.setDescription("p1 description");
        qavCommand1.setParameters(Lists.newArrayList(param));

        CommandDoc qavCommand2 = new CommandDoc();
        qavCommand2.setName("cmd2");
        qavCommand2.setDescription("cmd2 description");
        qavCommand2.setParameters(new ArrayList<>());
        qavCommand2.setResult("");

        CommandDoc qavCommand3 =  new CommandDoc();
        qavCommand3.setName("cmd3");
        qavCommand3.setDescription("cmd3 description");
        qavCommand3.setParameters(new ArrayList<>());
        qavCommand3.setResult("This is Result 3");

        // this is a duplicate to command 3. Happens e.g. with default parameters.
        CommandDoc qavCommand4 =  new CommandDoc();
        qavCommand4.setName("cmd3");
        qavCommand4.setDescription("cmd3 description");
        qavCommand4.setParameters(new ArrayList<>());
        qavCommand4.setResult("This is Result 3");

        pluginDocTree.addPlugin("plugin1", pluginDoc1);
        pluginDocTree.addCommand("plugin1", qavCommand1);
        pluginDocTree.addCommand("plugin1", qavCommand2);
        pluginDocTree.addCommand("plugin1", qavCommand3);
        pluginDocTree.addCommand("plugin1", qavCommand4);

        writerMock = mock(DocFileWriter.class);
        docGenerator = new DocGenerator(writerMock);
    }

    @Test
    public void testGenerator() {
        docGenerator.generateDoc(pluginDocTree);

        ArgumentCaptor<String> pluginNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> docCaptor = ArgumentCaptor.forClass(String.class);
        verify(writerMock).writeDocFile(pluginNameCaptor.capture(), docCaptor.capture());

        assertThat(pluginNameCaptor.getValue(), is("plugin1"));
        assertThat(docCaptor.getValue().length(), greaterThan(180));
        assertThat(docCaptor.getValue().length(), lessThan(300)); // cheap test that command 3 is not duplicated.
        assertThat(docCaptor.getValue().contains("\n==== cmd1"), is(true));
        assertThat(docCaptor.getValue().contains("\n[cols=\"1a,5a\"]"), is(true));
        assertThat(docCaptor.getValue().contains("This is Result 3"), is(true));
    }

    @Test
    public void testGeneratorWithTwoPlugins() {
        pluginDocTree.addPlugin("plugin2", pluginDoc2);
        docGenerator.generateDoc(pluginDocTree);

        verify(writerMock, times(2)).writeDocFile(any(String.class), any(String.class));
    }

    @Test
    public void testTrimEmptyPrefix() {
        assertThat(DocGenerator.trimDesc("asdf"), is("asdf"));
        assertThat(DocGenerator.trimDesc("   asdf"), is("asdf"));

        assertThat(DocGenerator.trimDesc("asdf\njklö"), is("asdf\njklö"));
        assertThat(DocGenerator.trimDesc("asdf\n\njklö"), is("asdf\n\njklö"));
        assertThat(DocGenerator.trimDesc("asdf\n  \njklö"), is("asdf\n\njklö"));
        assertThat(DocGenerator.trimDesc("asdf\n \t \njklö"), is("asdf\n\njklö"));
        assertThat(DocGenerator.trimDesc("asdf\n \t \n  jklö"), is("asdf\n\njklö"));
        assertThat(DocGenerator.trimDesc("asdf\n \t \n  jklö "), is("asdf\n\njklö "));
    }

    @Test
    public void testTrimDescRemovesJavaDocLinks() {
        assertThat(DocGenerator.trimDesc("asdf\n \t \n  {@link Jklo} "), is("asdf\n\n`Jklo` "));
        assertThat(DocGenerator.trimDesc("asdf\n \t \n  {@link Jklo } "), is("asdf\n\n`Jklo` "));
        assertThat(DocGenerator.trimDesc("asdf\n \t \n  {@link  Jklo } "), is("asdf\n\n`Jklo` "));
        assertThat(DocGenerator.trimDesc("asdf\n \t \n  {@link  org.Jklo } "), is("asdf\n\n`org.Jklo` "));
    }
}