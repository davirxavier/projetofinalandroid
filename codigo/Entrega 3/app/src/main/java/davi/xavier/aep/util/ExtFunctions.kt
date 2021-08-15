package davi.xavier.aep.util

import android.graphics.Bitmap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline

fun Polyline.addInfoWindow(map: GoogleMap, title: String, message: String): Marker? {
    val pointsOnLine = this.points.size
    val infoLatLng = this.points[(pointsOnLine / 2)]

    val invisibleMarker =
        BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))

    return map.addMarker(
        MarkerOptions()
            .position(infoLatLng)
            .title(title)
            .snippet(message)
            .alpha(0f)
            .icon(invisibleMarker)
            .anchor(0f, 0f)
    )
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, callback: (T) -> Unit) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T) {
            removeObserver(this)
            callback(t)
        }
    })
}

fun <T> LiveData<T>.observeOnce(callback: (T) -> Unit) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T) {
            removeObserver(this)
            callback(t)
        }
    })
}
