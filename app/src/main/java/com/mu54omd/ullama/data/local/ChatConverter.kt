package com.mu54omd.ullama.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mu54omd.ullama.domain.model.MessagesModel

class ChatConverter {
    private val gson = Gson()
    @TypeConverter
    fun messageModelToString(messagesModel: MessagesModel):String{
        return gson.toJson(messagesModel)
    }
    @TypeConverter
    fun stringToMessageModel(text: String):MessagesModel{
        return gson.fromJson(text, MessagesModel::class.java)
    }
    @TypeConverter
    fun fromListToString(input: List<Int>):String{
        return gson.toJson(input)
    }
    @TypeConverter
    fun fromStringToList(text: String):List<Int>{
        return gson.fromJson<List<Int>>(text,object : TypeToken<List<Int>>() {}.type)
    }
}