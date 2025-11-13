package vn.dainghia.callinspector.ui.screen.home.settings

import android.Manifest
import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Token
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import vn.dainghia.callinspector.R
import vn.dainghia.callinspector.ui.screen.home.settings.composable.AccessTokenInputDialog
import vn.dainghia.callinspector.ui.screen.home.settings.composable.CountryCodeChooserDialog
import vn.dainghia.callinspector.ui.screen.home.settings.composable.SettingsSwitchItem
import vn.dainghia.callinspector.ui.screen.home.settings.composable.SettingsValueItem
import vn.dainghia.callinspector.util.CallerInfoOverlayPermissionHelper

@Composable
fun SettingsPage(modifier: Modifier = Modifier, viewModel: SettingsViewModel = viewModel()) {
    val context = LocalContext.current
    val errorPermissionDenied = stringResource(R.string.error_permission_denied)

    val specialPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                    && result.resultCode == Activity.RESULT_OK ->
                viewModel.updateShouldShowCallerInfoOverlay(true)

            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                    CallerInfoOverlayPermissionHelper.hasSystemAlertWindowPermission(context) ->
                viewModel.updateShouldShowCallerInfoOverlay(true)

            else -> Toast.makeText(context, errorPermissionDenied, Toast.LENGTH_SHORT).show()
        }
    }

    val runtimePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }

        when {
            !allGranted -> Toast.makeText(context, errorPermissionDenied, Toast.LENGTH_SHORT).show()
            CallerInfoOverlayPermissionHelper.isEligible(context) ->
                viewModel.updateShouldShowCallerInfoOverlay(true)

            else -> requestSpecialPermissions(context, specialPermissionLauncher)
        }
    }

    var dialogType by remember { mutableStateOf<DialogType?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        SettingsValueItem(
            imageVector = Icons.Filled.Public,
            settingsName = stringResource(R.string.country_code),
            description = stringResource(R.string.country_code_description),
            value = viewModel.countryCode,
            onItemClicked = { dialogType = DialogType.CountryCode }
        )
        SettingsValueItem(
            imageVector = Icons.Filled.Token,
            settingsName = stringResource(R.string.truecaller_access_token),
            description = stringResource(R.string.truecaller_access_token_description),
            value = if (viewModel.accessToken.isEmpty()) stringResource(R.string.not_set) else stringResource(
                R.string.access_token_masked
            ),
            onItemClicked = { dialogType = DialogType.AccessToken }
        )
        SettingsSwitchItem(
            imageVector = Icons.Filled.ContactPhone,
            settingsName = stringResource(R.string.show_caller_information),
            description = stringResource(R.string.show_caller_information_description),
            value = viewModel.shouldShowCallerInfoOverlay,
            onCheckedChange = { enabled ->
                if (!enabled) {
                    viewModel.updateShouldShowCallerInfoOverlay(false)
                    return@SettingsSwitchItem
                }

                if (CallerInfoOverlayPermissionHelper.isEligible(context)) {
                    viewModel.updateShouldShowCallerInfoOverlay(true)
                    return@SettingsSwitchItem
                }

                startPermissionChain(runtimePermissionLauncher)
            }
        )
    }
    Dialogs(dialogType, viewModel) {
        dialogType = null
    }
}

@Composable
private fun Dialogs(
    dialogType: DialogType?,
    viewModel: SettingsViewModel,
    onDismissRequest: () -> Unit
) {
    when (dialogType) {
        DialogType.CountryCode -> CountryCodeChooserDialog(
            onLocaleSelected = viewModel::updateCountryCode,
            onDismissRequest = onDismissRequest,
        )

        DialogType.AccessToken -> AccessTokenInputDialog(
            currentValue = viewModel.accessToken,
            onSave = viewModel::updateAccessToken,
            onDismissRequest = onDismissRequest,
        )

        null -> Unit
    }
}

fun startPermissionChain(
    runtimePermissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        runtimePermissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_PHONE_STATE
            )
        )
    } else {
        runtimePermissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG
            )
        )
    }
}

private fun requestSpecialPermissions(
    context: Context,
    launcher: ActivityResultLauncher<Intent>
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val roleManager = context.getSystemService(RoleManager::class.java)
        val role = RoleManager.ROLE_CALL_SCREENING

        if (roleManager.isRoleAvailable(role) && !roleManager.isRoleHeld(role)) {
            val intent = roleManager.createRequestRoleIntent(role)
            launcher.launch(intent)
        }
    } else {
        if (!CallerInfoOverlayPermissionHelper.hasSystemAlertWindowPermission(context)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            launcher.launch(intent)
        }
    }
}

private enum class DialogType {
    CountryCode,
    AccessToken
}