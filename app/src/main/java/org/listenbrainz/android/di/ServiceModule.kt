package org.listenbrainz.android.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.listenbrainz.android.util.ListenBrainzServiceGenerator.createService
import org.listenbrainz.android.model.yimdata.YimData
import org.listenbrainz.android.service.BlogService
import org.listenbrainz.android.service.ListensService
import org.listenbrainz.android.service.LoginService
import org.listenbrainz.android.service.YimService
import org.listenbrainz.android.util.ListenBrainzServiceGenerator
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {
    
    @get:Provides
    @get:Singleton
    val blogService: BlogService = Retrofit.Builder()
        .baseUrl("https://public-api.wordpress.com/rest/v1.1/sites/")
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(BlogService::class.java)

    @get:Provides
    @get:Singleton
    val listensService: ListensService = Retrofit.Builder()
        .baseUrl("https://api.listenbrainz.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(ListensService::class.java)

    @get:Provides
    @get:Singleton
    val loginService: LoginService = createService(LoginService::class.java, false)

    private val yimGson: Gson = GsonBuilder()
        /** Since a TopRelease may or may not contain "caaId", "caaReleaseMbid" or "releaseMbid", so we perform a check. */
        /*.registerTypeAdapter(
            TopRelease::class.java, JsonDeserializer<TopRelease>
            { jsonElement: JsonElement, _: Type, _: JsonDeserializationContext ->

                val element = Gson().fromJson(jsonElement, TopRelease::class.java)
                val jsonObject = jsonElement.asJsonObject

                return@JsonDeserializer TopRelease(
                    artistMbids = element.artistMbids,
                    artistName = element.artistName,
                    listenCount = element.listenCount,
                    releaseName = element.releaseName,
                    caaId = if (jsonObject.has("caa_id")) element.caaId else null,
                    caaReleaseMbid = if (jsonObject.has("caa_release_mbid")) element.caaReleaseMbid else null,
                    releaseMbid = if (jsonObject.has("release_mbid")) element.releaseMbid else null
                )
            }
        )*/
        /** Check if a user is new with 0 listens*/
        .registerTypeAdapter(
            YimData::class.java, JsonDeserializer<YimData>
            { jsonElement: JsonElement, _: Type, _: JsonDeserializationContext ->

                val element = Gson().fromJson(jsonElement, YimData::class.java)
                return@JsonDeserializer if (element.totalListenCount == 0) null else element
                // "totalListenCount" field is our null checker.
            }
        )
        .create()

    @get:Provides
    @get:Singleton
    val yimService: YimService = Retrofit.Builder()
            .baseUrl(ListenBrainzServiceGenerator.LISTENBRAINZ_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(yimGson))
            .build()
            .create(YimService::class.java)
}