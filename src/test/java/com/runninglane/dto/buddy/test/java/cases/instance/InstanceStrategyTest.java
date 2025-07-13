package com.runninglane.dto.buddy.test.java.cases.instance;

import com.runninglane.dto.buddy.instance.DefaultInstanceStrategy;
import com.runninglane.dto.buddy.javainterop.DtoBuddy;
import org.junit.jupiter.api.Test;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InstanceStrategyTest {
    public abstract static class TestDto {
        private boolean flag = false;

        public boolean getFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public abstract String getName();

        public abstract void setName(String name);
    }

    static class TestInstanceStrategy extends DefaultInstanceStrategy {
        @NotNull
        @Override
        public Object create(@NotNull Class<?> concrete) {
            Object instance = super.create(concrete);
            ((TestDto) instance).setFlag(true);
            return instance;
        }

        @Override
        public void populate(@NotNull Object dto, Map<String, ?> params) {
            Map<String, ?> upperParams = params.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> e.getValue() instanceof String ? ((String) e.getValue()).toUpperCase() : e.getValue()
                ));
            super.populate(dto, upperParams);
        }
    }

    @Test
    public void shouldCustomizeInstanceStrategyCorrectly() {
        DtoBuddy dtoBuddy = new DtoBuddy(new TestInstanceStrategy());
        Class<?> concreteClass = dtoBuddy.implement(TestDto.class);
        TestDto dto = dtoBuddy.create(concreteClass, java.util.Map.of("flag", true, "name", "hello"));
        assertTrue(dto.getFlag());
        assertEquals("HELLO", dto.getName());
        dtoBuddy.populate(dto, java.util.Map.of("name", "world"));
        assertEquals("WORLD", dto.getName());
    }
}