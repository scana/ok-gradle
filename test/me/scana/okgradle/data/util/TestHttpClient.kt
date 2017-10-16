package me.scana.okgradle.data.util;

import org.apache.http.HttpHost
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.ProtocolVersion
import org.apache.http.client.HttpClient
import org.apache.http.client.ResponseHandler
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.conn.ClientConnectionManager
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicHttpResponse
import org.apache.http.message.BasicStatusLine
import org.apache.http.params.HttpParams
import org.apache.http.protocol.HttpContext
import java.io.IOException

class TestHttpClient : HttpClient {

    var responseToReturn = BasicHttpResponse(BasicStatusLine(ProtocolVersion("", 0, 0), 200, ""))
    var exceptionToThrow: Exception? = null
    var recentRequest: HttpUriRequest? = null

    fun throwExceptionOnRequest(e: Exception) {
        exceptionToThrow = e
    }

    fun returnBadRequestStatus() {
        exceptionToThrow = null
        responseToReturn = BasicHttpResponse(BasicStatusLine(ProtocolVersion("", 0, 0), 400, ""))
    }

    fun returnsJson(json: String) {
        exceptionToThrow = null
        val response = BasicHttpResponse(BasicStatusLine(ProtocolVersion("", 0, 0), 200, ""))
        response.entity = StringEntity(json, "UTF-8")
        responseToReturn = response
    }

    override fun execute(p0: HttpUriRequest?): HttpResponse {
        recentRequest = p0
        exceptionToThrow?.let {
            throw IOException()
        }
        return responseToReturn
    }

    override fun getParams(): HttpParams {
        TODO("not implemented")
    }

    override fun getConnectionManager(): ClientConnectionManager {
        TODO("not implemented")
    }

    override fun execute(p0: HttpUriRequest?, p1: HttpContext?): HttpResponse {
        TODO("not implemented")
    }

    override fun execute(p0: HttpHost?, p1: HttpRequest?): HttpResponse {
        TODO("not implemented")
    }

    override fun execute(p0: HttpHost?, p1: HttpRequest?, p2: HttpContext?): HttpResponse {
        TODO("not implemented")
    }

    override fun <T : Any?> execute(p0: HttpUriRequest?, p1: ResponseHandler<out T>?): T {
        TODO("not implemented")
    }

    override fun <T : Any?> execute(p0: HttpUriRequest?, p1: ResponseHandler<out T>?, p2: HttpContext?): T {
        TODO("not implemented")
    }

    override fun <T : Any?> execute(p0: HttpHost?, p1: HttpRequest?, p2: ResponseHandler<out T>?): T {
        TODO("not implemented")
    }

    override fun <T : Any?> execute(p0: HttpHost?, p1: HttpRequest?, p2: ResponseHandler<out T>?, p3: HttpContext?): T {
        TODO("not implemented")
    }
}