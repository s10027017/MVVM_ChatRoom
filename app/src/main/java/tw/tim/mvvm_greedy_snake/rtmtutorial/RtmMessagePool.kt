package tw.tim.mvvm_greedy_snake.rtmtutorial

import io.agora.rtm.RtmMessage
import java.util.*

class RtmMessagePool {
    private val mOfflineMessageMap: MutableMap<String, MutableList<RtmMessage>?> = HashMap()

    fun insertOfflineMessage(rtmMessage: RtmMessage, peerId: String) {
        val contains = mOfflineMessageMap.containsKey(peerId)
        val list = if (contains) mOfflineMessageMap[peerId] else ArrayList()
        list?.add(rtmMessage)
        if (!contains) {
            mOfflineMessageMap[peerId] = list
        }
    }

    fun getAllOfflineMessages(peerId: String): List<RtmMessage>? {
        return if (mOfflineMessageMap.containsKey(peerId)) mOfflineMessageMap[peerId] else ArrayList()
    }

    fun removeAllOfflineMessages(peerId: String) {
        mOfflineMessageMap.remove(peerId)
    }
}