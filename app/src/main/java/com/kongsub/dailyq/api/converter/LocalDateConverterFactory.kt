package com.kongsub.dailyq.api.converter

import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.time.LocalDate

/*
 API-응답(String) -> App(Date) -> API-요청(String) 으로 변환하는 과정이 번거롭다.
 따라서 LocalDateConverterFactory 를 생성을 하자!
 */

class LocalDateConverterFactory: Converter.Factory() {
    override fun stringConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, String>? {
        if (type == LocalDate::class.java){
            return Converter<LocalDate, String> { it.toString() }
        }
        return null
    }
}