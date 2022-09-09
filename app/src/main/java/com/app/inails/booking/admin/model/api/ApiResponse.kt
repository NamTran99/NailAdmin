package com.app.inails.booking.admin.model.api

class ApiResponse<T>(
    val data: T,
    val result: Boolean,
    val message: String
)