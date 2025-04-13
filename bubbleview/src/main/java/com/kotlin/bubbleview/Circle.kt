package com.kotlin.bubbleview

/**
 * 圆数据类
 */
data class Circle(
    var x: Float,
    var y: Float,
    var radius: Float
) {
    fun set(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
}