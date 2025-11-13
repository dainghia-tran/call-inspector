package vn.dainghia.callinspector.ui.screen.home.search.composable

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import vn.dainghia.callinspector.R
import vn.dainghia.callinspector.ui.theme.CallInspectorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberSearchBar(
    interactionSource: MutableInteractionSource,
    isFocused: Boolean,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val textFieldState = rememberTextFieldState()

    BasicTextField(
        state = textFieldState,
        modifier = modifier,
        interactionSource = interactionSource,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
            keyboardType = KeyboardType.Phone
        ),
        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
        onKeyboardAction = { onSearch(textFieldState.text.toString()) },
        lineLimits = TextFieldLineLimits.SingleLine,
        decorator = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = textFieldState.text.toString(),
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = SearchBarDefaults.inputFieldShape,
                label = if (textFieldState.text.isEmpty() && !isFocused) {
                    { Text(stringResource(R.string.search_phone_number_hint)) }
                } else {
                    null
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )
        }
    )
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SearchBarPreview() {
    CallInspectorTheme {
        PhoneNumberSearchBar(
            interactionSource = remember { MutableInteractionSource() },
            isFocused = true,
            onSearch = {}
        )
    }
}