@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package dev.entao.views

import android.annotation.SuppressLint
import android.content.Context
import android.database.DataSetObserver
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import dev.entao.appbase.*
import dev.entao.imgloader.loadURL
import dev.entao.views.*
import dev.entao.views.parentBottom
import dev.entao.views.parentCenterX
import java.util.ArrayList


typealias PagerNewViewCallback = (Context, Int) -> View
typealias PagerDestroyCallback = (View, Int) -> Unit
typealias PagerClickCallback = (View, Int) -> Unit

@SuppressLint("ClickableViewAccessibility")
class IndicatorPager(context: Context) : RelativeLayout(context) {
    private var items: ArrayList<Any> = ArrayList()
    val pointSize: Int = 8
    val adapter = MyPagerAdapter()
    val viewPager = ViewPager(context)
    val indicatorLayout: LinearLayout

    var clickCallback: PagerClickCallback = { _, _ -> }
    var destroyCallback: PagerDestroyCallback = { _, _ -> }
    var newViewCallback: PagerNewViewCallback = { _, _ ->
        ImageView(context).apply {
            styleDefault()
            scaleFitXY()
        }
    }


    var dotColor = Color.argb(180, 150, 150, 150)
    var dotLightColor = grayColor(255)

    var timer: MyTimer = MyTimer()


    init {
        addView(viewPager, Params.relative.widthFill.heightWrap)
        indicatorLayout = linearLayoutH {
            relativeParams {
                wrap.parentCenterX.parentBottom
            }
            padding(10)
        }

        adapter.registerDataSetObserver(object : DataSetObserver() {
            override fun onChanged() {
                bind()
            }

            override fun onInvalidated() {

            }
        })
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                selectIndicator(position)
            }
        })
        viewPager.adapter = adapter


        viewPager.setOnTouchListener { _, _ ->
            timer.paused = true
            false
        }

    }

    private fun bind() {
        indicatorLayout.removeAllViews()
        for (i in 0 until adapter.count) {
            indicatorLayout.imageView {
                linearParams {
                    size(pointSize).margins(pointSize / 2, 0, pointSize / 2, 0)
                }
                val d = StateList.drawable {
                    normal {
                        ShapeOval().fill(dotColor).value
                    }
                    lighted {
                        ShapeOval().fill(dotLightColor).value
                    }
                }.sized(pointSize)
                setImageDrawable(d)
                this.scaleCenterInside()

            }
        }
        if (adapter.count > 0) {
            selectIndicator(currentPage)
        }
    }

    fun hideIndicatorView() {
        indicatorLayout.gone()
    }


    fun setCurrentPageSmooth(n: Int) {
        viewPager.setCurrentItem(n, true)
    }

    var currentPage: Int
        get() = viewPager.currentItem
        set(value) {
            viewPager.setCurrentItem(value, false)
        }


    fun setItemsImageDrawable(ls: List<Drawable>) {
        setItemsImages(ls) { v, item ->
            v.setImageDrawable(item as Drawable)
        }
    }

    fun setItemsImageRes(ls: List<Int>) {
        setItemsImages(ls) { v, item ->
            v.setImageResource(item as Int)
        }
    }

    fun setItemsImageURLs(ls: List<String>) {
        setItemsImages(ls) { iv, url ->
            url as String
            iv.loadURL(url, 800)
        }
    }


    fun setItemsImages(ls: List<Any>, block: (ImageView, Any) -> Unit) {
        newViewCallback = { _, p ->
            ImageView(context).apply {
                styleDefault()
                scaleFitXY()
                block(this, getItem(p))
            }
        }
        setItems(ls)
    }

    fun setItemsViews(ls: List<View>) {
        newViewCallback = { _, p ->
            getItem(p) as View
        }
        setItems(ls)
    }

    fun setItems(ls: List<Any>) {
        this.items.clear()
        this.items.addAll(ls)
        adapter.notifyDataSetChanged()
    }

    fun getCount(): Int {
        return items.size
    }

    fun getItem(position: Int): Any {
        return items[position]
    }

    private fun selectIndicator(p: Int) {
        for (i in 0 until indicatorLayout.childCount) {
            indicatorLayout.getChildAt(i).isSelected = i == p
        }
    }

    private fun timerCallback() {
        Task.fore {
            val c = adapter.count
            val n = viewPager.currentItem
            viewPager.setCurrentItem((n + 1) % c, true)
        }
    }

    fun startFlip(period: Long) {
        timer.callback = {
            timerCallback()
        }
        timer.repeat(period)
    }

    fun stopFlip() {
        timer.cancel()
    }


    inner class MyPagerAdapter : PagerAdapter() {

        override fun getCount(): Int {
            return items.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = newViewCallback(container.context, position)
            container.addView(view)
            view.clickView {
                clickCallback(it, position)
            }
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val v = `object` as View
            container.removeView(v)
            destroyCallback(v, position)
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }
    }

}

