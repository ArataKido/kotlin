package com.dcop.exceptions

class InvalidCoordinatesException(val statusCode: Int, message: String) : Exception(message)