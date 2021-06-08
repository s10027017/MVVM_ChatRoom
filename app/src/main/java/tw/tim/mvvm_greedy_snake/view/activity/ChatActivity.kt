package tw.tim.mvvm_greedy_snake.view.activity

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import io.agora.rtm.RtmClient
import kotlinx.android.synthetic.main.activity_chat.*
import tw.tim.mvvm_greedy_snake.R
import tw.tim.mvvm_greedy_snake.rtmtutorial.AGApplication
import tw.tim.mvvm_greedy_snake.rtmtutorial.ChatManager
import tw.tim.mvvm_greedy_snake.utils.MessageUtil
import tw.tim.mvvm_greedy_snake.viewmodel.MainViewModel


/**
 *  實作聲網Agora聊天室
 *  https://www.agora.io/cn/
 */

class ChatActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private var mRtmClient: RtmClient? = null
    private lateinit var mChatManager: ChatManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        initActionBar()
        initChatLogin()

    }

    /**
     *  自定義ActionBar
     *  https://www.itread01.com/content/1570118526.html
     */
    private fun initActionBar() {
        // 設定好之後可直接抓取 supportActionBar .之類的fun 要省略可直接打supportActionBar?.apply{ }
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            val color = resources.getColor(R.color.white)
            val colorDrawable = ColorDrawable(color)
            setBackgroundDrawable(colorDrawable)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_black_arrow_left_40)
        }

        // 自訂的Textview 改Title 但會因為左邊圖案顯示而跑版 要再做額外判斷
        val titleColor = resources.getColor(R.color.black)
        tv_toolbar.setTextColor(titleColor)
//        tv_toolbar.text = getString(R.string.app_name)
        tv_toolbar.text = "聊天室"

    }

    /**
     *  登入聊天室
     */
    private fun initChatLogin() {

        val name = intent.getBundleExtra("name")?.getString("name")
//        mChatManager = ChatManager(this)
//        mChatManager?.init()

        // AGApplication Manifest要+
        mChatManager = AGApplication.the().getChatManager()
        mRtmClient = mChatManager.getRtmClient()

//        Log.e("mChatManager", mChatManager.toString())
//        Log.e("mRtmClient", mRtmClient.toString())

        btn_login.setOnClickListener {
            Log.e("ChatActivityTest", "ChatActivityTest")
//            viewModel.loginChat(name)
//            Log.e("viewModel.name",viewModel.name)
            mRtmClient?.login(null, name, object : ResultCallback<Void?> {
                override fun onSuccess(p0: Void?) {
                    // 沒加runOnUiThread 會跑不出來
                    runOnUiThread {
                        val intent = Intent(this@ChatActivity, SelectionActivity::class.java)
                        intent.putExtra(MessageUtil.INTENT_EXTRA_USER_ID, name)
                        startActivity(intent)
                        Toast.makeText(this@ChatActivity, "聊天室登入成功", Toast.LENGTH_SHORT).show()
                        Log.e("Login Success: ", p0.toString())
                    }
                }

                override fun onFailure(p0: ErrorInfo?) {
                    runOnUiThread {
                        Toast.makeText(this@ChatActivity, "聊天室登入失敗", Toast.LENGTH_SHORT).show()
                        Log.e("Login Failure: ", p0.toString())
                    }
                }

            })
        }

        btn_logout.setOnClickListener {
            initLogout()
        }
    }

    private fun initLogout() {
        mRtmClient!!.logout(null)
        MessageUtil.cleanMessageListBeanList()
        runOnUiThread {
            Toast.makeText(this@ChatActivity, "你已登出聊天室", Toast.LENGTH_SHORT).show()
            Log.e("Login out ", "Login out ")
        }
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