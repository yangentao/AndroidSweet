package dev.entao.page

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import android.widget.Toast
import dev.entao.appbase.AppFile
import dev.entao.appbase.FileUri
import dev.entao.appbase.uriLocal
import dev.entao.pages.hideInputMethod
import dev.entao.pages.toast
import dev.entao.pages.toastKey
import dev.entao.base.userName
import dev.entao.log.logd
import dev.entao.views.*
import java.io.File
import kotlin.reflect.KProperty


fun Page.toast(vararg texts: String) {
    this.context.toast(*texts)
}

fun Page.hideInputMethod() {
    this.activity.hideInputMethod()
}

fun Page.toastKey(key: String, vararg texts: String): Toast {
    return this.activity.toastKey(key, *texts)
}

fun <T : View> Page.findView(key: String): T {
    return this.pageView.findViewById(ViewID(key))
}

fun <T : View> Page.findView(key: Int): T {
    return this.pageView.findViewById(key)
}


fun PageActivity.pickImage(block: (Uri) -> Unit) {
    val i = Intent(Intent.ACTION_PICK)
    i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
    this.startActivityResult(i) {
        if (it.OK) {
            val uri = it.data?.data
            logd("pick image: ", uri)
            if (uri != null) {
                block(uri)
            }
        }
    }
}

fun PageActivity.takeImage(block: (File) -> Unit) {
    val fmt = "JPEG"
    val outputFile = AppFile.tempFile(fmt)
    val outUri = FileUri(outputFile)
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0)
    }
    intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri)
    intent.putExtra("outputFormat", fmt)
    this.startActivityResult(intent) {
        if (it.OK && outputFile.exists()) {
            block(outputFile)
        }
    }
}

fun PageActivity.takeViedo(sizeM: Int, block: (Uri) -> Unit) {
    val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
    intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, sizeM * 1024 * 1024)
    startActivityResult(intent) {
        if (it.OK) {
            block.invoke(it.data!!.data!!)
        }
    }
}

fun PageActivity.pickVideo(block: (Uri) -> Unit) {
    val i = Intent(Intent.ACTION_PICK)
    i.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*")
    startActivityResult(i) {
        if (it.OK) {
            block.invoke(it.data!!.data!!)
        }
    }
}

fun PageActivity.cropImage(uri: Uri, outX: Int, outY: Int, result: (Bitmap?) -> Unit) {
    val intent = Intent("com.android.camera.action.CROP")
    intent.setDataAndType(uri, "image/*")
    intent.putExtra("crop", "true")
    // aspectX aspectY 是宽高的比例
    intent.putExtra("aspectX", 1)
    intent.putExtra("aspectY", 1)
    // outputX outputY 是裁剪图片宽高
    intent.putExtra("outputX", outX)
    intent.putExtra("outputY", outY)
    intent.putExtra("return-data", true)
    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    // intent.putExtra("output",CAMERA_EXTRA_OUTPUT_FILE);

    startActivityResult(intent) {
        if (it.OK) {
            val extras = it.data?.extras
            var photo: Bitmap? = null
            if (extras != null) {
                photo = extras.getParcelable("data")
            }
            result.invoke(photo)
        } else {
            result.invoke(null)
        }
    }
}

fun PageActivity.selectPortrait(block: (Bitmap) -> Unit) {
    selectImage { uri ->
        cropImage(uri, 256, 256) {
            if (it != null) {
                block(it)
            }
        }
    }
}

fun PageActivity.selectImage(block: (Uri) -> Unit) {
    this.xdialog.showListString(null, listOf("拍照", "相册")) { _, s ->
        if (s == "拍照") {
            takeImage { f ->
                block(f.uriLocal)
            }
        } else {
            pickImage {
                block(it)
            }
        }
    }
}

//editName指定EditText的名称(使用EditText.named()函数指定)
//如果不指定editName, editName = property.userName + "Edit"
open class EditValue(private val editName: String, private val trimed: Boolean) {
    protected fun getText(thisRef: Page, property: KProperty<*>): String {
        val key = if (editName.isEmpty()) {
            property.userName + "Edit"
        } else {
            editName
        }
        return if (trimed) {
            thisRef.findView<TextView>(key).textTrim
        } else {
            thisRef.findView<TextView>(key).textS
        }
    }

    protected fun setText(thisRef: Page, property: KProperty<*>, value: String) {
        val key = if (editName.isEmpty()) {
            property.userName + "Edit"
        } else {
            editName
        }
        if (trimed) {
            thisRef.findView<TextView>(key).textTrim = value
        } else {
            thisRef.findView<TextView>(key).textS = value
        }
    }
}

//editName指定EditText的名称(使用EditText.named()函数指定)
//如果不指定editName, editName = property.userName + "Edit"
class EditString(editName: String = "", trimed: Boolean = false) : EditValue(editName, trimed) {
    operator fun getValue(thisRef: Page, property: KProperty<*>): String {
        return getText(thisRef, property)
    }

    operator fun setValue(thisRef: Page, property: KProperty<*>, value: String) {
        setText(thisRef, property, value)
    }
}

class EditInt(editName: String, private val emptyValue: Int = 0) : EditValue(editName, true) {
    operator fun getValue(thisRef: Page, property: KProperty<*>): Int {
        val s = getText(thisRef, property)
        return if (s.isEmpty()) {
            emptyValue
        } else {
            s.toInt()
        }
    }

    operator fun setValue(thisRef: Page, property: KProperty<*>, value: Int) {
        setText(thisRef, property, value.toString())
    }
}

class EditLong(editName: String, private val emptyValue: Long = 0L) : EditValue(editName, true) {
    operator fun getValue(thisRef: Page, property: KProperty<*>): Long {
        val s = getText(thisRef, property)
        return if (s.isEmpty()) {
            emptyValue
        } else {
            s.toLong()
        }
    }

    operator fun setValue(thisRef: Page, property: KProperty<*>, value: Long) {
        setText(thisRef, property, value.toString())
    }
}

class EditFloat(editName: String, private val emptyValue: Float = 0f) : EditValue(editName, true) {
    operator fun getValue(thisRef: Page, property: KProperty<*>): Float {
        val s = getText(thisRef, property)
        return if (s.isEmpty()) {
            emptyValue
        } else {
            s.toFloat()
        }
    }

    operator fun setValue(thisRef: Page, property: KProperty<*>, value: Float) {
        setText(thisRef, property, value.toString())
    }
}

class EditDouble(editName: String, private val emptyValue: Double = 0.0) : EditValue(editName, true) {
    operator fun getValue(thisRef: Page, property: KProperty<*>): Double {
        val s = getText(thisRef, property)
        return if (s.isEmpty()) {
            emptyValue
        } else {
            s.toDouble()
        }
    }

    operator fun setValue(thisRef: Page, property: KProperty<*>, value: Double) {
        setText(thisRef, property, value.toString())
    }
}