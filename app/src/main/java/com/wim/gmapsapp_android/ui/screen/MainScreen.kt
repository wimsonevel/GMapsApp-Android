package com.wim.gmapsapp_android.ui.screen

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.wim.gmapsapp_android.MyLocationSource
import com.wim.gmapsapp_android.PickActivity
import com.wim.gmapsapp_android.model.Address

@Composable
fun MainScreen(address: Address,
               modifier: Modifier) {
    val context = LocalContext.current

    val addressValue = remember { mutableStateOf(TextFieldValue("")) }
    val detailValue = remember { mutableStateOf(TextFieldValue("")) }
    val labelValue = remember { mutableStateOf(TextFieldValue("")) }

    val startActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val receivedData = result.data?.getStringExtra("address")

        if(receivedData != null) addressValue.value = TextFieldValue(text = receivedData.toString())
    }

    val fullWidthModifier =
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)

    Column(modifier = modifier) {

        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(200.dp)
        ) {
            PreviewMap(address = address)

            FloatingActionButton(
                backgroundColor = MaterialTheme.colors.secondary,
                onClick = {
                    val intent = Intent(context, PickActivity::class.java)
                    intent.putExtra("address", address)
                    startActivityLauncher.launch(intent)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp)
            ) {
                Icon(
                    Icons.Filled.Edit,
                    tint = Color.White,
                    contentDescription = "Add Address")
            }
        }

        OutlinedTextField(
            modifier = fullWidthModifier,
            value = addressValue.value,
            singleLine = true,
            label = { Text("Address") },
            placeholder = { Text("Street / Building Name") },
            isError = addressValue.value.text.isEmpty(),
            onValueChange = { newValue ->
                addressValue.value = newValue
            }
        )

        OutlinedTextField(
            modifier = fullWidthModifier,
            value = detailValue.value,
            singleLine = true,
            label = { Text("Detail") },
            placeholder = { Text("House no./Floor/Landmark") },
            isError = detailValue.value.text.isEmpty(),
            onValueChange = { newValue ->
                detailValue.value = newValue
            }
        )

        OutlinedTextField(
            modifier = fullWidthModifier,
            value = labelValue.value,
            singleLine = true,
            label = { Text("Label") },
            placeholder = { Text("e.g Home, Office, etc") },
            isError = labelValue.value.text.isEmpty(),
            onValueChange = { newValue ->
                labelValue.value = newValue
            }
        )

        Button(
            onClick = {
                if(!addressValue.value.text.isEmpty()
                    && !detailValue.value.text.isEmpty()
                    && !labelValue.value.text.isEmpty()) {
//                    address.copy(
//                        id = 1,
//                        address = addressValue.value.text,
//                        detail = detailValue.value.text,
//                        label = labelValue.value.text,
//                    )
                    Toast.makeText(context, "Siap dikirim", Toast.LENGTH_LONG).show()
                }else {
                    Toast.makeText(context, "Don't Leave Blank", Toast.LENGTH_LONG).show()
                }
            },
            modifier = fullWidthModifier,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text = "Save")
        }

    }
}

@Composable
fun PreviewMap(address: Address) {
    var isMapLoaded by remember { mutableStateOf(false) }
    var cameraPositionState = rememberCameraPositionState()

    var uiSettings by remember { mutableStateOf(MapUiSettings()) }

    val lastKnownLocation = address.lastKnownLocation
    val locationSource = MyLocationSource()

    val mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }

    LaunchedEffect(lastKnownLocation) {
        Log.d("Location", "Updating blue dot on map...")
        locationSource.onLocationChanged(lastKnownLocation)

        Log.d("Location", "Updating camera position....")
        val cameraPosition = CameraPosition.fromLatLngZoom(
            LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude),
            15f
        )

        cameraPositionState.animate(
            CameraUpdateFactory.newCameraPosition(cameraPosition),
            1_000
        )
    }

    // Detect when the map starts moving and print the reason
    LaunchedEffect(cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving) {
            Log.d("MainScreen", "Map camera started moving due to ${cameraPositionState.cameraMoveStartedReason.name}")
        }
    }

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                isMapLoaded = true
            },
            locationSource = locationSource,
            properties = mapProperties,
            uiSettings = uiSettings
        ) {
            uiSettings = uiSettings.copy(zoomControlsEnabled = false)
        }

        if (!isMapLoaded) {
            AnimatedVisibility(
                modifier = Modifier.fillMaxSize(),
                visible = !isMapLoaded,
                enter = EnterTransition.None,
                exit = fadeOut()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .background(MaterialTheme.colors.background)
                        .wrapContentSize()
                )
            }
        }

}