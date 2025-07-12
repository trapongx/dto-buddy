package com.runninglane.dto.buddy.test.java.cases.notnull;

public class Dummy {
    private final String name;

    public Dummy(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dummy dummy = (Dummy) o;
        return name.equals(dummy.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Dummy{" +
            "name='" + name + '\'' +
            '}';
    }
}
