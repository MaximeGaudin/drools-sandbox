package com.mgaudin.sandbox.drools.views;

import com.mgaudin.sandbox.drools.RuleEngineManager;
import com.mgaudin.sandbox.drools.models.Lead;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.inject.Inject;

@Controller
public class TestController {
    private final RuleEngineManager ruleEngineManager;

    @Inject
    public TestController(RuleEngineManager ruleEngineManager) {
        this.ruleEngineManager = ruleEngineManager;
    }

    @GetMapping("/test/newLead")
    public String simulateNewLead() {
        ruleEngineManager.process(new Lead("PROJET"));
        return "redirect:/rules";
    }
}
