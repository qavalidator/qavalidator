package de.qaware.qav.architecture.checker;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test for {@link AllComponentsImplementedChecker}.
 *
 * @author QAware GmbH
 */
public class AllComponentsImplementedCheckerTest extends AbstractArchitectureCheckerTest {

    @Test
    public void testIsOk() {
        init(false, false);

        AllComponentsImplementedChecker check = new AllComponentsImplementedChecker(architectureGraph, packageArchitecture);
        assertThat(check.isOk(), is(true));
        assertThat(check.getViolationMessages(), hasSize(0));
        assertThat(check.getViolationMessage(), nullValue());
    }

    @Test
    public void testWithUnImplementedComponents() {
        init(false, true);

        AllComponentsImplementedChecker check = new AllComponentsImplementedChecker(architectureGraph, packageArchitecture);
        assertThat(check.isOk(), is(false));
        assertThat(check.getViolationMessages(), hasSize(1));
        assertThat(check.getViolationMessages().get(0), is("Another"));
        assertThat(check.getViolationMessage(), is("1 components without corresponding classes: [Another]"));
    }
}
