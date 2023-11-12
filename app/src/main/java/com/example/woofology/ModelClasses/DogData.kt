package com.example.woofology.ModelClasses

class DogData(
    name: String,
    origin: String,
    height: String,
    weight: String,
    lifeSpan: String,
    temperament: String,
    health: String
) {
    var name = "name"
    var origin = "origin"
    var height = "height"
    var weight = "weight"
    var lifeSpan = "life_span"
    var temperament = "temperament"
    var health = "health"

    init {
        this.name = name
        this.origin = origin
        this.height = height
        this.weight = weight
        this.lifeSpan = lifeSpan
        this.temperament = temperament
        this.health = health
    }
}