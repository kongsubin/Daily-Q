package com.kongsub.dailyq.messaging

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kongsub.dailyq.Notifier

class MessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.data.let {
            when (it["type"]) {
                "follow" -> {
                    Notifier.showFollowNotification(this, it["username"]!!)
                }
                "answer" -> {
                    Notifier.showAnswerNotification(this, it["username"]!!)
                }
            }
        }
    }
}