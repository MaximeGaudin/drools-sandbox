package com.mgaudin.sandbox.drools;

import com.mgaudin.sandbox.drools.models.Lead;
import com.mgaudin.sandbox.drools.rules.Rule;
import com.mgaudin.sandbox.drools.rules.RuleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
public class RuleEngineManagerTest {
    @Inject
    private RuleRepository ruleRepository;

    @Test
    public void testRule() {
        RuleEngineManager ruleEngineManager = new RuleEngineManager(ruleRepository);

        ruleEngineManager.addRule(
                new Rule("leadMatch", "Lead(name == 'PROJET')", "System.out.println(\"LEAD MATCH\");")
        );

        assertThat(ruleEngineManager.process(new Lead("PROJET"))).isEqualTo(1);
    }
}