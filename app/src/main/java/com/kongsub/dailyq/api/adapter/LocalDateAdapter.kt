package com.kongsub.dailyq.api.adapter

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDate

// GSON 이 LocalDate 를 처리할 수 있도록 어뎁터를 생성
object LocalDateAdapter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    override fun serialize(
        src: LocalDate?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        src.toString()
        return JsonPrimitive(src.toString())
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDate {
        return LocalDate.parse(json!!.asString)
    }
}