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
package lr.gov.lacra.trace.presentation.transactions.transactiondetails

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import lr.gov.lacra.trace.R
import lr.gov.lacra.trace.base.ObserveEffects
import lr.gov.lacra.trace.domain.transactions.models.TraceabilityStatus
import lr.gov.lacra.trace.domain.transactions.models.TransactionModel
import lr.gov.lacra.trace.domain.transactions.models.TransactionsState
import lr.gov.lacra.trace.navigation.Screens
import lr.gov.lacra.trace.presentation.main.components.EmptyState
import lr.gov.lacra.trace.presentation.main.components.LoadingState
import lr.gov.lacra.trace.presentation.main.components.Toolbar2
import lr.gov.lacra.trace.presentation.settings.components.DialogButton
import lr.gov.lacra.trace.presentation.settings.components.DialogButtonsItem
import lr.gov.lacra.trace.presentation.transactions.transactiondetails.components.BaseDialog
import lr.gov.lacra.trace.presentation.transactions.transactiondetails.components.DialogTextItem
import lr.gov.lacra.trace.presentation.transactions.transactiondetails.components.SupplierHistoryItem
import lr.gov.lacra.trace.presentation.transactions.transactiondetails.components.TraceabilityStatusesDialog
import lr.gov.lacra.trace.presentation.ui.baseScreen.LoadingButton
import lr.gov.lacra.trace.presentation.ui.theme.TextStyleBodyS
import lr.gov.lacra.trace.presentation.ui.theme.LacraTraceTheme
import lr.gov.lacra.trace.utils.toJsonArgs
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        SuppliersHistoryScreen(
            modifier = Modifier.fillMaxSize(),
            navController = rememberNavController(),
            viewModel = null,
            transactionModel = null,
            isInitial = true,
        )
    }
}

private sealed class SuppliersHistoryDialog {
    data object None : SuppliersHistoryDialog()
    data class TraceabilityStatusDialog(val status: TraceabilityStatus) : SuppliersHistoryDialog()
    data class DownloadGeoJsonDialog(val description: String) : SuppliersHistoryDialog()
}

@Composable
fun SuppliersHistoryScreen(
    modifier: Modifier,
    navController: NavHostController,
    viewModel: SuppliersHistoryViewModel? = koinViewModel(),
    transactionModel: TransactionModel?,
    isInitial: Boolean,
) {
    val binding = viewModel?.observeViewBinding() ?: SuppliersHistoryContract.Binding()
    val context = LocalContext.current

    val createZipFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel?.setEvent(SuppliersHistoryContract.Event.SaveToFile(context, uri))
        }
    }

    val createCSVFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/csv")
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel?.setEvent(SuppliersHistoryContract.Event.SaveToFile(context, uri))
        }
    }

    val createTextFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel?.setEvent(SuppliersHistoryContract.Event.SaveToFile(context, uri))
        }
    }

    var downloadLocationsLoading by remember { mutableStateOf(false) }
    var requestLocationsLoading by remember { mutableStateOf(false) }
    var dialog by remember { mutableStateOf<SuppliersHistoryDialog>(SuppliersHistoryDialog.None) }

    if (viewModel != null) {
        ObserveEffects(viewModel) { effect ->
            when (effect) {
                is SuppliersHistoryContract.Effect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
                is SuppliersHistoryContract.Effect.TextDataDownloaded -> {
                    createTextFileLauncher.launch(effect.filename)
                }
                is SuppliersHistoryContract.Effect.ZipDataDownloaded -> {
                    createZipFileLauncher.launch(effect.filename)
                }
                is SuppliersHistoryContract.Effect.CSVDataDownloaded -> {
                    createCSVFileLauncher.launch(effect.filename)
                }
                is SuppliersHistoryContract.Effect.NavigateSupplier -> {
                    navController.navigate(
                        Screens.SuppliersHistory.putArgs(Screens.ARG_KEY_JSON to effect.transaction.toJsonArgs())
                    )
                }
                is SuppliersHistoryContract.Effect.NavigateInitialSupplier -> {
                    navController.popBackStack(Screens.InitialSuppliersHistory.route,false, false)
                }
                is SuppliersHistoryContract.Effect.ToggleDataLoader -> {
                    downloadLocationsLoading = effect.isLoading
                }
                is SuppliersHistoryContract.Effect.ToggleRequestLoader -> {
                    requestLocationsLoading = effect.isLoading
                }
            }
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        transactionModel?.let { viewModel?.setEvent(SuppliersHistoryContract.Event.OnCreate(it, isInitial = isInitial)) }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Toolbar2(
            navController = navController,
            title = binding.title,
            iconRes = R.drawable.ic_download,
            onIconClick = {
                dialog = SuppliersHistoryDialog.DownloadGeoJsonDialog(binding.dialogDescription)
            }
        )

        Box(
            modifier = Modifier.weight(1f)
        ) {
            SuppliersPage(
                viewModel = viewModel,
                state = binding.state,
                onRefresh = {
                    viewModel?.setEvent(SuppliersHistoryContract.Event.Refresh)
                },
                onLoadMore = {
                    viewModel?.setEvent(SuppliersHistoryContract.Event.NextPage)
                },
                onTraceabilityStatusClick = {
                    if (it != null) {
                        dialog = SuppliersHistoryDialog.TraceabilityStatusDialog(it)
                    }
                }
            )
        }

        if (binding.showInitialActionButtons) {
            InitialScreenActionButtons(
                transactionModel = transactionModel,
                requestLocationsLoading = requestLocationsLoading,
                requestLocationsClick = {
                    viewModel?.setEvent(SuppliersHistoryContract.Event.RequestLocations)
                }
            )
        }

        if (binding.showActionButtons) {
            ActionButtons(viewModel)
        }
    }

    HandleDialog(
        dialog = dialog,
        onDismiss = {
            dialog = SuppliersHistoryDialog.None
        },
        viewModel = viewModel,
    )

    if (downloadLocationsLoading) {
        LoadingState()
    }
}

@Composable
private fun HandleDialog(
    dialog: SuppliersHistoryDialog,
    onDismiss: () -> Unit,
    viewModel: SuppliersHistoryViewModel?,
) {
    when (dialog) {
        is SuppliersHistoryDialog.None -> {}
        is SuppliersHistoryDialog.TraceabilityStatusDialog -> {
            TraceabilityStatusesDialog(
                status = dialog.status,
                onDismiss = onDismiss
            )
        }
        is SuppliersHistoryDialog.DownloadGeoJsonDialog -> {
            BaseDialog(title = stringResource(R.string.download_locations_dialog)) {
                DialogTextItem(
                    title = dialog.description,
                )
                DialogButtonsItem(
                    actionButtonTitle = stringResource(R.string.download),
                    actionButtonBackgroundColor = MaterialTheme.colorScheme.primary,
                    actionButtonTitleColor = MaterialTheme.colorScheme.onPrimary,
                    onActionClick = {
                        onDismiss()
                        viewModel?.setEvent(SuppliersHistoryContract.Event.DownloadGeoJson)
                    },
                    onSecondClick = onDismiss,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuppliersPage(
    viewModel: SuppliersHistoryViewModel?,
    state: TransactionsState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onTraceabilityStatusClick: ((TraceabilityStatus?) -> Unit)? = null,
) {
    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = state is TransactionsState.Reloading,
        onRefresh = onRefresh,
    ) {
        when (state) {
            TransactionsState.Empty -> {
                EmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    iconRes = R.drawable.ic_empty,
                    title = stringResource(R.string.no_history),
                    description = ""
                )
            }
            TransactionsState.Reloading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.surface)
                )
            }
            is TransactionsState.PageLoading -> {
                SuppliersList(
                    listState = listState,
                    viewModel = viewModel,
                    transactions = state.transactions,
                    isLoading = true,
                    onTraceabilityStatusClick = onTraceabilityStatusClick,
                )
            }
            is TransactionsState.End -> {
                SuppliersList(
                    listState = listState,
                    viewModel = viewModel,
                    transactions = state.transactions,
                    isEnd = true,
                    onTraceabilityStatusClick = onTraceabilityStatusClick,
                )
            }
            is TransactionsState.Success -> {
                SuppliersList(
                    listState = listState,
                    viewModel = viewModel,
                    transactions = state.transactions,
                    onLoadMore = onLoadMore,
                    onTraceabilityStatusClick = onTraceabilityStatusClick,
                )
            }
            is TransactionsState.Error -> {
                EmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    iconRes = R.drawable.ic_empty,
                    title = stringResource(R.string.error),
                    description = state.errorMessage
                )
            }
        }
    }
}

@Composable
private fun SuppliersList(
    listState: LazyListState,
    viewModel: SuppliersHistoryViewModel?,
    transactions: List<TransactionModel>,
    isLoading: Boolean = false,
    isEnd: Boolean = false,
    onLoadMore: () -> Unit = {},
    onTraceabilityStatusClick: ((TraceabilityStatus?) -> Unit)? = null,
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
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        items(
            items = transactions,
            key = { it.id }
        ) {
            var onClick: (() -> Unit)? = null
            if (it.seller != null) {
                onClick = {
                    viewModel?.setEvent(SuppliersHistoryContract.Event.OnSupplierClick(it))
                }
            }

            SupplierHistoryItem(
                transaction = it,
                onTraceabilityStatusClick = onTraceabilityStatusClick,
                onClick = onClick,
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

@Composable
private fun InitialScreenActionButtons(
    transactionModel: TransactionModel?,
    requestLocationsLoading: Boolean,
    requestLocationsClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        if (transactionModel?.traceability == TraceabilityStatus.Incomplete ||
            transactionModel?.traceability == TraceabilityStatus.Partial) {
            LoadingButton(
                modifier = Modifier.fillMaxWidth(),
                isLoading = requestLocationsLoading,
                title = stringResource(R.string.request_missing_locations),
                onClick = requestLocationsClick
            )
        }
    }
}

@Composable
private fun ActionButtons(
    viewModel: SuppliersHistoryViewModel?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DialogButton(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.back_to_supplier_history),
            titleColor = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.surface,
            onClick = {
                viewModel?.setEvent(SuppliersHistoryContract.Event.OnBackToInitialClick)
            },
        )
    }
}