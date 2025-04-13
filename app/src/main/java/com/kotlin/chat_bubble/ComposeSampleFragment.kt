package com.kotlin.chat_bubble

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import java.util.Random

/**
 * Compose实现的气泡样本片段
 */
class ComposeSampleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            // 保持Compose视图在Fragment生命周期内有效
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            
            setContent {
                MaterialTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        ComposeSampleScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun ComposeSampleScreen() {
    val random = remember { Random() }
    
    // 可用的气泡颜色
    val bubbleColors = remember {
        listOf(
            Color.RED,
            Color.parseColor("#98F5FF"),
            Color.parseColor("#87CEFF"),
            Color.parseColor("#8B658B"),
            Color.parseColor("#B22222")
        )
    }
    
    // Compose界面状态
    var messageCount by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Jetpack Compose示例",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "这个片段演示了如何使用Jetpack Compose构建现代UI。而气泡视图则使用了自定义View。",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { messageCount++ }
            ) {
                Text("添加消息 ($messageCount)")
            }
            
            // 注意：Compose无法直接使用自定义View，这里只是示例
            // 实际上我们需要创建Compose版本的气泡视图或通过AndroidView使用现有视图
            Text(
                text = "点击左侧按钮添加消息",
                style = MaterialTheme.typography.body2
            )
        }
    }
}