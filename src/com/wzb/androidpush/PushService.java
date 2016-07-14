package com.wzb.androidpush;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * AUTHER wzb<wangzhibin_x@foxmail.com>
 * 2016-7-14обнГ02:20:22	
 */
public class PushService extends Service{
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
