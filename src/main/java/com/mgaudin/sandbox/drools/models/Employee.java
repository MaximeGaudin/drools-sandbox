package com.mgaudin.sandbox.drools.models;

public class Employee {
    String name;
    boolean manager;
    String message;
    boolean filter;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isManager() {
        return manager;
    }
    public void setManager(boolean manager) {
        this.manager = manager;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isFilter() {
        return filter;
    }
    public void setFilter(boolean filter) {
        this.filter = filter;
    }
    @Override
    public String toString() {
        return "Employee [name=" + name + ", manager=" + manager + ", message="
                + message + ", ]";
    }
}
