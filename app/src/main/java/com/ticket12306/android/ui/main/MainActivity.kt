package com.ticket12306.android.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ticket12306.android.R
import com.ticket12306.android.databinding.ActivityMainBinding
import com.ticket12306.android.util.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var currentNavId = R.id.navigation_ticket

    private val tabOrder = listOf(
        R.id.navigation_ticket,
        R.id.navigation_booking,
        R.id.navigation_order,
        R.id.navigation_profile
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        handleNotificationIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleNotificationIntent(it) }
    }

    /**
     * 处理通知点击跳转
     * 根据通知携带的Action路由到对应页面
     */
    private fun handleNotificationIntent(intent: android.content.Intent) {
        val action = intent.action ?: return
        val navController = findNavController(R.id.nav_host_fragment)

        when (action) {
            NotificationHelper.ACTION_VIEW_ORDER_DETAIL -> {
                navController.navigate(R.id.navigation_order)
            }
            NotificationHelper.ACTION_VIEW_BOOKING_DETAIL -> {
                navController.navigate(R.id.navigation_booking)
            }
        }
    }

    /**
     * 初始化导航组件
     * 步骤：
     * 1. 配置AppBar和底部导航
     * 2. 设置底部导航切换动画
     * 3. 监听导航目标变化，控制底部导航栏可见性
     */
    private fun setupNavigation() {
        val navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_ticket,
                R.id.navigation_booking,
                R.id.navigation_order,
                R.id.navigation_profile
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_ticket,
                R.id.navigation_booking,
                R.id.navigation_order,
                R.id.navigation_profile -> {
                    binding.navView.visibility = View.VISIBLE
                    applyNavAnimation(destination.id)
                }
                else -> {
                    binding.navView.visibility = View.GONE
                }
            }
        }
    }

    /**
     * 应用底部导航切换时的Fragment过渡动画
     * 步骤：
     * 1. 根据Tab顺序判断前进还是后退
     * 2. 应用对应的缩放淡入动画
     *
     * @param newNavId 新导航目标ID
     */
    private fun applyNavAnimation(newNavId: Int) {
        val currentIndex = tabOrder.indexOf(currentNavId)
        val newIndex = tabOrder.indexOf(newNavId)

        val navHostFragment = binding.navHostFragment
        if (currentIndex != newIndex && currentIndex >= 0 && newIndex >= 0) {
            val animRes = if (newIndex > currentIndex) {
                R.anim.fade_scale_in
            } else {
                R.anim.fade_scale_in
            }
            navHostFragment.startAnimation(
                AnimationUtils.loadAnimation(this, animRes)
            )
        }

        currentNavId = newNavId
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
