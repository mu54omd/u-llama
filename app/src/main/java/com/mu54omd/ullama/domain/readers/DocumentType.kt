package com.mu54omd.ullama.domain.readers

enum class DocumentType(val type: String) {
    PDF("pdf"),
    PlainText("txt"),
    MarkDown("md"),
    Docx("docx"),
    PNG("png"),
    JPG("jpg"),
    JPEG("jpeg")
}