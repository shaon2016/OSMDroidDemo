package com.durbinlabs.android.osmdroidpolyline

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val map = findViewById<MapView>(R.id.map)
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
        val waypoints = ArrayList<GeoPoint>()
        waypoints.add(startPoint)
        waypoints.add(GeoPoint(23.816237, 90.366725))
        val endPoint = GeoPoint(23.776498, 90.373592)
        waypoints.add(endPoint)

        addMarker(map, endPoint, "End Point")
        addPolyLine(map, waypoints)

    }

    private fun addPolyLine(map: MapView?, waypoints: ArrayList<GeoPoint>) {
        //add your points here
        val line = Polyline()   //see note below!
        line.points = waypoints
        line.setOnClickListener { polyline, mapView, eventPos ->
            Toast.makeText(this@MainActivity, "polyline with "
                    + polyline.points.size + "pts was tapped", Toast.LENGTH_LONG).show()
        true
        }
        map?.overlayManager?.add(line)
        map?.invalidate()
    }
}
