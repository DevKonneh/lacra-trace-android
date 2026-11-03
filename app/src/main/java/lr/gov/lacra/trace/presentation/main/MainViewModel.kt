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
package lr.gov.lacra.trace.presentation.main

import com.google.firebase.messaging.FirebaseMessaging
import lr.gov.lacra.trace.base.BaseViewModel
import lr.gov.lacra.trace.base.CoreViewEvent
import lr.gov.lacra.trace.data.base.common.onError
import lr.gov.lacra.trace.data.base.common.onSuccess
import lr.gov.lacra.trace.data.notifications.repository.NotificationsRepository
import lr.gov.lacra.trace.domain.commodity.CommodityInteractor
import lr.gov.lacra.trace.domain.commodity.models.CommodityBalanceFilter
import lr.gov.lacra.trace.domain.commodity.models.CommodityFilter
import lr.gov.lacra.trace.domain.harvestseasons.HarvestSeasonsInteractor
import lr.gov.lacra.trace.domain.harvestseasons.models.HarvestSeasonFilter
import lr.gov.lacra.trace.domain.notifications.PushNotificationsInteractor
import lr.gov.lacra.trace.domain.settings.SettingsInteractor
import lr.gov.lacra.trace.providers.SharedPreferencesProvider

class MainViewModel(
    private val sharedPreferencesProvider: SharedPreferencesProvider,
    private val settingsInteractor: SettingsInteractor,
    private val pushNotificationsInteractor: PushNotificationsInteractor,
    private val commodityInteractor: CommodityInteractor,
    private val harvestSeasonsInteractor: HarvestSeasonsInteractor,
    private val notificationsRepository: NotificationsRepository,
) : BaseViewModel<MainContract.Binding>() {

    override fun createBinding(): MainContract.Binding {
        return MainContract.Binding()
    }

    override fun handleEvents(event: CoreViewEvent) {
        super.handleEvents(event)
        when (event) {
            is MainContract.Event.OnCreate -> onCreate()
            is MainContract.Event.PostNotificationPermissionChanged -> postNotificationPermissionChanged(event.isGranted)
        }
    }

    override fun copyBinding(binding: MainContract.Binding): MainContract.Binding {
        return binding.copy()
    }

    private fun onCreate() {
        checkUnreadNotifications()
        updateAccountInfo()
        updateFCMToken()
        updateCommodities()
        updateHarvestSeasons()
        updateBalances()
    }

    private fun postNotificationPermissionChanged(isGranted: Boolean) {
        if (sharedPreferencesProvider.isNotificationsAllowed() && !isGranted) {
            sharedPreferencesProvider.setNotificationsAllowed(false)
        }
    }

    private fun checkUnreadNotifications() {
        launch {
            val notifications = notificationsRepository.getNotificationsFromDB(null)
            updateBinding { b ->
                b.haveUnreadNotifications = notifications.isNotEmpty()
            }

            notificationsRepository.getNotifications(
                search = null,
                page = 1,
                pageSize = 1,
                types = null,
                status = "pending",
                createdAtFrom = null,
                createdAtTo = null,
                userId = null,
            )
                .onSuccess {
                    updateBinding { b ->
                        b.haveUnreadNotifications = !it?.second.isNullOrEmpty()
                    }
                }
        }
    }

    private fun updateAccountInfo() {
        if (sharedPreferencesProvider.getAccount() == null) {
            launch {
                settingsInteractor.getAccountInfo()
                    .onSuccess { account ->
                        account?.let { sharedPreferencesProvider.setAccount(it) }
                    }
            }
        }
    }

    private fun updateFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                if (token != sharedPreferencesProvider.getFCMToken()) {
                    launch {
                        pushNotificationsInteractor.addPushNotificationDevice(token)
                            .onSuccess {
                                sharedPreferencesProvider.setFCMToken(token)
                            }
                            .onError {
                                it.printStackTrace()
                            }
                    }
                }
            }
    }

    private fun updateCommodities() {
        launch {
            if (commodityInteractor.getCommoditiesFromDB().isEmpty()) {
                commodityInteractor.getCommodities(CommodityFilter())
                    .onSuccess {
                        commodityInteractor.updateCommoditiesDB(it)
                    }
            }
        }
    }

    private fun updateHarvestSeasons() {
        launch {
            harvestSeasonsInteractor.getHarvestSeasons(HarvestSeasonFilter())
        }
    }

    private fun updateBalances() {
        launch {
            commodityInteractor.getBalances(CommodityBalanceFilter())
        }
    }
}
