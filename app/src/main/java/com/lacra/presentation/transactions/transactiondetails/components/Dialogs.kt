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
package com.lacra.presentation.transactions.transactiondetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lacra.R
import com.lacra.domain.transactions.models.TraceabilityStatus
import com.lacra.domain.transactions.models.TransactionStatus
import com.lacra.domain.transactions.models.UserModel
import com.lacra.presentation.main.components.TraceabilityStatusView
import com.lacra.presentation.ui.theme.TextStyleBodyS
import com.lacra.presentation.ui.theme.TextStyleMediumM
import com.lacra.presentation.ui.theme.LacraTraceTheme

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        TraceabilityStatusesDialog()
        UserInfoDialog(
            userModel = UserModel(
                id = "",
                username = "#155796",
                email = "johnsmith@example.com",
                phone = "+237 125 26 23",
            )
        )
        TransactionStatusDialog(TransactionStatus.NoResponse)
    }
}

@Composable
fun TransactionStatusDialog(
    status: TransactionStatus,
    onDismiss: () -> Unit = {},
) {
    var title = ""
    var text = ""

    when (status) {
        TransactionStatus.Accepted -> {
            title = stringResource(R.string.accepted)
            text = stringResource(R.string.accepted_description)
        }
        TransactionStatus.Rejected -> {
            title = stringResource(R.string.rejected)
            text = stringResource(R.string.rejected_description)
        }
        TransactionStatus.Pending -> {
            title = stringResource(R.string.pending)
            text = stringResource(R.string.pending_description)
        }
        TransactionStatus.NoResponse -> {
            title = stringResource(R.string.no_response)
            text = stringResource(R.string.nor_response_description)
        }
        TransactionStatus.Recorded -> {
            title = stringResource(R.string.recorded)
            text = stringResource(R.string.recorded_description)
        }
        TransactionStatus.Automatic -> {
            title = stringResource(R.string.automatic)
            text = stringResource(R.string.automatic_description)
        }
    }

    BaseDialog(
        title = title,
        onDismiss = onDismiss,
    ) {
        DialogTextItem(
            title = text
        )
    }
}

@Composable
fun UserInfoDialog(
    title: String = stringResource(R.string.user_information),
    userModel: UserModel,
    onDismiss: () -> Unit = {},
) {
    BaseDialog(
        title = title,
        onDismiss = onDismiss,
    ) {
        if (userModel.username != null) {
            UserInfoItem(
                iconRes = R.drawable.ic_user,
                title = userModel.username,
                description = stringResource(R.string.username)
            )
        }
        if (userModel.email != null) {
            UserInfoItem(
                iconRes = R.drawable.ic_envelope,
                title = userModel.email,
                description = stringResource(R.string.email_address)
            )
        }
        if (userModel.phone != null) {
            UserInfoItem(
                iconRes = R.drawable.ic_phone,
                title = userModel.phone,
                description = stringResource(R.string.phone_number)
            )
        }
    }
}

@Composable
fun UserInfoItem(
    modifier: Modifier = Modifier,
    iconRes: Int,
    title: String,
    description: String,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            modifier = Modifier.size(28.dp),
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = title,
                style = TextStyleMediumM,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                modifier = modifier.fillMaxWidth(),
                text = description,
                style = TextStyleBodyS,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TraceabilityStatusesDialog(
    status: TraceabilityStatus? = null,
    onDismiss: () -> Unit = {},
) {
    BaseDialog(
        title = stringResource(R.string.traceability_status),
        onDismiss = onDismiss
    ) {
        if (status == null) {
            DialogTextItem(
                title = stringResource(R.string.traceability_status_description)
            )
            TraceabilityStatusItem(
                status = TraceabilityStatus.Full,
                title = getTraceabilityStatusDescription(TraceabilityStatus.Full),
            )
            TraceabilityStatusItem(
                status = TraceabilityStatus.Conditional,
                title = getTraceabilityStatusDescription(TraceabilityStatus.Conditional),
            )
            TraceabilityStatusItem(
                status = TraceabilityStatus.Partial,
                title = getTraceabilityStatusDescription(TraceabilityStatus.Partial),
            )
            TraceabilityStatusItem(
                status = TraceabilityStatus.Incomplete,
                title = getTraceabilityStatusDescription(TraceabilityStatus.Incomplete),
            )
        } else {
            TraceabilityStatusItem(
                status = status,
                title = getTraceabilityStatusDescription(status),
            )
        }
    }
}

@Composable
private fun getTraceabilityStatusDescription(status: TraceabilityStatus): String {
    return when(status) {
        TraceabilityStatus.Full -> stringResource(R.string.full_traceability_status_description)
        TraceabilityStatus.Conditional -> stringResource(R.string.conditional_traceability_status_description)
        TraceabilityStatus.Partial -> stringResource(R.string.partial_traceability_status_description)
        TraceabilityStatus.Incomplete -> stringResource(R.string.incomplete_traceability_status_description)
    }
}

@Composable
fun TraceabilityStatusItem(
    modifier: Modifier = Modifier,
    status: TraceabilityStatus,
    title: String,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {

        TraceabilityStatusView(status = status)

        Text(
            modifier = modifier.fillMaxWidth(),
            text = title,
            style = TextStyleBodyS,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DialogTextItem(
    modifier: Modifier = Modifier,
    title: String,
) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        text = title,
        style = TextStyleBodyS,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}