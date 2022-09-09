package com.app.inails.booking.admin.datasource.remote

import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.helper.network.Async


class MockAsync<T>(private val value: T?) : Async<T> {
		override suspend fun awaitNullable(): T? {
				return value
		}
		
		override suspend fun clone(): Async<T> {
				return this
		}
}

class MockApiAsync<T>(private val value: T?) : ApiAsync<T> {
		override suspend fun awaitNullable(): T? {
				return value
		}
		
		override suspend fun clone(): Async<T> {
				return this
		}
}