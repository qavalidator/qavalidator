package de.qaware.qav.doc.mapper;

import de.qaware.qav.doc.QavPluginDoc;
import de.qaware.qav.doc.model.PluginDoc;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author QAware GmbH
 */
public class PluginDocMapperTest {

    // Mocks:
    private QavPluginDoc qavPluginDoc;

    @Before
    public void setup() {
        qavPluginDoc = mock(QavPluginDoc.class);
        when(qavPluginDoc.name()).thenReturn("plugin1");
        when(qavPluginDoc.description()).thenReturn("plugin1.desc");
    }

    @Test
    public void toDto() throws Exception {
        PluginDoc result = new PluginDocMapper().toDto(qavPluginDoc);

        PluginDoc expected = new PluginDoc();
        expected.setName("plugin1");
        expected.setDescription("plugin1.desc");

        assertThat(result, notNullValue());
        assertThat(result, is(expected));
    }

}