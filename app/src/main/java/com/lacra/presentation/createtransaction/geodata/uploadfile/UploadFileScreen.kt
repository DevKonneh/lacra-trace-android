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
package com.lacra.presentation.createtransaction.geodata.uploadfile

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lacra.R
import com.lacra.base.ObserveEffects
import com.lacra.domain.createtransaction.models.CreateTransactionModel
import com.lacra.domain.createtransaction.models.LocationProvider
import com.lacra.domain.transactions.models.TransactionModel
import com.lacra.extensions.findActivity
import com.lacra.presentation.createtransaction.components.CreateTransactionButton3
import com.lacra.presentation.createtransaction.components.CreateTransactionWarning
import com.lacra.presentation.createtransaction.components.FileItem
import com.lacra.presentation.createtransaction.geodata.FarmGeoDataActivity
import com.lacra.presentation.main.components.Toolbar2
import com.lacra.presentation.ui.baseScreen.MainButton
import com.lacra.presentation.ui.theme.TextStyleBodyXS
import com.lacra.presentation.ui.theme.LacraTraceTheme
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        UploadFileScreen(
            modifier = Modifier.fillMaxSize(),
            navController = rememberNavController(),
            viewModel = null,
        )
    }
}

@Composable
fun UploadFileScreen(
    modifier: Modifier,
    navController: NavHostController,
    transactionModel: TransactionModel? = null,
    createTransactionModel: CreateTransactionModel? = null,
    viewModel: UploadFileViewModel? = koinViewModel(),
) {
    val binding = viewModel?.observeViewBinding() ?: UploadFileContract.Binding()
    val context = LocalContext.current

    if (viewModel != null) {
        ObserveEffects(viewModel) { effect ->
            when (effect) {
                is UploadFileContract.Effect.FileConfirmed -> {
                    val activity = context.findActivity() as FarmGeoDataActivity
                    activity.setResult(
                        locationProvider = LocationProvider.File,
                        location = effect.location,
                        file = effect.file,
                    )
                    activity.finish()
                }
            }
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        if (createTransactionModel != null) {
            viewModel?.setEvent(UploadFileContract.Event.OnCreate(createTransactionModel.file))
        }
        if (transactionModel != null) {
            viewModel?.setEvent(UploadFileContract.Event.OnCreate2(transactionModel))
        }
    }

    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            viewModel?.setEvent(UploadFileContract.Event.OnFileChanged(context, uri))
        }
    )

    val mimeTypes = arrayOf(
        "application/geo+json",
        "text/csv",
        "application/gpx+xml",
        "application/vnd.google-earth.kmz",
        "application/geopackage+sqlite3",
        "*/*"
    )

    Column(modifier = modifier) {
        Toolbar2(
            navController = navController,
            title = stringResource(R.string.farm_geodata),
        )

        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CreateTransactionWarning(
                modifier = Modifier.padding(horizontal = 16.dp),
                iconRes = R.drawable.ic_information,
                title = stringResource(R.string.upload_file_description),
            )

            if (binding.fileChosen) {
                FileItem(
                    title = binding.fileName,
                    description = binding.fileDescription,
                    onEditClick = {
                        pickFileLauncher.launch(mimeTypes)
                    },
                    onDeleteClick = {
                        viewModel?.setEvent(UploadFileContract.Event.OnFileRemoved)
                    },
                )

            } else {
                CreateTransactionButton3(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .height(IntrinsicSize.Min),
                    iconRes = R.drawable.ic_upload,
                    title = stringResource(R.string.upload_file),
                    description = stringResource(R.string.upload_file_description_long)
                ) {
                    pickFileLauncher.launch(mimeTypes)
                }

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = stringResource(R.string.upload_file_description2),
                    style = TextStyleBodyXS,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            MainButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                isEnabled = binding.fileChosen,
                title = stringResource(R.string.confirm),
                onClick = {
                    viewModel?.setEvent(UploadFileContract.Event.OnConfirm(context))
                }
            )
        }
    }
}
