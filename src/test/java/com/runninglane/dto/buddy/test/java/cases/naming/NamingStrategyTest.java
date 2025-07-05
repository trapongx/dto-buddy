package com.runninglane.dto.buddy.test.java.cases.naming;

import com.runninglane.dto.buddy.DtoBuddy;
import com.runninglane.dto.buddy.naming.NamingStrategy;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class NamingStrategyTest {
    static class TestNamingStrategy implements NamingStrategy {
        @NotNull
        @Override
        public String buildPackageName(@NotNull Class<?> baseClass) {
            return "com.example.jlp123456789.dto.customized";
        }

        @NotNull
        @Override
        public String buildClassName(@NotNull Class<?> baseClass) {
            return "CustomName";
        }
    }

    public interface AnyInterface {
    }

    @Test
    public void shouldCustomizeNameCorrectly() {
        DtoBuddy dtoBuddy = new DtoBuddy(new TestNamingStrategy());
        Class<?> concreteClass = dtoBuddy.implementor(AnyInterface.class).implement();
        assert concreteClass.getSimpleName().equals("CustomName");
        assert concreteClass.getPackage().getName().equals("com.example.jlp123456789.dto.customized");
    }
}