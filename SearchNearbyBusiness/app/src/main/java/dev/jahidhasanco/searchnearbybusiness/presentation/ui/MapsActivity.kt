package dev.jahidhasanco.searchnearbybusiness.presentation.ui


import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil.setContentView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dev.jahidhasanco.searchnearbybusiness.R
import dev.jahidhasanco.searchnearbybusiness.databinding.ActivityMapsBinding
import dev.jahidhasanco.searchnearbybusiness.presentation.data.model.NearbyPlaces
import dev.jahidhasanco.searchnearbybusiness.presentation.data.model.Place
import dev.jahidhasanco.searchnearbybusiness.presentation.data.remote.PlacesService
import dev.jahidhasanco.searchnearbybusiness.utils.API_KEY
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    private var currentLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesService: PlacesService
    private lateinit var mapFragment: SupportMapFragment
    private var places: List<Place>? = null
    private var markers: MutableList<Marker> = emptyList<Marker>().toMutableList()
    private var rMarker: MutableList<Marker> = emptyList<Marker>().toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentView(this, R.layout.activity_maps)

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        placesService = PlacesService.create()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mapFragment.getMapAsync(this)
        setUpMaps()
        binding.idSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                for (marker in rMarker) {
                    marker.remove()
                }

                currentLocation?.let {
                    if (query != null) {
                        getNearbyPlaces(it, query)
                    }
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        binding.locationBtn.setOnClickListener {
            setUpMaps()
        }

    }

    private fun getCurrentLocation(onSuccess: (Location) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            currentLocation = location
            onSuccess(location)
        }.addOnFailureListener {

        }
    }

    private fun setUpMaps() {
        mapFragment.getMapAsync { googleMap ->
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@getMapAsync
            }
            googleMap.isMyLocationEnabled = true
            getCurrentLocation {
                googleMap.addMarker(
                    MarkerOptions().title("Your Location").position(it.latLng)
                )
                val pos = CameraPosition.fromLatLngZoom(it.latLng, 15f)
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos))

            }
            mMap = googleMap
        }
    }

    private fun getNearbyPlaces(location: Location, query: String) {
        val apiKey = API_KEY
        placesService.nearbyPlaces(
            apiKey = apiKey,
            location = "${location.latitude},${location.longitude}",
            radiusInMeters = 10000,
            placeType = query
        ).enqueue(object : Callback<NearbyPlaces> {
            override fun onFailure(call: Call<NearbyPlaces>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<NearbyPlaces>, response: Response<NearbyPlaces>
            ) {
                if (!response.isSuccessful) {
                    return
                }

                val places = response.body()?.results ?: emptyList()
                this@MapsActivity.places = places
                addPlaces()
            }
        })
    }

    private fun addPlaces() {

        val places = places ?: return

        for (place in places) {
            mMap.let {
                val marker = it.addMarker(
                    MarkerOptions().position(place.geometry.location.latLng).title(place.name)
                )
                if (marker != null) {
                    marker.tag = place
                    markers.add(marker)
                    rMarker.add(marker)
                }
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.setPadding(0, 0, 10, 0)
    }

    private val Location.latLng: LatLng
        get() = LatLng(this.latitude, this.longitude)

}