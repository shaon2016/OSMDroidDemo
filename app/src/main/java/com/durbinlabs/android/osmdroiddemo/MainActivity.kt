package com.durbinlabs.android.osmdroiddemo

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.util.GeoPoint
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.bonuspack.routing.RoadManager
import android.os.StrictMode
import org.osmdroid.bonuspack.routing.RoadNode
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import org.osmdroid.bonuspack.routing.Road
import java.util.concurrent.Callable
import java.util.function.Supplier


class MainActivity : FragmentActivity() {
    private lateinit var map: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        map = findViewById<MapView>(R.id.map)
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)

        val startPoint = GeoPoint(23.852130, 90.408124)
        val mapController = map.controller
        mapController.setZoom(14)
        mapController.setCenter(startPoint)

        addMarker(map, startPoint, "Start Point")

        addingWaypoints(map, startPoint)
    }

    private fun addMarker(map: MapView?, point: GeoPoint, title: String) {
        val startMarker = Marker(map)
        //Lat ‎23.746466 Lng 90.376015
        startMarker.position = point
        startMarker.title = title
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map?.overlays?.add(startMarker)
        map?.invalidate()
    }

    private fun addingWaypoints(map: MapView?, startPoint: GeoPoint) {
        val roadManager = OSRMRoadManager(this)

        val waypoints = ArrayList<GeoPoint>()
        waypoints.add(startPoint)
        waypoints.add(GeoPoint(23.816237, 90.366725))
        val endPoint = GeoPoint(23.776498, 90.373592)
        waypoints.add(endPoint)

//        MyRoadAsyncTask(roadManager, waypoints).execute()

        Observable.fromCallable {
            retrievingRoad(roadManager, waypoints)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                }, {

                }, {
                    map?.invalidate()
                })

        addMarker(map, endPoint, "End Point")
    }

    private fun retrievingRoad(roadManager: OSRMRoadManager, waypoints: ArrayList<GeoPoint>) {
        // Retrieving road

        val road = roadManager.getRoad(waypoints)
        val roadOverlay = RoadManager.buildRoadOverlay(road)
        map?.overlays?.add(roadOverlay);

        val nodeIcon = map?.context?.resources?.getDrawable(R.drawable.ic_navigation)
        for (i in 0 until road.mNodes.size) {
            val node = road.mNodes[i]
            val nodeMarker = Marker(map)
            nodeMarker.position = node.mLocation
            nodeMarker.setIcon(nodeIcon)
            nodeMarker.title = "Step $i"
            map?.overlays?.add(nodeMarker)
            nodeMarker.snippet = node.mInstructions;
            nodeMarker.subDescription = Road.getLengthDurationText(map?.context, node.mLength, node.mDuration);
        }
    }


    private inner class MyRoadAsyncTask(val roadManager: OSRMRoadManager,
                                        val waypoints: ArrayList<GeoPoint>) : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String? {
            val road = roadManager.getRoad(waypoints)
            val roadOverlay = RoadManager.buildRoadOverlay(road)
            map?.overlays?.add(roadOverlay);

            val nodeIcon = map?.context?.resources?.getDrawable(R.drawable.ic_navigation)
            for (i in 0 until road.mNodes.size) {
                val node = road.mNodes[i]
                val nodeMarker = Marker(map)
                nodeMarker.position = node.mLocation
                nodeMarker.setIcon(nodeIcon)
                nodeMarker.title = "Step $i"
                map?.overlays?.add(nodeMarker)
                nodeMarker.snippet = node.mInstructions;
                nodeMarker.subDescription = Road.getLengthDurationText(map?.context, node.mLength, node.mDuration);
            }

            return null
        }

        override fun onPostExecute(result: String?) {
            map?.invalidate()
        }
    }
}
