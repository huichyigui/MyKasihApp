package rsd.mad.mykasihv1.fcm

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import rsd.mad.mykasihv1.Helper.Companion.BASE_URL

class RetrofitInstance {

    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(NotificationAPI::class.java)
        }
    }
}