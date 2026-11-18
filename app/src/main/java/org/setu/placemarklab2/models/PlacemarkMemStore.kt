package org.setu.placemarklab2.models

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.io.File
import java.util.concurrent.atomic.AtomicLong

class PlacemarkMemStore(private val file: File) {
    private val placemarks = ArrayList<PlacemarkModel>()
    private val lastId = AtomicLong(0L)

    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    init {
        load()
    }

    private fun load() {
        if (!file.exists()) return

        val type = object : TypeToken<ArrayList<PlacemarkModel>>() {}.type

        val loaded: ArrayList<PlacemarkModel> =
            gson.fromJson(file.readText(), type) ?: arrayListOf()

        placemarks.clear()
        placemarks.addAll(loaded)

        lastId.set(placemarks.maxOfOrNull { it.id } ?: 0L)
    }

    private fun save() {
        file.parentFile?.mkdirs()
        file.writeText(gson.toJson(placemarks))
    }

    fun findAll(): List<PlacemarkModel> {
        Timber.d("PlacemarkMemStore: findAll() fetched ${placemarks.size} items")
        return placemarks
    }

    fun create(placemark: PlacemarkModel) {
        placemark.validate()
        placemark.id = lastId.incrementAndGet()
        placemarks.add(placemark)
        Timber.i("PlacemarkMemStore: Created mark ID ${placemark.id}")
        save()
    }

    fun update(placemark: PlacemarkModel): Boolean {
        placemark.validate()

        val foundPlacemark = findOne(placemark.id)
        return if (foundPlacemark != null) {
            foundPlacemark.title = placemark.title;
            foundPlacemark.description = placemark.description;
            foundPlacemark.x = placemark.x;
            foundPlacemark.y = placemark.y;
            foundPlacemark.imageUri = placemark.imageUri;
            Timber.i("PlacemarkMemStore: Updated mark ID ${placemark.id}")
            save()
            true
        } else {
            Timber.w("PlacemarkMemStore: Update failed, ID ${placemark.id} not found")
            false
        }
    }

    fun delete(id: Long): Boolean {
        val foundPlacemark = findOne(id)
        return if (foundPlacemark != null) {
            placemarks.remove(foundPlacemark)
            Timber.i("PlacemarkMemStore: Deleted mark ID $id")
            save()
            true
        } else {
            Timber.w("PlacemarkMemStore: Delete failed, ID $id not found")
            false
        }
    }

    fun findOne(id: Long): PlacemarkModel? {
        return placemarks.find { p -> p.id == id }
    }
}