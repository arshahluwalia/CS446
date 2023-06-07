package com.example.jukebox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.example.jukebox.ui.theme.JukeboxTheme

// Uses Google Code Scanning API: https://developers.google.com/ml-kit/vision/barcode-scanning/code-scanner
class QRActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JukeboxTheme()  {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    startBarcodeScanner()
                }
            }
        }
    }

    fun startBarcodeScanner() {
        // Configure the barcode scanner options
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC
            )
            .build()

        // Create an instance of GmsBarcodeScannerClient
        val barcodeScannerClient = GmsBarcodeScanning.getClient(this, options)

        // Start the barcode scanning
        barcodeScannerClient.startScan()
            .addOnSuccessListener { barcode ->
                // Handle the scanned barcode
                val result = barcode.rawValue
                // Process the barcode value as needed
            }
            .addOnCanceledListener {
                // Scanning canceled
            }
            .addOnFailureListener { exception ->
                // Error occurred while scanning
                // Handle the exception
            }
    }
}