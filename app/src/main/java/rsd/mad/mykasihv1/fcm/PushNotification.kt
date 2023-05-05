package rsd.mad.mykasihv1.fcm

data class PushNotification(
    val data: NotificationData,
    val to: String
)