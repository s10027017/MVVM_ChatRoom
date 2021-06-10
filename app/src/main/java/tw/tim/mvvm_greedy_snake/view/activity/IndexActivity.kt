package tw.tim.mvvm_greedy_snake.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import io.agora.rtm.RtmClient
import kotlinx.android.synthetic.main.activity_index.*
import kotlinx.android.synthetic.main.dialog_chat_room_login.view.*
import kotlinx.android.synthetic.main.dialog_login.view.*
import kotlinx.android.synthetic.main.dialog_logout.view.*
import tw.tim.mvvm_greedy_snake.R
import tw.tim.mvvm_greedy_snake.rtmtutorial.AGApplication
import tw.tim.mvvm_greedy_snake.rtmtutorial.ChatManager
import tw.tim.mvvm_greedy_snake.utils.MessageUtil
import tw.tim.mvvm_greedy_snake.viewmodel.MainViewModel

/**
 *  首頁
 */
class IndexActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private var mRtmClient: RtmClient? = null
    private lateinit var mChatManager: ChatManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        initButtons()
        checkName()
        initFCMtest()

    }

    private fun initFCMtest() {
        Firebase.messaging.isAutoInitEnabled = true

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("FCM token failed", task.exception.toString())
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
//            val msg = getString(R.string.msg_token_fmt, token)
            val msg = token
            if (msg != null) {
                Log.e("msg", msg)
            }
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            Log.e("FCM token:",msg.toString())
        })
    }

    // 開始遊戲
    private fun initButtons() {
        // 註冊按鈕
        btn_signup.setOnClickListener {

        }
        // 登入按鈕
        btn_signin.setOnClickListener {
            // 自定義AlertDialog  也可以專寫一個class 繼承自定義AlertDialog 改寫然後處理他
            val alertDialog: AlertDialog = AlertDialog.Builder(this).create()
            //取得自訂的版面。
            val inflater = LayoutInflater.from(this)
            val v: View = inflater.inflate(R.layout.dialog_login, null)
            // 設置view
            alertDialog.setView(v)

            v.dialog_enter.setOnClickListener {
                val name = v.dialog_et_name.text.toString()
                if(name.equals("")){
                    Toast.makeText(this, getString(R.string.name_cannot_be_empty), Toast.LENGTH_SHORT).show()
                }else{
                    alertDialog.dismiss()
                }
                index_name_title.text = "Hi "
                index_name.text = name

                Toast.makeText(this, "你已成功登入帳號!", Toast.LENGTH_SHORT).show()

                checkName()
            }

            // 點擊範圍外無反應
            alertDialog.setCancelable(false)

            alertDialog.show()

            // AlertDialog 用有設定好圓角的xml 顯示會無法顯示
            // https://stackoverflow.com/questions/16861310/android-dialog-rounded-corners-and-transparency
            alertDialog.window?.setBackgroundDrawableResource(R.color.transparent)
        }
        // 貪吃蛇遊戲
        btn_greedy_snake_game.setOnClickListener {
            val name = index_name.text.toString()
            val bundle = Bundle()
            val intent = Intent(this, GameActivity().javaClass)
            bundle.putString("name", name)
            intent.putExtra("name",bundle)
            startActivity(intent)
        }
        // 貪吃蛇排行榜
        btn_greedy_snake_rank.setOnClickListener {
            val intent = Intent(this, RankActivity().javaClass)
            startActivity(intent)
        }
        // 登入聊天室
        btn_chat_room.setOnClickListener {
            mChatManager = AGApplication.the().getChatManager()
            mRtmClient = mChatManager.getRtmClient()
            val name = index_name.text.toString()
            val alertDialog: AlertDialog = AlertDialog.Builder(this).create()
            val inflater = LayoutInflater.from(this)
            val v: View = inflater.inflate(R.layout.dialog_chat_room_login, null)
            alertDialog.setView(v)
            v.dialog_chat_confirm.setOnClickListener {
                mRtmClient?.login(null, name, object : ResultCallback<Void?> {
                    override fun onSuccess(p0: Void?) {
                        // 沒加runOnUiThread 會跑不出來
                        runOnUiThread {
                            val intent = Intent(this@IndexActivity, ChatRoomActivity::class.java)
                            intent.putExtra(MessageUtil.INTENT_EXTRA_USER_ID, name)
                            startActivity(intent)
                            alertDialog.dismiss()
                            Toast.makeText(this@IndexActivity, "聊天室登入成功", Toast.LENGTH_SHORT).show()
                            Log.e("Login Success: ", p0.toString())
                        }
                    }

                    override fun onFailure(p0: ErrorInfo?) {
                        runOnUiThread {
                            Toast.makeText(this@IndexActivity, "聊天室登入失敗", Toast.LENGTH_SHORT).show()
                            Log.e("Login Failure: ", p0.toString())
                        }
                    }

                })
            }
            v.dialog_chat_cancel.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.setCancelable(false)
            alertDialog.show()
            alertDialog.window?.setBackgroundDrawableResource(R.color.transparent)
//            val bundle = Bundle()
//            val intent = Intent(this, ChatActivity().javaClass)
//            bundle.putString("name", name)
//            intent.putExtra("name",bundle)
//            startActivity(intent)
        }
        // 帳號登出
        btn_signout.setOnClickListener {
            val alertDialog: AlertDialog = AlertDialog.Builder(this).create()
            val inflater = LayoutInflater.from(this)
            val v: View = inflater.inflate(R.layout.dialog_logout, null)
            alertDialog.setView(v)
            v.dialog_logout.setOnClickListener {
                alertDialog.dismiss()
                index_name.text = ""
                Toast.makeText(this, "你已成功登出帳號!", Toast.LENGTH_SHORT).show()
                checkName()
            }
            v.dialog_logout_cancel.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.setCancelable(false)
            alertDialog.show()
            alertDialog.window?.setBackgroundDrawableResource(R.color.transparent)
        }
    }

    private fun checkName(){
        val name = index_name.text.toString()
        Log.e("name",name)
        if(name.equals("")){
            btn_signout.visibility = View.GONE
            btn_signup.visibility = View.VISIBLE
            btn_signin.visibility = View.VISIBLE
            btn_greedy_snake_game.visibility = View.GONE
            btn_greedy_snake_rank.visibility = View.GONE
            btn_chat_room.visibility = View.GONE
            index_name_title.text = "Hello! "
        }else{
            btn_signout.visibility = View.VISIBLE
            btn_signup.visibility = View.GONE
            btn_signin.visibility = View.GONE
            btn_greedy_snake_game.visibility = View.VISIBLE
            btn_greedy_snake_rank.visibility = View.VISIBLE
            btn_chat_room.visibility = View.VISIBLE
            index_name_title.text = "Hi "
        }
    }

}