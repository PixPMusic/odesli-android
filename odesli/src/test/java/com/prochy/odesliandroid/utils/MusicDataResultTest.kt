package com.prochy.odesliandroid.utils

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class MusicDataResultTest {
    @Test
    fun retirementCodeIdentifiesCurrentApiResponse() {
        val response = Response.error<OdesliData>(
            401,
            """{"statusCode":401,"code":"PUBLIC_API_ACCESS_DEPRECATED"}"""
                .toResponseBody("application/json".toMediaType())
        )
        assertSame(MusicDataResult.PublicApiRetired, musicDataResult(response))
    }

    @Test
    fun goneResponseIdentifiesRetirementWithoutJson() {
        assertTrue(isPublicApiRetired(410, null))
        assertTrue(isPublicApiRetired(410, "Gone"))
    }

    @Test
    fun unrelatedHttpFailuresStayFailures() {
        val retiredBody = """{"code":"PUBLIC_API_ACCESS_DEPRECATED"}"""
        for (status in listOf(400, 403, 404, 429, 500)) {
            assertFalse("HTTP $status", isPublicApiRetired(status, retiredBody))
        }
        assertFalse(isPublicApiRetired(401, """{"code":"UNAUTHORIZED"}"""))
    }

    @Test
    fun missingMalformedAndUnexpectedErrorBodiesStayFailures() {
        val bodies = listOf(
            null, "", " ", "Not authorized", "{", "null", "[]", "401", "{}",
            """{"code":null}""", """{"code":401}""", """{"code":{}}""",
            """{"code":["PUBLIC_API_ACCESS_DEPRECATED"]}"""
        )
        for (body in bodies) {
            assertFalse("Body: $body", isPublicApiRetired(401, body))
        }
    }

    @Test
    fun successfulApiResponsePreservesProviderData() {
        val entity = EntitiesData(
            "1813949260", "song", "Dumpster fire", "Artist", "https://example.com/art.jpg",
            100, 100, "itunes", listOf("appleMusic")
        )
        val data = OdesliData(
            "ITUNES_SONG::1813949260", "US", "https://song.link/i/1813949260",
            mapOf("ITUNES_SONG::1813949260" to entity),
            mapOf("appleMusic" to LinksData("https://music.apple.com/example", entityUniqueId = "ITUNES_SONG::1813949260"))
        )
        val result = musicDataResult(Response.success(data)) as MusicDataResult.Success
        assertSame(data, result.data)
        assertEquals(data.linksByPlatform, result.data.linksByPlatform)
    }

    @Test
    fun emptySuccessfulResponseIsFailure() {
        assertSame(MusicDataResult.Failure, musicDataResult(Response.success(null)))
    }

    @Test
    fun unrelatedErrorResponseIsFailure() {
        val response = Response.error<OdesliData>(
            401, "Unauthorized".toResponseBody("text/plain".toMediaType())
        )
        assertSame(MusicDataResult.Failure, musicDataResult(response))
    }
}
