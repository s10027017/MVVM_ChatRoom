package tw.tim.mvvm_greedy_snake.view.activity

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.dialog_game.view.*
import tw.tim.mvvm_greedy_snake.R
import tw.tim.mvvm_greedy_snake.model.enums.Direction
import tw.tim.mvvm_greedy_snake.model.enums.GameState
import tw.tim.mvvm_greedy_snake.viewmodel.MainViewModel


/**
 *  實作MVVM 貪吃蛇  綠豆湯學院
 *  https://www.youtube.com/watch?v=LMpJ35tndUw&t=25s
 */

// 遇到的問題
// 抓取指定viewModel
// lambda
// GameView
// BUG 1 Score 無即時更新
// BUG 2 當沒有初始化方向為向左的話 再你往右邊撞牆後 再按REPLAY 將會無限循環
// BUG 3 連點按鈕會造成多個錯誤 鎖定機制
// BUG ?
// 自定義toolbar 、 alertDialog
// (MVVM) Model後臺業務邏輯、資料部分  View UI介面  ViewModel處理UI業務邏輯

class GameActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // gradle 要+ implementation 'androidx.appcompat:appcompat:1.1.0'
        // implementation 'androidx.lifecycle:lifecycle-extensions:2.2.0' 不然找不到ViewModelProvider(this).get 這個fun
        // https://stackoverflow.com/questions/49405616/cannot-resolve-symbol-viewmodelproviders-on-appcompatactivity
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        initActionBar()
        initUniObserve()
        initStartGame()
        initButtons()

    }

    private fun initFCMtest() {
//        Firebase.messaging.isAutoInitEnabled = true
//
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.e("FCM token failed", task.exception.toString())
//                return@OnCompleteListener
//            }
//
//            // Get new FCM registration token
//            val token = task.result
//
//            // Log and toast
////            val msg = getString(R.string.msg_token_fmt, token)
//            val msg = token
//            if (msg != null) {
//                Log.e("msg", msg)
//            }
////            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//            Log.e("FCM token:",msg.toString())
//        })
    }

    /**
    *  自定義ActionBar
     *  https://www.itread01.com/content/1570118526.html
    */
    private fun initActionBar() {
        // 設定好之後可直接抓取 supportActionBar .之類的fun 要省略可直接打supportActionBar?.apply{ }
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            val color = resources.getColor(R.color.transparent)
            val colorDrawable = ColorDrawable(color)
            setBackgroundDrawable(colorDrawable)
            // 在Layout上有做一個TextView了 所以不用內建的
//            setTitle(R.string.app_name)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_white_arrow_left_40)
        }

        // 自訂的Textview 改Title 但會因為左邊圖案顯示而跑版 要再做額外判斷
        val titleColor = resources.getColor(R.color.white)
        tv_toolbar.setTextColor(titleColor)
        tv_toolbar.text = getString(R.string.greedy_snake_game)

        // 讓toolbar在最上層 也可在Layout上直接調位置
//        toolbar.bringToFront()

        // 顯示左邊返回按鈕 圖案可自選 不寫在supportActionBar?.apply{ }的方法
        // 要用監聽事件 一樣可用menu的fun
//        supportActionBar?.setHomeButtonEnabled(true)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_launcher_background)
    }

    /**
     *  建立唯一觀察者
     */
    private fun initUniObserve() {
        // gradle plugins 要擴展kotlin-android-extensions 才能省略findViewByID
        // https://blog.csdn.net/feiyukill/article/details/72781965
        // 在這之前還有Butter Knife奶油刀也可以做到這件事, 有興趣可以看看
        // DataBinding 也可以, 而且更多功能
        viewModel.snake.observe(this, {
            game_view.snakeBody = it
            // 強制重繪View，調用invalidate()
            // http://androidbiancheng.blogspot.com/2010/05/viewinvalidate.html
            game_view.invalidate()
        })

        // 更新紅點
        viewModel.bonusPosition.observe(this, {
            game_view.updateBonus(it)
        })

        // 更新遊戲分數
        viewModel.scoreData.observe(this, {
            score.text = it.toString()
        })

        // 寫入分數
        viewModel.insertLiveData.observe(this, {
            Log.e("it.body()", it.body().toString())
            if (it.body()?.State == true) {
                Toast.makeText(this, getString(R.string.record_insert_success), Toast.LENGTH_SHORT).show()
            }

        })

        // 抓取全部分數表  移至RankActivity
//        viewModel.getAllLiveData.observe(this, {
//            Log.e("getRankData",it.toString())
//            Log.e("getRankData[0]",it[0].toString())
//            Log.e("getRankData[1]",it[1].toString())
//        })

        // 依照遊戲狀態更新
        viewModel.gameState.observe(this, {
//            Log.e("it", it.toString())
            if (it == GameState.GAME_OVER) {
                // 原生AlertDialog
//                AlertDialog.Builder(this)
//                    .setTitle(getString(R.string.game_state))
//                    .setMessage(getString(R.string.game_over))
//                    // dialog, which -> Lambda寫法 省略很多
//                    .setPositiveButton(getString(R.string.control_replay)) { dialog, which ->
//                        viewModel.start()
//                    }
//                    .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
//                        dialog.cancel()
//                    }
//                    .show()

                // 自定義AlertDialog  也可以專寫一個class 繼承自定義AlertDialog 改寫然後處理他
                val alertDialog: AlertDialog = AlertDialog.Builder(this).create()
                //取得自訂的版面。
                val inflater = LayoutInflater.from(this@GameActivity)
                val v: View = inflater.inflate(R.layout.dialog_game, null)
                // 設置view
                alertDialog.setView(v)

                // 一定要透過View 抓取AlertDialog上的元件 才能執行動作
                v.score.text = getString(R.string.total_score) + viewModel.total_score
                v.dialog_start.setOnClickListener {
                    viewModel.start()
                    alertDialog.dismiss()
                }

                // 取消
                v.dialog_cancel.setOnClickListener {
                    alertDialog.dismiss()
                }

                // 儲存紀錄
                v.dialog_save.setOnClickListener {
                    viewModel.snakeScoreInsert()
                }

                // 聊天室測試
//                v.dialog_room.setOnClickListener {
//                    val bundle = Bundle()
//                    val intent = Intent(this, ChatActivity().javaClass)
//                    bundle.putString("name", viewModel.name)
//                    intent.putExtra("name",bundle)
//                    startActivity(intent)
//                }

                // Rank排行榜
//                v.dialog_get.setOnClickListener {
//                    val intent = Intent(this, RankActivity().javaClass)
//                    startActivity(intent)
//                }

                // 點擊範圍外無反應
                alertDialog.setCancelable(false)

                alertDialog.show()

                // AlertDialog 用有設定好圓角的xml 顯示會無法顯示
                // https://stackoverflow.com/questions/16861310/android-dialog-rounded-corners-and-transparency
                alertDialog.window?.setBackgroundDrawableResource(R.color.transparent)

            }
        })

        // 遊戲開始
//        viewModel.start()

    }

    // 開始遊戲
    private fun initStartGame() {

        viewModel.start()

        val name = intent.getBundleExtra("name")?.getString("name")
        viewModel.name = name.toString()

        // 自定義AlertDialog  也可以專寫一個class 繼承自定義AlertDialog 改寫然後處理他
//        val alertDialog: AlertDialog = AlertDialog.Builder(this).create()
//        //取得自訂的版面。
//        val inflater = LayoutInflater.from(this@MainActivity)
//        val v: View = inflater.inflate(R.layout.start_dialog, null)
//        // 設置view
//        alertDialog.setView(v)
//
//        v.dialog_start.setOnClickListener {
//            val name = v.dialog_et_name.text.toString()
//            if(name.equals("")){
//                Toast.makeText(this, getString(R.string.name_cannot_be_empty), Toast.LENGTH_SHORT).show()
//            }
//            viewModel.name = name
//            viewModel.start()
//            alertDialog.dismiss()
//        }
//
//        // 點擊範圍外無反應
//        alertDialog.setCancelable(false)
//
//        alertDialog.show()
//
//        // AlertDialog 用有設定好圓角的xml 顯示會無法顯示
//        // https://stackoverflow.com/questions/16861310/android-dialog-rounded-corners-and-transparency
//        alertDialog.window?.setBackgroundDrawableResource(R.color.transparent)
    }

    /**
     *  Button 按鈕事件
     */
    private fun initButtons() {
        up.setOnClickListener { viewModel.move(Direction.UP) }
        down.setOnClickListener { viewModel.move(Direction.DOWN) }
        left.setOnClickListener { viewModel.move(Direction.LEFT) }
        right.setOnClickListener { viewModel.move(Direction.RIGHT) }
        replay.setOnClickListener { viewModel.start() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}