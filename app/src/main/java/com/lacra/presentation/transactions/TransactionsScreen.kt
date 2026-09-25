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
package com.lacra.presentation.transactions

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lacra.R
import com.lacra.base.ObserveEffects
import com.lacra.domain.createtransaction.models.PendingTransactionModel
import com.lacra.domain.createtransaction.models.getCommodityShortText
import com.lacra.domain.transactions.models.TransactionAction
import com.lacra.domain.transactions.models.TransactionModel
import com.lacra.domain.transactions.models.TransactionType
import com.lacra.domain.transactions.models.TransactionsState
import com.lacra.domain.transactions.models.getCommodityShortText
import com.lacra.extensions.millisToLocalDateTime
import com.lacra.extensions.toFormattedDateString
import com.lacra.extensions.toShortFormattedDateString
import com.lacra.navigation.Screens
import com.lacra.presentation.createtransaction.geodata.FarmGeoDataActivity
import com.lacra.presentation.main.components.EmptyState
import com.lacra.presentation.main.components.SearchFilterBar
import com.lacra.presentation.main.components.TabBar
import com.lacra.presentation.main.components.TabItem
import com.lacra.presentation.main.components.Toolbar
import com.lacra.presentation.main.components.TransactionItem
import com.lacra.presentation.notifications.NotificationsActivity
import com.lacra.presentation.settings.components.DialogButtonsItem
import com.lacra.presentation.transactions.transactiondetails.TransactionDetailsActivity
import com.lacra.presentation.transactions.transactiondetails.components.BaseDialog
import com.lacra.presentation.ui.baseScreen.MainIconButton
import com.lacra.presentation.ui.theme.TextStyleBodyS
import com.lacra.presentation.ui.theme.TextStyleMediumM
import com.lacra.presentation.ui.theme.LacraTraceTheme
import com.lacra.utils.getResult
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        TransactionsScreen(
            modifier = Modifier.fillMaxSize(),
            navController = rememberNavController(),
            haveUnreadNotifications = false,
            viewModel = null,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SimpleDateFormat")
@Composable
fun TransactionsScreen(
    modifier: Modifier,
    navController: NavHostController,
    haveUnreadNotifications: Boolean,
    viewModel: TransactionsViewModel? = koinViewModel(),
) {

    val tabs = TransactionsTab.entries.map {
        TabItem(title = stringResource(it.titleResId), icon = it.icon)
    }

    val binding = viewModel?.observeViewBinding() ?: TransactionsContract.Binding()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel?.setEvent(TransactionsContract.Event.Refresh(binding.currentTab))
            }
        }
    )

    if (viewModel != null) {
        ObserveEffects(viewModel) { effect ->
            when (effect) {
                is TransactionsContract.Effect.NavigateTransactionDetails -> {
                    TransactionDetailsActivity.openTransactionDetails(context, launcher, effect.transaction)
                }
                is TransactionsContract.Effect.NavigateAddGeolocation -> {
                    FarmGeoDataActivity.openFarmGeoData(context, launcher, effect.transaction)
                }
                is TransactionsContract.Effect.NavigateNotifications -> {
                    NotificationsActivity.openNotifications(navController.context, launcher)
                }
            }
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        viewModel?.setEvent(TransactionsContract.Event.OnCreate)
    }

    if (navController.getResult<Boolean>("createTransactionResult") == true) {
        viewModel?.setEvent(TransactionsContract.Event.Refresh(binding.currentTab))
    }

    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val tab = TransactionsTab.entries[page]
            viewModel?.setEvent(TransactionsContract.Event.TabChanged(tab))
        }
    }

    var showCalendar by remember { mutableStateOf(false) }
    val datePickerState = rememberDateRangePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= Calendar.getInstance().timeInMillis
            }
        }
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Toolbar(
            title = stringResource(R.string.transactions_title),
            iconRes = if (haveUnreadNotifications) R.drawable.ic_notification_dot else R.drawable.ic_notification,
        ) {
            viewModel?.setEvent(TransactionsContract.Event.NotificationsClicked)
        }

        SearchFilterBar(
            modifier = Modifier.padding(all = 16.dp),
            query = binding.query ?: "",

            filterIsActive = datePickerState.selectedStartDateMillis != null,
            onSearch = {
                viewModel?.setEvent(TransactionsContract.Event.QueryChanged(it))
            },
            onFilterClick = {
                showCalendar = true
            }
        )

        TabBar(
            pagerState = pagerState,
            tabs = tabs,
        )

        Box {
            HorizontalPager(
                state = pagerState,
            ) { index ->
                val tab = TransactionsTab.entries[index]
                val tabState = binding.tabStates[tab]
                val pendingTransactions = binding.pendingTransactions[tab]

                TransactionPage(
                    tabState = tabState!!,
                    pendingTransactions = pendingTransactions!!,
                    onRefresh = {
                        viewModel?.setEvent(TransactionsContract.Event.Refresh(tab))
                    },
                    onLoadMore = {
                        viewModel?.setEvent(TransactionsContract.Event.NextPage(tab))
                    },
                    onTransactionClick = {
                        viewModel?.setEvent(TransactionsContract.Event.TransactionClicked(it))
                    },
                    onAddLocationClick = {
                        viewModel?.setEvent(TransactionsContract.Event.AddGeoDataClicked(it))
                    },
                    onAddTransactionClick = {
                        navController.navigate(Screens.CreateTransaction.route)
                    }
                )
            }

            val tab = TransactionsTab.entries[pagerState.currentPage]
            val tabState = binding.tabStates[tab]

            if (tabState is TransactionsState.Success || tabState is TransactionsState.End) {
                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    shape = FloatingActionButtonDefaults.smallShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.loweredElevation(),
                    onClick = {
                        navController.navigate(Screens.CreateTransaction.route)
                    },
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.ic_add_transaction),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
    }

    if (showCalendar) {
        val startDate = datePickerState.selectedStartDateMillis?.millisToLocalDateTime()
        val endDate = datePickerState.selectedEndDateMillis?.millisToLocalDateTime()?.plusDays(1)?.minusSeconds(1)

        var rangeText = ""

        if (startDate != null && endDate != null) {
            rangeText = startDate.toShortFormattedDateString() + " - " + endDate.toShortFormattedDateString()

        } else if (startDate != null) {
            rangeText = startDate.toShortFormattedDateString()
        }

        datePickerState.displayedMonthMillis = datePickerState.selectedStartDateMillis ?: Calendar.getInstance().timeInMillis

        BaseDialog(
            title = stringResource(R.string.select_dates),
            onDismiss = { showCalendar = false }
        ) {
            if (startDate != null) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    text = rangeText,
                    style = TextStyleMediumM,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            DateRangePicker(
                modifier = Modifier.height(500.dp),
                state = datePickerState,
                title = null,
                headline = null,
                showModeToggle = false,
                colors = DatePickerDefaults.colors().copy(
                    dayInSelectionRangeContentColor = MaterialTheme.colorScheme.onSecondary,
                    dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.secondary,
                )
            )

            DialogButtonsItem(
                actionButtonTitle = stringResource(R.string.confirm),
                actionButtonBackgroundColor = MaterialTheme.colorScheme.primary,
                actionButtonTitleColor = MaterialTheme.colorScheme.onPrimary,
                secondButtonTitle = stringResource(R.string.clear),
                onActionClick = {
                    showCalendar = false
                    viewModel?.setEvent(TransactionsContract.Event.DatesChanged(startDate, endDate))
                },
                onSecondClick = {
                    datePickerState.setSelection(null, null)
                    viewModel?.setEvent(TransactionsContract.Event.DatesChanged())
                    showCalendar = false
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionPage(
    tabState: TransactionsState,
    pendingTransactions: List<PendingTransactionModel>,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onTransactionClick: (TransactionModel) -> Unit = {},
    onAddLocationClick: (TransactionModel) -> Unit = {},
    onAddTransactionClick: () -> Unit = {},
) {
    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = tabState is TransactionsState.Reloading,
        onRefresh = onRefresh,
    ) {
        when (tabState) {
            TransactionsState.Empty -> {
                EmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    iconRes = R.drawable.ic_empty,
                    title = stringResource(R.string.no_transactions),
                    description = stringResource(R.string.no_transactions_description),
                ) {
                    MainIconButton(
                        modifier = Modifier.padding(top = 24.dp),
                        iconRes = R.drawable.ic_add_transaction,
                        title = stringResource(R.string.add_transaction),
                        onClick = onAddTransactionClick,
                    )
                }
            }
            TransactionsState.Reloading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.surface)
                )
            }
            is TransactionsState.PageLoading -> {
                TransactionsList(
                    listState = listState,
                    pendingTransactions = pendingTransactions,
                    transactions = tabState.transactions,
                    isLoading = true,
                    onTransactionClick = onTransactionClick,
                    onAddLocationClick = onAddLocationClick,
                )
            }
            is TransactionsState.End -> {
                TransactionsList(
                    listState = listState,
                    pendingTransactions = pendingTransactions,
                    transactions = tabState.transactions,
                    isEnd = true,
                    onTransactionClick = onTransactionClick,
                    onAddLocationClick = onAddLocationClick,
                )
            }
            is TransactionsState.Success -> {
                TransactionsList(
                    listState = listState,
                    pendingTransactions = pendingTransactions,
                    transactions = tabState.transactions,
                    onLoadMore = onLoadMore,
                    onTransactionClick = onTransactionClick,
                    onAddLocationClick = onAddLocationClick,
                )
            }
            is TransactionsState.Error -> {
                EmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    iconRes = R.drawable.ic_empty,
                    title = stringResource(R.string.error),
                    description = tabState.errorMessage
                )
            }
        }
    }
}

@Composable
fun TransactionsList(
    listState: LazyListState,
    pendingTransactions: List<PendingTransactionModel>,
    transactions: List<TransactionModel>,
    isLoading: Boolean = false,
    isEnd: Boolean = false,
    onLoadMore: () -> Unit = {},
    onPendingTransactionClick: (PendingTransactionModel) -> Unit = {},
    onTransactionClick: (TransactionModel) -> Unit = {},
    onAddLocationClick: (TransactionModel) -> Unit = {},
) {
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val totalItems = listState.layoutInfo.totalItemsCount
            val nearEnd = lastVisibleIndex >= totalItems - 1
            nearEnd && !isLoading
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { shouldLoadMore }
            .distinctUntilChanged()
            .collect { load ->
                if (load) onLoadMore()
            }
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(pendingTransactions) {
            val transaction = it.transactionModel
            val icon = if (transaction.action == TransactionAction.Buying) {
                R.drawable.ic_status_buy
            } else {
                R.drawable.ic_status_sell
            }
            TransactionItem(
                iconRes = icon,
                title = transaction.getCommodityShortText() ?: "",
                description = stringResource(R.string.offline_transaction),
                onClick = { onPendingTransactionClick(it) },
            )
        }

        items(
            items = transactions,
            key = { it.id }
        ) {
            val icon = if (it.action == TransactionAction.Buying) {
                R.drawable.ic_status_buy
            } else {
                R.drawable.ic_status_sell
            }
            TransactionItem(
                iconRes = icon,
                title = it.getCommodityShortText(),
                description = it.createdDate.toFormattedDateString(),
                status = it.status,
                showAddLocation = it.type == TransactionType.Producer && it.locationProvider == null,
                onClick = { onTransactionClick(it) },
                onAddLocationClick = { onAddLocationClick(it) }
            )
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        if (isEnd) {
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    text = stringResource(R.string.end),
                    style = TextStyleBodyS,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}