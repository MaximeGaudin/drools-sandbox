package com.mgaudin.sandbox.drools.rules;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class Rule {
    private static final String PACKAGE_ROOT = "com.mgaudin.sandbox.drools.models";
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String whenClause;
    private String thenClause;

    public Rule(String name) {
        this.name = name;
    }

    public Rule(String name, String whenClause, String thenClause) {
        this.name = name;

        this.whenClause = whenClause;
        this.thenClause = thenClause;
    }

    public String toDrl() {
        return
                "package com.mgaudin.sandbox.drools.rules;\n\n" +
                        "import " + PACKAGE_ROOT + ".Lead;\n\n" +
                        "rule \"" + name + "\"\n" +
                        "\twhen\n\t\t" + whenClause + "\n" +
                        "\tthen\n\t\t" + thenClause + "\n" +
                        "end";
    }
}
