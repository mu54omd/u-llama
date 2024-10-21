package com.example.ollamaui.domain.model.objectbox

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Document(
    @Id var docId: Long = 0,
    var docText: String = "",
    var docFileName: String = "",
    var docAddedTime: Long = 0,
)
