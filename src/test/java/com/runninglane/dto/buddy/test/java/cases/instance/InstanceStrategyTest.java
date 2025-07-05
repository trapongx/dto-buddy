package com.runninglane.dto.buddy.test.java.cases.instance;

import com.runninglane.dto.buddy.DtoBuddy;
import com.runninglane.dto.buddy.instance.DefaultInstanceStrategy;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstanceStrategyTest {
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
            //noinspection DataFlowIssue
            Map<String, ?> upperParams = params.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> e.getValue() != null ? e.getValue().toString().toUpperCase() : null
                ));
            super.populate(dto, upperParams);
        }
    }

    @Test
    public void shouldCustomizeInstanceStrategyCorrectly() {
        DtoBuddy dtoBuddy = new DtoBuddy(new TestInstanceStrategy());
        Class<?> concreteClass = dtoBuddy.implementor(TestDto.class).implement();
        TestDto dto = dtoBuddy.create(concreteClass, java.util.Map.of("name", "hello"));
        assertTrue(dto.getFlag());
        assertEquals("HELLO", dto.getName());
        dtoBuddy.populate(dto, java.util.Map.of("name", "world"));
        assertEquals("WORLD", dto.getName());
    }
}