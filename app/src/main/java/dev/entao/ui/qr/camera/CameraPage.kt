package dev.entao.kan.qr.camera

import android.Manifest
import android.graphics.ImageFormat
import android.widget.LinearLayout

import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import dev.entao.appbase.Task
import dev.entao.pages.toast
import dev.entao.appbase.reqPerm
import dev.entao.views.Params
import dev.entao.views.fill
import dev.entao.views.needId
import dev.entao.log.logd
import dev.entao.page.LinearPage
import dev.entao.ui.qr.camera.CameraPreview


/**
 * Created by entaoyang@163.com on 2016-10-29.
 */

open class CameraPage : LinearPage(), CameraPreview.PreviewDataCallback {
    var title: String = "Title"

    lateinit var cameraView: CameraPreview
    val DELAY_BEEP: Long = 150

    override fun onCreateContent(contentView: LinearLayout) {
        super.onCreateContent(contentView)
        titleBar.title(title)

        cameraView = CameraPreview(context).needId()
        cameraView.previewDataCallback = this
        cameraView.preferSize = Size(800, 600)
        cameraView.pictureFormat = ImageFormat.YUV_420_888
        onConfigCamera(cameraView)
        contentView.addView(cameraView, Params.linear.fill)
//        capture.decode()
    }

    open fun onConfigCamera(cameraView: CameraPreview) {

    }

    fun requestFrame() {
        cameraView.requestPreview()
    }

    override fun onResume() {
        logd("QRPage.OnResume")
        super.onResume()
        reqPerm(Manifest.permission.CAMERA) {
            if (it) {
                cameraView.resume()
            }
        }
    }

    override fun onPreviewData(data: SourceData) {
        logd("onPreviewData: ", data.data.size, data.rotation)
        requestFrame()
    }

}

class QRPage : CameraPage() {
    private val reader: MultiFormatReader = MultiFormatReader().apply {
        setHints(
            mapOf<DecodeHintType, Collection<BarcodeFormat>>(
                Pair(DecodeHintType.POSSIBLE_FORMATS, arrayListOf(BarcodeFormat.QR_CODE))
            )
        )
    }

    init {
        title = "二维码扫描"
    }

    override fun onCreateContent(contentView: LinearLayout) {
        super.onCreateContent(contentView)

    }

    override fun onConfigCamera(cameraView: CameraPreview) {
        cameraView.apply {
            cameraId = 0
            preferSize = Size(800, 600)
            barcodeSceneMode = true
        }
    }

    override fun onPreviewData(data: SourceData) {
        logd("onPreviewData: ", data.data.size, data.rotation)
        Task.back {
            doAnalyze(data)
            requestFrame()
        }
    }

    private fun doAnalyze(image: SourceData) {
        val w = image.dataWidth
        val h = image.dataHeight
//        logd("Analyze...", w, h)
        logd("Foramt: ", image.imageFormat, "w: $w,  h:$h,  r:", image.rotation)

        val src = image.createSource()
//        val src = PlanarYUVLuminanceSource(image.data, w, h, 0, 0, w, h, false)
        val bmp = BinaryBitmap(HybridBinarizer(src))
        try {
            val r = reader.decode(bmp)
            logd("结果: ", r.text)
            toast(r.text)

        } catch (ex: NotFoundException) {
            logd("ERR: ", ex.message)
            ex.printStackTrace()
        }
    }
}
