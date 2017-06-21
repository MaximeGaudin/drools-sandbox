package com.mgaudin.sandbox.drools;

import com.mgaudin.sandbox.drools.models.Employee;
import com.mgaudin.sandbox.drools.models.Lead;
import com.mgaudin.sandbox.drools.rules.Rule;
import com.mgaudin.sandbox.drools.rules.RuleRepository;
import lombok.extern.slf4j.Slf4j;
import org.drools.core.WorkingMemory;
import org.drools.core.event.DebugAgendaEventListener;
import org.drools.core.event.DebugProcessEventListener;
import org.drools.core.event.DebugRuleRuntimeEventListener;
import org.drools.core.io.impl.ClassPathResource;
import org.drools.verifier.Verifier;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class RuleEngineManager {
    private final KieServices kieServices;
    private final RuleRepository ruleRepository;
    private KieContainer kieContainer;
    private ReleaseId releaseId;
    private int minorVersion = 0;

    @Inject
    public RuleEngineManager(RuleRepository ruleRepository) {
        testRule();
        this.ruleRepository = ruleRepository;

        this.kieServices = KieServices.Factory.get();

//        addRule(new Rule("default", "l: Object()", "System.out.println(l.getClass().getCanonicalName());"));
        addRule(new Rule("defaultLead", "Lead()", "System.out.println(\"LEAD MATCH\");"));
    }

    private void updateRuleSet(KieServices kieServices, Iterable<Rule> rules) {
        ReleaseId releaseId = kieServices.newReleaseId("com.mgaudin.sandbox", "drools", "1.0." + minorVersion++ + "-SNAPSHOT");

        KieFileSystem kieFs = kieServices.newKieFileSystem();
        kieFs.generateAndWritePomXML(releaseId);

        int ruleIndex = 0;
        for (Rule rule : rules) {
            kieFs.write("src/main/resources/rule" + ruleIndex++ + ".drl", rule.toDrl());
        }

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFs);
        kieBuilder.buildAll();
        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kieBuilder.getResults().toString());
        }

        this.releaseId = releaseId;
        this.kieContainer = kieServices.newKieContainer(releaseId);
    }

    public <T> void process2(T obj) {
        KieSession kieSession = kieContainer.getKieBase().newKieSession();

        kieSession.addEventListener(new DebugAgendaEventListener());
        kieSession.addEventListener(new DebugRuleRuntimeEventListener());
        kieSession.addEventListener(new DebugProcessEventListener());

        kieSession.insert(obj);
        kieSession.fireAllRules();
        kieSession.dispose();
    }

    public <T> void processList(List<T> objs) {
        KieSession kieSession = kieContainer.getKieBase().newKieSession();

        for (T obj : objs) {
            kieSession.insert(obj);
        }

        kieSession.fireAllRules();
        kieSession.dispose();
    }

    public void testRule() {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(
                ResourceFactory.newClassPathResource("Lead.drl", RuleEngineManager.class),
                ResourceType.DRL
        );


        if (kbuilder.hasErrors()) {
            System.out.println(kbuilder.getErrors().toString());
            throw new RuntimeException("Unable to compile \"HelloWorld.drl\".");
        }


        final Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();
        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(pkgs);

        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.addEventListener(new DebugAgendaEventListener());
        ksession.addEventListener(new DebugRuleRuntimeEventListener());
        ksession.addEventListener(new DebugProcessEventListener());


        ksession.insert(new Lead());
        ksession.fireAllRules();
        ksession.dispose();
    }

    public void addRule(Rule newRule) {
        log.info("Adding rule : \n" + newRule.toDrl());

        ruleRepository.save(newRule);
        updateRuleSet(this.kieServices, ruleRepository.findAll());
    }
}

