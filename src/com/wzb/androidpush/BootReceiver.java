package com.wzb.androidpush;

import android.content.Context;
import android.content.Intent;

/**
 * AUTHER wzb<wangzhibin_x@foxmail.com> 2016-7-15обнГ02:55:14
 */
public class BootReceiver extends android.content.BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent i = new Intent(context, PushService.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(i);
	}

}
