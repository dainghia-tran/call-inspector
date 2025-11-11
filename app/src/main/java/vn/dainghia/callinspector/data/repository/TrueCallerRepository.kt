package vn.dainghia.callinspector.data.repository

import io.ktor.client.call.body
import io.ktor.http.isSuccess
import vn.dainghia.callinspector.data.model.TrueCallerResponse
import vn.dainghia.callinspector.data.service.TrueCallerService
import javax.inject.Inject

class TrueCallerRepository @Inject constructor(
    private val service: TrueCallerService,
    private val appConfigRepository: AppConfigRepository
) {

    suspend fun searchCallerInfo(phoneNumber: String): Result<TrueCallerResponse> {
        val countryCode = appConfigRepository.getCountryCode()
        val response = service.searchCallerInfo(countryCode, phoneNumber)
        if (response.status.isSuccess()) {
            return Result.success(response.body())
        }
        return Result.failure(Exception(response.status.description))
    }
}