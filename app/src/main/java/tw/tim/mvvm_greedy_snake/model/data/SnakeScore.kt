package tw.tim.mvvm_greedy_snake.model.data

import com.google.gson.annotations.SerializedName

data class SnakeScore(

    @SerializedName("State")
    var State: Boolean = false,

    @SerializedName("Message")
    var Message: String = "",

    @SerializedName("ID")
    var ID: Int = 0,

    @SerializedName("Name")
    var Name: String = "",

    @SerializedName("Score")
    var Score: Int = 0,

)