package me.scana.okgradle.data.repository

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.IOException

class NetworkClient(private val okHttpClient: OkHttpClient) {

    fun <T> execute(request: Request, parse: ResponseBody.() -> T): NetworkResult<T> {
        try {
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return NetworkResult.Failure(HttpException(response.code, response.message))
            }
            return try {
                val result = response.body!!.use(parse)
                NetworkResult.Success(result)
            } catch (exception: Exception) {
                NetworkResult.Failure(ResponseParseException(exception))
            }
        } catch (exception: IOException) {
            return NetworkResult.Failure(exception)
        }
    }
}
