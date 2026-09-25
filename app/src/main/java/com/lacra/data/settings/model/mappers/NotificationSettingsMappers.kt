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
package com.lacra.data.settings.model.mappers

import com.lacra.data.settings.model.entity.NotificationSettingEntity
import com.lacra.data.settings.model.response.NotificationSettings
import com.lacra.data.settings.model.response.NotificationSettingsResponse
import com.lacra.domain.notifications.models.NotificationType
import com.lacra.domain.settings.models.NotificationSettingsModel

fun NotificationSettingsResponse.toDomain(): List<NotificationSettingsModel> {
    return data.mapNotNull { it.toDomain() }
}

fun NotificationSettings.toDomain(): NotificationSettingsModel? {
    val type = NotificationType.entries.find { it.typeName == type }

    if (type == null) return null

    return NotificationSettingsModel(
        type = type,
        isEnabled = is_enabled
    )
}

fun NotificationSettingsModel.toData(): NotificationSettings {
    return NotificationSettings(
        type = type.typeName,
        is_enabled = isEnabled,
    )
}

fun NotificationSettingsModel.toEntity(): NotificationSettingEntity {
    return NotificationSettingEntity(
        type = type.typeName,
        isEnabled = isEnabled,
    )
}

fun NotificationSettingEntity.toDomain(): NotificationSettingsModel {
    val type = NotificationType.entries.find { it.typeName == type }

    return NotificationSettingsModel(
        type = type!!,
        isEnabled = isEnabled,
    )
}