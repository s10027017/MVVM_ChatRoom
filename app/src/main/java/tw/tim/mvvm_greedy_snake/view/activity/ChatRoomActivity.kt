package tw.tim.mvvm_greedy_snake.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatCheckBox
import io.agora.rtm.RtmClient
import kotlinx.android.synthetic.main.activity_chat_room.*
import kotlinx.android.synthetic.main.dialog_chat_room_logout.view.*
import tw.tim.mvvm_greedy_snake.R
import tw.tim.mvvm_greedy_snake.rtmtutorial.AGApplication
import tw.tim.mvvm_greedy_snake.rtmtutorial.ChatManager
import tw.tim.mvvm_greedy_snake.utils.MessageUtil

class ChatRoomActivity : Activity() {
    private val CHAT_REQUEST_CODE = 1

    private var mIsPeerToPeerMode = true // whether peer to peer mode or channel mode\

    private var mTargetName: String? = null
    private var mUserId: String? = null

    private var mRtmClient: RtmClient? = null
    private lateinit var mChatManager: ChatManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        mChatManager = AGApplication.the().chatManager
        mRtmClient = mChatManager.rtmClient

        initUIAndData()
    }

    private fun initUIAndData() {
        val intent: Intent = intent
        // 枚舉或者其他方式也許也可以做?
        mUserId = intent.getStringExtra(MessageUtil.INTENT_EXTRA_USER_ID)
        // RadioButton
        val modeGroup: RadioGroup = findViewById<RadioGroup>(R.id.mode_radio_group)
        modeGroup.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.peer_radio_button -> {
                    // 一開始為私聊狀態
                    mIsPeerToPeerMode = true
                    selection_title.text = getString(R.string.title_peer_msg)
                    selection_chat_btn.text = getString(R.string.btn_chat)
                    selection_name.hint = getString(R.string.hint_friend)
                }
                // 點選群聊
                R.id.selection_tab_channel -> {
                    mIsPeerToPeerMode = false
                    selection_title.setText(getString(R.string.title_channel_message))
                    selection_chat_btn.setText(getString(R.string.btn_join))
                    selection_name.setHint(getString(R.string.hint_channel))
                }
            }
        }
        // 私聊
        val peerMode: RadioButton = findViewById<RadioButton>(R.id.peer_radio_button)
        peerMode.isChecked = true
        // 離線訊息
        val mOfflineMsgCheck: AppCompatCheckBox = findViewById(R.id.offline_msg_check)
        mOfflineMsgCheck.isChecked = mChatManager?.isOfflineMessageEnabled == true
        mOfflineMsgCheck.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            mChatManager?.enableOfflineMessage(isChecked)
        }
    }

    fun onClickChat(v: View?) {
        mTargetName = selection_name!!.text.toString()
        // 不得為空 長度>64 空格 null 模式名字ID一樣
        if (mTargetName == "") {
            showToast(getString(if (mIsPeerToPeerMode) R.string.account_empty else R.string.channel_name_empty))
        } else if (mTargetName!!.length >= MessageUtil.MAX_INPUT_NAME_LENGTH) {
            showToast(getString(if (mIsPeerToPeerMode) R.string.account_too_long else R.string.channel_name_too_long))
        } else if (mTargetName!!.startsWith(" ")) {
            showToast(getString(if (mIsPeerToPeerMode) R.string.account_starts_with_space else R.string.channel_name_starts_with_space))
        } else if (mTargetName == "null") {
            showToast(getString(if (mIsPeerToPeerMode) R.string.account_literal_null else R.string.channel_name_literal_null))
        } else if (mIsPeerToPeerMode && mTargetName == mUserId) {
            showToast(getString(R.string.account_cannot_be_yourself))
        } else {
            selection_chat_btn!!.isEnabled = false
            jumpToMessageActivity()
        }
    }

    private fun jumpToMessageActivity() {
        val intent = Intent(this, MessageActivity::class.java)
        // true為私聊 false為群聊
        intent.putExtra(MessageUtil.INTENT_EXTRA_IS_PEER_MODE, mIsPeerToPeerMode)
        // 聊天室名稱
        intent.putExtra(MessageUtil.INTENT_EXTRA_TARGET_NAME, mTargetName)
        // 使用者名稱
        intent.putExtra(MessageUtil.INTENT_EXTRA_USER_ID, mUserId)
        startActivityForResult(intent, CHAT_REQUEST_CODE)
    }

    override fun onResume() {
        super.onResume()
        selection_chat_btn!!.isEnabled = true
    }

    fun onClickFinish(v: View?) {
        // 登出AlertDialog
        val alertDialog: AlertDialog = AlertDialog.Builder(this).create()
        val inflater = LayoutInflater.from(this)
        val v: View = inflater.inflate(R.layout.dialog_chat_room_logout, null)
        alertDialog.setView(v)
        v.dialog_chat_confirm.setOnClickListener {
            initLogout()
            alertDialog.dismiss()
            finish()
        }
        v.dialog_chat_cancel.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(R.color.transparent)

    }

    private fun initLogout() {
        mRtmClient!!.logout(null)
        MessageUtil.cleanMessageListBeanList()
        runOnUiThread {
            Toast.makeText(this, "聊天室登出成功", Toast.LENGTH_SHORT).show()
            Log.e("Login out ", "Login out ")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHAT_REQUEST_CODE) {
            if (resultCode == MessageUtil.ACTIVITY_RESULT_CONN_ABORTED) {
                finish()
            }
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}