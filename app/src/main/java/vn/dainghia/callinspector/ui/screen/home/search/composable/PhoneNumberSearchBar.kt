package vn.dainghia.callinspector.ui.screen.home.search.composable

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview

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
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
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
                    { Text("Search phone number") }
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
@Composable
fun SearchBarPreview() {
    PhoneNumberSearchBar(
        interactionSource = remember { MutableInteractionSource() },
        isFocused = true,
        onSearch = {}
    )
}