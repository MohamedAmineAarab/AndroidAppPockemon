package com.aarabik.pockemon

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.aarabik.pockemon.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.lang.Exception
import kotlin.reflect.KMutableProperty0

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
         //declare la fun pour travail en premier
        SynchPockemon() //call fun pour avoir mettre des pockemon dans la carte
        chekPermission() //call the fun for excute in the main fun
    }
    val LocationAccess = 124 //code if the the man accept
    fun chekPermission(){ //fonction qui excute dans la fanction Main qui permetre de donnner lacces a la localisation
        if (Build.VERSION.SDK_INT >= 23){ //teste la version d'android si il est 23 ou plus

            if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                //teste le permission si il est active dans le android mobile ou non

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),LocationAccess) //demande d'acces a activer les permission
                return //atende le repense de l'utilisateur
            }
        }

        getUserLocation()
    }

    //function pour voir si l'utilisateur accepter le damande de permission
    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray){
        when(LocationAccess){
            LocationAccess -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){ //teste si l'utilisateur accepte le permission
                    getUserLocation() //appel la fun de prend data de localisation
                }else{ //si l'utilisateur n'accepte pas le permission
                    Toast.makeText(this,"Acces locatin is deny",Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //fonction pour pour acces les paramettre de app et suivez les changment de position
    fun getUserLocation(){ //fun pour avoir acces a les paramettre de app
        Toast.makeText(this,"location acces now",Toast.LENGTH_LONG).show()
        //TODO: acces user location

        val mylocayion = MyLocationListener() //appel class de detection de device
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager //entrant a la systeme service
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,    5000,5f,mylocayion) //check permission et synchroniser apres 3s et 3metre
        val myThread = Mythread() //appel la fun pour travaill apres detection de permission
        Mythread().start() //start loperation de detection de device
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    //Class pour pouvoir enregistre localisation de device
    var l0: Location?=null //declare un variable qui prendre location de device
    inner class MyLocationListener:LocationListener{
        constructor(){
            l0 = Location("Me")
            l0!!.latitude = 0.0
            l0!!.longitude=0.0
        }
        override fun onLocationChanged(location: Location) {
            l0 = location
        }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }
        override fun onProviderEnabled(provider: String) {

        }
        override fun onProviderDisabled(provider: String) {

        }
    }

    //class pour pouvoir toujour localisation de device etre synchronisee
    var oldingLocation:Location?=null
    inner class Mythread:Thread{
        constructor():super(){
            oldingLocation = Location("this is my old location")
            oldingLocation!!.latitude=0.0
            oldingLocation!!.longitude=0.0
        }

        override fun run() {
            while (true){
                try {
                    if (oldingLocation!!.distanceTo(l0)==0f){ continue }//teste si la distance entre my location

                    oldingLocation=l0  //si la distance entre old et neauvau location mettre la meme location ente eux
                runOnUiThread { //function pour pouvoir acces a la mainactivity on utilise fun runOniThread tous ces code il permettre de afficher sur le front d'app
                    //My location
                    mMap!!.clear() //clear data pour ne pas avoir des lag sur les location
                    val Sydney = LatLng(l0!!.latitude, l0!!.longitude)
                    mMap!!.addMarker(MarkerOptions()
                        .position(Sydney)
                        .title("Me")
                        .snippet("this is my location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.imagepro)))
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(Sydney,14f))

                    //mettre des pockemon on map
                    for (i in 0..listeDePockemon.size-1){ //mettre un boucle pour conter les numero des pock
                        var thenewPockemon = listeDePockemon[i] //enreistre les nombre des pockemon dans ce variable
                        if (thenewPockemon.isCatch==false){ //teste si le pockemon est catche on non
                            val pockemonLocation = LatLng(thenewPockemon.location!!.latitude,thenewPockemon.location!!.longitude)
                            mMap!!.addMarker(MarkerOptions()
                                .position(pockemonLocation)
                                .title(thenewPockemon.name)
                                .snippet(thenewPockemon.descrip + "and the power is :" + thenewPockemon.power)
                                .icon(BitmapDescriptorFactory.fromResource(thenewPockemon.image!!)))
                            if (thenewPockemon!!.location!!.distanceTo(l0)<=2){ //teste si location est closte the pockemon
                                thenewPockemon.isCatch=true //mettre true si oui
                                myPower = myPower + thenewPockemon.power!! //change le power
                                listeDePockemon[i] = thenewPockemon //mettre a new liste
                                Toast.makeText(applicationContext,"you catch a Pockemon your power now is $myPower",Toast.LENGTH_LONG).show() //mettre a msg
                            }
                        }
                    }
                }
                 //attende 5 second pour pouvoir synchronisheer location
                    Thread.sleep(5000)
                }catch (ex:Exception){}
            }
        }
    }

    var myPower:Double = 0.0
    //mettre pockemon on map
    var listeDePockemon= ArrayList<Pockemon>()
    fun SynchPockemon(){
        listeDePockemon.add(Pockemon("Bad guy","Living in japon",R.drawable.image1,60.0,133.0,-66.9))
        listeDePockemon.add(Pockemon("good guy","Living in Eu",R.drawable.image2,70.0,13.0,-36.9))
        listeDePockemon.add(Pockemon("Little man","Living in africa",R.drawable.image3,80.0,3.0,-606.9))
        listeDePockemon.add(Pockemon("The best one","he live in america he have the muste power",R.drawable.image5,100.00,155.0,-22.00))
    }
}

