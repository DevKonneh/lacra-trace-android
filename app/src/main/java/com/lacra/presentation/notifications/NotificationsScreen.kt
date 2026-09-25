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
package com.lacra.presentation.notifications

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.lacra.domain.notifications.models.NotificationModel
import com.lacra.domain.notifications.models.NotificationType
import com.lacra.domain.notifications.models.NotificationsState
import com.lacra.domain.transactions.models.TransactionModel
import com.lacra.domain.transactions.models.getCommodityFullText
import com.lacra.extensions.findActivity
import com.lacra.extensions.toRelativeFormattedDateString
import com.lacra.presentation.main.components.EmptyState
import com.lacra.presentation.main.components.TabBar
import com.lacra.presentation.main.components.TabItem
import com.lacra.presentation.main.components.Toolbar2
import com.lacra.presentation.notifications.components.NotificationItemBase
import com.lacra.presentation.transactions.transactiondetails.TransactionDetailsActivity
import com.lacra.presentation.ui.theme.TextStyleBodyS
import com.lacra.presentation.ui.theme.LacraTraceTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        NotificationsScreen(
            modifier = Modifier.fillMaxSize(),
            navController = rememberNavController(),
            viewModel = null,
        )
    }
}

@Composable
fun NotificationsScreen(
    modifier: Modifier,
    navController: NavHostController,
    viewModel: NotificationsViewModel? = koinViewModel(),
) {
    val binding = viewModel?.observeViewBinding() ?: NotificationsContract.Binding()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val activity = context.findActivity()
                activity.setResult(Activity.RESULT_OK)
            }
        }
    )

    if (viewModel != null) {
        ObserveEffects(viewModel) { effect ->
            when (effect) {
                is NotificationsContract.Effect.NavigateTransactionDetails -> {
                    TransactionDetailsActivity.openTransactionDetails(context, launcher, effect.transaction)
                }
            }
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        viewModel?.setEvent(NotificationsContract.Event.OnCreate)
    }

    Column(modifier = modifier) {
        Toolbar2(
            navController = navController,
            title = stringResource(R.string.notifications),
        )

        val tabs = NotificationsTab.entries.mapIndexed { index, tab ->
            TabItem(title = stringResource(tab.tabNameRes))
        }
        val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                val tab = NotificationsTab.entries[page]
                viewModel?.setEvent(NotificationsContract.Event.TabChanged(tab))
            }
        }

        TabBar(
            pagerState = pagerState,
            tabs = tabs,
        )

        HorizontalPager(state = pagerState) { index ->
            val tab = NotificationsTab.entries[index]
            val tabState = binding.tabStates[tab]

            NotificationPage(
                tabState = tabState!!,
                onRefresh = {
                    viewModel?.setEvent(NotificationsContract.Event.Refresh(tab))
                },
                onLoadMore = {
                    viewModel?.setEvent(NotificationsContract.Event.NextPage(tab))
                },
                onDetailsClick = {
                    viewModel?.setEvent(NotificationsContract.Event.DetailsClicked(it))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPage(
    tabState: NotificationsState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onDetailsClick: (TransactionModel) -> Unit = {},
) {
    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = tabState is NotificationsState.Reloading,
        onRefresh = onRefresh,
    ) {
        when (tabState) {
            NotificationsState.Empty -> {
                EmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    iconRes = R.drawable.ic_empty,
                    title = stringResource(R.string.all_caught_up),
                    description = "No new messages or requests for now.\n\nWe’ll notify you if something needs your attention."
                )
            }
            NotificationsState.Reloading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.surface)
                )
            }
            is NotificationsState.PageLoading -> {
                NotificationsList(
                    listState = listState,
                    notifications = tabState.notifications,
                    isLoading = true,
                    onDetailsClick = onDetailsClick,
                )
            }
            is NotificationsState.End -> {
                NotificationsList(
                    listState = listState,
                    notifications = tabState.notifications,
                    isEnd = true,
                    onDetailsClick = onDetailsClick,
                )
            }
            is NotificationsState.Success -> {
                NotificationsList(
                    listState = listState,
                    notifications = tabState.notifications,
                    onLoadMore = onLoadMore,
                    onDetailsClick = onDetailsClick,
                )
            }
            is NotificationsState.Error -> {
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
fun NotificationsList(
    listState: LazyListState,
    notifications: List<NotificationModel>,
    isLoading: Boolean = false,
    isEnd: Boolean = false,
    onLoadMore: () -> Unit = {},
    onDetailsClick: (TransactionModel) -> Unit = {},
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
        items(
            items = notifications,
            key = { it.id }
        ) {

            var onClick: (() -> Unit)? = null

            if (it.transaction != null) {
                if (it.type == NotificationType.TransactionPending ||
                    it.type == NotificationType.GeoDataMissing) {
                    onClick = { onDetailsClick(it.transaction) }
                }
            }

            val title = when (it.type) {
                NotificationType.TransactionPending -> stringResource(R.string.transaction_approval_request)
                NotificationType.TransactionAccepted -> stringResource(R.string.transaction_accepted)
                NotificationType.TransactionRejected -> stringResource(R.string.transaction_rejected)
                NotificationType.TransactionExpired -> stringResource(R.string.transaction_expired)
                NotificationType.GeoDataMissing -> stringResource(R.string.missing_location_request)
                NotificationType.GeoDataUpdated -> stringResource(R.string.location_updated)
                null -> ""
            }

            NotificationItemBase(
                title = title,
                description = it.transaction?.getCommodityFullText() ?: "",
                date = it.createdDate.toRelativeFormattedDateString(),
                onDetailsClick = onClick,
            )
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        if (isEnd) {
            item {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    text = stringResource(R.string.end),
                    style = TextStyleBodyS,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}