package tw.tim.mvvm_greedy_snake.model

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tw.tim.mvvm_greedy_snake.api.ServiceManager
import tw.tim.mvvm_greedy_snake.api.SnakeScore
import tw.tim.mvvm_greedy_snake.api.SnakeScoreApi

class DataModel {
    private var mSnakeScoreService: SnakeScoreApi? = null

    constructor() {
        mSnakeScoreService = ServiceManager.instance!!.getSnakeScoreService()
    }

    fun snakeScoreInsert(Name: String, Score: Int, callback: OnDataReadyCallback) {
        Log.e("name",Name.toString() )
        Log.e("score",Score.toString() )
        // {"state":true, "message":"分數寫入成功!!"}   不能有List<>
        mSnakeScoreService!!.snakeScoreInsert(Name, Score).enqueue(object : Callback<SnakeScore> {
            override fun onFailure(call: Call<SnakeScore>?, t: Throwable?) {
                Log.e("DataModel Error", "searchRepositories error")
                Log.e("t", t.toString())
            }

            override fun onResponse(call: Call<SnakeScore>, response: Response<SnakeScore>) {
                callback.onData(response)
                Log.e("response", response.toString())
            }

        })
    }

    fun getSnakeScore(callback: OnDataReadyCallback) {
        // [SnakeScore(state=false, message=, ID=1, Name=tim, Score=10), SnakeScore(state=false, message=, ID=3, Name=weiting, Score=20)]
        mSnakeScoreService!!.getSnakeScore().enqueue(object : Callback<List<SnakeScore>> {
            override fun onFailure(call: Call<List<SnakeScore>>?, t: Throwable?) {
                Log.e("DataModel Error", "searchRepositories error")
                Log.e("call", call.toString())
                Log.e("t", t.toString())
            }

            override fun onResponse(call: Call<List<SnakeScore>>?, response: Response<List<SnakeScore>>?) {
//                callback.onDataReady(response!!.body()!!.getItems())
                callback.onListData(response?.body())
                Log.e("call", call.toString())
                Log.e("response", response.toString())
            }
        })
    }

    interface OnDataReadyCallback {
        fun onListData(data: List<SnakeScore>?)
        fun onData(data: Response<SnakeScore>?)
    }

}