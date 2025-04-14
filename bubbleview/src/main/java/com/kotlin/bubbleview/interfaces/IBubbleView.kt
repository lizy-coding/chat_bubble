package com.kotlin.bubbleview.interfaces

/**
 * 气泡视图接口
 * 定义气泡视图基本行为
 */
interface IBubbleView {
    /**
     * 设置文本内容
     */
    fun setText(text: String)
    
    /**
     * 获取当前文本
     */
    fun getText(): String
    
    /**
     * 设置文本颜色
     */
    fun setTextColor(color: Int)
    
    /**
     * 获取当前文本颜色
     */
    fun getTextColor(): Int
    
    /**
     * 设置气泡颜色
     */
    fun setCircleColor(color: Int)
    
    /**
     * 获取当前气泡颜色
     */
    fun getCircleColor(): Int
    
    /**
     * 设置文本大小
     */
    fun setTextSize(size: Float)
    
    /**
     * 清除当前状态并重置
     */
    fun clearStatus()
}