package com.example.ollamaui.data.local.objectbox

import com.example.ollamaui.domain.model.objectbox.Chunk
import com.example.ollamaui.domain.model.objectbox.Chunk_
import io.objectbox.kotlin.and
import javax.inject.Inject

class ChunkDatabase @Inject constructor() {
    private val chunksBox = ObjectBoxStore.store.boxFor(Chunk::class.java)

    fun addChunk(chunk: Chunk){
        chunksBox.put(chunk)
    }

    fun removeChunk(docId: Long, onProgress: (deleted: Int,total: Int) -> Unit){
        val chunksToDelete = chunksBox.query().equal(Chunk_.docId, docId).build().find()
        val total = chunksToDelete.size
        var deleted = 0
        chunksToDelete.chunked(500).forEach { batch ->
            chunksBox.remove(batch)
            deleted += batch.size
            onProgress(deleted, total)
        }
    }

    fun getSimilarChunks(docIds: List<Long>, queryEmbedding: FloatArray, n: Int = 5): List<Pair<Float, Chunk>> {
        /*
        Use maxResultCount to set the maximum number of objects to return by the ANN condition.
        Hint: it can also be used as the "ef" HNSW parameter to increase the search quality in combination
        with a query limit. For example, use maxResultCount of 100 with a Query limit of 10 to have 10 results
        that are of potentially better quality than just passing in 10 for maxResultCount
        (quality/performance tradeoff).
         */
        val result = mutableListOf<Pair<Float, Chunk>>()
        docIds.forEach { docId ->
            result += chunksBox.query(Chunk_.docId.equal(docId) and  Chunk_.chunkEmbedding.nearestNeighbors(queryEmbedding, 100))
                .build()
                .findWithScores(0,n.toLong())
                .map { Pair(it.score.toFloat(), it.get()) }
        }
        return result
    }

}