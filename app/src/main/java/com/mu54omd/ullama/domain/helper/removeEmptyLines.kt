package com.mu54omd.ullama.domain.helper

fun String.removeEmptyLines():String{
    return replace(Regex("(?m)^[ \t]*\r?\n"), "")
}