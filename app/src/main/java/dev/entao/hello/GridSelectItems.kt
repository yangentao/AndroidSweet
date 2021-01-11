package dev.entao.hello

import android.content.Context
import android.view.View
import android.widget.TextView
import dev.entao.appbase.*
import dev.entao.theme.ColorX
import dev.entao.views.*


fun showGridDialog(context: Context, title: String, vararg ps: Pair<String, Int>, block: (String) -> Unit) {
    showGridDialogList(context, title, ps.toList(), block)
}

@Suppress("UNCHECKED_CAST")
fun showGridDialogList(context: Context, title: String, ps: List<Pair<String, Int>>, block: (String) -> Unit) {
    XDialog(context).apply {
        this.contentLayout.minimumWidth = 250.dp
        title(title)
        body {
            grid {
                numColumns = if (ps.size <= 3) {
                    ps.size
                } else if (App.isLandscape) {
                    3
                } else {
                    2
                }
                callback = object : GridCallback {
                    override fun onBindView(itemView: View, item: Any) {
                        itemView as TextView
                        item as Pair<String, Int>
                        itemView.text = item.first
                        itemView.topImage = item.second.resDrawable.mutate().tinted(ColorX.cyanDark).sized(60)
                    }
                }
                clickCallback = object : AdapterClickCallback {
                    override fun onClickAdapter(position: Int, item: Any) {
                        dismiss()
                        item as Pair<String, Int>
                        block(item.first)
                    }
                }
                setItems(ps)
            }
//            bodyViewParam.marginX(20)
        }
        show()
    }
}