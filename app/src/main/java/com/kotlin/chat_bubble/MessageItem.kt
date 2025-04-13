package com.kotlin.chat_bubble

import android.graphics.Color

/**
 * 消息数据模型
 */
data class MessageItem(
    val message: String,
    val count: String = "",
    val textColor: Int = Color.WHITE,
    val bubbleColor: Int = Color.RED,
    var isVisible: Boolean = true
)