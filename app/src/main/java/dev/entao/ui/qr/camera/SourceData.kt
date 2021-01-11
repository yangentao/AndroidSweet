package dev.entao.kan.qr.camera

import android.graphics.*
import com.google.zxing.PlanarYUVLuminanceSource
import java.io.ByteArrayOutputStream


class SourceData(val data: ByteArray, val dataWidth: Int, val dataHeight: Int, val imageFormat: Int, var rotation: Int, var cropRect: Rect) {

    val isRotated: Boolean
        get() = rotation % 180 != 0

    fun createSource(): PlanarYUVLuminanceSource {
        val rotated = rotateCameraPreview(rotation, data, dataWidth, dataHeight)
        return if (isRotated) {
//            PlanarYUVLuminanceSource(rotated, dataHeight, dataWidth, cropRect.left, cropRect.top, cropRect.width(), cropRect.height(), false)
//            PlanarYUVLuminanceSource(rotated, dataHeight, dataWidth, 0, 0, dataHeight, dataWidth, false)
            PlanarYUVLuminanceSource(rotated, dataHeight, dataWidth, dataHeight / 4, dataWidth / 4, dataHeight * 3 / 4, dataWidth * 3 / 4, false)
        } else {
            PlanarYUVLuminanceSource(rotated, dataWidth, dataHeight, dataWidth / 4, dataHeight / 4, dataWidth * 3 / 4, dataHeight * 3 / 4, false)
        }
    }

    val bitmap: Bitmap
        get() = getBitmap(1)

    /**
     * Return the source bitmap (cropped; in display orientation).
     *
     * @param scaleFactor factor to scale down by. Must be a power of 2.
     * @return the bitmap
     */
    fun getBitmap(scaleFactor: Int): Bitmap {
        return getBitmap(cropRect, scaleFactor)
    }

    private fun getBitmap(cropR: Rect, scaleFactor: Int): Bitmap {
        var cropRect = cropR
        if (isRotated) {
            cropRect = Rect(cropRect.top, cropRect.left, cropRect.bottom, cropRect.right)
        }

        // TODO: there should be a way to do this without JPEG compression / decompression cycle.
        val img = YuvImage(data, imageFormat, dataWidth, dataHeight, null)
        val buffer = ByteArrayOutputStream()
        img.compressToJpeg(cropRect, 90, buffer)
        val jpegData = buffer.toByteArray()
        val options = BitmapFactory.Options()
        options.inSampleSize = scaleFactor
        var bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.size, options)

        // Rotate if required
        if (rotation != 0) {
            val imageMatrix = Matrix()
            imageMatrix.postRotate(rotation.toFloat())
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, imageMatrix, false)
        }
        return bitmap
    }

    companion object {
        fun rotateCameraPreview(cameraRotation: Int, data: ByteArray, imageWidth: Int, imageHeight: Int): ByteArray {
            return when (cameraRotation) {
                0 -> data
                90 -> rotateCW(data, imageWidth, imageHeight)
                180 -> rotate180(data, imageWidth, imageHeight)
                270 -> rotateCCW(data, imageWidth, imageHeight)
                else ->                 // Should not happen
                    data
            }
        }

        /**
         * Rotate an image by 90 degrees CW.
         *
         * @param data        the image data, in with the first width * height bytes being the luminance data.
         * @param imageWidth  the width of the image
         * @param imageHeight the height of the image
         * @return the rotated bytes
         */
        fun rotateCW(data: ByteArray, imageWidth: Int, imageHeight: Int): ByteArray {
            // Adapted from http://stackoverflow.com/a/15775173
            // data may contain more than just y (u and v), but we are only interested in the y section.
            val yuv = ByteArray(imageWidth * imageHeight)
            var i = 0
            for (x in 0 until imageWidth) {
                for (y in imageHeight - 1 downTo 0) {
                    yuv[i] = data[y * imageWidth + x]
                    i++
                }
            }
            return yuv
        }

        /**
         * Rotate an image by 180 degrees.
         *
         * @param data        the image data, in with the first width * height bytes being the luminance data.
         * @param imageWidth  the width of the image
         * @param imageHeight the height of the image
         * @return the rotated bytes
         */
        fun rotate180(data: ByteArray, imageWidth: Int, imageHeight: Int): ByteArray {
            val n = imageWidth * imageHeight
            val yuv = ByteArray(n)
            var i = n - 1
            for (j in 0 until n) {
                yuv[i] = data[j]
                i--
            }
            return yuv
        }

        /**
         * Rotate an image by 90 degrees CCW.
         *
         * @param data        the image data, in with the first width * height bytes being the luminance data.
         * @param imageWidth  the width of the image
         * @param imageHeight the height of the image
         * @return the rotated bytes
         */
        fun rotateCCW(data: ByteArray, imageWidth: Int, imageHeight: Int): ByteArray {
            val n = imageWidth * imageHeight
            val yuv = ByteArray(n)
            var i = n - 1
            for (x in 0 until imageWidth) {
                for (y in imageHeight - 1 downTo 0) {
                    yuv[i] = data[y * imageWidth + x]
                    i--
                }
            }
            return yuv
        }
    }
}