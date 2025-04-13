package com.example.chat_bubble

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kotlin.bubbleview.BubbleView

class BubbleSampleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bubble_sample, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 设置气泡的动画结束监听器
        val bubbleView = view.findViewById<BubbleView>(R.id.bubbleView)
        bubbleView.onAnimationEndListener = object : BubbleView.OnAnimationEndListener {
            override fun onEnd(bubbleView: BubbleView) {
                // 动画结束后的操作，可以在这里添加更多逻辑
            }
        }
    }
}