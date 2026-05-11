package com.example.safebite.view

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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.example.safebite.scanner.BarcodeAnalyzer
import com.example.safebite.controller.ProductController // ✅ Importamos tu controlador

@Composable
fun ScannerScreen(
    navController: NavHostController,
    productController: ProductController
) { // ✅ Añadido el controlador
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
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

                                    // ✅ 1. LE PEDIMOS AL CONTROLADOR QUE BUSQUE LOS DATOS (NutriScore, etc)
                                    productController.fetchProduct(barcode)

                                    // 2. Usamos el hilo principal para navegar
                                    (context as? Activity)?.runOnUiThread {
                                        // ✅ 2. NAVEGAMOS (Usamos "productDetail" a secas si el controlador guarda el estado)
                                        navController.navigate("productDetail") {
                                            popUpTo("scanner") { inclusive = true }
                                        }
                                    }
                                }
                            })
                        }

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analyzer
                    )
                }, executor)
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}