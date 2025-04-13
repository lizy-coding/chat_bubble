package com.kotlin.bubbleview

/**
 * 点数据类
 * 使用Kotlin数据类替代Java类
 */
data class Point(
    var x: Float = 0f,
    var y: Float = 0f
) {
    fun set(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
}