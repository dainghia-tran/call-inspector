package vn.dainghia.callinspector.ui.screen.home.settings.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Token
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.dainghia.callinspector.ui.theme.CallInspectorTheme

@Composable
fun SettingsValueItem(
    imageVector: ImageVector,
    settingsName: String,
    description: String,
    value: String?,
    modifier: Modifier = Modifier,
    onItemClicked: () -> Unit = {},
) {
    BaseSettingsItem(imageVector, settingsName, description, modifier, onItemClicked) {
        if (value != null) {
            Text(
                value,
                modifier = modifier.widthIn(max = 100.dp),
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SettingsSwitchItem(
    imageVector: ImageVector,
    settingsName: String,
    description: String,
    value: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    BaseSettingsItem(imageVector, settingsName, description, modifier, onItemClicked = {
        onCheckedChange(!value)
    }) {
        Switch(
            checked = value,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun BaseSettingsItem(
    imageVector: ImageVector,
    settingsName: String,
    description: String,
    modifier: Modifier = Modifier,
    onItemClicked: () -> Unit = {},
    valueContent: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .clickable { onItemClicked() }
            .padding(vertical = 20.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = imageVector, null, tint = MaterialTheme.colorScheme.onSurface)
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f)
        ) {
            Text(
                settingsName,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                description,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        valueContent()
    }
}

@Preview
@Composable
private fun SettingsValueItemPreview() {
    val settingsName = "Settings"
    val description = "This is description"
    CallInspectorTheme {
        Column {
            SettingsValueItem(
                imageVector = Icons.Filled.Public,
                settingsName = settingsName,
                description = description,
                value = "VN"
            )
            SettingsSwitchItem(
                imageVector = Icons.Filled.Token,
                settingsName = settingsName,
                description = description,
                value = true
            )
        }
    }
}