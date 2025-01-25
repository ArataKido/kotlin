package com.dcop.exceptions


class OutOfRangeException(val statusCode: Int, message: String) : Exception(message)
