package com.example.ollamaui.data.local.objectbox

import android.util.Log
import com.example.ollamaui.domain.model.objectbox.Chunk
import com.example.ollamaui.domain.model.objectbox.Chunk_
import javax.inject.Inject

class ChunkDatabase @Inject constructor() {
    private val chunksBox = ObjectBoxStore.store.boxFor(Chunk::class.java)

    fun addChunk(chunk: Chunk){
        chunksBox.put(chunk)
    }

    fun removeChunk(docId: Long){
        chunksBox.removeByIds(chunksBox.query(Chunk_.docId.equal(docId)).build().findIds().toList())
    }

    fun getSimilarChunks(queryEmbedding: FloatArray, n: Int = 5): List<Pair<Float, Chunk>> {
        /*
        Use maxResultCount to set the maximum number of objects to return by the ANN condition.
        Hint: it can also be used as the "ef" HNSW parameter to increase the search quality in combination
        with a query limit. For example, use maxResultCount of 100 with a Query limit of 10 to have 10 results
        that are of potentially better quality than just passing in 10 for maxResultCount
        (quality/performance tradeoff).
         */
        val result = chunksBox
                        .query(Chunk_.chunkEmbedding.nearestNeighbors(queryEmbedding, 25))
                        .build()
                        .findWithScores()
                        .map { Pair(it.score.toFloat(), it.get()) }
//                        .subList(0, n)
        return result.subList(0,n)
    }

}