package com.example.ollamaui.data.local.objectbox

import com.example.ollamaui.domain.model.objectbox.Image
import com.example.ollamaui.domain.model.objectbox.Image_
import io.objectbox.kotlin.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ImageDatabase @Inject constructor() {

    private val imagesBox = ObjectBoxStore.store.boxFor(Image::class.java)

    fun addImage(image: Image): Long {
        return imagesBox.put(image)
    }

    fun removeImage(imageId: Long) {
        imagesBox.remove(imageId)
    }

    fun getImagesCount(): Long {
        return imagesBox.count()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllImages(): Flow<MutableList<Image>> =
        imagesBox.query(Image_.imageId.notNull()).build().flow().flowOn(Dispatchers.IO)
}