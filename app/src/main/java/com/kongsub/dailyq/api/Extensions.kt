package com.kongsub.dailyq.api

import android.content.ContentResolver
import android.net.Uri
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source

// 갤러리에서 선택한 사진의 URI 를 uploadimage() 메서드로 업로드 할 수 있도록, URI 를 RequestBody 를 만들어 반환함.
fun Uri.asRequestBody(cr: ContentResolver): RequestBody {
    return object : RequestBody() {
        override fun contentType(): MediaType? =
            cr.getType(this@asRequestBody)?.toMediaTypeOrNull()

        override fun contentLength(): Long = -1

        override fun writeTo(sink: BufferedSink) {
            val source = cr.openInputStream(this@asRequestBody)?.source()
            source?.use { sink.writeAll(it)}
        }
    }
}