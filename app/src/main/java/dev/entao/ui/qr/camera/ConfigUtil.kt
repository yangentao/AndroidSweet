/*
 * Copyright (C) 2014 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused", "DEPRECATION")

package dev.entao.kan.qr.camera

import android.graphics.Rect
import android.hardware.Camera
import android.os.Build
import android.util.Log
import java.util.*
import java.util.regex.Pattern

/**
 * Utility methods for configuring the Android camera.
 *
 * @author Sean Owen
 */
object ConfigUtil {
    private const val TAG = "CameraConfiguration"
    private val SEMICOLON = Pattern.compile(";")
    private const val MAX_EXPOSURE_COMPENSATION = 1.5f
    private const val MIN_EXPOSURE_COMPENSATION = 0.0f
    private const val MIN_FPS = 10
    private const val MAX_FPS = 20
    private const val AREA_PER_1000 = 400

    fun setFocus(parameters: Camera.Parameters) {
        val s = findSettableValue(
            parameters.supportedFocusModes,
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO,
            Camera.Parameters.FOCUS_MODE_MACRO,
            Camera.Parameters.FOCUS_MODE_AUTO
        ) ?: return
        parameters.focusMode = s
    }

    fun setTorch(parameters: Camera.Parameters, on: Boolean) {
        val flashMode: String = if (on) {
            findSettableValue(
                parameters.supportedFlashModes,
                Camera.Parameters.FLASH_MODE_TORCH,
                Camera.Parameters.FLASH_MODE_ON
            )
        } else {
            findSettableValue(
                parameters.supportedFlashModes,
                Camera.Parameters.FLASH_MODE_OFF
            )
        } ?: return
        if (flashMode != parameters.flashMode) {
            parameters.flashMode = flashMode
        }
    }

    fun setBestExposure(parameters: Camera.Parameters, lightOn: Boolean) {
        val minExposure = parameters.minExposureCompensation
        val maxExposure = parameters.maxExposureCompensation
        val step = parameters.exposureCompensationStep
        if ((minExposure != 0 || maxExposure != 0) && step > 0.0f) {
            // Set low when light is on
            val targetCompensation = if (lightOn) MIN_EXPOSURE_COMPENSATION else MAX_EXPOSURE_COMPENSATION
            var compensationSteps = Math.round(targetCompensation / step)
            val actualCompensation = step * compensationSteps
            // Clamp value:
            compensationSteps = Math.max(Math.min(compensationSteps, maxExposure), minExposure)
            if (parameters.exposureCompensation == compensationSteps) {
                Log.i(TAG, "Exposure compensation already set to $compensationSteps / $actualCompensation")
            } else {
                Log.i(TAG, "Setting exposure compensation to $compensationSteps / $actualCompensation")
                parameters.exposureCompensation = compensationSteps
            }
        }
    }

    fun setBestPreviewFPS(parameters: Camera.Parameters, minFPS: Int = MIN_FPS, maxFPS: Int = MAX_FPS) {
        val bestRange = parameters.supportedPreviewFpsRange?.firstOrNull {
            it[Camera.Parameters.PREVIEW_FPS_MIN_INDEX] >= minFPS * 1000 && it[Camera.Parameters.PREVIEW_FPS_MAX_INDEX] <= maxFPS * 1000
        } ?: return

        val currentFpsRange = IntArray(2)
        parameters.getPreviewFpsRange(currentFpsRange)
        if (!Arrays.equals(currentFpsRange, bestRange)) {
            parameters.setPreviewFpsRange(
                bestRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
                bestRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
            )
        }
    }

    fun setFocusArea(parameters: Camera.Parameters) {
        if (parameters.maxNumFocusAreas > 0) {
            val middleArea = buildMiddleArea(AREA_PER_1000)
            parameters.focusAreas = middleArea
        }
    }

    fun setMetering(parameters: Camera.Parameters) {
        if (parameters.maxNumMeteringAreas > 0) {
            val middleArea = buildMiddleArea(AREA_PER_1000)
            parameters.meteringAreas = middleArea
        }
    }

    private fun buildMiddleArea(areaPer1000: Int): List<Camera.Area> {
        return listOf(Camera.Area(Rect(-areaPer1000, -areaPer1000, areaPer1000, areaPer1000), 1))
    }

    fun setVideoStabilization(parameters: Camera.Parameters) {
        if (parameters.isVideoStabilizationSupported) {
            if (parameters.videoStabilization) {
            } else {
                parameters.videoStabilization = true
            }
        }
    }

    fun setBarcodeSceneMode(parameters: Camera.Parameters) {
        if (Camera.Parameters.SCENE_MODE_BARCODE == parameters.sceneMode) {
            return
        }
        val sceneMode = findSettableValue(
            parameters.supportedSceneModes,
            Camera.Parameters.SCENE_MODE_BARCODE
        )
        if (sceneMode != null) {
            parameters.sceneMode = sceneMode
        }
    }

    fun setZoom(parameters: Camera.Parameters, targetZoomRatio: Double) {
        if (parameters.isZoomSupported) {
            val zoom = indexOfClosestZoom(parameters, targetZoomRatio) ?: return
            if (parameters.zoom == zoom) {
                Log.i(TAG, "Zoom is already set to $zoom")
            } else {
                Log.i(TAG, "Setting zoom to $zoom")
                parameters.zoom = zoom
            }
        } else {
            Log.i(TAG, "Zoom is not supported")
        }
    }

    private fun indexOfClosestZoom(parameters: Camera.Parameters, targetZoomRatio: Double): Int? {
        if(!parameters.isZoomSupported) {
            return null
        }
        val ratios = parameters.zoomRatios ?: return null
        Log.i(TAG, "Zoom ratios: $ratios")
        val maxZoom = parameters.maxZoom
        if (ratios.isEmpty() || ratios.size != maxZoom + 1) {
            Log.w(TAG, "Invalid zoom ratios!")
            return null
        }
        val target100 = 100.0 * targetZoomRatio

        var smallestDiff = java.lang.Double.POSITIVE_INFINITY
        var closestIndex = 0
        for (i in ratios.indices) {
            val diff = Math.abs(ratios[i] - target100)
            if (diff < smallestDiff) {
                smallestDiff = diff
                closestIndex = i
            }
        }
        Log.i(TAG, "Chose zoom ratio of " + ratios[closestIndex] / 100.0)
        return closestIndex
    }

    fun setInvertColor(parameters: Camera.Parameters) {
        if (Camera.Parameters.EFFECT_NEGATIVE == parameters.colorEffect) {
            Log.i(TAG, "Negative effect already set")
            return
        }
        val colorMode = findSettableValue(
            parameters.supportedColorEffects,
            Camera.Parameters.EFFECT_NEGATIVE
        )
        if (colorMode != null) {
            parameters.colorEffect = colorMode
        }
    }

    private fun findSettableValue(supportedValues: Collection<String>?, vararg desiredValues: String): String? {
        val all = supportedValues ?: return null
        for (v in desiredValues) {
            if (all.contains(v)) {
                return v
            }
        }
        return null
    }

    private fun toString(arrays: Collection<IntArray>?): String {
        if (arrays == null || arrays.isEmpty()) {
            return "[]"
        }
        val buffer = StringBuilder()
        buffer.append('[')
        val it = arrays.iterator()
        while (it.hasNext()) {
            buffer.append(Arrays.toString(it.next()))
            if (it.hasNext()) {
                buffer.append(", ")
            }
        }
        buffer.append(']')
        return buffer.toString()
    }

    private fun toString(areas: Iterable<Camera.Area>?): String? {
        if (areas == null) {
            return null
        }
        val result = StringBuilder()
        for (area in areas) {
            result.append(area.rect).append(':').append(area.weight).append(' ')
        }
        return result.toString()
    }

    fun collectStats(parameters: Camera.Parameters): String {
        return collectStats(parameters.flatten())
    }

    fun collectStats(flattenedParams: CharSequence?): String {
        val result = StringBuilder(1000)

        result.append("BOARD=").append(Build.BOARD).append('\n')
        result.append("BRAND=").append(Build.BRAND).append('\n')
        result.append("CPU_ABI=").append(Build.CPU_ABI).append('\n')
        result.append("DEVICE=").append(Build.DEVICE).append('\n')
        result.append("DISPLAY=").append(Build.DISPLAY).append('\n')
        result.append("FINGERPRINT=").append(Build.FINGERPRINT).append('\n')
        result.append("HOST=").append(Build.HOST).append('\n')
        result.append("ID=").append(Build.ID).append('\n')
        result.append("MANUFACTURER=").append(Build.MANUFACTURER).append('\n')
        result.append("MODEL=").append(Build.MODEL).append('\n')
        result.append("PRODUCT=").append(Build.PRODUCT).append('\n')
        result.append("TAGS=").append(Build.TAGS).append('\n')
        result.append("TIME=").append(Build.TIME).append('\n')
        result.append("TYPE=").append(Build.TYPE).append('\n')
        result.append("USER=").append(Build.USER).append('\n')
        result.append("VERSION.CODENAME=").append(Build.VERSION.CODENAME).append('\n')
        result.append("VERSION.INCREMENTAL=").append(Build.VERSION.INCREMENTAL).append('\n')
        result.append("VERSION.RELEASE=").append(Build.VERSION.RELEASE).append('\n')
        result.append("VERSION.SDK_INT=").append(Build.VERSION.SDK_INT).append('\n')

        if (flattenedParams != null) {
            val params = SEMICOLON.split(flattenedParams)
            Arrays.sort(params)
            for (param in params) {
                result.append(param).append('\n')
            }
        }

        return result.toString()
    }
}
