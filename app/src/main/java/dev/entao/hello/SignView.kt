package dev.entao.hello

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * desc : 签名View
 */
class SignView(context: Context) : View(context) {
    private var paint: Paint = Paint()
    private var path: Path = Path()
    private var signBitmap: Bitmap = Bitmap.createBitmap(320, 240, Bitmap.Config.RGB_565)
    private lateinit var cacheCanvas: Canvas

    private val paintColor = Color.BLACK

    private val paintWidth = 15f
    private var xAlixs = 0.0f
    private var yAlixs = 0.0f

    private val mBackColor = Color.WHITE

    private var isSigned = false

    init {
        paint.apply {
            color = paintColor
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeWidth = paintWidth
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        signBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
        cacheCanvas = Canvas(signBitmap)
        cacheCanvas.drawColor(mBackColor)
        isSigned = false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //画此次笔画之前的签名
        canvas.drawBitmap(signBitmap, 0f, 0f, paint)
        // 通过画布绘制多点形成的图形
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //记录每次 X ， Y轴的坐标
        xAlixs = event.x
        yAlixs = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.reset()
                path.moveTo(xAlixs, yAlixs)
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(xAlixs, yAlixs)
                isSigned = true
            }
            MotionEvent.ACTION_UP -> {
                //将路径画到bitmap中，即一次笔画完成才去更新bitmap，而手势轨迹是实时显示在画板上的。
                cacheCanvas.drawPath(path, paint)
                path.reset()
            }
            else -> {
            }
        }

        // 更新绘制
        invalidate()
        return true
    }


    fun clear() {
        isSigned = false
        path.reset()
        paint.color = paintColor
        cacheCanvas.drawColor(mBackColor, PorterDuff.Mode.CLEAR)
        invalidate()
    }

    fun getBitmap(): Bitmap {
        return signBitmap
    }

    /**
     * quality  1-100
     */
    fun save(path: String, png: Boolean, quality: Int) {
        val bitmap = signBitmap
        val bos = ByteArrayOutputStream()
        if (png) {
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, bos)
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos)
        }
        val buffer = bos.toByteArray()
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
        try {
            val outputStream = FileOutputStream(file)
            outputStream.write(buffer)
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


}