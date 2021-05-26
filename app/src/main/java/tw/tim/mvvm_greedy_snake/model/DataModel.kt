package tw.tim.mvvm_greedy_snake.model

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tw.tim.mvvm_greedy_snake.api.ServiceManager
import tw.tim.mvvm_greedy_snake.model.data.SnakeScore
import tw.tim.mvvm_greedy_snake.api.SnakeScoreApi

class DataModel {
    private var mSnakeScoreService: SnakeScoreApi? = null

    constructor() {
        mSnakeScoreService = ServiceManager.instance!!.getSnakeScoreService()
    }

    fun snakeScoreInsert(name: String, score: Int, callback: OnDataReadyCallback) {
        // {"state":true, "message":"分數寫入成功!!"}   不能有List<>
        mSnakeScoreService!!.snakeScoreInsert(name, score).enqueue(object : Callback<SnakeScore> {
            override fun onFailure(call: Call<SnakeScore>?, t: Throwable?) {
                Log.e("DataModel Insert Error", t.toString())
            }

            override fun onResponse(call: Call<SnakeScore>, response: Response<SnakeScore>) {
                callback.onData(response)
                Log.e("insert Response", response.toString())
            }

        })
    }

    fun getSnakeScore(callback: OnDataReadyCallback) {
        // [SnakeScore(state=false, message=, ID=1, Name=tim, Score=10), SnakeScore(state=false, message=, ID=3, Name=weiting, Score=20)]
        // 要用List []  除非規則不一樣 不然要改寫自定義規則
        mSnakeScoreService!!.getSnakeScore().enqueue(object : Callback<List<SnakeScore>> {
            override fun onFailure(call: Call<List<SnakeScore>>?, t: Throwable?) {
                Log.e("get Rank Error", t.toString())
            }

            override fun onResponse(call: Call<List<SnakeScore>>?, response: Response<List<SnakeScore>>?) {
//                callback.onDataReady(response!!.body()!!.getItems())
                callback.onListData(response?.body())
                Log.e("get Rank Response", response.toString())
            }
        })
    }

    interface OnDataReadyCallback {
        fun onListData(data: List<SnakeScore>?)
        fun onData(data: Response<SnakeScore>?)
    }

}