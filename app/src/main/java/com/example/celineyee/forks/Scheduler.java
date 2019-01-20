package com.example.celineyee.forks;

import android.app.Notification;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class Scheduler {
    Timer timer;
    public Context context;
    public NotificationPop pop;

    public int threshold = 90;

    public int period_second = 30;
    public int delay_second = 2;

    public Scheduler(Context context, NotificationPop pop) {
        this.context = context;
        this.pop = pop;

        Log.i("SchedulerTimer","on Scheduler");
        timer = new Timer();
        timer.schedule(new RemindTask(),
                delay_second * 1000,     //initial delay
                period_second * 1000);  //subsequent rate for the next PROC chance
    }

    class RemindTask extends TimerTask {
        public void run() {
            Log.i("SchedulerTimer","Scheduler running");
            double numberGen = Math.random() * 100;

            if (Double.compare(numberGen, threshold) > 0) {
                // Send Notification
                Notification.Builder builder = pop.getFakeNotification();
                pop.getManager().notify(new Random().nextInt(), builder.build());

                // Send Ringtone sound
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                MediaPlayer mp = MediaPlayer.create(context, notification);
                mp.start();

                Log.i("SchedulerTimer","Success! "+numberGen);
            } else {
                Log.i("SchedulerTimer","Fail! "+numberGen);
            }
        }
    }
}