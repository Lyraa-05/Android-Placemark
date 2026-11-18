package org.setu.placemarklab2

import org.setu.placemarklab2.models.PlacemarkMemStore
import android.content.Context
import java.io.File

object AppData {
    lateinit var placedMarkList: PlacemarkMemStore
        private set

    fun initialize(context: Context) {
        val file = File(
            context.filesDir,
            "place_list.json"
        )

        placedMarkList = PlacemarkMemStore(file)
    }
}