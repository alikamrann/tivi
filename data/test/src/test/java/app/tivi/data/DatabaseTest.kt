/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.tivi.data

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.moviebase.tmdb.Tmdb3
import app.moviebase.trakt.Trakt
import app.tivi.core.analytics.AnalyticsComponent
import app.tivi.data.traktauth.RefreshTraktTokensInteractor
import app.tivi.data.traktauth.TraktAuthState
import app.tivi.data.traktauth.TraktOAuthInfo
import app.tivi.data.traktauth.store.AuthStore
import app.tivi.extensions.unsafeLazy
import app.tivi.inject.ApplicationScope
import app.tivi.tmdb.TmdbComponent
import app.tivi.tmdb.TmdbOAuthInfo
import app.tivi.trakt.TraktComponent
import app.tivi.util.Logger
import app.tivi.util.LoggerComponent
import app.tivi.util.TiviLogger
import io.mockk.mockk
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import okhttp3.OkHttpClient
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class DatabaseTest {
    @get:Rule(order = 1)
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    val component: TestApplicationComponent by unsafeLazy {
        TestApplicationComponent::class.create(ApplicationProvider.getApplicationContext())
    }
}

@Component
@ApplicationScope
abstract class TestApplicationComponent(
    @get:Provides val application: Application,
) : TmdbComponent,
    TraktComponent,
    AnalyticsComponent,
    LoggerComponent,
    TestDataSourceComponent(),
    TestRoomDatabaseComponent {

    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    fun provideTraktOAuthInfo(): TraktOAuthInfo = TraktOAuthInfo(
        clientId = "c7ed11dc2b59f732ff65084cae915fc80fa394932e05b41f05d77fcb89c94891",
        clientSecret = "7eb76aa366a8149280fa8706e9653ee718a6c0427ec393209b8c184d0beb73ca",
        redirectUri = "urn:ietf:wg:oauth:2.0:oob",
    )

    @Provides
    fun provideTmdbOAuthInfo(): TmdbOAuthInfo = TmdbOAuthInfo(
        apiKey = "",
    )

    @Provides
    fun provideTraktAuthState(): TraktAuthState = TraktAuthState.LOGGED_IN

    @Provides
    fun provideRefreshTraktTokensInteractor(): RefreshTraktTokensInteractor {
        return RefreshTraktTokensInteractor { null }
    }

    @Provides
    override fun provideTrakt(
        client: OkHttpClient,
        authStore: AuthStore,
        oauthInfo: TraktOAuthInfo,
        refreshTokens: Lazy<RefreshTraktTokensInteractor>,
    ): Trakt = Trakt("fakefakefake")

    @Provides
    override fun provideTmdb(
        client: OkHttpClient,
        tmdbOAuthInfo: TmdbOAuthInfo,
    ): Tmdb3 = Tmdb3("fakefakefake")

    @Provides
    override fun provideLogger(bind: TiviLogger): Logger = mockk(relaxUnitFun = true)
}
