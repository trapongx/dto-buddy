package com.runninglane.dto.buddy.test.cases.generic.multiple

interface DtoInterfaceWithMultipleTypeParameters<S, L, K, V> {
    val simple: S
    val list: List<L>
    val map: Map<K, V>?
}