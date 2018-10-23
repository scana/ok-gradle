package me.scana.okgradle.data

import okhttp3.*

class TestInterceptor : Interceptor {

    var recentRequest: Request? = null
    private var response: Response = buildResponse(ResponseBody.create(MediaType.get("text/plain"), ""))

    override fun intercept(chain: Interceptor.Chain): Response {
        recentRequest = chain.request()
        return response
    }

    fun returnsJson(json: String) {
        val body = ResponseBody.create(
                MediaType.get("application/json"),
                json
        )
        response = buildResponse(body)
    }

    private fun buildResponse(body: ResponseBody): Response {
        return Response.Builder()
                .code(200)
                .body(body)
                .protocol(Protocol.HTTP_2)
                .message("")
                .request(Request.Builder().url("http://url.com").build())
                .build()
    }
}

class MockOkHttpClient {

    private val testInterceptor = TestInterceptor()
    private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(testInterceptor)
            .build()

    fun returnsJson(json: String) {
        testInterceptor.returnsJson(json)
    }

    fun instance() = okHttpClient

    fun recentRequest(): Request? = testInterceptor.recentRequest

}