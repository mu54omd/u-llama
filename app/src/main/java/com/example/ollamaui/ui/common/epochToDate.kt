package com.example.ollamaui.ui.common

import java.time.Instant

fun epochToDate(date: Long):String{
    return Instant.ofEpochMilli(date).toString().split("T")[0]
}