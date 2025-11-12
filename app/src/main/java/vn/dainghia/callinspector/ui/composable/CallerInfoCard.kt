package vn.dainghia.callinspector.ui.composable

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.dainghia.callinspector.data.model.Address
import vn.dainghia.callinspector.data.model.PhoneInfo
import vn.dainghia.callinspector.data.model.TrueCallerResponse
import vn.dainghia.callinspector.ui.theme.CallInspectorTheme
import vn.dainghia.callinspector.util.CountryCodeUtil

@Composable
fun CallerInfoCard(
    trueCallerResponse: TrueCallerResponse,
    modifier: Modifier = Modifier,
    onDrag: (Offset) -> Unit = {}
) {
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount)
                }
            }
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(size = 24.dp)
            )
            .padding(16.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircleName(name = trueCallerResponse.name)
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                ) {
                    val displayName = trueCallerResponse.name.ifBlank {
                        "Unknown"
                    }
                    Text(
                        displayName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        trueCallerResponse.phones.first().countryCode,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                if (trueCallerResponse.isFraud) {
                    FraudWarning()
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Address(trueCallerResponse.addresses)
            PhoneInformation(
                trueCallerResponse.phones,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun FraudWarning(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.error,
                shape = RoundedCornerShape(size = 12.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Warning,
            contentDescription = "Fraud Warning Icon",
            tint = MaterialTheme.colorScheme.onError
        )
        Text(
            "Fraud",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onError
        )
    }
}

@Composable
private fun CircleName(name: String, modifier: Modifier = Modifier) {
    val trimmedName = name.trim()
    val lastNameFirstChar = if (trimmedName.isEmpty()) {
        ""
    } else {
        val index = trimmedName.lastIndexOf(" ")
        if (index != -1 && index + 1 < trimmedName.length) {
            trimmedName[index + 1].uppercaseChar().toString()
        } else {
            trimmedName.first().uppercaseChar().toString()
        }
    }
    Box(
        modifier = modifier
            .size(50.dp)
            .background(
                MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(size = 25.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            lastNameFirstChar,
            fontSize = 21.sp,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun Address(addresses: List<Address>, modifier: Modifier = Modifier) {
    Text(
        "Address",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSecondary
    )
    addresses.forEach { address ->
        val countryName = CountryCodeUtil.getCountryName(address.countryCode) ?: address.countryCode
        Text(
            text = listOfNotNull(address.area, address.city, countryName)
                .joinToString(", "),
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier.padding(top = 4.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

@Composable
private fun PhoneInformation(phones: List<PhoneInfo>, modifier: Modifier = Modifier) {
    Text(
        "Phone",
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSecondary
    )
    phones.forEach { phone ->
        Text(
            text = phone.countryCode,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 2.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        )
        Text(
            text = buildString {
                if(phone.carrier.isNotBlank())
                    append("${phone.carrier} - ")
                append(phone.nationalFormat)
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun CallerInfoCardPreview() {
    val response = TrueCallerResponse(
        id = "id",
        name = "Random Name",
        score = 0.3f,
        access = "access",
        phones = listOf(
            PhoneInfo(
                e164Format = "+84123456789",
                numberType = "MOBILE",
                nationalFormat = "0123456789",
                dialingCode = 84,
                countryCode = "VN",
                carrier = "Mobifone",
                type = "openPhone"
            )
        ),
        addresses = listOf(
            Address(
                area = "Saigon",
                city = "Ho Chi Minh City",
                countryCode = "VN",
                timeZone = "+07:00",
                type = "address"
            )
        ),
        isFraud = true
    )

    CallInspectorTheme {
        CallerInfoCard(response)
    }
}