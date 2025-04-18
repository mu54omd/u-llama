package com.mu54omd.ullama.ui.common

fun String.splitAndKeepDot(): List<String>{
    val result = ArrayList<String>()
    var start = 0
    var end = this.indexOf('.')

    while (end != -1) {
        if (end > start) {
            result.add(this.substring(start, end) + ".")
        }
        start = end + 1
        end = this.indexOf('.', start)
    }
    if (start < this.length) {
        result.add(this.substring(start) + ".")
    }
    return result
}