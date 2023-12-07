package com.example.woofology

interface RandomBreedListener {
    fun onFetch(response: RandomBreedResponse?, message: String?)
    fun onError(message: String?)
}