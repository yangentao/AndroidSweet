package dev.entao.kan.qrx

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import dev.entao.appbase.AppFile
import dev.entao.log.logd
import dev.entao.log.loge
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

//屏幕是1280*1024 横屏

class CameraXManager(val previewView: PreviewView) {
    val context: Context get() = previewView.context
    var resolution = Size(1280, 720)
    var onReady: () -> Unit = {}
    var onResult: (String) -> Unit = {}

    private var imageCapture: ImageCapture? = null

    //    private var imageAnalyzer: ImageAnalysis? = null
    private var cameraInst: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var exeService: ExecutorService = Executors.newSingleThreadExecutor()
    private var lensFace: Int = CameraSelector.LENS_FACING_BACK

    @Suppress("unused")
    fun stop() {
        cameraProvider?.unbindAll()
        cameraProvider = null
        this.imageCapture = null
//        this.imageAnalyzer = null
        this.cameraInst = null
        exeService.shutdown()

    }

    fun start(lifeOwner: LifecycleOwner, cfg: CameraCfg) {
        if (exeService.isShutdown) {
            exeService = Executors.newSingleThreadExecutor()
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            doStart(lifeOwner, cameraProvider, cfg)
            onReady()
        }, ContextCompat.getMainExecutor(context))
    }

    private fun doStart(lifeOwner: LifecycleOwner, cameraProvider: ProcessCameraProvider, cfg: CameraCfg) {
        if (cfg.front) {
            if (!cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                loge("NO FRONT Camera!")
                return
            }
        } else {
            if (!cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                loge("NO BACK Camera!")
                return
            }
        }
        lensFace = if (cfg.front) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK

        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFace).build()

        val pre = Preview.Builder().apply {
//            setTargetAspectRatio(cfg.aspectRatio)
            setTargetRotation(cfg.rotation)
            setTargetResolution(Size(960, 640))
        }.build()


//        val qrAnaly = QRImageAnalysis()
////        qrAnaly.onResult = {
////            invokeResult(it)
////        }
//        val imgAnalyzer = ImageAnalysis.Builder()
//            .setTargetAspectRatio(cfg.aspectRatio)
//            .setTargetRotation(cfg.rotation)
//            .build().also {
//                it.setAnalyzer(exeService, qrAnaly)
//            }

        val imageCapture =
            ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setTargetAspectRatio(cfg.aspectRatio)
                .setTargetRotation(cfg.rotation)
//                .setTargetResolution(Size(1280, 720))
                .build()
        this.cameraProvider = cameraProvider
//        val camera = cameraProvider.bindToLifecycle(lifeOwner, cameraSelector, pre, imageCapture, imgAnalyzer)
        val camera = cameraProvider.bindToLifecycle(lifeOwner, cameraSelector, pre, imageCapture)
        pre.setSurfaceProvider(previewView.surfaceProvider)
        this.imageCapture = imageCapture
//        this.imageAnalyzer = imgAnalyzer
        this.cameraInst = camera
        zoom(0f)


    }

    fun isTorchOn(): Boolean {
        return this.cameraInst?.cameraInfo?.torchState?.value == TorchState.ON
    }

    fun setTorchOn(on: Boolean) {
        this.cameraInst?.cameraControl?.enableTorch(on)
    }

    //[0-1]
    fun zoom(n: Float) {
        this.cameraInst?.cameraControl?.setLinearZoom(n)
    }

    fun takePic(cfg: CameraCfg, block: (File) -> Unit) {
        val imgCap = this.imageCapture ?: return

        val photoFile = AppFile.publicImageFile(AppFile.makeTempFileName("jpg"))
        val metadata = ImageCapture.Metadata().apply {
//            isReversedHorizontal = lensFace == CameraSelector.LENS_FACING_FRONT
        }
        if (cfg.reverseHorizontal) {
            metadata.isReversedHorizontal = true
        }
        if (cfg.reverseVertical) {
            metadata.isReversedVertical = true
        }
        logd("reverse: hor=", metadata.isReversedHorizontal, "  ver=", metadata.isReversedVertical)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
            .setMetadata(metadata)
            .build()

        // Setup image capture listener which is triggered after photo has been taken
        imgCap.takePicture(
            outputOptions, exeService, object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    loge("Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                    logd("Photo capture succeeded: $savedUri")
                    block(photoFile)
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        context.sendBroadcast(Intent(android.hardware.Camera.ACTION_NEW_PICTURE, savedUri))
                    }
                    MediaScannerConnection.scanFile(context, arrayOf(photoFile.parentFile!!.absolutePath), arrayOf(".jpg"), object : MediaScannerConnection.OnScanCompletedListener {
                        override fun onScanCompleted(path: String?, uri: Uri?) {
                            logd("scan completed: ", path, "  ", uri?.toString())

                        }

                    })

                }
            }
        )
    }

    private fun invokeResult(s: String) {
        this.onResult(s)
    }


}