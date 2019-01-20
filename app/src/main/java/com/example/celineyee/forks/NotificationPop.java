package com.example.celineyee.forks;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;


public class NotificationPop extends ContextWrapper {
    private static final String ID = "com.example.celineyee.forks.FakeNotif";
    private static final String NAME = "Fake Notification";
    private NotificationManager manager;
    public NotificationPop(Context base){
        super(base);
        createChannels();
    }

    private void createChannels() {
        NotificationChannel fakeNotification = new NotificationChannel(ID,NAME,NotificationManager.IMPORTANCE_DEFAULT);
        fakeNotification.enableLights(true);
        fakeNotification.enableVibration(true);
        fakeNotification.setLightColor(Color.BLUE);
        fakeNotification.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(fakeNotification);
    }

    public NotificationManager getManager() {
        if(manager == null){
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public Notification.Builder getFakeNotification(){
        return new Notification.Builder(getApplicationContext(),ID)
                .setContentText("You just got hacked and rolled")
                .setContentTitle("Hacknroll 2019")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true);
    }

}
