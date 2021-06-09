package com.example.dampingi.models

data class Response(
    val detections: List<Detection>,
    val image: String
)