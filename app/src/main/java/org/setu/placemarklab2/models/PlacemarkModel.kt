package org.setu.placemarklab2.models

data class PlacemarkModel(
    var id: Long = 0L,
    var title: String = "",
    var description: String = "",
    var x: Double = 0.0,
    var y: Double = 0.0
)