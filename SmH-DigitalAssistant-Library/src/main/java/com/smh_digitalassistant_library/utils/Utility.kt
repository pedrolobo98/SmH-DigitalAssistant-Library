package com.smh_digitalassistant_library.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object Utility {
    //------------------------------------ HIDE KEYBOARD -------------------------------------//
    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //------------------------------------ BUTTON UTILS --------------------------------------//
    fun preventOverclick(view: View) {
        view.isEnabled = false
        view.postDelayed({ view.isEnabled = true }, 1000)
    }

    //------------------------------------ API FUNCTIONS -------------------------------------//
    private val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient()

    @Throws(IOException::class)
    fun apiget(url: String): String? {
        val request: Request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).execute().use {
                response -> return response.body?.string()
        }
    }

    @Throws(IOException::class)
    fun apipost(url: String, body: String): String? {
        val bodyJson: RequestBody = body.toRequestBody(JSON)
        val request: Request = Request.Builder()
            .url(url)
            .post(bodyJson)
            .build()
        client.newCall(request).execute().use {
                response -> return response.body?.string()
        }
    }
}