package tw.tim.mvvm_greedy_snake.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.agora.rtm.*
import kotlinx.android.synthetic.main.activity_chat_room.*
import kotlinx.android.synthetic.main.activity_chat_room.toolbar
import kotlinx.android.synthetic.main.activity_rank.*
import kotlinx.android.synthetic.main.dialog_chat_room_logout.view.*
import kotlinx.android.synthetic.main.item_friends.view.*
import kotlinx.android.synthetic.main.item_messages.view.*
import tw.tim.mvvm_greedy_snake.R
import tw.tim.mvvm_greedy_snake.model.bean.MessageBean
import tw.tim.mvvm_greedy_snake.model.bean.MessageListBean
import tw.tim.mvvm_greedy_snake.rtmtutorial.AGApplication
import tw.tim.mvvm_greedy_snake.rtmtutorial.ChatManager
import tw.tim.mvvm_greedy_snake.utils.MessageUtil
import java.util.*
import kotlin.collections.HashMap

class ChatRoomActivity : AppCompatActivity() {
    private val CHAT_REQUEST_CODE = 1

    private var mIsPeerToPeerMode = true // whether peer to peer mode or channel mode\

    private var mTargetName: String? = null
    private var mUserId: String? = null

    private  var mChatManager: ChatManager = AGApplication.the().chatManager
    private var mRtmClient: RtmClient? = mChatManager.rtmClient

    private val friendsAdapter = FriendsApapter()
    private val channelAdapter = ChannelApapter()
    private val messagesAdapter = MessagesApapter()

//    private var Messages: Map<String, List<RtmMessage>>? = null

    private var Messages: Map<String, List<RtmMessage>> = HashMap<String, List<RtmMessage>>()
    private var onlineMessages: Map<String, List<RtmMessage>> = HashMap<String, List<RtmMessage>>()
    private lateinit var historyMessages: List<RtmMessage>
    private val friendslist = mutableListOf<String>()

    // https://www.jianshu.com/p/125ac7848077  java kotlin差異 kotlin中 ArrayList無法調用clear addall 要用MutableList
//    private val mMessageBeanList: List<MessageBean> = ArrayList()
    private val mMessageBeanList: MutableList<MessageBean> = ArrayList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

//        initMessages()
        Messages()
        initActionBar()
        initRecycleView()
        initUIAndData()

        Log.e("onCreate: ", "onCreate: ")

    }

//    override fun onResume() {
//        super.onResume()
//        Log.e("onResume: ", "onResume: ")
//        initMessages()
//    }

    private fun Messages() {
//        mChatManager = AGApplication.the().chatManager
//        mRtmClient = mChatManager.rtmClient

//        // 要做接收全部訊息的 但目前只能抓到未讀訊息
//        Messages = mChatManager.allMessages
//
//        Log.e("Messages", Messages.toString())
//        Log.e("Messageskeys", Messages.keys.toString())
//        Log.e("Messagesvalues", Messages.values.toString())
//
//        // 只會出現最後一筆
//        onlineMessages = mChatManager.onlineMessages
//        // 再聊天室外接收到的未讀訊息
//        historyMessages = mChatManager.getAllOfflineMessages("456")
//
//        Log.e("onlineMessages", onlineMessages.toString())
//        Log.e("historyMessages", historyMessages.toString())

        if(friendslist != null){
            messagesAdapter.updateNameList(friendslist)
        }

    }

    private fun initMessages() {
        mChatManager = AGApplication.the().chatManager
        mRtmClient = mChatManager.rtmClient

        Messages = mChatManager.allMessages
        Log.e("ChatRoomActivity_Messages", Messages.toString())

        for (i in 0 until friendslist.size-1){
//            Log.e("friendslist.get(i)",friendslist.get(i))
            Log.e("friendslist[i]", friendslist[i])

            // load history chat records  讀取歷史訊息 帶入對方名字   第二次進來才有資料
            val messageListBean = MessageUtil.getExistMessageListBean(friendslist[i])
            val messageslist = mutableListOf<String>()
//            var message_name: Map<String, List<MessageBean>>? = HashMap<String, List<RtmMessage>>()
            val message_name: HashMap<String, List<MessageBean>> = HashMap<String, List<MessageBean>>()
//            val hashMap:HashMap<Int,String> = HashMap<Int,String>()

            if (messageListBean != null) {
//                mMessageBeanList.addAll(messageListBean.messageBeanList)
//                messageslist.addAll(listOf(friendslist[i]))
//                messagesAdapter.update(mMessageBeanList, messageslist)

                message_name.put(friendslist[i], messageListBean.messageBeanList)
                messagesAdapter.updatetest(message_name)

                Log.e("message_name", message_name.toString())
                Log.e("history_friendslist[i]", friendslist[i].toString())
                Log.e("messageListBean_messageBeanList", messageListBean.messageBeanList.toString())
                Log.e("testhowmany", "testhowmany")
//                mMessageBeanList.clear()
            }

            // load offline messages since last chat with this peer.  讀取加載後要清除   第一次進來  messageListBean -> null
            // Then clear cached offline messages from message pool
            // since they are already consumed.

            // 這邊不該做 removeAllOfflineMessages 的動作
//            val offlineMessageBean = MessageListBean(friendslist[i], mChatManager)
//            mMessageBeanList.addAll(offlineMessageBean.messageBeanList)
//            mChatManager.removeAllOfflineMessages(friendslist[i])

            Log.e("mMessageBeanList", mMessageBeanList.toString())

//            val bean: MessageBean = mMessageBeanList.get(i)
//            if (bean.isBeSelf) {
////                holder.textViewSelfName.setText(bean.account)
//            } else {
////                holder.textViewOtherName.setText(bean.account)
//                if (bean.background != 0) {
////                    holder.textViewOtherName.setBackgroundResource(bean.background)
//                }
//            }
//
//            holder.itemView.setOnClickListener(View.OnClickListener { v: View? -> if (listener != null) listener.onItemClick(bean) })
//
//            // Error : Attempt to invoke virtual method 'int io.agora.rtm.RtmMessage.getMessageType()' on a null object reference
//            val rtmMessage = bean.message
//
//            Log.e("rtmMessage",rtmMessage.toString())
        }

        Log.e("friendslist", friendslist.toString())
    }

    private fun initActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_white_arrow_left_40)
        }
    }

    private fun initRecycleView() {
        // 私聊
        val friends_layoutManager = LinearLayoutManager(this)
        ry_friends.layoutManager = friends_layoutManager
        ry_friends.adapter = friendsAdapter

        // 群聊
        val channel_layoutManager = LinearLayoutManager(this)
        ry_channel.layoutManager = channel_layoutManager
        ry_channel.adapter = channelAdapter

        // 訊息
        val messages_layoutManager = LinearLayoutManager(this)
        ry_messages.layoutManager = messages_layoutManager
        ry_messages.adapter = messagesAdapter
    }

    private fun initUIAndData() {
        val intent: Intent = intent
        // 枚舉或者其他方式也許也可以做?
        mUserId = intent.getStringExtra(MessageUtil.INTENT_EXTRA_USER_ID)
        tv_username.text = mUserId
        // RadioButton
        val modeGroup: RadioGroup = findViewById<RadioGroup>(R.id.mode_radio_group)
        modeGroup.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                // 點選私聊頁面
                R.id.selection_friends -> {
                    // 一開始為私聊狀態
                    mIsPeerToPeerMode = true
                    tv_toolbar.text = getString(R.string.friends_list)
                    selection_chat_btn.text = getString(R.string.add_friends)
                    selection_name.hint = getString(R.string.friends_name)

                    cy_chat_room.visibility = View.VISIBLE
                    ry_friends.visibility = View.VISIBLE
                    ry_channel.visibility = View.GONE
                    ry_messages.visibility = View.GONE
                }
                // 點選群聊頁面
                R.id.selection_channel -> {
                    mIsPeerToPeerMode = false
                    tv_toolbar.text = getString(R.string.group)
                    selection_chat_btn.text = getString(R.string.add_group)
                    selection_name.hint = getString(R.string.group_name)

                    cy_chat_room.visibility = View.VISIBLE
                    ry_friends.visibility = View.GONE
                    ry_channel.visibility = View.VISIBLE
                    ry_messages.visibility = View.GONE
                }
                // 點選訊息頁面
                R.id.selection_messages -> {
                    tv_toolbar.text = getString(R.string.messages)
                    cy_chat_room.visibility = View.GONE
                    ry_messages.visibility = View.VISIBLE

//                    initMessages()
                    Messages()

//                    mIsPeerToPeerMode = false
//                    tv_toolbar.setText(getString(R.string.title_channel_message))
//                    selection_chat_btn.setText(getString(R.string.btn_join))
//                    selection_name.setHint(getString(R.string.hint_channel))
                }
            }
        }
        // 私聊 預設狀態
        selection_friends.isChecked = true
        // 離線訊息
        val mOfflineMsgCheck: AppCompatCheckBox = findViewById(R.id.offline_msg_check)
        mOfflineMsgCheck.isChecked = mChatManager?.isOfflineMessageEnabled == true
        mOfflineMsgCheck.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
//            mChatManager?.enableOfflineMessage(isChecked)  允許離線訊息
            mChatManager?.enableOfflineMessage(true)
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
//            selection_chat_btn!!.isEnabled = false
            if(mIsPeerToPeerMode){
                friendsAdapter.update(mTargetName!!)
                selection_name.text = null

                friendslist.addAll(listOf(mTargetName!!))
//                val offlineMessageBean = MessageListBean(mTargetName, mChatManager)
//                mMessageBeanList.addAll(offlineMessageBean.messageBeanList)
//                mChatManager.removeAllOfflineMessages(mTargetName)
//                Log.e("mMessageBeanList",mMessageBeanList.toString())
            }else{
                channelAdapter.update(mTargetName!!)
                selection_name.text = null

            }

//            jumpToMessageActivity()
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

//    override fun onResume() {
//        super.onResume()
//        selection_chat_btn!!.isEnabled = true
//    }

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
        mChatManager?.removeAllMessages()
        runOnUiThread {
            Toast.makeText(this, "聊天室登出成功", Toast.LENGTH_SHORT).show()
            Log.e("Login out ", "Login out ")
        }

//        mRtmClient?.logout(object : ResultCallback<Void?> {
//            override fun onSuccess(p0: Void?) {
//                runOnUiThread {
//                    Toast.makeText(this@ChatRoomActivity, "聊天室登出成功", Toast.LENGTH_SHORT).show()
//                    Log.e("Login out ", "Login out ")
//                }
//            }
//
//            override fun onFailure(p0: ErrorInfo?) {
//                Log.d("Chat", "聊天室登出失敗")
//            }
//        })

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
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
        }
        return super.onOptionsItemSelected(item)
    }

    inner class FriendsApapter : RecyclerView.Adapter<FriendsApapter.FriendsApapterViewHolder>() {

        val list = mutableListOf<String>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsApapterViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_friends, parent, false)
            return FriendsApapterViewHolder(v)
        }

        override fun onBindViewHolder(holder: FriendsApapterViewHolder, position: Int) {
            val rootView = holder.itemView
            val data = list[position]

            rootView.tv_friends.text = data

            rootView.bg_item.setOnClickListener {
                val intent = Intent(this@ChatRoomActivity, MessageActivity::class.java)
                // true為私聊 false為群聊
                intent.putExtra(MessageUtil.INTENT_EXTRA_IS_PEER_MODE, mIsPeerToPeerMode)
                // 聊天室名稱
                intent.putExtra(MessageUtil.INTENT_EXTRA_TARGET_NAME, data)
                // 使用者名稱
                intent.putExtra(MessageUtil.INTENT_EXTRA_USER_ID, mUserId)
                startActivityForResult(intent, CHAT_REQUEST_CODE)
            }

        }

        override fun getItemCount(): Int {
            return list.size
        }

        fun update(updateList: String) {
//            list.clear()
            list.addAll(listOf(updateList))
            notifyDataSetChanged()
        }

        inner class FriendsApapterViewHolder(v: View) : RecyclerView.ViewHolder(v)

    }

    inner class ChannelApapter : RecyclerView.Adapter<ChannelApapter.ChannelApapterViewHolder>() {

        val list = mutableListOf<String>()

        val channellist = mutableListOf<String>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelApapterViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_friends, parent, false)
            return ChannelApapterViewHolder(v)
        }

        override fun onBindViewHolder(holder: ChannelApapterViewHolder, position: Int) {
            val rootView = holder.itemView
            val data = list[position]

            rootView.tv_friends.text = data

            rootView.bg_item.setOnClickListener {

                // load offline messages since last chat with this peer.  讀取加載後要清除
                // Then clear cached offline messages from message pool
                // since they are already consumed.
                val offlineMessageBean = MessageListBean(data, mChatManager)
//                mMessageBeanList.addAll(offlineMessageBean.messageBeanList)
                mMessageBeanList.forEach {
                    channellist.add(offlineMessageBean.messageBeanList.toString())
                }
                Log.e("channellist", channellist.toString())
//                mChatManager.removeAllOfflineMessages(data)

                val intent = Intent(this@ChatRoomActivity, MessageActivity::class.java)
                // true為私聊 false為群聊
                intent.putExtra(MessageUtil.INTENT_EXTRA_IS_PEER_MODE, mIsPeerToPeerMode)
                // 聊天室名稱
                intent.putExtra(MessageUtil.INTENT_EXTRA_TARGET_NAME, data)
                // 使用者名稱
                intent.putExtra(MessageUtil.INTENT_EXTRA_USER_ID, mUserId)
                startActivityForResult(intent, CHAT_REQUEST_CODE)
            }

        }

        override fun getItemCount(): Int {
            return list.size
        }

        fun update(updateList: String) {
//            list.clear()
            list.addAll(listOf(updateList))
            notifyDataSetChanged()
        }

        inner class ChannelApapterViewHolder(v: View) : RecyclerView.ViewHolder(v)

    }

    inner class MessagesApapter : RecyclerView.Adapter<MessagesApapter.MessagesApapterViewHolder>() {

        val messageslist = mutableListOf<MessageBean>()
        var namelist = mutableListOf<String>()
        var message_namelist: HashMap<String, List<MessageBean>> = HashMap<String, List<MessageBean>>()

//        private val mMessageBeanList: MutableList<MessageBean> = ArrayList()   kotlin list不能add 要用MutableList
        val messageBeanList: MutableList<MessageBean> = ArrayList()
        var adapterHistoryMessages: MutableList<RtmMessage> = ArrayList()

//        private var mRtmClientAdapter: RtmClient? = rtmMessagePool?.rtmClient
        val arrayList = mutableSetOf<String>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesApapterViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_messages, parent, false)
            return MessagesApapterViewHolder(v)
        }

        override fun onBindViewHolder(holder: MessagesApapterViewHolder, position: Int) {

            var state = false
            val rootView = holder.itemView

//            val messages = messageslist[position]
            val name = namelist[position]
//            val message_name = message_namelist[position]

            arrayList.add(namelist[position])

            Log.e("position", position.toString())
            Log.e("name", name)
            Log.e("outarrayList", arrayList.toString())


            // 試做沒資料不顯示 但有問題 資料刪不掉 要另外處理 再List上重新排序
//            if(rootView.tv_name.text == "789"){
//                rootView.visibility = View.GONE
//            }

            mRtmClient?.queryPeersOnlineStatus(arrayList, object : ResultCallback<Map<String, Boolean>> {
                override fun onSuccess(p0: Map<String, Boolean>?) {
                    runOnUiThread {
                        Log.e("p0", "$p0")
                        if (p0 != null) {
                            // 會閃退 會因為p0裡面沒有元素而閃退
//                            state = p0[arrayList.elementAt(0)] == true
                            state = p0.containsValue(true)

//                            Log.e("arrayList", arrayList.elementAt(0))
                            Log.e("state", state.toString())

                            if (state) {
                                rootView.ic_online_circle2.visibility = View.VISIBLE
                            }else{
                                rootView.ic_online_circle2.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
                override fun onFailure(p0: ErrorInfo?) {
                    rootView.ic_online_circle2.visibility = View.GONE
                }
            })

            // 再聊天室外接收到的訊息
//            private lateinit var historyMessages: List<RtmMessage>
            adapterHistoryMessages = mChatManager.getAllHistoryMessages(name)
            Log.e("MessagesApapter_adapterHistoryMessages", adapterHistoryMessages.toString())

            // 只能抓到再聊天室裡接收到的訊息  這邊會把紀錄給清掉
            // load history chat records  讀取歷史訊息 帶入對方名字   第一次先寫入入 第二次進來才有資料
            var messageListBean: MessageListBean? = null
            messageListBean = MessageUtil.getTestMessageListBean(name)
//            Log.e("MessagesApapter_messageListBean", messageListBean.toString())
            if (messageListBean != null) {
                messageBeanList.addAll(messageListBean.messageBeanList)
            }

            // 有資料 但閃退
//            Log.e("MessagesApapter_messageListBean", messageListBean.messageBeanList.toString())

            // 聊天室外接收到的訊息 可以抓到  在外面抓的話 再進聊天室會抓不到
            // adapterHistoryMessages 判斷有誤 不能寫 != null
            if(adapterHistoryMessages.isNotEmpty()){
                messageBeanList.clear()
                for (m in adapterHistoryMessages) {
                    // All offline messages are from peer users
                    val bean = MessageBean(name, m, false)
                    messageBeanList.add(bean)
                }
                Log.e("MessagesApapter_adapterHistoryMessages_messageBeanList", messageBeanList.toString())

            }

            var a = 0
            var aText = ""
            for(i in 0..messageBeanList.size-1){
                val bean = messageBeanList[i]
                val rtmMessage = bean.message

                Log.e("MessagesApapter_bean", bean.toString())
                Log.e("MessagesApapter_rtmMessage", rtmMessage.toString())
                when(rtmMessage.messageType){
                    RtmMessageType.TEXT -> {
                        aText = rtmMessage.getText()
                        if(adapterHistoryMessages.isNotEmpty()){
                            a++
                        }
                        Log.e("MessagesApapter_a", a.toString())
                        Log.e("MessagesApapter_aText", aText)
                    }
                }
            }

            rootView.tv_name.text = name
            rootView.tv_message_nm.text = a.toString()
            rootView.tv_last_message.text = aText

            adapterHistoryMessages.clear()
            messageBeanList.clear()
            arrayList.clear()

            rootView.item_chat_list.setOnClickListener {
                val intent = Intent(this@ChatRoomActivity, MessageActivity::class.java)
                // true為私聊 false為群聊
                intent.putExtra(MessageUtil.INTENT_EXTRA_IS_PEER_MODE, mIsPeerToPeerMode)
                // 聊天室名稱
                intent.putExtra(MessageUtil.INTENT_EXTRA_TARGET_NAME, name)
                // 使用者名稱
                intent.putExtra(MessageUtil.INTENT_EXTRA_USER_ID, mUserId)
                startActivityForResult(intent, CHAT_REQUEST_CODE)
            }

//            Log.e("data", messages.toString())

//            Log.e("namelist[0]",namelist[0])
//            Log.e("namelist[1]",namelist[1])

//            val bean: MessageBean = messageslist[position]
//            if(bean.isBeSelf){
//
//            }else{
//                // todo
//            }

//            val name = namelist[position]
//
//            rootView.tv_name.text = name
//            rootView.tv_last_message.text = messages.toString()

//             Log.e("MessagesApapter_messages", messages.toString())

//            rootView.tv_name.text = message_namelist.keys.toString()

//            Log.e("message_namelist[position].toString()", message_namelist[position].toString())  null
//            Log.e("message_namelist.keys.toString()", message_namelist.keys.toString())
//            rootView.tv_name.text = message_namelist.keys.toString()

//            Log.e("message_namelist.values.toString()", message_namelist.values.toString())
//            rootView.tv_message_nm.text = (message_namelist.values.size + 1).toString()

//            rootView.tv_friends.text = data
//
//            rootView.bg_item.setOnClickListener {

                // load offline messages since last chat with this peer.  讀取加載後要清除
                // Then clear cached offline messages from message pool
                // since they are already consumed.
//                val offlineMessageBean = MessageListBean(data, mChatManager)
////                mMessageBeanList.addAll(offlineMessageBean.messageBeanList)
//                mMessageBeanList.forEach {
//                    testlist.add(offlineMessageBean.messageBeanList.toString())
//                }
//                Log.e("testlist", testlist.toString())
//                mChatManager.removeAllOfflineMessages(data)

//                val intent = Intent(this@ChatRoomActivity, MessageActivity::class.java)
//                // true為私聊 false為群聊
//                intent.putExtra(MessageUtil.INTENT_EXTRA_IS_PEER_MODE, mIsPeerToPeerMode)
//                // 聊天室名稱
//                intent.putExtra(MessageUtil.INTENT_EXTRA_TARGET_NAME, data)
//                // 使用者名稱
//                intent.putExtra(MessageUtil.INTENT_EXTRA_USER_ID, mUserId)
//                startActivityForResult(intent, CHAT_REQUEST_CODE)
//            }

        }

        override fun getItemCount(): Int {
            return namelist.size
        }

        fun update(updateList: MutableList<MessageBean>, name: MutableList<String>) {
//            list.clear()
            messageslist.addAll(updateList)
            namelist.addAll(name)
            notifyDataSetChanged()
        }

        fun updatetest(test: HashMap<String, List<MessageBean>> = HashMap<String, List<MessageBean>>()){
            message_namelist = test
            notifyDataSetChanged()
        }
        // friendslist = mutableListOf<String>()
        fun updateNameList(list: MutableList<String>){
            namelist = list
            notifyDataSetChanged()
        }

        inner class MessagesApapterViewHolder(v: View) : RecyclerView.ViewHolder(v)

    }

}