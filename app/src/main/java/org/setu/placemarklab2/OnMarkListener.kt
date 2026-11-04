package org.setu.placemarklab2

import org.setu.placemarklab2.models.PlacemarkModel

interface OnMarkListener {
    fun onMarkClick(mark: PlacemarkModel)
    fun onMarkLongClick(mark: PlacemarkModel): Boolean

}