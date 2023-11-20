package com.example.woofology

interface AllBreedsListener {
    fun onFetch(response: AllBreedsResponse?, message: String?)
    fun onError(message: String?)
}