package tw.tim.mvvm_greedy_snake.api

import android.database.Observable
import retrofit2.Call
import retrofit2.http.*
import tw.tim.mvvm_greedy_snake.model.data.SnakeScore

interface SnakeScoreApi {
    // 標記註解 @Multipart @FormUrlEncoded 區別
    // https://www.jianshu.com/p/345304325511

    // @Multipart 傳字串會自帶" "
    // https://segmentfault.com/q/1010000009737456

    @Multipart
    @POST("/api/snakeScore_insert_api.php")
    fun snakeScoreInsert(
        @Part("name") name: String,
        @Part("score") score: Int
    ): Call<SnakeScore>

//    @FormUrlEncoded
//    @POST("/api/snakeScore_insert_api.php")
//    fun snakeScoreInsert(
//            @Field("name") name: String,
//            @Field("score") score: Int
//    ): Call<SnakeScore>

    @GET("/api/snakeScore_api.php")
    fun getSnakeScore(

    ): Call<List<SnakeScore>>


}