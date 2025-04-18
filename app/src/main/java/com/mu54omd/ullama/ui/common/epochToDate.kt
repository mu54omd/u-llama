package com.mu54omd.ullama.ui.common

import java.time.Instant

fun epochToDate(date: Long):String{
    return Instant.ofEpochMilli(date).toString().split("T")[0]
}