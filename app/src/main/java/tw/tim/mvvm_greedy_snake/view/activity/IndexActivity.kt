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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
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
import kotlinx.android.synthetic.main.dialog_signup.view.*
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
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001
    private val TAG = "Tim"
    private lateinit var signUpalertDialog: AlertDialog
    private lateinit var logInalertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        initButtons()
        initUniObserve()
        checkName()
        initFCM()
        googleLogin()

    }

    // FCM 推播實做
    private fun initFCM() {
        // 初始化
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
            Log.e("FCM token:", msg.toString())
        })
    }

    // Google 登入實做
    private fun googleLogin() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    private fun googleSignIn(){
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // 在您的 Activity 的onStart方法中，檢查用戶是否已通過 Google 登錄您的應用。
    override fun onStart() {
        super.onStart()
        // --START on_start_sign_in--
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        // 取得上次登入的狀態
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account != null){
            updateUI(account)
        }

        //--END on_start_sign_in--
        Log.e("onStart: ", "onStart: ")
    }

    // 用戶GoogleSignInAccount後，您可以在活動的onActivityResult方法中為用戶獲取GoogleSignInAccount對象。
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    // GoogleSignInAccount對象包含有關登錄用戶的信息，例如用戶名。
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
//            mStatusTextView.setText(getString(R.string.signed_in_fmt, account.getDisplayName()));
//            val g_DisplayName = account.displayName //暱稱  account. google帳號所有的東西都在這邊 可以選
//            String g_Email=account.getEmail();  //信箱
//            String g_GivenName=account.getGivenName(); //Firstname
//            String g_FamilyName=account.getFamilyName(); //Last name

            // 中文名不能進聊天室
            index_name.text = account.displayName
//            index_name.text = account.email
            checkName()

            //-------改變圖像--------------
//            val User_IMAGE = account.photoUrl ?: return
//            img = findViewById<View>(R.id.google_icon) as CircleImgView
//            object : AsyncTask<String?, Void?, Bitmap?>() {
//                protected fun doInBackground(vararg params: String): Bitmap? {
//                    val url = params[0]
//                    return getBitmapFromURL(url)
//                }
//
//                override fun onPostExecute(result: Bitmap?) {
//                    img.setImageBitmap(result) //setImageBitmap
//                    super.onPostExecute(result)
//                }
//
//                override fun doInBackground(vararg params: String?): Bitmap? {
//                    TODO("Not yet implemented")
//                }
//            }.execute(User_IMAGE.toString().trim())
//            //            String g_id=account.getId();
//            findViewById<View>(R.id.sign_in_button).visibility = View.GONE
//            findViewById<View>(R.id.sign_out_and_disconnect).visibility = View.VISIBLE
        } else {
            index_name.text = ""
            checkName()
//            mStatusTextView.setText(R.string.signed_out)
//            findViewById<View>(R.id.sign_in_button).visibility = View.VISIBLE
//            findViewById<View>(R.id.sign_out_and_disconnect).visibility = View.GONE
        }
    }

    private fun initUniObserve() {
        viewModel.signUpLiveData.observe(this, {
            Log.e("it.body()", it.body().toString())
            if (it.body()?.State == true) {
                Toast.makeText(this, it.body()!!.Message, Toast.LENGTH_SHORT).show()
                signUpalertDialog.dismiss()
            }
        })

        viewModel.logInLiveData.observe(this, {
            Log.e("logInData", it.toString())
            Log.e("logInDataNickname", it.get(0).Nickname)
            if (it.get(0).Nickname.equals("")) {
                Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.login_failure), Toast.LENGTH_SHORT).show()
                logInalertDialog.dismiss()
                index_name.text = it.get(0).Nickname
                checkName()
            }
        })
    }

    private fun signOut() {
        mGoogleSignInClient!!.signOut() //登出
            .addOnCompleteListener(this) {
                //--START_EXCLUDE--
                updateUI(null) //登出動作寫在這
                // [END_EXCLUDE]
//                img.setImageResource(R.drawable.googleg_color) //還原圖示  換頭像
            }
    }

    // 開始遊戲
    private fun initButtons() {
        // 註冊按鈕
        btn_signup.setOnClickListener {
            // 自定義AlertDialog  也可以專寫一個class 繼承自定義AlertDialog 改寫然後處理他
            signUpalertDialog = AlertDialog.Builder(this).create()
            //取得自訂的版面。
            val inflater = LayoutInflater.from(this)
            val v: View = inflater.inflate(R.layout.dialog_signup, null)
            // 設置view
            signUpalertDialog.setView(v)

            // 確認
            v.dialog_signup_enter.setOnClickListener {

                val account = v.dialog_et_signup_account.text.trim().toString()
                val nickname = v.dialog_et_signup_nickname.text.trim().toString()
                val email = v.dialog_et_signup_email.text.trim().toString()
                val password = v.dialog_et_signup_password.text.trim().toString()
                val confirm_password  = v.dialog_et_signup_confirm_password.text.trim().toString()

                viewModel.signUpInsert(account, nickname, email, password)

            }
            // 取消
            v.dialog_signup_cancel.setOnClickListener {
                signUpalertDialog.dismiss()
            }

            // 點擊範圍外無反應
            signUpalertDialog.setCancelable(false)

            signUpalertDialog.show()

            // AlertDialog 用有設定好圓角的xml 顯示會無法顯示
            // https://stackoverflow.com/questions/16861310/android-dialog-rounded-corners-and-transparency
            signUpalertDialog.window?.setBackgroundDrawableResource(R.color.transparent)
        }
        // 登入按鈕
        btn_signin.setOnClickListener {
            logInalertDialog = AlertDialog.Builder(this).create()
            val inflater = LayoutInflater.from(this)
            val v: View = inflater.inflate(R.layout.dialog_login, null)
            logInalertDialog.setView(v)

            v.dialog_login_enter.setOnClickListener {

                val account = v.dialog_et_login_account.text.trim().toString()
                val password = v.dialog_et_login_password.text.trim().toString()

                viewModel.logIn(account, password)

//                index_name.text = account
//                checkName()
//                logInalertDialog.dismiss()

            }

            v.dialog_login_cancel.setOnClickListener {
                logInalertDialog.dismiss()
            }

//            v.dialog_enter.setOnClickListener {
//                val name = v.dialog_et_name.text.toString()
//                if(name.equals("")){
//                    Toast.makeText(
//                        this,
//                        getString(R.string.name_cannot_be_empty),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }else{
//                    alertDialog.dismiss()
//                }
//                index_name_title.text = "Hi "
//                index_name.text = name
//
//                Toast.makeText(this, "你已成功登入帳號!", Toast.LENGTH_SHORT).show()
//
//                checkName()
//            }

//            alertDialog.setCancelable(false)

            logInalertDialog.show()

            logInalertDialog.window?.setBackgroundDrawableResource(R.color.transparent)
        }
        // Google登入
        btn_google_signin.setOnClickListener {
            googleSignIn()
        }
        // 貪吃蛇遊戲
        btn_greedy_snake_game.setOnClickListener {
            val name = index_name.text.toString()
            val bundle = Bundle()
            val intent = Intent(this, GameActivity().javaClass)
            bundle.putString("name", name)
            intent.putExtra("name", bundle)
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
                            Toast.makeText(this@IndexActivity, getString(R.string.chatroom_login_success), Toast.LENGTH_SHORT).show()
                            Log.e("Login Success: ", p0.toString())
                        }
                    }

                    override fun onFailure(p0: ErrorInfo?) {
                        runOnUiThread {
                            Toast.makeText(this@IndexActivity, getString(R.string.chatroom_login_failure), Toast.LENGTH_SHORT).show()
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
                signOut()
                Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
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
        Log.e("name", name)
        if(name.equals("")){
            btn_signout.visibility = View.GONE
            btn_signup.visibility = View.VISIBLE
            btn_signin.visibility = View.VISIBLE
            btn_google_signin.visibility = View.VISIBLE
            btn_greedy_snake_game.visibility = View.GONE
            btn_greedy_snake_rank.visibility = View.GONE
            btn_chat_room.visibility = View.GONE
            index_name_title.text = getString(R.string.hello_visitor)
        }else{
            btn_signout.visibility = View.VISIBLE
            btn_signup.visibility = View.GONE
            btn_signin.visibility = View.GONE
            btn_google_signin.visibility = View.GONE
            btn_greedy_snake_game.visibility = View.VISIBLE
            btn_greedy_snake_rank.visibility = View.VISIBLE
            btn_chat_room.visibility = View.VISIBLE
            index_name_title.text = getString(R.string.hello)
        }
    }

}