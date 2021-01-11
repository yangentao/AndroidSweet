@file:Suppress("MemberVisibilityCanBePrivate", "DEPRECATION")

package dev.entao.kan.qr.camera

import android.Manifest
import android.content.Context
import android.graphics.*
import android.hardware.Camera
import android.view.TextureView
import android.widget.FrameLayout
import dev.entao.appbase.App
import dev.entao.appbase.hasPerm
import dev.entao.pages.toast
import dev.entao.views.Params
import dev.entao.views.fill
import dev.entao.log.logd


class CameraPreview(context: Context) : FrameLayout(context), TextureView.SurfaceTextureListener, Camera.PreviewCallback {

    private val textureView: TextureView = TextureView(context)

    private var taskHandler: TaskHandler = TaskHandler("decoder")
    private var camera: Camera? = null
    private var cameraInfo: Camera.CameraInfo? = null
    private var focusManager: AutoFocusManager? = null
    private var previewing: Boolean = false

    val isOpen: Boolean get() = camera != null

    var displayOrientationDegree: Int = 0
        private set

    var previewSize: Size = Size(0, 0)
        private set

    var preferSize: Size = Size(800, 600) //1280*720

    var barcodeSceneMode: Boolean = false
    var pictureFormat: Int = ImageFormat.JPEG
    var previewFormat: Int = ImageFormat.YUV_420_888

    var previewDataCallback: PreviewDataCallback? = null
    var cameraId: Int = 0

    init {
        setBackgroundColor(Color.BLACK)
        textureView.surfaceTextureListener = this
        addView(textureView, Params.frame.fill)
    }


    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        logd("onSurfaceTextureAvailable", width, height)
        openCamera()
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        logd("onSurfaceTextureSizeChanged", width, height)
        openCamera()
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        logd("onSurfaceTextureDestroyed")
        stopCamera()
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
    }

    fun resume() {
        if (!isOpen && textureView.isAvailable) {
            openCamera()
        }
    }

    private fun stopCamera() {
        taskHandler.quit()
        focusManager?.stop()
        focusManager = null
        val c = this.camera ?: return
        if (previewing) {
            c.stopPreview()
            previewing = false
        }
        c.release()
        camera = null
    }

    private fun openCamera() {
        logd("startCamera: ", width, height)
        if (!context.hasPerm(Manifest.permission.CAMERA)) {
            context.toast("没有相机权限")
            return
        }
        val ca = if (camera != null) {
            camera!!
        } else {
            logd("open new")
            val c = Camera.open(cameraId) ?: return
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(cameraId, info)
            this.camera = c
            this.cameraInfo = info
            logd("supportedPreviewFormats:", c.parameters.supportedPreviewFormats)
            c.useParams {
                ConfigUtil.setFocus(it)
                if (barcodeSceneMode) {
                    ConfigUtil.setBarcodeSceneMode(it)
                }
                ConfigUtil.setTorch(it, false)
                val reqSize = CameraHelper.findBestSize(it, preferSize)
                it.setPreviewSize(reqSize.width, reqSize.height)
                previewSize = reqSize
                logd("PreviewSize: ", reqSize.width, reqSize.height)
                it.pictureFormat = pictureFormat
//                it.previewFormat = previewFormat


            }
            c.useParams {
                ConfigUtil.setVideoStabilization(it)
                ConfigUtil.setFocusArea(it)
                ConfigUtil.setMetering(it)
            }
            c
        }
        val r = CameraHelper.makeCameraDisplayOrientation(App.context, cameraInfo!!)
        ca.setDisplayOrientation(r)
        this.displayOrientationDegree = r
        logd("DisplayOrientation: ", r)

        if (previewing) {
            previewing = false
            ca.stopPreview()
            focusManager?.stop()
            focusManager = null
        }
        ca.setPreviewTexture(this.textureView.surfaceTexture)
        previewing = true
        ca.startPreview()
        focusManager = AutoFocusManager(ca)

        requestPreview()
    }

    fun requestPreview() {
        if (previewing) {
            camera?.setOneShotPreviewCallback(this)
//            camera?.setPreviewCallback(this)
        }
    }


    override fun onPreviewFrame(data: ByteArray, camera: Camera) {

        val r = makeCrop(previewSize.width, previewSize.height, displayOrientationDegree)

        val source = SourceData(data, previewSize.width, previewSize.height, camera.parameters.previewFormat, displayOrientationDegree, r)
        previewDataCallback?.onPreviewData(source)
    }

    fun makeCrop(w: Int, h: Int, degree: Int): Rect {

        val a = Rect(0, 0, w, h)
        val d = Math.abs(w - h) / 2
        if (degree % 180 == 0) {
            if (w > h) {
                a.inset(d, 0)
            } else {
                a.inset(0, d)
            }
            return a
        } else {
            val a = Rect(0, 0, h, w)
        }
        return a
    }

    fun interface PreviewDataCallback {
        fun onPreviewData(data: SourceData)
    }

}
