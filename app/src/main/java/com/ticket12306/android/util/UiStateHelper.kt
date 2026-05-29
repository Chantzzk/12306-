package com.ticket12306.android.util

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.button.MaterialButton
import com.ticket12306.android.R

/**
 * 空状态视图配置器
 * 用于统一管理空状态视图的插图、标题、副标题和操作按钮
 *
 * @param rootView 包含空状态布局的父视图
 */
class EmptyStateHelper(private val rootView: View) {

    private val ivIllustration: ImageView = rootView.findViewById(R.id.iv_empty_illustration)
    private val tvTitle: TextView = rootView.findViewById(R.id.tv_empty_title)
    private val tvSubtitle: TextView = rootView.findViewById(R.id.tv_empty_subtitle)
    private val btnAction: MaterialButton = rootView.findViewById(R.id.btn_empty_action)

    /**
     * 配置并显示空状态视图
     * 步骤：
     * 1. 设置插图图标
     * 2. 设置标题和副标题文字
     * 3. 可选：设置操作按钮文字和点击事件
     * 4. 显示空状态视图
     */
    fun show(
        iconRes: Int = android.R.drawable.ic_menu_search,
        title: String,
        subtitle: String,
        actionText: String? = null,
        onActionClick: (() -> Unit)? = null
    ) {
        ivIllustration.setImageResource(iconRes)
        tvTitle.text = title
        tvSubtitle.text = subtitle

        if (actionText != null && onActionClick != null) {
            btnAction.text = actionText
            btnAction.visibility = View.VISIBLE
            btnAction.setOnClickListener { onActionClick() }
        } else {
            btnAction.visibility = View.GONE
        }

        rootView.visibility = View.VISIBLE
    }

    /** 隐藏空状态视图 */
    fun hide() {
        rootView.visibility = View.GONE
    }

    /** 判断空状态视图是否正在显示 */
    fun isShowing(): Boolean = rootView.visibility == View.VISIBLE
}

/**
 * 骨架屏控制器
 * 用于统一管理Shimmer骨架屏的显示和隐藏
 *
 * @param shimmerLayout ShimmerFrameLayout实例
 */
class SkeletonHelper(private val shimmerLayout: ShimmerFrameLayout) {

    /** 显示骨架屏并启动Shimmer动画 */
    fun show() {
        shimmerLayout.visibility = View.VISIBLE
        shimmerLayout.startShimmer()
    }

    /** 停止Shimmer动画并隐藏骨架屏 */
    fun hide() {
        shimmerLayout.stopShimmer()
        shimmerLayout.visibility = View.GONE
    }

    /** 判断骨架屏是否正在显示 */
    fun isShowing(): Boolean = shimmerLayout.visibility == View.VISIBLE
}

/**
 * 视图状态管理器
 * 统一管理内容列表、骨架屏、空状态、加载进度条之间的切换逻辑
 *
 * @param contentView 主内容视图（通常是RecyclerView）
 * @param skeletonHelper 骨架屏控制器
 * @param emptyStateHelper 空状态控制器
 */
class ViewStateManager(
    private val contentView: View,
    private val skeletonHelper: SkeletonHelper,
    private val emptyStateHelper: EmptyStateHelper
) {

    enum class State {
        LOADING,
        EMPTY,
        CONTENT,
        ERROR
    }

    private var currentState = State.LOADING

    /**
     * 切换到加载状态
     * 步骤：
     * 1. 隐藏内容视图
     * 2. 显示骨架屏
     * 3. 隐藏空状态
     */
    fun showLoading() {
        currentState = State.LOADING
        contentView.visibility = View.GONE
        skeletonHelper.show()
        emptyStateHelper.hide()
    }

    /**
     * 切换到内容状态
     * 步骤：
     * 1. 显示内容视图
     * 2. 隐藏骨架屏
     * 3. 隐藏空状态
     */
    fun showContent() {
        currentState = State.CONTENT
        contentView.visibility = View.VISIBLE
        skeletonHelper.hide()
        emptyStateHelper.hide()
    }

    /**
     * 切换到空状态
     * 步骤：
     * 1. 隐藏内容视图
     * 2. 隐藏骨架屏
     * 3. 配置并显示空状态
     *
     * @param iconRes 插图资源ID
     * @param title 标题文字
     * @param subtitle 副标题文字
     * @param actionText 操作按钮文字（可选）
     * @param onActionClick 操作按钮点击事件（可选）
     */
    fun showEmpty(
        iconRes: Int = android.R.drawable.ic_menu_search,
        title: String,
        subtitle: String,
        actionText: String? = null,
        onActionClick: (() -> Unit)? = null
    ) {
        currentState = State.EMPTY
        contentView.visibility = View.GONE
        skeletonHelper.hide()
        emptyStateHelper.show(iconRes, title, subtitle, actionText, onActionClick)
    }

    /**
     * 根据列表数据自动切换内容/空状态
     * 步骤：
     * 1. 列表为空 -> 显示空状态
     * 2. 列表不为空 -> 显示内容
     *
     * @param isEmpty 列表是否为空
     * @param iconRes 空状态插图
     * @param title 空状态标题
     * @param subtitle 空状态副标题
     * @param actionText 操作按钮文字
     * @param onActionClick 操作按钮点击事件
     */
    fun showContentOrEmpty(
        isEmpty: Boolean,
        iconRes: Int = android.R.drawable.ic_menu_search,
        title: String,
        subtitle: String,
        actionText: String? = null,
        onActionClick: (() -> Unit)? = null
    ) {
        if (isEmpty) {
            showEmpty(iconRes, title, subtitle, actionText, onActionClick)
        } else {
            showContent()
        }
    }

    /** 判断当前是否处于加载状态 */
    fun isShowing(): Boolean = currentState == State.LOADING
}

/**
 * 卡片展开/折叠动画工具
 * 用于实现卡片内容的展开和折叠切换效果
 */
object CardAnimationHelper {

    private const val ANIMATION_DURATION = 300L

    /**
     * 切换视图的展开/折叠状态
     * 步骤：
     * 1. 记录目标高度
     * 2. 折叠时：从当前高度动画到0
     * 3. 展开时：从0动画到目标高度
     *
     * @param contentView 需要展开/折叠的内容视图
     * @param isExpanded 当前是否已展开
     * @return 动画结束后是否展开（取反后的状态）
     */
    fun toggleExpand(contentView: View, isExpanded: Boolean): Boolean {
        if (isExpanded) {
            collapse(contentView)
        } else {
            expand(contentView)
        }
        return !isExpanded
    }

    /**
     * 展开视图
     * 步骤：
     * 1. 测量目标高度
     * 2. 从0开始动画到目标高度
     * 3. 动画结束后设置可见性
     */
    fun expand(view: View) {
        view.visibility = View.VISIBLE
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val targetHeight = view.measuredHeight

        view.layoutParams.height = 0
        view.requestLayout()

        val animator = android.animation.ValueAnimator.ofInt(0, targetHeight)
        animator.duration = ANIMATION_DURATION
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            view.layoutParams.height = animation.animatedValue as Int
            view.requestLayout()
        }
        animator.start()
    }

    /**
     * 折叠视图
     * 步骤：
     * 1. 获取当前高度
     * 2. 从当前高度动画到0
     * 3. 动画结束后设置GONE
     */
    fun collapse(view: View) {
        val currentHeight = view.measuredHeight

        val animator = android.animation.ValueAnimator.ofInt(currentHeight, 0)
        animator.duration = ANIMATION_DURATION
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            view.layoutParams.height = animation.animatedValue as Int
            view.requestLayout()
            if (animation.animatedValue as Int == 0) {
                view.visibility = View.GONE
            }
        }
        animator.start()
    }
}
