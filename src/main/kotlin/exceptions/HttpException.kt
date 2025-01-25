package com.dcop.exceptions

class HttpException(val statusCode: Int, message: String) : Exception(message)