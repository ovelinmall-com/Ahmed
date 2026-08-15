package com.ovelin.admin

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

/**
 * عميل بسيط لتسجيل FCM token عند السيرفر بمفتاح الأدمن الثابت.
 */
object AdminApi {

    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    fun registerToken(token: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val body = JSONObject().apply {
            put("token", token)
        }.toString().toRequestBody(JSON)

        val request = Request.Builder()
            .url("${BuildConfig.API_BASE_URL}/push/admin/fcm-token")
            .addHeader("X-Admin-Key", BuildConfig.ADMIN_PUSH_KEY)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e.message ?: "خطأ في الاتصال")
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                response.use {
                    if (it.isSuccessful) {
                        onSuccess()
                    } else {
                        onError("HTTP ${it.code}")
                    }
                }
            }
        })
    }
}
