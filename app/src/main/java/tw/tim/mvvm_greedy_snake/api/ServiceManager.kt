package tw.tim.mvvm_greedy_snake.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ServiceManager private constructor() {

    private var mRetrofit: Retrofit = createRetrofit()

    companion object {
        var instance = ServiceManager()
    }

    private fun createRetrofit(): Retrofit {
//        val httpClient = OkHttpClient.Builder()
//            .addInterceptor(TwApiInterceptor())
//            .connectTimeout(15, TimeUnit.SECONDS)
//            .readTimeout(15, TimeUnit.SECONDS)
//            .writeTimeout(15, TimeUnit.SECONDS)
//            .build()
        return Retrofit.Builder()
            .baseUrl("https://medicalcarehelper.com/")
            .addConverterFactory(GsonConverterFactory.create())
//            .client(httpClient)
            .build()
    }

    fun getSnakeScoreService(): SnakeScoreApi {
        return mRetrofit.create(SnakeScoreApi::class.java)
    }

}


