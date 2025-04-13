package com.kotlin.bubbleview

/**
 * 圆数据类
 * 使用Kotlin数据类替代Java类
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