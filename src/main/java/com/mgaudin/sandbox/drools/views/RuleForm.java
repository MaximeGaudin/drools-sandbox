package com.mgaudin.sandbox.drools.views;

import com.google.common.base.Joiner;
import com.mgaudin.sandbox.drools.rules.Rule;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Arrays;

import static java.util.stream.Collectors.toList;

@Data
public class RuleForm {
    @NotNull
    private String name;

    @Valid
    private MatchItem[] matchItems;

    public Rule toRule() {
        Rule rule = new Rule(name);

        rule.setWhenClause(toWhenClause(matchItems));
        rule.setThenClause("System.out.println(\"" + name + "\");");

        return rule;
    }

    public String toWhenClause(MatchItem[] matchItems) {
        return "Lead("
                + Joiner.on(",")
                .join(
                        Arrays.stream(matchItems)
                                .map(MatchItem::toWhenClause)
                                .collect(toList())
                )
                + ");";

    }
}
