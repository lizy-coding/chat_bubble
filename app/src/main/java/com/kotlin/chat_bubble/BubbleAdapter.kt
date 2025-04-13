package com.kotlin.chat_bubble

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.bubbleview.BubbleView
import com.kotlin.chat_bubble.databinding.ItemBubbleBinding

/**
 * 气泡列表适配器 - 使用ListAdapter和ViewBinding
 */
class BubbleAdapter : ListAdapter<MessageItem, BubbleAdapter.BubbleViewHolder>(BubbleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BubbleViewHolder {
        val binding = ItemBubbleBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return BubbleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BubbleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BubbleViewHolder(private val binding: ItemBubbleBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(messageItem: MessageItem) {
            binding.tvMessage.text = messageItem.message
            binding.bubbleView.apply {
                visibility = if (messageItem.isVisible) View.VISIBLE else View.GONE
                setText(messageItem.count)
                setTextColor(messageItem.textColor)
                setCircleColor(messageItem.bubbleColor)
                onAnimationEndListener = object : BubbleView.OnAnimationEndListener {
                    override fun onEnd(bubbleView: BubbleView) {
                        messageItem.isVisible = false
                        bubbleView.visibility = View.GONE
                    }
                }
            }
        }
    }
}

/**
 * DiffUtil.ItemCallback实现高效列表更新
 */
class BubbleDiffCallback : DiffUtil.ItemCallback<MessageItem>() {
    override fun areItemsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
        return oldItem.message == newItem.message
    }

    override fun areContentsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
        return oldItem == newItem
    }
}