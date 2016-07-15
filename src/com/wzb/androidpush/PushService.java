package com.wzb.androidpush;


import java.util.HashMap;
import java.util.Map;

import cn.trinea.android.common.util.HttpUtils;
import cn.trinea.android.common.util.JSONUtils;
import android.R.integer;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.os.SystemProperties;

/**
 * AUTHER wzb<wangzhibin_x@foxmail.com> 2016-7-14ÏÂÎç02:20:22
 */
public class PushService extends Service {
	
	private static final String ANDROID_PUSH_HTTP_URL_NOVATECH="persist.sys.push_url_novatech";
	private static final String ANDROID_PUSH_HTTP_URL_NOVATECH_DEFAULT="http://api.huayinghealth.com/push.php";
	
	private static final String ANDROID_PUSH_INTERVAL_NOVATECH="persist.sys.push_int_novatech";
	private static final String ANDROID_PUSH_ID_NOVATECH="persist.sys.push_id_novatech";
	private static final int ANDROID_PUSH_ID_NOVATECH_DEFAULT=0;
	private static final int ANDROID_PUSH_INTERVAL_NOVATECH_DEFAULT=3; //s default 3minutes
	private static final int ANDROID_PUSH_INTERVAL_NOVATECH_MAX=900*1000;//max interval 15minutes
	private Context mContext;
	private int requestCode = (int) SystemClock.uptimeMillis();
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.d("wzb","android push service onStart");
		mContext=this;
		mHandler.sendEmptyMessage(0xff);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onDestroy() {
		Intent intent = new Intent("com.wzb.androidpush.start");  
	    mContext.sendBroadcast(intent);  
		super.onDestroy();
	};
	
	Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case 0xf1:
				Bundle data=msg.getData();
				String val=data.getString("value");
				Log.d("wzb","val="+val);
				if(val!=null){
					handle_event(val);
				}
				break;
			case 0xff:
				//String http_url=SystemProperties.get(ANDROID_PUSH_HTTP_URL_NOVATECH, ANDROID_PUSH_HTTP_URL_NOVATECH_DEFAULT);
				http_request(ANDROID_PUSH_HTTP_URL_NOVATECH_DEFAULT);
				break;
			default:
				break;
			}
		};
	};

	private void http_request(final String http_url) {

		Runnable request_task = new Runnable() {
			String rs = null;

			@Override
			public void run() {
				Message msg = new Message();
				Bundle data = new Bundle();
				int notify_id=SystemProperties.getInt(ANDROID_PUSH_ID_NOVATECH, ANDROID_PUSH_ID_NOVATECH_DEFAULT);
				Map<String,String> map=new HashMap<String, String>();
				map.put("id",""+notify_id);
				
				String http_url_paras=HttpUtils.getUrlWithParas(http_url, map);
				rs=HttpUtils.httpGetString(http_url_paras);
				//Log.d("wzb","url="+http_url+" rs="+rs);
				data.putString("value", rs);
				msg.setData(data);
				msg.what = 0xf1;
				mHandler.sendMessage(msg);
				int interval=SystemProperties.getInt(ANDROID_PUSH_INTERVAL_NOVATECH, ANDROID_PUSH_INTERVAL_NOVATECH_DEFAULT)*1000;
				interval=interval>ANDROID_PUSH_INTERVAL_NOVATECH_MAX? ANDROID_PUSH_INTERVAL_NOVATECH_MAX:interval;
				mHandler.sendEmptyMessageDelayed(0xff, interval);

			}
		};

		Thread request_thread = new Thread(request_task);
		request_thread.start();

	}
	
	void handle_event(String msg){
		int interval=JSONUtils.getInt(msg, "interval", 3);
		//Log.d("wzb","interval="+interval);
		if(interval>3){
			SystemProperties.set(ANDROID_PUSH_INTERVAL_NOVATECH, ""+interval);
		}
		int id=JSONUtils.getInt(msg, "id",0);
		if(id>0){
			SystemProperties.set(ANDROID_PUSH_ID_NOVATECH, ""+id);
		}
		String ticker=JSONUtils.getString(msg, "ticker","");
		//Log.d("wzb","ticker="+ticker);
		String title=JSONUtils.getString(msg, "title","");
		//Log.d("wzb","title="+title);
		String content=JSONUtils.getString(msg, "content","");
		//Log.d("wzb","content="+content);
		String url=JSONUtils.getString(msg, "url","");
		//Log.d("wzb","url="+url);
		if(!TextUtils.isEmpty(content) && !TextUtils.isEmpty(title)){
			show_notify(ticker,title,content,url);
		}
		
	}
	
	private void show_notify(String ticker,String title,String content,String url) {
		final Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pIntent = PendingIntent.getActivity(mContext,
				requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		int smallIcon = R.drawable.ic_launcher;
		NotifyUtil notify2 = new NotifyUtil(mContext, 2);
		notify2.notify_normail_moreline(pIntent, smallIcon, ticker, title,
				content, true, true, false);
	}

}
