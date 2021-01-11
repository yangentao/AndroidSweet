package dev.entao.hello

import android.widget.EditText
import android.widget.LinearLayout
import dev.entao.views.*
import dev.entao.log.logd
import dev.entao.pages.*

class MyConfigPage : ConfigPage() {

    override fun onCreateContent(contentView: LinearLayout) {
        super.onCreateContent(contentView)
        titleBar.title("设置")

        groupLine("基本设置")

        labelValue {
            label = "打印机"
//            value = "/dev/ttyACM0"
            value = "/dev"
            arrow()
            input("选择设备", object : InputConfig {
                override fun onEditConfig(ed: EditText) {
                    ed.inputTypePhone()
                    ed.setMaxLength(5)
                }

                override fun onInputValue(inputText: String): Pair<Boolean, String> {
                    return if (inputText.length != 3) {
                        false to "长度必须是3"
                    } else {
                        true to inputText
                    }
                }
            })
        }
        labelValueX<Int> {
            label = "拍照"
            arrow()
            optionPairs("前置" to 1, "后置" to 2)
            value = 1
            onValueChanged = {
                logd("new Value: ", it.value)
            }
        }
        groupLine("设备设置")
        labelValue {
            label = "人脸识别"
            value = "前置"
            arrow()
            onItemClick { lv ->
                xdialog.showListString("选择摄像头", listOf("前置", "后置")) { _, s ->
                    lv.value = s
                }

            }
        }
        labelSwitch {
            label = "记住密码"
            value = true
            onValueChanged {
                logd("password: ", it.value)
            }
        }
        labelValue {
            label = "水平对置"
            value = "否"
            arrow()
            icon(R.mipmap.test)
            optionItems("是", "否")

        }
        labelValueX<Int> {
            label = "等级"
            arrow()

            input("Level", object : InputConfigX<Int> {
                override fun textToValue(inputText: String): Int? {
                    return inputText.toIntOrNull()
                }

                override fun valueToText(v: Any?): String {
                    return v?.toString() ?: ""
                }

                override fun onCheck(inputText: String): String? {
                    val v = inputText.toIntOrNull() ?: return "不是数字"
                    if (v > 10) return "不能大于10 "
                    return null
                }

                override fun onConfigEdit(ed: EditText) {
                    ed.inputTypeNumber()
                }

            })
            value = 1
            onValueChanged = {
                logd("new Value: ", it.value)
            }
        }

        button("注册") {
            itemView.onClick {
                activity.toastKey("reg", "Hello")
            }
        }

    }
}