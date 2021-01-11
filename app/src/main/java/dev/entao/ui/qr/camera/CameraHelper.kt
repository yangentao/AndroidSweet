@file:Suppress("DEPRECATION")

package dev.entao.kan.qr.camera

import android.content.Context
import android.hardware.Camera
import android.view.Surface
import android.view.WindowManager
import dev.entao.log.logd

fun Camera.useParams(block: (Camera.Parameters) -> Unit) {
    try {
        val p = this.parameters
        block(p)
        this.parameters = p
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}


object CameraHelper {

    fun makeCameraDisplayOrientation(context: Context, info: Camera.CameraInfo): Int {
//        val rotation = context.windowManager.defaultDisplay.rotation;
        val rotation = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        var result: Int = 0
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result
    }

    fun openBack(): Pair<Camera, Camera.CameraInfo>? {
        for (i in 0 until Camera.getNumberOfCameras()) {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(i, info)
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                val camera = Camera.open(i)
                return camera to info
                break
            }
        }
        return null
    }

    fun findBestSize(ps: Camera.Parameters, preferSize: Size): Size {
        val ls = ps.supportedPreviewSizes
        val ls2 = ls.sortedBy { it.width * it.height }
        ls2.forEach {
            logd("相机preview: ", it.width, it.height)
        }
        val a = ls2.firstOrNull {
            it.width * it.height >= preferSize.width * preferSize.height
        } ?: ls2.last()
        if (a != null) {
            return Size(a.width, a.height)
        } else {
            return Size(800, 600)
        }

//        val sz = ls.firstOrNull {
//            it.height == 1080 && it.width == 1440
//        } ?: ls.firstOrNull {
//            it.height == 1080 && it.width == 1920
//        } ?: ls.firstOrNull {
//            it.height == 1080
//        } ?: ls.firstOrNull {
//            it.height == 720
//        } ?: ls.firstOrNull {
//            it.height == 960
//        } ?: ls.filter { it.height >= 720 }.minBy { it.height } ?: ls.firstOrNull {
//            it.height == 480 && it.width == 800
//        } ?: ls.firstOrNull {
//            it.height == 640
//        } ?: ls.first()
//        return Size(sz.width, sz.height)
    }

}