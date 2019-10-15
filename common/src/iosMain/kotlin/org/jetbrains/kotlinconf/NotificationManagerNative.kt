package org.jetbrains.kotlinconf

import io.ktor.util.date.*
import kotlinx.cinterop.*
import kotlinx.coroutines.*
import org.jetbrains.kotlinconf.storage.*
import platform.Foundation.*
import platform.UserNotifications.*
import kotlin.coroutines.*
import kotlin.native.concurrent.*

actual class NotificationManager actual constructor(context: ApplicationContext) {
    private val center = UNUserNotificationCenter.currentNotificationCenter()

    actual suspend fun isEnabled(): Boolean = true

    actual suspend fun requestPermission(): Boolean = suspendCancellableCoroutine {
        center.requestAuthorizationWithOptions(UNAuthorizationOptionAlert) { allowed, error ->
            if (error != null) {
                it.resumeWithException(error.asException())
            } else {
                it.resume(allowed)
            }
        }
    }

    actual suspend fun schedule(
        sessionData: SessionData
    ): String? {
        val title = sessionData.title
        val delay = (sessionData.startsAt.timestamp - GMTDate().timestamp) / 1000.0
        if (delay <= 0) {
            return null
        }

        val body = "Starts in 15 minutes"

        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(body)
        }

        val date = NSDate.dateWithTimeIntervalSinceNow(delay)

        val componentsSet =
            (NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay or NSCalendarUnitHour or NSCalendarUnitMinute or NSCalendarUnitSecond)

        val triggerDate = NSCalendar.currentCalendar.components(
            componentsSet, date
        )

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            triggerDate, repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(title, content, trigger).freeze()
        val withCompletionHandler: (NSError?) -> Unit = { error: NSError? -> }.freeze()

        center.addNotificationRequest(request, withCompletionHandler)
        return title
    }

    actual fun cancel(sessionData: SessionData) {
        center.removePendingNotificationRequestsWithIdentifiers(listOf(sessionData.title))
    }
}

internal fun GMTDate.toNSDateComponents(): NSDateComponents = NSDateComponents().apply {
    setYear(this@toNSDateComponents.year.convert())
    setMonth(this@toNSDateComponents.month.ordinal.convert())
    setDay(this@toNSDateComponents.dayOfMonth.convert())
    setHour(this@toNSDateComponents.hours.convert())
    setMinute(this@toNSDateComponents.minutes.convert())
    setSecond(this@toNSDateComponents.seconds.convert())
}

internal class NativeException(val error: NSError) : Exception()

internal fun NSError.asException(): NativeException = NativeException(this)
