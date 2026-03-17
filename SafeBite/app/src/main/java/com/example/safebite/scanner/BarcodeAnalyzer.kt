package com.example.safebite.scanner

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(private val onBarcodeDetected: (String) -> Unit) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()

    // ✅ Flag para evitar múltiples detecciones del mismo código
    private var isProcessed = false

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        // ✅ Si ya detectamos un código, ignoramos los siguientes frames
        if (isProcessed) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { value ->
                            if (!isProcessed) {
                                isProcessed = true  // ✅ Bloqueamos antes de llamar al callback
                                onBarcodeDetected(value)
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    // ✅ Añadido manejo de errores que faltaba
                    it.printStackTrace()
                }
                .addOnCompleteListener {
                    imageProxy.close() // ✅ Siempre se cierra el frame
                }
        } else {
            imageProxy.close()
        }
    }
}