package com.bsw.coursework2000.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bsw.coursework2000.MainActivity;
import com.bsw.coursework2000.R;

public class NotificationHelper {

    private static final String ID = "SeaViewNotificationID";

    private static int notificationIdCounter = 0;

    //gives each notification a unique id
    public static int generateUniqueNotificationId() {
        return notificationIdCounter++;
    }

    public static void initializeNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL = "SeaViewNotificationChannel";
            NotificationChannel channel = new NotificationChannel(
                    ID,
                    CHANNEL,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Handles all notifications from the SeaView app.");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotification(Context context, String title, String content) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            //notification intent
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ID)
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    //When the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(generateUniqueNotificationId(), builder.build());
        }
    }

    public void notifyBookingSuccess(Context context, String date) {
        createNotification(context, "My Reservation", "Your reservation for the " + date +" was successful.");
    }

    public void notifyReviewThanks(Context context) {
        createNotification(context, "Review", "Thank you for leaving a review.");
    }

    public void notifyUpdatedBooking(Context context) {
        createNotification(context, "My Reservation", "You have successfully updated your reservation");
    }

    public void notifyCancelBooking(Context context,String date) {
        createNotification(context, "Cancellation", "You have canceled your booking for the "+ date + ". Sorry to see you go.");
    }

}
