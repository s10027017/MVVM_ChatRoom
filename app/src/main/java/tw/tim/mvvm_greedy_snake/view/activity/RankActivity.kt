package tw.tim.mvvm_greedy_snake.view.activity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.activity_rank.*
import kotlinx.android.synthetic.main.activity_rank.toolbar
import kotlinx.android.synthetic.main.item_rank.view.*
import tw.tim.mvvm_greedy_snake.R
import tw.tim.mvvm_greedy_snake.model.data.SnakeScore
import tw.tim.mvvm_greedy_snake.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

class RankActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    val handler = Handler()
    private var runnable: Runnable? = null
    private val scoreAdapter = ScoreApapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        initActionBar()
        initUniObserve()
        initSwipeRefresh()
        initRecycleView()
        getRankData()
        setTime()
    }

    /**
     *  自定義ActionBar
     *  https://www.itread01.com/content/1570118526.html
     *
     *  互動式CollapsingToolbarLayout
     *  https://ithelp.ithome.com.tw/articles/10244448
     */
    private fun initActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            // 換返回鍵圖案
            setHomeAsUpIndicator(R.drawable.ic_black_arrow_left_40)
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
            Log.e("getRankData",it.toString())
            Log.e("getRankData[0]",it[0].toString())
            Log.e("getRankData[1]",it[1].toString())
            scoreAdapter.update(it as MutableList<SnakeScore>)
        })
    }

    /**
     *  Recycleview 下拉更新
     */
    private fun initSwipeRefresh() {
        val handler = Handler()
        // 下拉時觸發SwipeRefreshLayout的下拉動畫，動畫完畢之後就會回調這個方法
        swipe_refresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            // 開始刷新，設置當前為刷新狀態
            //swipeRefreshLayout.setRefreshing(true);
            viewModel.getRankData()
            // 這裡是主線程
            // 一些比較耗時的操作，比如聯網獲取數據，需要放到子線程去執行
            object : Thread() {
                override fun run() {
                    super.run()
                    // 同步加載網絡數據
                    // 加載數據 完畢後 關閉刷新狀態 切回主線程
                    handler.postDelayed({ // 加載完數據設置為不刷新狀態，將下拉進度收起來
                        swipe_refresh.setRefreshing(false)
                    }, 100)
                }
            }.start()
        })
    }

    /**
     *  設定Recycleview Layout
     */
    private fun initRecycleView() {
        val layoutManager = LinearLayoutManager(this)
        ry_rank.layoutManager = layoutManager
        ry_rank.adapter = scoreAdapter
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
                    val event_date: Date = dateFormat.parse("2021-06-30T12:00:00")

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

    // ScoreApapter : RecyclerView.Adapter<> 裡面要放ViewHolder 再自己建立一個 然後抓取後就可以覆寫3fun
    inner class ScoreApapter : RecyclerView.Adapter<ScoreApapter.ScoreViewHolder>() {

        val list = mutableListOf<SnakeScore>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_rank, parent, false)
            return ScoreViewHolder(v)
        }

        override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
            val rootView = holder.itemView
            val data = list[position]

//            rootView.tv_name.text = data.Name
//            rootView.tv_score.text = data.Score.toString()
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

        override fun getItemCount(): Int {
            return list.size
        }

        fun update(updateList: MutableList<SnakeScore>) {
            list.clear()
            list.addAll(updateList)
            notifyDataSetChanged()
        }

        inner class ScoreViewHolder(v: View) : RecyclerView.ViewHolder(v)

    }

}