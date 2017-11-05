package de.qaware.qav.architecture.checker;

import com.google.common.collect.Lists;
import de.qaware.qav.architecture.dsl.model.ClassSet;
import de.qaware.qav.architecture.dsl.model.Component;
import de.qaware.qav.graph.api.Constants;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests for {@link AllExplicitRulesUsedChecker}.
 *
 * @author QAware GmbH
 */
public class AllExplicitRulesUsedCheckerTest extends AbstractArchitectureCheckerTest {

    @Test
    public void testOk() {
        init(false, false);

        new DependencyChecker(architectureGraph, packageArchitecture);

        // trivial, as there are no "uses" definitions
        AllExplicitRulesUsedChecker checker = new AllExplicitRulesUsedChecker(architectureGraph, packageArchitecture);
        assertThat(checker.isOk(), is(true));
    }

    @Test
    public void testWithImplementedUsesRelation() {
        init(false, false);

        Component component = packageArchitecture.getParentComponent("com.my.a.b");
        assertThat(component, notNullValue());
        component.getUses().put("newRule", new ClassSet("newRule", Lists.newArrayList("com.my.a.e")));
        new DependencyChecker(architectureGraph, packageArchitecture);

        AllExplicitRulesUsedChecker checker = new AllExplicitRulesUsedChecker(architectureGraph, packageArchitecture);
        assertThat(checker.isOk(), is(true));
        assertThat(checker.getViolationMessage(), nullValue());
    }

    @Test
    public void testNotOk() {
        init(false, false);

        @SuppressWarnings("unchecked")
        List<String> uses = (List<String>) architectureGraph.getNode("com.my.a").getProperty(Constants.USES);
        uses.add("com.my.other");
        new DependencyChecker(architectureGraph, packageArchitecture);

        AllExplicitRulesUsedChecker checker = new AllExplicitRulesUsedChecker(architectureGraph, packageArchitecture);
        assertThat(checker.isOk(), is(false));
        assertThat(checker.getViolationMessage(), is("1 unused rules: [com.my.a: com.my.other]"));
    }
}
