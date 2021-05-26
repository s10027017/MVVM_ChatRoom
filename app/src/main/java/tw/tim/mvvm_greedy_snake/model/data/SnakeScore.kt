package tw.tim.mvvm_greedy_snake.model.data

import com.google.gson.annotations.SerializedName

data class SnakeScore(

    @SerializedName("State")
    private var State: Boolean = false,

    @SerializedName("Message")
    private var Message: String = "",

    @SerializedName("ID")
    private var ID: Int = 0,

    @SerializedName("Name")
    private var Name: String = "",

    @SerializedName("Score")
    private var Score: Int = 0,

) {

    fun getState():Boolean = State

}
