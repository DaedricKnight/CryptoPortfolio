package com.research.whitedzenin.cryptoportfolio.Network

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader

class NetworkClient {
    fun get(url: String): InputStream {
        val request = Request.Builder().url(url).build()
        val response = OkHttpClient().newCall(request).execute()
        val body = response.body()
        // body.toString() returns a string representing the object and not the body itself, probably
        // kotlins fault when using third party libraries. Use byteStream() and convert it to a String
        return body!!.byteStream()
    }

    fun readStream(inputStream: BufferedInputStream): String {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        bufferedReader.forEachLine { stringBuilder.append(it) }
        return stringBuilder.toString()
    }
}