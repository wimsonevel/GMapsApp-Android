package com.wim.gmapsapp_android.ui.screen

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.wim.gmapsapp_android.MyLocationSource
import com.wim.gmapsapp_android.model.Address
import com.wim.gmapsapp_android.ui.components.AutoCompleteTextField
import com.wim.gmapsapp_android.ui.theme.Purple200
import com.wim.gmapsapp_android.ui.viewmodel.GeoViewModel

@Composable
fun MapScreen(
    address: Address,
    viewModel: GeoViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val suggestions by viewModel.suggestions.observeAsState(emptyList())
    val picked by viewModel.picked.observeAsState(emptyList())

    val lastKnownLocation = address.lastKnownLocation

    var isMapLoaded by remember { mutableStateOf(false) }
    var cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude), 15f
        )
    }

    val locationSource = MyLocationSource()

    var uiSettings by remember { mutableStateOf(MapUiSettings()) }
    var mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }

    LaunchedEffect(lastKnownLocation) {
        Log.d("Location", "Updating blue dot on map..")
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

        viewModel.reverseGeocode("${cameraPositionState.position.target.latitude},${cameraPositionState.position.target.longitude}")
    }


    LaunchedEffect(cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving) {
            Log.d("Location", "Map camera started moving due to ${cameraPositionState.cameraMoveStartedReason.name}")

            viewModel.reverseGeocode("${cameraPositionState.position.target.latitude},${cameraPositionState.position.target.longitude}")
        }
    }

    val onSearch: (String) -> Unit = { searchText ->
        if(searchText.length > 5)  {
            viewModel.fetchSuggestions(searchText)
        }
    }

    val onItemSelected: (String) -> Unit = { selectedItem ->
        val intent = Intent().apply {
            putExtra("address", selectedItem)
        }

        (context as? Activity)?.apply {
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                isMapLoaded = true
            },
            locationSource = locationSource,
            properties = mapProperties,
            uiSettings = uiSettings
        ) {
            uiSettings = uiSettings.copy(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false
            )
        }

        AutoCompleteTextField(
            modifier = Modifier
                .padding(top = 50.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth()
                .background(Color.White)
                .clip(RoundedCornerShape(8.dp))
                .align(Alignment.TopCenter),
            suggestions = suggestions,
            onItemSelected = onItemSelected,
            onSearch = onSearch,
        )

        Icon(
            imageVector = Icons.Default.Place,
            tint = Purple200,
            contentDescription = "Pin",
            modifier = Modifier
                .fillMaxHeight(0.05f)
                .aspectRatio(1f)
                .align(Alignment.Center)
        )

        Column(
            modifier = Modifier
                .padding(bottom = 50.dp, start = 50.dp, end = 50.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .height(140.dp)
                .background(Color.White)
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Center
        ) {

            if(picked.isNotEmpty()) {
                Text(
                    textAlign = TextAlign.Center,
                    text = picked.get(0).title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                )
                Button(
                    onClick = {
                        onItemSelected(picked.get(0).title)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "Pick Location")
                }
            }else{
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }

        if (!isMapLoaded) {
            AnimatedVisibility(
                modifier = Modifier
                    .matchParentSize(),
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

}
