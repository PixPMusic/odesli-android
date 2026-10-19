package com.prochy.odesliandroid.utils

import com.google.gson.JsonParser
import retrofit2.Response
import java.net.URLEncoder

sealed class MusicDataResult {
    data class Success(val data: OdesliData) : MusicDataResult()
    object PublicApiRetired : MusicDataResult()
    object Failure : MusicDataResult()
}

internal fun musicDataResult(response: Response<OdesliData>): MusicDataResult {
    if (response.isSuccessful) {
        return response.body()?.let { MusicDataResult.Success(it) } ?: MusicDataResult.Failure
    }
    val errorBody = runCatching { response.errorBody()?.string() }.getOrNull()
    return if (isPublicApiRetired(response.code(), errorBody)) {
        MusicDataResult.PublicApiRetired
    } else {
        MusicDataResult.Failure
    }
}

internal fun isPublicApiRetired(statusCode: Int, errorBody: String?): Boolean {
    if (statusCode == 410) return true
    if (statusCode != 401 || errorBody.isNullOrBlank()) return false
    return runCatching {
        val error = JsonParser.parseString(errorBody)
        val code = if (error.isJsonObject) error.asJsonObject.get("code") else null
        code?.isJsonPrimitive == true && code.asJsonPrimitive.isString &&
            code.asString == "PUBLIC_API_ACCESS_DEPRECATED"
    }.getOrDefault(false)
}

fun songlinkWebsiteUrl(originalUrl: String): String {
    // Songlink accepts an encoded music URL as a path, rather than a query parameter.
    val encodedUrl = URLEncoder.encode(originalUrl.trim(), "UTF-8").replace("+", "%20")
    return "https://song.link/$encodedUrl"
}
