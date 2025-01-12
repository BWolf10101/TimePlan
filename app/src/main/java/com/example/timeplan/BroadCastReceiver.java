package com.example.timeplan;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class BroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Ambil deskripsi dari Intent
        String description = intent.getStringExtra("description");
        if (description == null) {
            description = "Alarm Reminder";
        }

        // Membuka MainActivity setelah alarm berbunyi
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                i,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Periksa izin untuk vibrasi
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.VIBRATE)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(new long[]{0, 1000, 500, 1000}, -1); // Pola getaran
            } else if (vibrator != null) {
                vibrator.vibrate(2000); // Getar 2 detik
            }
        } else {
            Toast.makeText(context, "Vibration permission not granted", Toast.LENGTH_SHORT).show();
        }

        // Bangun notifikasi
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Notify")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Your Plan")
                .setContentText(description) // Deskripsi alarm
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        // Kirim notifikasi
        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(200, builder.build());
        } catch (SecurityException e) {
            Toast.makeText(context, "Notification permission not granted", Toast.LENGTH_SHORT).show();
        }

        // Suara alarm
        try {
            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (sound == null) {
                sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            Ringtone ringtone = RingtoneManager.getRingtone(context, sound);
            ringtone.play();
        } catch (Exception e) {
            Toast.makeText(context, "Failed to play alarm sound", Toast.LENGTH_SHORT).show();
        }
    }
}
