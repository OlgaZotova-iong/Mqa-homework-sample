package ru.netology.testing.uiautomator

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

const val SETTINGS_PACKAGE = "com.android.settings"
const val MODEL_PACKAGE = "ru.netology.testing.uiautomator"
const val TIMEOUT = 5000L

@RunWith(AndroidJUnit4::class)
class ChangeTextTest {

    private lateinit var device: UiDevice
    private val textToSet = "Netology"
    private val emptyText = "   " // Пробельные символы тоже считаем пустой строкой

    private fun waitForPackage(packageName: String) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(packageName)), TIMEOUT)
    }

    @Before
    fun beforeEachTest() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()
        val launcherPackage = device.launcherPackageName
        device.wait(Until.hasObject(By.pkg(launcherPackage)), TIMEOUT)
    }

    // --- Существующие тесты из задания ---

    @Test
    fun testInternetSettings() {
        waitForPackage(SETTINGS_PACKAGE)
        device.findObject(
            UiSelector().resourceId("android:id/title").instance(0)
        ).click()
    }

    @Test
    fun testChangeText() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        device.findObject(By.res(packageName, "userInput")).text = textToSet
        device.findObject(By.res(packageName, "buttonChange")).click()

        val result = device.findObject(By.res(packageName, "textToBeChanged")).text
        assertEquals(textToSet, result)
    }

    // --- НОВЫЕ ТЕСТЫ ---

    /**
     * Тест 1: Попытка установки пустой строки (или пробелов).
     * Текст в поле TextView не должен измениться.
     */
    @Test
    fun testSetEmptyString() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        // Запомним исходный текст
        val initialText = device.findObject(By.res(packageName, "textToBeChanged")).text

        // Вводим пустоту и жмем кнопку
        device.findObject(By.res(packageName, "userInput")).text = emptyText
        device.findObject(By.res(packageName, "buttonChange")).click()

        // Проверяем, что текст остался прежним (не обновился на пустой)
        val resultText = device.findObject(By.res(packageName, "textToBeChanged")).text
        assertEquals(initialText, resultText)
    }

    /**
     * Тест 2: Открытие текста в новой Activity.
     * Проверяем, что на втором экране отображается введенный нами текст.
     */
    @Test
    fun testOpenTextInNewActivity() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        // Вводим текст
        device.findObject(By.res(packageName, "userInput")).text = textToSet
        // Нажимаем кнопку открытия второй Activity
        device.findObject(By.res(packageName, "buttonActivity")).click()

        // Ждем появления элемента с ID "text" (это ID из activity_second.xml)
        device.wait(Until.hasObject(By.res(packageName, "text")), TIMEOUT)

        // Сравниваем текст на втором экране с тем, что вводили
        val resultOnSecondActivity = device.findObject(By.res(packageName, "text")).text
        assertEquals(textToSet, resultOnSecondActivity)
    }
}




