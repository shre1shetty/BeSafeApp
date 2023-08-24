package com.firstapp.besafeapp;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.media.VolumeProviderCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.media.VolumeProviderCompat;

public class SERVICEv extends Service {
    private MediaSessionCompat mediaSession;

    @Override
    public void onCreate() {
        super.onCreate();


        mediaSession = new MediaSessionCompat(this, "PlayerService");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0) //you simulate a player which plays something.
                .build());

        //this will only work on Lollipop and up, see https://code.google.com/p/android/issues/detail?id=224134
        VolumeProviderCompat myVolumeProvider =
                new VolumeProviderCompat(VolumeProviderCompat.VOLUME_CONTROL_RELATIVE, /*max volume*/100, /*initial volume level*/50) {
            int count=0;
            @Override
            public void onAdjustVolume(int direction) {
              if(direction==1){
                  count++;
              }
                /*
                -1 -- volume down
                1 -- volume up
                0 -- volume button released
                 */
                if (count>30){
                    Intent intent=new Intent(SERVICEv.this,AlertMessageActivity.class);
                    intent.putExtra("s1",true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    count=0;
                }
            }

        };

        mediaSession.setPlaybackToRemote(myVolumeProvider);
        mediaSession.setActive(true);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaSession.release();
    }
}