package com.app.inails.booking.admin.helper

class PairLookup<A, B>(pairs: Array<out Pair<A, B>>) {
    private val value2Key = hashMapOf<B, A>()
    private val key2Value = pairs.toMap().onEach {
        value2Key[it.value] = it.key
    }

    fun keyOf(value: B): A? {
        return value2Key[value]
    }

    fun valueOf(key: A): B? {
        return key2Value[key]
    }

    fun requireValue(key: A): B {
        return key2Value[key] ?: error("Not found value by key $key")
    }

    fun requireKey(value: B): A {
        return value2Key[value] ?: error("Not found key by value $value")
    }

}

fun <A, B> pairLookupOf(
    vararg pairs: Pair<A, B>,
): PairLookup<A, B> {
    return PairLookup(pairs)
}