package com.kotlin.bubbleview

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue


/**
 * 将dp转换为像素
 */
fun Context.dpToPx(dp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        resources.displayMetrics
    ).toInt()
}

/**
 * 将像素转换为dp
 */
fun Context.pxToDp(px: Float): Int {
    return (px / resources.displayMetrics.density + 0.5f).toInt()
}

/**
 * 将sp转换为像素
 */
fun Context.spToPx(sp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        sp,
        resources.displayMetrics
    ).toInt()
}

/**
 * 将像素转换为sp
 */
@Suppress("DEPRECATION")
fun Context.pxToSp(px: Float): Int {
    return (px / resources.displayMetrics.scaledDensity + 0.5f).toInt()
}