package com.ticket12306.android

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.ticket12306.android.ui.main.MainActivity
import org.hamcrest.CoreMatchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TicketFragment 集成测试
 * 测试范围：
 * 1. 界面初始化状态（搜索栏、日期、车次类型筛选、排序按钮）
 * 2. 搜索交互（出发站/到达站点击跳转、日期选择）
 * 3. 筛选交互（车次类型切换、筛选弹窗）
 * 4. 排序交互（排序字段切换）
 * 5. 交换按钮交互
 * 6. 下拉刷新
 * 7. 列表展示（空状态/有数据状态）
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class TicketFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // ==================== 界面初始化状态测试 ====================

    /** 测试车票页面启动后，出发站输入框可见 */
    @Test
    fun onLaunch_fromStationInputIsVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.et_from_station))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /** 测试车票页面启动后，到达站输入框可见 */
    @Test
    fun onLaunch_toStationInputIsVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.et_to_station))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /** 测试车票页面启动后，日期输入框可见 */
    @Test
    fun onLaunch_dateInputIsVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.et_date))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /** 测试车票页面启动后，搜索按钮可见 */
    @Test
    fun onLaunch_searchButtonIsVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_search))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /** 测试车票页面启动后，车次类型筛选ChipGroup可见 */
    @Test
    fun onLaunch_trainTypeChipGroupIsVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.chip_group_train_type))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /** 测试车票页面启动后，排序按钮可见 */
    @Test
    fun onLaunch_sortButtonsAreVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_sort_departure))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.btn_sort_arrival))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.btn_sort_duration))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    // ==================== 日期导航测试 ====================

    /** 测试前一天导航按钮可见 */
    @Test
    fun onLaunch_previousDayButtonIsVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_previous_day))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /** 测试后一天导航按钮可见 */
    @Test
    fun onLaunch_nextDayButtonIsVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_next_day))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /** 测试日期快捷标签可见 */
    @Test
    fun onLaunch_dateTagChipsAreVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.chip_today))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.chip_tomorrow))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.chip_day_after_tomorrow))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    // ==================== 交互测试 ====================

    /** 测试点击出发站输入框，不崩溃 */
    @Test
    fun clickFromStationInput_doesNotCrash() {
        Espresso.onView(ViewMatchers.withId(R.id.et_from_station))
            .perform(ViewActions.click())
    }

    /** 测试点击到达站输入框，不崩溃 */
    @Test
    fun clickToStationInput_doesNotCrash() {
        Espresso.onView(ViewMatchers.withId(R.id.et_to_station))
            .perform(ViewActions.click())
    }

    /** 测试点击交换按钮，不崩溃 */
    @Test
    fun clickSwapButton_doesNotCrash() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_swap))
            .perform(ViewActions.click())
    }

    /** 测试点击后一天导航按钮，不崩溃 */
    @Test
    fun clickNextDayButton_doesNotCrash() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_next_day))
            .perform(ViewActions.click())
    }

    /** 测试点击搜索按钮（无输入），不崩溃 */
    @Test
    fun clickSearchButton_noInput_doesNotCrash() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_search))
            .perform(ViewActions.click())
    }

    // ==================== 车次类型筛选测试 ====================

    /** 测试点击"全部"车次类型Chip，不崩溃 */
    @Test
    fun clickAllTrainTypeChip_doesNotCrash() {
        Espresso.onView(ViewMatchers.withId(R.id.chip_all))
            .perform(ViewActions.click())
    }

    /** 测试点击"高铁"车次类型Chip，不崩溃 */
    @Test
    fun clickHighSpeedTrainTypeChip_doesNotCrash() {
        Espresso.onView(ViewMatchers.withId(R.id.chip_high_speed))
            .perform(ViewActions.click())
    }

    // ==================== 排序交互测试 ====================

    /** 测试点击出发时间排序按钮，不崩溃 */
    @Test
    fun clickSortDeparture_doesNotCrash() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_sort_departure))
            .perform(ViewActions.click())
    }

    /** 测试连续点击同一排序按钮两次，不崩溃 */
    @Test
    fun clickSortDepartureTwice_doesNotCrash() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_sort_departure))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.btn_sort_departure))
            .perform(ViewActions.click())
    }

    // ==================== 筛选弹窗测试 ====================

    /** 测试点击筛选按钮，不崩溃 */
    @Test
    fun clickFilterButton_doesNotCrash() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_filter))
            .perform(ViewActions.click())
    }

    // ==================== 下拉刷新测试 ====================

    /** 测试下拉刷新控件存在 */
    @Test
    fun swipeRefreshLayoutIsDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.swipe_refresh))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    // ==================== 空状态测试 ====================

    /** 测试初始状态下RecyclerView存在 */
    @Test
    fun recyclerViewIsDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}
