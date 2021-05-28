package tw.tim.mvvm_greedy_snake.api

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// Firebase google官方教學
// 1. Tools - Firebase - Cloud Messaging - 連結
// https://firebase.google.com/docs/cloud-messaging/android/client#kotlin+ktx

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.notification != null) {
            Log.i("MyFirebaseService", "title " + remoteMessage.notification!!.title);
            Log.i("MyFirebaseService", "body " + remoteMessage.notification!!.body);
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("MyFirebaseService", token)
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        // 可以傳token給後端 再透過後端傳送token message給Firebase 最後Firebase再推播下來
        // TODO: Implement this method to send token to your app server.
    }

}