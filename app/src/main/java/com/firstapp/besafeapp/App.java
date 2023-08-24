package com.firstapp.besafeapp;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {
    public static final String CHANNEL_ONE_ID="channel_one_id";
    public static final String CHANNEL_TWO_ID="channel_two_id";

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ONE_ID, "My Channel ID", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("s1");
            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ONE_ID, "My Channel ID", NotificationManager.IMPORTANCE_DEFAULT);
            channel2.setDescription("s2");

            NotificationManager manager = (NotificationManager)
                    getSystemService(
                            Context.NOTIFICATION_SERVICE
                    );
            List<NotificationChannel>channels=new ArrayList<>();
            channels.add(channel);
            channels.add(channel2);

            manager.createNotificationChannels(channels);

        }

    }
}
