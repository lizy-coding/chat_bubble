# 气泡拖拽动画模块架构文档

## 重构目标

对气泡拖拽动画模块进行重构，主要目标是：
1. 明确控制层与视图层的分离
2. 采用策略模式实现不同的粒子效果
3. 提高代码可维护性和可扩展性
4. 遵循面向接口编程的原则

## 架构设计

重构后的项目采用了MVC架构模式，并结合了策略模式：

### 核心组件：

1. **视图层 (View)**
   - `BubbleView`：最终用户交互的视图组件，处理绘制与显示

2. **控制层 (Controller)**
   - `BubbleController`：负责处理气泡的状态变化、路径计算和用户交互逻辑

3. **模型层与策略 (Model & Strategy)**
   - `DefaultParticleStrategy`：粒子效果默认实现
   - `Circle`/`Point`/`Particle`：数据模型类

4. **接口层 (Interfaces)**
   - `IBubbleView`：定义气泡视图的基本行为
   - `IBubbleController`：定义控制器接口
   - `IParticleEffectStrategy`：定义粒子效果策略接口

### 项目结构

```
bubbleview/
├── src/main/java/com/kotlin/bubbleview/
│   ├── interfaces/
│   │   ├── IBubbleView.kt           # 气泡视图接口
│   │   ├── IBubbleController.kt     # 气泡控制器接口
│   │   └── IParticleEffectStrategy.kt # 粒子效果策略接口
│   ├── controllers/
│   │   └── BubbleController.kt      # 气泡控制器实现
│   ├── strategies/
│   │   └── DefaultParticleStrategy.kt # 默认粒子效果策略
│   ├── BubbleView.kt                # 气泡视图实现
│   ├── Circle.kt                    # 圆形数据模型
│   ├── Particle.kt                  # 粒子数据模型
│   ├── Point.kt                     # 点坐标数据模型
│   └── Extensions.kt                # Kotlin扩展工具函数
```

## 各组件职责

### 接口层

**1. IBubbleView.kt**
```kotlin
interface IBubbleView {
    fun setText(text: String)
    fun getText(): String
    fun setTextColor(color: Int)
    fun getTextColor(): Int
    fun setCircleColor(color: Int)
    fun getCircleColor(): Int
    fun setTextSize(size: Float)
    fun clearStatus()
}
```
定义了气泡视图的基本操作，使视图层职责清晰。

**2. IParticleEffectStrategy.kt**
```kotlin
interface IParticleEffectStrategy {
    fun generateParticles(bitmap: Bitmap, cx: Float, cy: Float, radius: Float): List<Particle>
    fun updateParticles(particles: List<Particle>, animationProgress: Float, width: Int, height: Int)
    fun setEffectParams(count: Int, duration: Int, speedFactor: Float, sizeFactor: Float, alphaFactor: Float)
}
```
定义了粒子效果策略的行为，便于扩展不同的爆炸效果。

**3. IBubbleController.kt**
```kotlin
interface IBubbleController {
    fun init(width: Int, height: Int)
    fun setParticleEffectStrategy(strategy: IParticleEffectStrategy)
    fun setStateChangeListener(listener: BubbleController.OnBubbleStateChangeListener)
    fun setBreakDistanceFactor(factor: Float)
    fun handleTouchEvent(event: MotionEvent, createBitmapCallback: () -> Bitmap): Boolean
    fun computePath()
    fun clearStatus()
}
```
定义了控制器的行为，管理气泡状态变化。

### 控制层

**BubbleController.kt**
- 负责处理用户交互逻辑
- 计算气泡拖拽路径
- 维护气泡状态
- 通知视图层和粒子效果策略

```kotlin
class BubbleController {
    // 状态监听器接口
    interface OnBubbleStateChangeListener {
        fun onBubbleBreak()
        fun onAnimationEnd()
    }
    
    // 核心方法
    fun handleTouchEvent(event: MotionEvent, createBitmapCallback: () -> Bitmap): Boolean { ... }
    fun computePath() { ... }
    fun drawPath(canvas: Canvas, drawPathCallback: (Path) -> Unit, drawCircleCallback: (Circle) -> Unit) { ... }
}
```

### 策略层

**DefaultParticleStrategy.kt**
- 实现默认的粒子爆炸效果
- 处理粒子生成和动画更新
- 可自定义参数调整视觉效果

```kotlin
class DefaultParticleStrategy : IParticleEffectStrategy {
    override fun generateParticles(bitmap: Bitmap, cx: Float, cy: Float, radius: Float): List<Particle> { ... }
    override fun updateParticles(particles: List<Particle>, animationProgress: Float, width: Int, height: Int) { ... }
    override fun setEffectParams(count: Int, duration: Int, speedFactor: Float, sizeFactor: Float, alphaFactor: Float) { ... }
}
```

### 视图层

**BubbleView.kt**
- 管理视图绘制和显示
- 集成控制器和策略
- 对外提供简洁的API
- 实现IBubbleView接口

## 交互流程

1. **初始化流程**:
   - BubbleView创建时初始化控制器和策略
   - 设置事件监听和回调

2. **拖拽流程**:
   - 用户触摸 → BubbleView.onTouchEvent → BubbleController.handleTouchEvent
   - 控制器更新圆的位置和状态
   - BubbleView重绘视图

3. **爆炸流程**:
   - 拖拽距离超过临界值 → BubbleController通知气泡断开
   - BubbleView使用粒子策略生成粒子
   - 启动ValueAnimator动画
   - 策略更新粒子状态
   - BubbleView重绘粒子效果

## 如何扩展

### 添加新的粒子效果策略

1. 创建新的策略类实现IParticleEffectStrategy接口:

```kotlin
class FireworkParticleStrategy : IParticleEffectStrategy {
    // 实现接口方法
    override fun generateParticles(...) { /* 烟花样式粒子生成 */ }
    override fun updateParticles(...) { /* 烟花轨迹动画 */ }
    override fun setEffectParams(...) { /* 自定义参数设置 */ }
}
```

2. 在BubbleView中使用新策略:

```kotlin
val bubbleView = findViewById<BubbleView>(R.id.bubbleView)
bubbleView.setParticleStrategy(FireworkParticleStrategy())
```

### 自定义控制器行为

可以创建新的控制器实现以支持不同的交互行为:

```kotlin
class ElasticBubbleController : IBubbleController {
    // 实现更具弹性的拖拽效果
}
```

## 总结

通过此次重构，项目遵循了以下设计原则:

1. **单一职责原则**: 每个类都有明确的职责
2. **开闭原则**: 可以扩展新功能而无需修改现有代码
3. **依赖倒置原则**: 高层模块依赖于抽象接口
4. **接口隔离原则**: 接口职责单一，避免臃肿
5. **组合优于继承**: 使用策略模式而非继承层次

重构后的架构提高了代码可维护性和可扩展性，使项目更加健壮和灵活。