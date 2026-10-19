package com.prochy.odesliandroid.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URI
import java.net.URLDecoder

class SonglinkWebsiteUrlTest {
    @Test
    fun providedAppleMusicUrlsSurviveAsSingleEncodedPathSegments() {
        val urls = listOf(
            "https://music.apple.com/us/album/dumpster-fire/1813949259?i=1813949260&ls",
            "https://music.apple.com/us/album/k1/1813949259?ls",
            "https://music.apple.com/us/album/marrow/6814885746?ls"
        )
        for (original in urls) {
            val uri = URI(songlinkWebsiteUrl(original))
            assertEquals("https", uri.scheme)
            assertEquals("song.link", uri.host)
            assertNull(uri.rawQuery)
            assertNull(uri.rawFragment)
            assertEquals(1, uri.rawPath.count { it == '/' })
            assertEquals(original, URLDecoder.decode(uri.rawPath.removePrefix("/"), "UTF-8"))
        }
        assertTrue(songlinkWebsiteUrl(urls.first()).endsWith("%3Fi%3D1813949260%26ls"))
    }

    @Test
    fun percentPlusUnicodeAndFragmentArePreserved() {
        val original = "https://example.com/číslo%2Fone?q=a+b&name=with space#part"
        val uri = URI(songlinkWebsiteUrl(original))
        assertEquals(original, URLDecoder.decode(uri.rawPath.removePrefix("/"), "UTF-8"))
        assertTrue(uri.rawPath.contains("%252F"))
        assertTrue(uri.rawPath.contains("a%2Bb"))
        assertTrue(uri.rawPath.contains("with%20space"))
        assertNull(uri.rawQuery)
        assertNull(uri.rawFragment)
    }

    @Test
    fun surroundingWhitespaceIsTrimmed() {
        assertEquals(songlinkWebsiteUrl("https://example.com/music"), songlinkWebsiteUrl("  https://example.com/music\n"))
    }
}
