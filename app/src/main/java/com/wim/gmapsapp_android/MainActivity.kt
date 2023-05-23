package com.wim.gmapsapp_android

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.LocationSource
import com.wim.gmapsapp_android.model.Address
import com.wim.gmapsapp_android.model.addressList
import com.wim.gmapsapp_android.ui.screen.MainScreen
import com.wim.gmapsapp_android.ui.theme.GMapsAppAndroidTheme
import com.wim.gmapsapp_android.ui.viewmodel.MapsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val REQUEST_CHECK_SETTINGS = 123

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val viewModel: MapsViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.getLocation(fusedLocationProviderClient)
            }
        }

    private fun askPermissions() = when {
        ContextCompat.checkSelfPermission(
            this,
            ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED -> {
            viewModel.getLocation(fusedLocationProviderClient)
        }
        else -> {
            requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        }
    }

    private fun checkLocationSettings() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, TimeUnit.MINUTES.toMillis(5)
        ).apply {
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setDurationMillis(TimeUnit.MINUTES.toMillis(5))
            setWaitForAccurateLocation(true)
            setMaxUpdates(1)
        }.build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            askPermissions()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        checkLocationSettings()
        askPermissions()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GMapsAppAndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MyApp(address = viewModel.address.value)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                askPermissions()
            } else {
                Toast.makeText(this, "Location settings not enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun MyApp(
    address: Address) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Add Address")
                },
                elevation = 8.dp,
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
            )
        }
    ) { innerPadding ->
        MainScreen(
            address = address,
            modifier = Modifier.padding(innerPadding)
        )
    }

}

class MyLocationSource : LocationSource {

    private var listener: LocationSource.OnLocationChangedListener? = null

    override fun activate(listener: LocationSource.OnLocationChangedListener) {
        this.listener = listener
    }

    override fun deactivate() {
        listener = null
    }

    fun onLocationChanged(location: Location) {
        listener?.onLocationChanged(location)
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun DefaultPreview() {
    GMapsAppAndroidTheme {
        MyApp(address = addressList.get(0))
    }
}
