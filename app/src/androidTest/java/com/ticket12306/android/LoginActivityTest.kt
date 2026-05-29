package com.ticket12306.android

import android.view.View
import android.widget.EditText
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.ticket12306.android.ui.login.LoginActivity
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * LoginActivity 集成测试
 * 测试范围：
 * 1. 界面初始化状态（输入框、按钮、验证码区域）
 * 2. 表单输入交互（用户名、密码、验证码输入）
 * 3. 登录按钮状态（输入不完整时禁用）
 * 4. 验证码区域交互（点击刷新）
 * 5. 输入验证（表单错误提示）
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    // ==================== 界面初始化状态测试 ====================

    /** 测试Activity启动后，用户名输入框可见 */
    @Test
    fun onLaunch_usernameInputIsVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.et_username))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /** 测试Activity启动后，密码输入框可见 */
    @Test
    fun onLaunch_passwordInputIsVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.et_password))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /** 测试Activity启动后，验证码输入框可见 */
    @Test
    fun onLaunch_captchaInputIsVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.et_captcha))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /** 测试Activity启动后，登录按钮可见 */
    @Test
    fun onLaunch_loginButtonIsVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_login))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /** 测试Activity启动后，验证码图片区域可见 */
    @Test
    fun onLaunch_captchaImageAreaIsVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.fl_captcha))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    // ==================== 表单输入交互测试 ====================

    /** 测试输入用户名，验证输入框文本更新 */
    @Test
    fun typeUsername_updatesEditText() {
        Espresso.onView(ViewMatchers.withId(R.id.et_username))
            .perform(ViewActions.typeText("testuser"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.et_username))
            .check(ViewAssertions.matches(ViewMatchers.withText(CoreMatchers.containsString("testuser"))))
    }

    /** 测试输入密码，验证输入框文本更新 */
    @Test
    fun typePassword_updatesEditText() {
        Espresso.onView(ViewMatchers.withId(R.id.et_password))
            .perform(ViewActions.typeText("password123"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.et_password))
            .check(ViewAssertions.matches(ViewMatchers.withText(CoreMatchers.containsString("password123"))))
    }

    /** 测试输入验证码，验证输入框文本更新 */
    @Test
    fun typeCaptcha_updatesEditText() {
        Espresso.onView(ViewMatchers.withId(R.id.et_captcha))
            .perform(ViewActions.typeText("1234"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.et_captcha))
            .check(ViewAssertions.matches(ViewMatchers.withText(CoreMatchers.containsString("1234"))))
    }

    // ==================== 登录按钮状态测试 ====================

    /** 测试初始状态下登录按钮禁用（所有输入为空） */
    @Test
    fun onLaunch_loginButtonIsDisabled() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_login))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isEnabled())))
    }

    /** 测试仅输入用户名时登录按钮禁用 */
    @Test
    fun onlyUsername_loginButtonIsDisabled() {
        Espresso.onView(ViewMatchers.withId(R.id.et_username))
            .perform(ViewActions.typeText("testuser"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.btn_login))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isEnabled())))
    }

    /** 测试输入所有字段后登录按钮启用 */
    @Test
    fun allFieldsFilled_loginButtonIsEnabled() {
        Espresso.onView(ViewMatchers.withId(R.id.et_username))
            .perform(ViewActions.typeText("testuser"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.et_password))
            .perform(ViewActions.typeText("password123"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.et_captcha))
            .perform(ViewActions.typeText("1234"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.btn_login))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    // ==================== 验证码区域交互测试 ====================

    /** 测试点击验证码图片区域触发刷新（不崩溃） */
    @Test
    fun clickCaptchaArea_doesNotCrash() {
        Espresso.onView(ViewMatchers.withId(R.id.fl_captcha))
            .perform(ViewActions.click())
    }

    // ==================== 辅助方法 ====================

    /** 自定义ViewAction：强制启用View */
    private fun forceEnable(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isEnabled()
            }

            override fun getDescription(): String {
                return "force enable view"
            }

            override fun perform(uiController: UiController?, view: View?) {
                view?.isEnabled = true
            }
        }
    }
}
