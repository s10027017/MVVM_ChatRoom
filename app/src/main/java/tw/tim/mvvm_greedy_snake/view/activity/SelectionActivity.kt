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
import kotlinx.android.synthetic.main.activity_selection.*
import kotlinx.android.synthetic.main.chat_room_login_dialog.view.*
import tw.tim.mvvm_greedy_snake.R
import tw.tim.mvvm_greedy_snake.rtmtutorial.AGApplication
import tw.tim.mvvm_greedy_snake.rtmtutorial.ChatManager
import tw.tim.mvvm_greedy_snake.utils.MessageUtil

class SelectionActivity : Activity() {
    private val CHAT_REQUEST_CODE = 1


    private var mIsPeerToPeerMode = true // whether peer to peer mode or channel mode\

    private var mTargetName: String? = null
    private var mUserId: String? = null

    private var mRtmClient: RtmClient? = null
    private lateinit var mChatManager: ChatManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        mChatManager = AGApplication.the().getChatManager()
        mRtmClient = mChatManager.getRtmClient()

        initUIAndData()
    }

    private fun initUIAndData() {
        val intent: Intent = getIntent()
        mUserId = intent.getStringExtra(MessageUtil.INTENT_EXTRA_USER_ID)
        val modeGroup: RadioGroup = findViewById<RadioGroup>(R.id.mode_radio_group)
        modeGroup.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.peer_radio_button -> {
                    mIsPeerToPeerMode = true
                    selection_title.setText(getString(R.string.title_peer_msg))
                    selection_chat_btn.setText(getString(R.string.btn_chat))
                    selection_name.setHint(getString(R.string.hint_friend))
                }
                R.id.selection_tab_channel -> {
                    mIsPeerToPeerMode = false
                    selection_title.setText(getString(R.string.title_channel_message))
                    selection_chat_btn.setText(getString(R.string.btn_join))
                    selection_name.setHint(getString(R.string.hint_channel))
                }
            }
        }
        val peerMode: RadioButton = findViewById<RadioButton>(R.id.peer_radio_button)
        peerMode.isChecked = true
        val mOfflineMsgCheck: AppCompatCheckBox = findViewById(R.id.offline_msg_check)
        mOfflineMsgCheck.isChecked = mChatManager?.isOfflineMessageEnabled() == true
        mOfflineMsgCheck.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            mChatManager?.enableOfflineMessage(
                isChecked
            )
        }
    }

    fun onClickChat(v: View?) {
        mTargetName = selection_name!!.text.toString()
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
        intent.putExtra(MessageUtil.INTENT_EXTRA_IS_PEER_MODE, mIsPeerToPeerMode)
        intent.putExtra(MessageUtil.INTENT_EXTRA_TARGET_NAME, mTargetName)
        intent.putExtra(MessageUtil.INTENT_EXTRA_USER_ID, mUserId)
        startActivityForResult(intent, CHAT_REQUEST_CODE)
    }

    override fun onResume() {
        super.onResume()
        selection_chat_btn!!.isEnabled = true
    }

    fun onClickFinish(v: View?) {
        val alertDialog: AlertDialog = AlertDialog.Builder(this).create()
        val inflater = LayoutInflater.from(this)
        val v: View = inflater.inflate(R.layout.chat_room_logout_dialog, null)
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
            Toast.makeText(this, "你已登出聊天室", Toast.LENGTH_SHORT).show()
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