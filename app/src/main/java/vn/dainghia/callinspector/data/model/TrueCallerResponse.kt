package vn.dainghia.callinspector.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrueCallerResponse(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("score")
    val score: Float,
    @SerialName("access")
    val access: String,
    @SerialName("phones")
    val phones: List<PhoneInfo>,
    @SerialName("addresses")
    val addresses: List<Address>,
    @SerialName("isFraud")
    val isFraud: Boolean,
)


@Serializable
data class PhoneInfo(
    @SerialName("e164Format")
    val e164Format: String,
    @SerialName("numberType")
    val numberType: String,
    @SerialName("nationalFormat")
    val nationalFormat: String,
    @SerialName("dialingCode")
    val dialingCode: Int,
    @SerialName("countryCode")
    val countryCode: String,
    @SerialName("carrier")
    val carrier: String,
    @SerialName("type")
    val type: String,
)

@Serializable
data class Address(
    @SerialName("area")
    val area: String? = null,
    @SerialName("city")
    val city: String? = null,
    @SerialName("countryCode")
    val countryCode: String,
    @SerialName("timeZone")
    val timeZone: String? = null,
    @SerialName("type")
    val type: String
)