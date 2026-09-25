/*
 * Copyright (c) 2025 EFI (https://efi.int/)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.lacra

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import com.lacra.di.authModule
import com.lacra.di.commodityModule
import com.lacra.di.createTransactionsModule
import com.lacra.di.dataBaseModule
import com.lacra.di.geoDataModule
import com.lacra.di.mainModule
import com.lacra.di.networkModule
import com.lacra.di.notificationsModule
import com.lacra.di.providersModule
import com.lacra.di.settingsModule
import com.lacra.di.transactionsModule
import com.lacra.services.MyFirebaseMessagingService.Companion.PUSH_NOTIFICATIONS_CHANNEL_ID
import com.lacra.services.MyFirebaseMessagingService.Companion.PUSH_NOTIFICATIONS_CHANNEL_NAME
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.logger.Level

class LacraTraceApp : Application() {

    var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        // Initialize Koin
        GlobalContext.startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(applicationContext)
            modules(
                networkModule,
                dataBaseModule,
                providersModule,
                authModule,
                mainModule,
                transactionsModule,
                createTransactionsModule,
                commodityModule,
                geoDataModule,
                notificationsModule,
                settingsModule
            )
        }

        // Register activity lifecycle callbacks
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                currentActivity = activity
            }

            override fun onActivityStarted(activity: Activity) {
                currentActivity = activity
            }

            override fun onActivityResumed(activity: Activity) {
                currentActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            /* id = */ PUSH_NOTIFICATIONS_CHANNEL_ID,
            /* name = */ PUSH_NOTIFICATIONS_CHANNEL_NAME,
            /* importance = */ NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

} 