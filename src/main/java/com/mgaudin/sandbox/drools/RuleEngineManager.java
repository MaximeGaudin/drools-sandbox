package com.mgaudin.sandbox.drools;

import com.mgaudin.sandbox.drools.rules.Rule;
import com.mgaudin.sandbox.drools.rules.RuleRepository;
import lombok.extern.slf4j.Slf4j;
import org.drools.core.event.DebugAgendaEventListener;
import org.drools.core.event.DebugProcessEventListener;
import org.drools.core.event.DebugRuleRuntimeEventListener;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
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
        this.ruleRepository = ruleRepository;
        this.kieServices = KieServices.Factory.get();
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

    public <T> int process(T obj) {
        KieSession kieSession = createNewSession();

        kieSession.insert(obj);
        int processedRules = kieSession.fireAllRules();
        kieSession.dispose();

        return processedRules;
    }

    public <T> void processList(List<T> objs) {
        KieSession kieSession = createNewSession();

        for (T obj : objs) {
            kieSession.insert(obj);
        }

        kieSession.fireAllRules();
        kieSession.dispose();
    }

    private KieSession createNewSession() {
        KieSession kieSession = kieContainer.getKieBase().newKieSession();

        kieSession.addEventListener(new DebugAgendaEventListener());
        kieSession.addEventListener(new DebugRuleRuntimeEventListener());
        kieSession.addEventListener(new DebugProcessEventListener());

        return kieSession;
    }

    public void addRule(Rule newRule) {
        log.info("Adding rule : \n" + newRule.toDrl());

        ruleRepository.save(newRule);
        updateRuleSet(this.kieServices, ruleRepository.findAll());
    }
}

