package com.app.inails.booking.admin.extention

fun <T> lock(any: T?, function: T.() -> Unit) {
		if (any != null) function(any)
}

fun String?.safe(def: String = ""): String {
		return this ?: def
}

fun String?.or(def: String = ""): String {
		if (this != null) {
				if (this.isBlank()) return def
				return this
		}
		return def
}

fun Int?.safe(def: Int = 0): Int {
		return this ?: def
}

fun Double?.safe(def: Double = 0.0): Double {
		return this ?: def
}

fun Float?.safe(def: Float = 0f): Float {
		return this ?: def
}

fun Long?.safe(def: Long = 0): Long {
	return this ?: def
}

fun String?.noInfo(def: String = "No info"): String {
	return this ?: def
}

fun Boolean?.safe(def: Boolean = false): Boolean {
		return this ?: def
}

fun String.sub(start: Int, end: Int): String {
		return substring(start, if (length < end) length else end)
}

fun <T> lazyNone(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)

fun <T> tryCall(function: () -> T): Pair<T?, Throwable?> {
		return try {
				function() to null
		} catch (t: Throwable) {
				null to t
		}
}

fun <E> List<E>.findIndex(function: (E) -> Boolean): Int {
		for (i in 0 until size) {
				if (function(this[i])) return i
		}
		return -1
}

@Suppress("unchecked_cast")
inline fun <reified T> Any?.cast(): T? {
		if (this is T) return this
		return null
}

fun <E> List<E>.toMap(keyOf: (E) -> String): Map<String, E> {
		return hashMapOf<String, E>().also { hMap ->
				forEach {
						hMap[keyOf(it)] = it
				}
		}
}