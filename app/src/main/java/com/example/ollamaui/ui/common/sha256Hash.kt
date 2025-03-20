package com.example.ollamaui.ui.common

import java.security.MessageDigest

fun sha256Hash(content: String): String? {
    return try {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(content.toByteArray())
        hashBytes.joinToString("") { String.format("%02x", it) }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}