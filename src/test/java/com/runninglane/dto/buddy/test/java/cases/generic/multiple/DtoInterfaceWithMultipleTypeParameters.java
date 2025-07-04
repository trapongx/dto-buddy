package com.runninglane.dto.buddy.test.java.cases.generic.multiple;

import java.util.List;
import java.util.Map;

public interface DtoInterfaceWithMultipleTypeParameters<S, L, K, V> {
    S getSimple();
    List<L> getList();
    Map<K, V> getMap();
}