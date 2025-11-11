package vn.dainghia.callinspector.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import vn.dainghia.callinspector.BuildConfig
import vn.dainghia.callinspector.data.repository.AppConfigRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HttpClientModule {

    @Provides
    @Singleton
    fun provideHttpClient(appConfigRepository: AppConfigRepository): HttpClient {
        return HttpClient(CIO) {
            defaultRequest {
                url(BuildConfig.BASE_URL)
                header("Host", BuildConfig.HOST)
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        val accessToken = appConfigRepository.getToken()
                        BearerTokens(accessToken, refreshToken = null)
                    }
                }
            }
        }
    }
}