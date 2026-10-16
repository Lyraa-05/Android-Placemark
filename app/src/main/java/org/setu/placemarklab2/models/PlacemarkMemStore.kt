package org.setu.placemarklab2.models

import timber.log.Timber
import java.util.concurrent.atomic.AtomicLong

class PlacemarkMemStore {
    private val placemarks = ArrayList<PlacemarkModel>()
    private val lastId = AtomicLong(0L)


    fun findAll(): List<PlacemarkModel> {
        Timber.d("PlacemarkMemStore: findAll() fetched ${placemarks.size} items")
        return placemarks
    }

    fun create(placemark: PlacemarkModel) {
        placemark.id = lastId.incrementAndGet()
        placemarks.add(placemark)
        Timber.i("PlacemarkMemStore: Created mark ID ${placemark.id}")
    }

    fun update(placemark: PlacemarkModel): Boolean {
        val foundPlacemark = findOne(placemark.id)
        return if (foundPlacemark != null) {
            foundPlacemark.title = placemark.title;
            foundPlacemark.description = placemark.description;
            foundPlacemark.x = placemark.x;
            foundPlacemark.y = placemark.y;
            Timber.i("PlacemarkMemStore: Updated mark ID ${placemark.id}")
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