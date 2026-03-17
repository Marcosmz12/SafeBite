package com.example.safebite.view

import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController

// IMPORTANTE: Cambia esta ruta por la carpeta real donde guardaste el BarcodeAnalyzer
import com.example.safebite.scanner.BarcodeAnalyzer

// Archivo: view/ScannerScreen.kt
@Composable
fun ScannerScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var hasCameraPermission by remember { mutableStateOf(false) }

    // --- ESTE ES EL SEGURO ---
    var isNavigating by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        hasCameraPermission = it
    }
    LaunchedEffect(Unit) { launcher.launch(android.Manifest.permission.CAMERA) }

    if (hasCameraPermission) {
        AndroidView(
            factory = { context ->
                val previewView = PreviewView(context)
                val executor = ContextCompat.getMainExecutor(context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val analyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(executor, BarcodeAnalyzer { barcode ->
                                // SI YA ESTAMOS NAVEGANDO, NO HACEMOS NADA
                                if (!isNavigating && barcode.isNotEmpty()) {
                                    isNavigating = true
                                    // Usamos el hilo principal para navegar
                                    (context as? Activity)?.runOnUiThread {
                                        navController.navigate("productDetail/$barcode") {
                                            // Quitamos el escáner del historial para que al volver no se reactive solo
                                            popUpTo("home")
                                        }
                                    }
                                }
                            })
                        }

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analyzer)
                }, executor)
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}