package de.qaware.qav.doc.model;

import de.qaware.qav.test.beans.BeanTestUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Increase test coverage - this helps to focus on really untested code.
 *
 * @author QAware GmbH
 */
public class BeanTest {

    @Test
    public void testBean() {
        CommandDoc cd1 = getCommandDoc("Doc", "Result");
        CommandDoc cd2 = getCommandDoc("Doc", "Result");
        CommandDoc cd3 = getCommandDoc("Other Doc", "Result");

        BeanTestUtil.checkEqualsMethod(cd1, cd2, cd3);
    }

    @Test
    public void testBean2() {
        // do this another time - but make the difference in an attribute which is part of the child class
        // in the other test it's an attribute of the parent class.
        CommandDoc cd1 = getCommandDoc("Doc", "Result");
        CommandDoc cd2 = getCommandDoc("Doc", "Result");
        CommandDoc cd3 = getCommandDoc("Doc", "Other Result");

        BeanTestUtil.checkEqualsMethod(cd1, cd2, cd3);
    }

    private CommandDoc getCommandDoc(String name, String result) {
        CommandDoc commandDoc = new CommandDoc();
        commandDoc.setName(name);
        commandDoc.setResult(result);

        ParameterDoc parameterDoc = new ParameterDoc();
        parameterDoc.setName("Param " + name);
        commandDoc.setParameters(Collections.singletonList(parameterDoc));

        return commandDoc;
    }
}