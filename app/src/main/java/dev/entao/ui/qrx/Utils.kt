package dev.entao.kan.qrx

import android.widget.SeekBar
import dev.entao.appbase.StateList
import dev.entao.appbase.darkColor

fun SeekBar.customColors(thumColor: Int, progressColor: Int, backColor: Int) {
    val tc = darkColor(thumColor, 0.7)
    val pc = darkColor(progressColor, 0.7)
    val bc = darkColor(backColor, 0.7)
    this.thumbTintList = StateList.color(thumColor) {
        focused(tc).pressed(tc)
    }
    this.progressTintList = StateList.color(progressColor) {
        focused(pc).pressed(pc)
    }
    this.progressBackgroundTintList = StateList.color(backColor) {
        focused(bc).pressed(bc)
    }
}