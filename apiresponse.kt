package com.example.myapplication

data class ApiResponse(
    val status: String,
    val message: String,
    val user: Map<String, Any>? = null,
    val reset_link: String? = null,
    val patient: Map<String, Any>? = null,
    val doctor: Map<String, Any>? = null,
    val doctors: List<Map<String, Any>>? = null,
    val patients: List<Map<String, Any>>? = null,
    val count: Int? = null,
    val anaemia_history: List<Map<String, Any>>? = null,
    val alerts: List<Map<String, Any>>? = null,
    val unread_count: Int? = null,
    val prescriptions: List<Map<String, Any>>? = null,
    val history: List<Map<String, Any>>? = null,
    val image_url: String? = null
)
