package com.example.anew

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException



val JSON: MediaType = "application/json; charset=utf-8".toMediaType()

var client = OkHttpClient()

@Throws(IOException::class)
fun post(url: String, json: String): String? {
    val body: RequestBody = RequestBody.create(JSON , json)
    val request: Request = Request.Builder().url(url).post(body).build()
    client.newCall(request).execute().use { response -> return response.body!!.string() }
}