package tw.tim.mvvm_greedy_snake.view.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_rank.*
import kotlinx.android.synthetic.main.activity_rank.toolbar
import kotlinx.android.synthetic.main.item_rank.view.*
import tw.tim.mvvm_greedy_snake.R
import tw.tim.mvvm_greedy_snake.model.data.SnakeScore
import tw.tim.mvvm_greedy_snake.view.adapter.BaseRecyclerViewAdapter
import tw.tim.mvvm_greedy_snake.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

class RankActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    val handler = Handler()
    private var runnable: Runnable? = null
    private val rankAdapter = RankAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        initActionBar()
        initListView()
        initUniObserve()
        getRankData()
        setTime()
    }

    fun initListView() {
        val layoutManager = LinearLayoutManager(this)
        ry_rank.layoutManager = layoutManager
        ry_rank.adapter = rankAdapter
    }
    private fun initActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            // 換返回鍵圖案
//            setHomeAsUpIndicator(R.drawable.ic_launcher_background)
        }
        val titleColor = resources.getColor(R.color.black)
        tv_rank_toolbar.setTextColor(titleColor)
        tv_rank_toolbar.text = getString(R.string.leaderboard)
    }

    /**
     *  建立唯一觀察者
     */
    private fun initUniObserve() {
        // 抓取全部分數表
        viewModel.getRankLiveData.observe(this, {
            rankAdapter.update(it)
            Log.e("getRankData",it.toString())
            Log.e("getRankData[0]",it[0].toString())
            Log.e("getRankData[1]",it[1].toString())
        })
    }

    /**
     *  取得Rank資料
     */
    private fun getRankData() {
        viewModel.getRankData()
    }

    /**
     *  活動倒數
     */
    private fun setTime() {
        runnable = object : Runnable {
            override fun run() {
                try {
                    handler.postDelayed(this, 1000)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    val event_date: Date = dateFormat.parse("2021-06-14T12:00:00")

                    val current_date = Date()
                    if (!current_date.after(event_date)) {
                        val diff: Long = event_date.getTime() - current_date.getTime()
                        val Days = diff / (24 * 60 * 60 * 1000)
                        val Hours = diff / (60 * 60 * 1000) % 24
                        val Minutes = diff / (60 * 1000) % 60
                        val Seconds = diff / 1000 % 60

                        time_days.setText(String.format("%02d", Days))
                        time_hours.setText(String.format("%02d", Hours))
                        time_minutes.setText(String.format("%02d", Minutes))
                        time_seconds.setText(String.format("%02d", Seconds))

                    } else {
                        runnable?.let { handler.removeCallbacks(it) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        handler.postDelayed(runnable as Runnable, 0)
    }

    // 有要做menu再用
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.activity_user, menu)
//        menu?.let {
//            for (i: Int in 0 until it.size()) {
//                menu.getItem(i)?.isVisible = false
//            }
//
//            //初始畫面的item
//            menu.findItem(R.id.btn_charity_sign_up).isVisible = true
//        }
//        return super.onCreateOptionsMenu(menu)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        runnable?.let { handler.removeCallbacks(it) }
    }
    inner class RankAdapter : BaseRecyclerViewAdapter<SnakeScore, ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_rank, parent, false)
            return ViewHolder(view)
        }

        @RequiresApi(Build.VERSION_CODES.M)
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val rootView = holder.itemView
            val data = list[position]

            rootView.tv_rank_name.text = data.Name
            if(position == 0){
                rootView.bg_item.setBackgroundColor(getColor(R.color.gold_color))
            }else if (position%2 == 1){
                rootView.bg_item.setBackgroundColor(getColor(R.color.white_smoke_color))
            }else{
                rootView.bg_item.setBackgroundColor(getColor(R.color.white))
            }
            rootView.tv_rank.text = "#"+(position+1).toString()
            rootView.tv_score.text = data.Score.toString()


        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)
}