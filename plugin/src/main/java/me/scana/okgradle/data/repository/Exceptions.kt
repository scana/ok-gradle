package me.scana.okgradle.data.repository

class ArtifactSearchException(
        name: String,
        exception: Throwable
) : Exception("$name encountered an exception: ${exception.message}")

class HttpException(
        code: Int,
        message: String
) : RuntimeException("HTTP $code:$message")

class ResponseParseException(
        throwable: Throwable
) : Exception(throwable)