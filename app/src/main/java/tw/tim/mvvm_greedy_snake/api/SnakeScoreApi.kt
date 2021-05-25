package tw.tim.mvvm_greedy_snake.api

import retrofit2.Call
import retrofit2.http.*

interface SnakeScoreApi {

    @Multipart
    @POST("/api/snakeScore_insert_api.php")
    fun snakeScoreInsert(
        @Part("Name") Name: String,
        @Part("Score") Score: Int
    ): Call<SnakeScore>

    @GET("/api/snakeScore_api.php")
    fun getSnakeScore(

    ): Call<List<SnakeScore>>


}