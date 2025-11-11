package vn.dainghia.callinspector.data.service

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import javax.inject.Inject

class TrueCallerService @Inject constructor(private val httpClient: HttpClient) {

    suspend fun searchCallerInfo(countrCode: String, phoneNumber: String): HttpResponse =
        httpClient.get("search/v2") {
            url {
                parameters.apply {
                    append("countryCode", countrCode)
                    append("q", phoneNumber)
                    append("type", "44")
                }
            }
        }
}