package com.mu54omd.ullama.domain.model.objectbox

import io.objectbox.annotation.Entity
import io.objectbox.annotation.HnswIndex
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index

@Entity
data class Chunk(
    @Id var chunkId: Long = 0,
    @Index var docId: Long = 0,
    var docFileName: String = "",
    var chunkData: String = "",
    @HnswIndex(dimensions = 384) var chunkEmbedding: FloatArray = floatArrayOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Chunk

        if (chunkId != other.chunkId) return false
        if (docId != other.docId) return false
        if (docFileName != other.docFileName) return false
        if (chunkData != other.chunkData) return false
        if (!chunkEmbedding.contentEquals(other.chunkEmbedding)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chunkId.hashCode()
        result = 31 * result + docId.hashCode()
        result = 31 * result + docFileName.hashCode()
        result = 31 * result + chunkData.hashCode()
        result = 31 * result + chunkEmbedding.contentHashCode()
        return result
    }
}
