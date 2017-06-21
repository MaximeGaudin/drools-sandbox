package com.mgaudin.sandbox.drools.views;

import com.mgaudin.sandbox.drools.RuleEngineManager;
import com.mgaudin.sandbox.drools.models.Lead;
import com.mgaudin.sandbox.drools.rules.Rule;
import com.mgaudin.sandbox.drools.rules.RuleRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import javax.validation.Valid;

@Controller
public class RuleController {
    private final RuleRepository ruleRepository;
    private final RuleEngineManager ruleEngineManager;

    @Inject
    public RuleController(RuleRepository ruleRepository, RuleEngineManager ruleEngineManager) {
        this.ruleRepository = ruleRepository;
        this.ruleEngineManager = ruleEngineManager;
    }

    @RequestMapping("/rules")
    public ModelAndView listRules() {
        return new ModelAndView("listRules", new ModelMap("rules", ruleRepository.findAll()));
    }

    @GetMapping("/rules/add")
    public ModelAndView addRulesForm() {
        ModelMap model = new ModelMap();
//        model.addAttribute("availableFields", Lead.getAvailabledFields());
        return new ModelAndView("addRule", model);
    }

    @PostMapping("/rules/add")
    public String submitNewRuleForm(@Valid RuleForm ruleForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "addRule";
        }

        Rule newRule = ruleForm.toRule();
        ruleEngineManager.addRule(newRule);

        return "redirect:/rules";
    }
}
