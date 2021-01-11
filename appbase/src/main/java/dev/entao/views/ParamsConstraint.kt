@file:Suppress("unused")

package dev.entao.views

import androidx.constraintlayout.widget.Barrier
import androidx.constraintlayout.widget.ConstraintHelper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import dev.entao.appbase.dp


fun ConstraintParams.edgesParentHor(horBias: Float = 0.5f): ConstraintParams {
    leftToLeft = 0
    rightToRight = 0
    horizontalBias = horBias
    return this
}

fun ConstraintParams.edgesParentVer(verBias: Float = 0.5f): ConstraintParams {
    topToTop = 0
    bottomToBottom = 0
    verticalBias = verBias
    return this
}

fun ConstraintParams.edgesParent(horBias: Float = 0.5f, verBias: Float = 0.5f): ConstraintParams {
    leftToLeft = 0
    rightToRight = 0
    topToTop = 0
    bottomToBottom = 0
    horizontalBias = horBias
    verticalBias = verBias
    return this
}


fun ConstraintParams.circle(viewId: Int, angle: Int, radius: Int): ConstraintParams {
    this.circleConstraint = viewId
    this.circleAngle = angle.toFloat()
    this.circleRadius = radius.dp
    return this
}


fun ConstraintParams.widthPercent(percent: Float = 1F): ConstraintParams {
    this.width = 0
    matchConstraintPercentWidth = percent
    return this
}

fun ConstraintParams.heightPercent(percent: Float = 1F): ConstraintParams {
    this.height = 0
    matchConstraintPercentHeight = percent
    return this
}

fun ConstraintParams.heightRange(minVal: Int, maxVal: Int): ConstraintParams {
    this.height = GroupParams.WRAP_CONTENT
    this.matchConstraintMinHeight = minVal.dp
    this.matchConstraintMaxHeight = maxVal.dp
    return this
}


fun ConstraintParams.widthRange(minVal: Int, maxVal: Int): ConstraintParams {
    this.width = GroupParams.WRAP_CONTENT
    this.matchConstraintMinWidth = minVal.dp
    this.matchConstraintMaxWidth = maxVal.dp
    return this
}

fun ConstraintParams.heightWeight(v: Float): ConstraintParams {
    this.height = 0
    this.verticalWeight = v
    return this
}

fun ConstraintParams.widthWeight(v: Float): ConstraintParams {
    this.width = 0
    this.horizontalWeight = v
    return this
}

//W,1:2
fun ConstraintParams.ratioW(w: Int, h: Int): ConstraintParams {
    this.dimensionRatio = "W,$w:$h"
    return this
}

private fun Any.setFieldValue(fieldName: String, value: Any?) {
    val f = this.javaClass.getDeclaredField(fieldName)
    f.isAccessible = true
    f.set(this, value)
}

fun ConstraintParams.chainSpreadHorizontal(): ConstraintParams {
    this.horizontalChainStyle = ConstraintParams.CHAIN_SPREAD
    return this
}

fun ConstraintParams.chainPackedHorizontal(): ConstraintParams {
    this.horizontalChainStyle = ConstraintParams.CHAIN_PACKED
    return this
}

fun ConstraintParams.chainSpreadInsideHorizontal(): ConstraintParams {
    this.horizontalChainStyle = ConstraintParams.CHAIN_SPREAD_INSIDE
    return this
}

fun ConstraintParams.chainSpreadVertical(): ConstraintParams {
    this.verticalChainStyle = ConstraintParams.CHAIN_SPREAD
    return this
}

fun ConstraintParams.chainPackedVertical(): ConstraintParams {
    this.verticalChainStyle = ConstraintParams.CHAIN_PACKED
    return this
}

fun ConstraintParams.chainSpreadInsideVertical(): ConstraintParams {
    this.verticalChainStyle = ConstraintParams.CHAIN_SPREAD_INSIDE
    return this
}


fun ConstraintLayout.guideline(block: Guideline.() -> Unit): Guideline {
    val g = Guideline(context)
    g.needId()
    g.layoutParams = ConstraintLayout.LayoutParams(-2, -2) // WRAP_CONTENT
    addView(g)
    g.block()
    return g
}

fun ConstraintLayout.barrier(block: Barrier.() -> Unit): Barrier {
    val g = Barrier(context)
    g.layoutParams = ConstraintLayout.LayoutParams(-2, -2) // WRAP_CONTENT
    addView(g)
    g.block()
    return g
}


fun <T : ConstraintHelper> T.referIds(vararg ids: Int): T {
    this.referencedIds = IntArray(ids.size) {
        ids[it]
    }
    return this
}
