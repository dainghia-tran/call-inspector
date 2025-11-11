package vn.dainghia.callinspector.ui.screen.home.settings.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import vn.dainghia.callinspector.ui.theme.CallInspectorTheme
import vn.dainghia.callinspector.util.CountryCodeUtil

@Composable
fun CountryCodeChooserDialog(
    onLocaleSelected: (String) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(375.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    "Select Country Code",
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                val edgeBrush = Brush.verticalGradient(
                    0f to Color.Transparent,
                    0.3f to MaterialTheme.colorScheme.background,
                    0.7f to MaterialTheme.colorScheme.background,
                    1f to Color.Transparent
                )
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                brush = edgeBrush,
                                blendMode = BlendMode.DstIn
                            )
                        }
                ) {
                    items(CountryCodeUtil.getSupportedLocales()) {
                        Text(
                            "${CountryCodeUtil.getFlagEmoji(it.country)} ${it.displayCountry}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onLocaleSelected(it.country)
                                    onDismissRequest()
                                }
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            fontSize = 17.sp
                        )
                    }
                }
                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Preview
@Composable
fun CountryCodeChooserDialogPreview() {
    CallInspectorTheme {
        CountryCodeChooserDialog(
            onLocaleSelected = {},
            onDismissRequest = {}
        )
    }
}