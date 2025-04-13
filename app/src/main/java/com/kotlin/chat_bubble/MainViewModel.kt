package com.kotlin.chat_bubble

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Random

/**
 * 主界面ViewModel - 管理消息列表数据
 */
class MainViewModel : ViewModel() {

    private val _messageItems = MutableLiveData<List<MessageItem>>()
    val messageItems: LiveData<List<MessageItem>> = _messageItems
    
    private val random = Random()
    
    // 各种可选的气泡颜色
    private val bubbleColors = listOf(
        Color.RED,
        Color.parseColor("#98F5FF"),
        Color.parseColor("#87CEFF"),
        Color.parseColor("#8B658B"),
        Color.parseColor("#B22222")
    )
    
    // 可选的文本颜色
    private val textColors = listOf(
        Color.BLACK,
        Color.WHITE
    )
    
    init {
        loadMessages()
    }
    
    /**
     * 生成示例消息列表
     */
    private fun loadMessages() {
        val items = List(50) { index ->
            MessageItem(
                message = "消息 #${index + 1}",
                count = index.toString(),
                bubbleColor = bubbleColors[random.nextInt(bubbleColors.size)],
                textColor = textColors[random.nextInt(textColors.size)]
            )
        }
        _messageItems.value = items
    }
    
    /**
     * 更新消息可见性
     */
    fun updateMessageVisibility(position: Int, isVisible: Boolean) {
        val currentList = _messageItems.value?.toMutableList() ?: return
        if (position < 0 || position >= currentList.size) return
        
        currentList[position] = currentList[position].copy(isVisible = isVisible)
        _messageItems.value = currentList
    }
}