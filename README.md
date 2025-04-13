# Chat Bubble

基于Kotlin和Jetpack组件的聊天气泡示例应用程序。

## 功能特点

- 使用Kotlin语言编写
- 采用MVVM架构模式
- 使用Jetpack组件（ViewModel、LiveData等）
- 支持自定义气泡视图
- 气泡拖拽和爆炸效果
- RecyclerView高效显示消息列表
- DiffUtil优化列表更新
- Jetpack Compose现代UI示例

## 项目结构

- **app**: 主应用程序模块
- **bubbleview**: 气泡视图库模块

## 技术细节

### 气泡视图库

气泡视图库模块包含：

- `BubbleView`: 主要的自定义视图，实现拖拽和爆炸效果
- `Circle`: 圆形数据模型
- `Point`: 点数据模型
- `Particle`: 粒子效果模型

### 应用程序

- `MainActivity`: 主界面活动
- `MainViewModel`: 管理消息数据的ViewModel
- `BubbleAdapter`: RecyclerView的ListAdapter实现
- `ComposeSampleFragment`: Jetpack Compose示例片段

## 使用方法

1. 在布局文件中添加BubbleView:

```xml
<com.kotlin.bubbleview.BubbleView
    android:id="@+id/bubbleView"
    android:layout_width="24dp"
    android:layout_height="24dp"
    app:bubbleColor="@color/primary"
    app:textColor="@color/white"
    app:bubbleText="1" />
```

2. 在代码中控制BubbleView:

```kotlin
bubbleView.setText("99+")
bubbleView.setTextColor(Color.WHITE)
bubbleView.setCircleColor(Color.RED)
bubbleView.setOnAnimationEndListener { bubbleView ->
    // 处理动画结束事件
}
```