package com.mgaudin.sandbox.drools.views;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MatchItem {
    @NotNull
    private String field;

    @NotNull
    private String operator;

    @NotNull
    private String value;

    public String toWhenClause() {
        return field + " " + operator + "\"" + value + "\"";
    }

}
