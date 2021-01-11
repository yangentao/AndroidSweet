package dev.entao.kan.qrx

import android.annotation.SuppressLint
import android.view.Surface
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.camera.view.PreviewView
import dev.entao.appbase.*
import dev.entao.views.*
import dev.entao.log.logd
import dev.entao.page.LinearPage
import dev.entao.theme.ColorX

class QRPageX : LinearPage() {
    private lateinit var cameraXMgr: CameraXManager
    var title: String = "扫描二维码"

    val cfg: CameraCfg = CameraCfg().apply {
//        this.rotation = Surface.ROTATION_270
        this.reverseHorizontal = false
        this.reverseVertical = false
    }

    private var progressZoom: Int = 0


    @SuppressLint("SetTextI18n")
    override fun onCreateContent(contentView: LinearLayout) {
        super.onCreateContent(contentView)

        titleBar.gone()
//        titleBar {
//            title(this@QRPageX.title)
//        }
        contentView.relativeLayout(Params.linear.fill) {
            append<PreviewView> {
                relativeParams {
                    val h = Math.max(App.screenWidthPx, App.screenHeightPx)
                    val w: Int = h * 9 / 16
                    width(w).height(h).parentTop.parentCenterX
                }
                cameraXMgr = CameraXManager(this)
            }

            linearLayoutV {
                relativeParams {
                    width(120).heightWrap.parentRight.marginRight(10)
                }
                val pm = Params.linear.widthFill.heightWrap.marginY(0)
                button(pm) {
                    text = "拍照"
                    onClick {
                        takePic()
                    }

                }

                button(pm) {
                    text = "前置摄像头"
                    onClick {
                        cfg.front = true
                        restart()
                    }

                }
                button(pm) {
                    text = "后置摄像头"
                    onClick {
                        cfg.front = false
                        restart()
                    }
                }
                button(pm) {
                    text = "水平翻转"
                }
                button(pm) {
                    linearParams { widthFill.heightWrap.marginY(5) }
                    text = "竖直翻转"
                }
                append<SeekBar> {
                    linearParams { widthFill.heightWrap.marginY(5) }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        this.min = 0
                    }
                    this.max = 100
                    this.customColors(ColorX.cyanDark, ColorX.blueDark, ColorX.redDark)
                    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            progressZoom = progress
                            mergeAction("seek.zoom", 1000) {
                                logd("Merge: $progressZoom")
                                onZoom(progressZoom)
                            }
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }

                    })
                }
                button(pm) {
                    text = "不旋转"
                    onClick {
                        cfg.rotation = Surface.ROTATION_0
                        restart()
                    }
                }
                button(pm) {
                    text = "逆时针90度"
                    onClick {
                        cfg.rotation = Surface.ROTATION_90
                        restart()
                    }
                }
                button(pm) {
                    text = "逆时针180度"
                    onClick {
                        cfg.rotation = Surface.ROTATION_180
                        restart()
                    }
                }
                button(pm) {
                    text = "逆时针270度"
                    onClick {
                        cfg.rotation = Surface.ROTATION_270
                        restart()
                    }
                }
            }


        }

        cameraXMgr.onReady = {
        }

        cameraXMgr.onResult = {
            onQRResult(it)
        }

    }

    private fun takePic() {
        cameraXMgr.takePic(cfg) {
            logd(it.absolutePath)
            logd(it.length())
        }
    }

    //[0-100]
    private fun onZoom(percent: Int) {
        cameraXMgr.zoom(percent / 100.0f)
    }

    private fun restart() {
        cameraXMgr.stop()
        Task.fore {
            cameraXMgr.start(this, cfg)
        }
    }

    private fun onQRResult(s: String) {
        logd("二维码扫描结果:  ", s)

    }

    override fun onResume() {
        super.onResume()

        reqPerm(ManiPerm.CAMERA) {
            if (it) {
                cameraXMgr.start(this, cfg)
            }
        }
    }

    override fun onPause() {
        cameraXMgr.stop()
        super.onPause()
    }

}