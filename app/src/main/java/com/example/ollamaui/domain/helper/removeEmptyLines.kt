package com.example.ollamaui.domain.helper

fun String.removeEmptyLines():String{
    return replace(Regex("(?m)^[ \t]*\r?\n"), "")
}