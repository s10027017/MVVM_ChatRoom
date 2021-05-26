package tw.tim.mvvm_greedy_snake.api

import android.util.Log
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.lang.String
import java.util.concurrent.TimeUnit

class ServiceManager private constructor() {

    private var mRetrofit: Retrofit = createRetrofit()

    companion object {
        var instance = ServiceManager()
    }

    // https://medium.com/%E5%B7%A5%E7%A8%8B%E5%B8%AB%E6%B1%82%E7%94%9F%E6%8C%87%E5%8D%97-sofware-engineer-survival-guide/retrofit-2-%E8%B5%B7%E6%89%8B%E5%BC%8F-212644f33a9a
    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                // 自定義攔截器
                .addNetworkInterceptor(LoggingInterceptor())
                .connectTimeout(60L, TimeUnit.SECONDS)
                .readTimeout(60L, TimeUnit.SECONDS)
                .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
                .build()
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            //設定請求URL
            .baseUrl("https://medicalcarehelper.com/")
            // 新增自定義攔截器
            .client(createOkHttpClient())
            // 解決@Multipart 傳String 帶" " 問題
            .addConverterFactory(ScalarsConverterFactory.create())
            // 設定Gson解析工具
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getSnakeScoreService(): SnakeScoreApi {
        return mRetrofit.create(SnakeScoreApi::class.java)
    }

}

// Retrofit 攔截器
internal class LoggingInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val t1 = System.nanoTime()
        Log.e("intercept: ", String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()))
        val response: Response = chain.proceed(request)
        val t2 = System.nanoTime()
        Log.e("intercept: ",String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6, response.headers()))
        return response
    }
}


