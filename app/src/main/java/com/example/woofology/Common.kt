package com.example.woofology

import java.util.Locale

object Common {

    fun getBreedFromLink(link: String): String {
        var modLink = link.substring(30)
        val lastPathIndex = modLink.indexOf("/")
        modLink = modLink.substring(0, lastPathIndex)
        return modLink
    }
}