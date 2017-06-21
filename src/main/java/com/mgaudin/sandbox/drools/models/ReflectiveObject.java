package com.mgaudin.sandbox.drools.models;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ReflectiveObject implements Serializable {
    public static List<String> getAvailabledFields() {
        Class currentClass = new Object() {
        }.getClass().getEnclosingClass();

        return Arrays.stream(currentClass.getDeclaredFields())
                .map(f -> f.getName())
                .collect(toList());
    }
}
