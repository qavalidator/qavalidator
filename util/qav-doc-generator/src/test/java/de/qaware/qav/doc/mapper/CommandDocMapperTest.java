package de.qaware.qav.doc.mapper;

import com.google.common.collect.Lists;
import de.qaware.qav.doc.QavCommand;
import de.qaware.qav.doc.model.CommandDoc;
import de.qaware.qav.doc.model.ParameterDoc;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link CommandDocMapper}.
 *
 * @author QAware GmbH
 */
public class CommandDocMapperTest {

    // Mocks:
    private QavCommand qavCommand1;
    private QavCommand qavCommand2;

    @Before
    public void setup() {
        qavCommand1 = mock(QavCommand.class);
        when(qavCommand1.name()).thenReturn("cmd1");
        when(qavCommand1.description()).thenReturn("desc1");
        when(qavCommand1.parameters()).thenReturn(new QavCommand.Param[]{});
        when(qavCommand1.result()).thenReturn("result1");

        qavCommand2 = mock(QavCommand.class);
        when(qavCommand2.name()).thenReturn("cmd2");
        when(qavCommand2.description()).thenReturn("desc2");

        QavCommand.Param param1 = mock(QavCommand.Param.class);
        when(param1.name()).thenReturn("param1");
        when(param1.description()).thenReturn("param1 description");
        QavCommand.Param param2 = mock(QavCommand.Param.class);
        when(param2.name()).thenReturn("param2");
        when(param2.description()).thenReturn("param2 description");
        QavCommand.Param[] params = {
                param1, param2
        };
        when(qavCommand2.parameters()).thenReturn(params);
        when(qavCommand2.result()).thenReturn("result2");
    }

    @Test
    public void testToDto() {
        CommandDoc commandDoc = new CommandDocMapper().toDto(qavCommand1);

        CommandDoc expected = new CommandDoc();
        expected.setName("cmd1");
        expected.setDescription("desc1");
        expected.setResult("result1");

        assertThat(commandDoc, notNullValue());
        assertThat(commandDoc, is(expected));
    }

    @Test
    public void testToDtoWithParameters() {
        CommandDoc commandDoc = new CommandDocMapper().toDto(qavCommand2);

        CommandDoc expected = new CommandDoc();
        expected.setName("cmd2");
        expected.setDescription("desc2");
        expected.setResult("result2");

        ParameterDoc param1 = new ParameterDoc();
        param1.setName("param1");
        param1.setDescription("param1 description");
        ParameterDoc param2 = new ParameterDoc();
        param2.setName("param2");
        param2.setDescription("param2 description");
        expected.setParameters(Lists.newArrayList(param1, param2));

        assertThat(commandDoc, notNullValue());
        assertThat(commandDoc, is(expected));
    }
}