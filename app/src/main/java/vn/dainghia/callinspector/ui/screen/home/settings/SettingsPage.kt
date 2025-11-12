package vn.dainghia.callinspector.ui.screen.home.settings

import android.Manifest.permission.READ_PHONE_STATE
import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.viewmodel.compose.viewModel
import vn.dainghia.callinspector.R
import vn.dainghia.callinspector.ui.screen.home.settings.composable.AccessTokenInputDialog
import vn.dainghia.callinspector.ui.screen.home.settings.composable.CountryCodeChooserDialog
import vn.dainghia.callinspector.ui.screen.home.settings.composable.SettingsSwitchItem
import vn.dainghia.callinspector.ui.screen.home.settings.composable.SettingsValueItem

@Composable
fun SettingsPage(modifier: Modifier = Modifier, viewModel: SettingsViewModel = viewModel()) {
    val context = LocalContext.current
    val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager

    // Get string resources at composable level
    val errorRoleNotHeld = stringResource(R.string.error_caller_info_overlay_role_not_held)
    val errorPermissionDenied = stringResource(R.string.error_permission_denied)
    val errorRoleNotAvailable = stringResource(R.string.error_role_not_available)

    val roleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.updateShouldShowCallerInfoOverlay(true)
            } else {
                Toast.makeText(
                    context,
                    errorRoleNotHeld,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )
    val phoneStateLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            when {
                !isGranted -> {
                    Toast.makeText(
                        context,
                        errorPermissionDenied,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING) -> {
                    viewModel.updateShouldShowCallerInfoOverlay(true)
                }

                else -> requestRole(roleManager, roleLauncher)

            }
        }
    )

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
            value = if (viewModel.accessToken.isEmpty()) stringResource(R.string.not_set) else stringResource(R.string.access_token_masked),
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
                when {
                    isEligibleForCallerInfoOverlay(context) -> {
                        viewModel.updateShouldShowCallerInfoOverlay(true)
                    }

                    checkSelfPermission(
                        context,
                        READ_PHONE_STATE
                    ) != PackageManager.PERMISSION_GRANTED -> {
                        phoneStateLauncher.launch(READ_PHONE_STATE)
                    }

                    !roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING) -> {
                        requestRole(roleManager, roleLauncher)
                    }

                    else -> {
                        Toast.makeText(
                            context,
                            errorRoleNotAvailable,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
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

private fun requestRole(
    roleManager: RoleManager,
    roleLauncher: ActivityResultLauncher<Intent>
) {
    if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
        roleLauncher.launch(intent)
    }
}

fun isEligibleForCallerInfoOverlay(context: Context): Boolean {
    val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
    return checkSelfPermission(context, READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
            roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
}

private enum class DialogType {
    CountryCode,
    AccessToken
}