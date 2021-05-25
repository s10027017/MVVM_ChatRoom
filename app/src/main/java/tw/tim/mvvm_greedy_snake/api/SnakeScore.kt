package tw.tim.mvvm_greedy_snake.api

import com.google.gson.annotations.SerializedName

data class SnakeScore(

    @SerializedName("state")
    private var state: Boolean = false,

    @SerializedName("message")
    private var message: String = "",

    @SerializedName("ID")
    private var ID: Int = 0,

    @SerializedName("Name")
    private var Name: String = "",

    @SerializedName("Score")
    private var Score: Int = 0,

)
