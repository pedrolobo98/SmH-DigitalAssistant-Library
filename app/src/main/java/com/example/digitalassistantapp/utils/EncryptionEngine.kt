package com.example.digitalassistantapp.utils

import java.security.MessageDigest

object EncryptionEngine {

    fun encryptsha1(input: String): String {
        val HEX_CHARS = "0123456789abcdef"
        val bytes = MessageDigest
            .getInstance("SHA-1")
            .digest(input.toByteArray())
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }

        return result.toString()
    }
}