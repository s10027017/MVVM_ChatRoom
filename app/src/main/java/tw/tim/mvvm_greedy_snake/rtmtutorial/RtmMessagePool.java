package tw.tim.mvvm_greedy_snake.rtmtutorial;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.rtm.RtmMessage;
import tw.tim.mvvm_greedy_snake.model.bean.MessageListBean;

/**
 * Receives and manages messages from RTM engine.
 * 不再聊天室外接收到的訊息
 */
public class RtmMessagePool {
    private Map<String, List<RtmMessage>> mOfflineMessageMap = new HashMap<>();
    private Map<String, List<RtmMessage>> mHistoryMessageMap = new HashMap<>();
    private Map<String, List<RtmMessage>> mOnlineMessageMap = new HashMap<>();


    void insertOfflineMessage(RtmMessage rtmMessage, String peerId ,Boolean online) {
        // 帶ID進去 HashMap裡面有ID的資料為true
        boolean contains = mOfflineMessageMap.containsKey(peerId);
        boolean onlinecontains = mOnlineMessageMap.containsKey(peerId);
        List<RtmMessage> list = contains ? mOfflineMessageMap.get(peerId) : new ArrayList<>();
        // 用來接收 再聊天室外接收到的訊息 不然會把要在聊天室裡的訊息吃掉
        List<RtmMessage> historylist = contains ? mHistoryMessageMap.get(peerId) : new ArrayList<>();
        List<RtmMessage> onlinelist = new ArrayList<>();

//        if(online){
//            mOnlineMessageMap.put(peerId, list);
//            // 聊天室訊息會*2
////            mHistoryMessageMap.put(peerId, list);
//            Log.e("RtmMessagePool_mOnlineMessageMap",mOnlineMessageMap.toString());
//        }else {
        if(online){
//            if (onlinelist != null) {
                onlinelist.add(rtmMessage);
//            }
//            if(!onlinecontains){
                // 只會存最後一筆 可以只抓最後一筆的資料 (再聊天室裡)
                mOnlineMessageMap.put(peerId, onlinelist);
                Log.e("RtmMessagePool_mOnlineMessageMap",mOnlineMessageMap.toString());
//            }
        }else{
            // 有訊息再繼續+到list裡來
            if (list != null) {
                list.add(rtmMessage);
                historylist.add(rtmMessage);
            }
            // 判別的時候才會執行這邊 DEBUG看
            if (!contains) {
                mOfflineMessageMap.put(peerId, list);
                mHistoryMessageMap.put(peerId, historylist);
                Log.e("RtmMessagePool_mOfflineMessageMap",mOfflineMessageMap.toString());
                Log.e("RtmMessagePool_mHistoryMessageMap",mHistoryMessageMap.toString());

//                if(online){
//                    mOnlineMessageMap.put(peerId, list);
//                    Log.e("RtmMessagePool_mOnlineMessageMap",mOnlineMessageMap.toString());
//                }

            }
        }


//            if(online){
                // 只能一次抓一筆
//                if (!contains) {
//                    mOnlineMessageMap.put(peerId, list);
//                    Log.e("RtmMessagePool_mOnlineMessageMap",mOnlineMessageMap.toString());
//                }
//            }else{
                // 判斷mOfflineMessageMap 有無資料

//            }


//        }

    }

    List<RtmMessage> getAllOfflineMessages(String peerId) {
        return mOfflineMessageMap.containsKey(peerId) ?
                mOfflineMessageMap.get(peerId) : new ArrayList<>();
    }

    List<RtmMessage> getAllHistoryMessages(String peerId) {
        return mHistoryMessageMap.containsKey(peerId) ?
                mHistoryMessageMap.get(peerId) : new ArrayList<>();
    }

    Map<String, List<RtmMessage>> getAllMessages(){
        return mHistoryMessageMap;
    }

    Map<String, List<RtmMessage>> getOnlineMessages(){
        return mOnlineMessageMap;
    }

    // 當離開聊天室 移除離線訊息 可做未讀訊息用
    void removeAllOfflineMessages(String peerId) {
        mOfflineMessageMap.remove(peerId);
    }

    // 移除所有訊息
    void removeAllMessages() {
        mOfflineMessageMap.clear();
        mHistoryMessageMap.clear();
    }

}
