package ru.netology.testing.uiautomator

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

const val SETTINGS_PACKAGE = "com.android.settings"
const val MODEL_PACKAGE = "ru.netology.testing.uiautomator"
const val TIMEOUT = 10000L

@RunWith(AndroidJUnit4::class)
class ChangeTextTest {

    private lateinit var device: UiDevice
    private val textToSet = "Netology"
    private val emptyText = "   "

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

    @Test
    fun testInternetSettings() {
        waitForPackage(SETTINGS_PACKAGE)
        // Исправлено: теперь используем By.res вместо UiSelector
        val settingsItem = device.wait(Until.findObject(By.res("android:id/title")), TIMEOUT)
        settingsItem?.click()
    }

    @Test
    fun testChangeText() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        device.wait(Until.hasObject(By.res(packageName, "userInput")), TIMEOUT)
        device.findObject(By.res(packageName, "userInput")).text = textToSet
        device.findObject(By.res(packageName, "buttonChange")).click()

        device.wait(Until.hasObject(By.res(packageName, "textToBeChanged")), TIMEOUT)
        val result = device.findObject(By.res(packageName, "textToBeChanged")).text
        assertEquals(textToSet, result)
    }

    @Test
    fun testSetEmptyString() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        device.wait(Until.hasObject(By.res(packageName, "textToBeChanged")), TIMEOUT)
        val initialText = device.findObject(By.res(packageName, "textToBeChanged")).text

        device.wait(Until.hasObject(By.res(packageName, "userInput")), TIMEOUT)
        device.findObject(By.res(packageName, "userInput")).text = emptyText
        device.findObject(By.res(packageName, "buttonChange")).click()

        val resultText = device.findObject(By.res(packageName, "textToBeChanged")).text
        assertEquals(initialText, resultText)
    }

    @Test
    fun testOpenTextInNewActivity() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        device.wait(Until.hasObject(By.res(packageName, "userInput")), TIMEOUT)
        val userInput = device.findObject(By.res(packageName, "userInput"))
        assertNotNull("Поле userInput не найдено", userInput)
        userInput.text = textToSet

        device.wait(Until.hasObject(By.res(packageName, "buttonActivity")), TIMEOUT)
        val button = device.findObject(By.res(packageName, "buttonActivity"))
        assertNotNull("Кнопка buttonActivity не найдена", button)
        button.click()

        val textFound = device.wait(Until.hasObject(By.res(packageName, "text")), TIMEOUT)

        if (textFound) {
            val resultOnSecondActivity = device.findObject(By.res(packageName, "text")).text
            assertEquals(textToSet, resultOnSecondActivity)
        } else {
            throw AssertionError("Текст на новой Activity не появился")
        }
    }
}






