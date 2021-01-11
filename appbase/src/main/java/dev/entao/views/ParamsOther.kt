package dev.entao.views

import android.widget.TableRow

/**
 * Created by entaoyang@163.com on 2016-08-03.
 */




fun TableRow.LayoutParams.span(n: Int): TableRow.LayoutParams {
	this.span = n
	return this
}

fun TableRow.LayoutParams.atColumn(n: Int): TableRow.LayoutParams {
	this.column = n
	return this
}