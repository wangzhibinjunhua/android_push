package com.wzb.androidpush;


import cn.trinea.android.common.util.HttpUtils;
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
import android.util.Log;
import android.os.SystemProperties;

/**
 * AUTHER wzb<wangzhibin_x@foxmail.com> 2016-7-14����02:20:22
 */
public class PushService extends Service {
	
	private static final String ANDROID_PUSH_HTTP_URL_NOVATECH="persist.sys.custom_adbroot";
	private static final String ANDROID_PUSH_HTTP_URL_NOVATECH_DEFAULT="http://lib.huayinghealth.com/";
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
				String http_url=SystemProperties.get(ANDROID_PUSH_HTTP_URL_NOVATECH, ANDROID_PUSH_HTTP_URL_NOVATECH_DEFAULT);
				http_request(http_url);
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
				rs=HttpUtils.httpGetString(http_url);
				data.putString("value", rs);
				msg.setData(data);
				msg.what = 0xf1;
				mHandler.sendMessage(msg);
				mHandler.sendEmptyMessageDelayed(0xff, 3000);

			}
		};

		Thread request_thread = new Thread(request_task);
		request_thread.start();

	}
	
	void handle_event(String msg){
		
	}
	
	private void show_notify() {
		final Uri uri = Uri.parse("http://www.baidu.com");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pIntent = PendingIntent.getActivity(mContext,
				requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		int smallIcon = R.drawable.ic_launcher;
		String ticker = "����һ����֪ͨ";
		String title = "��������ǹ�����ϯ ����ϯ�������ݴ�����ϯ";
		String content = "play.google.com/store/apps/developer?id=Microsoft+Corporation��̨�塰�����硱������������ϯ�����׽���(18��)���г��ᱨ�棬Ϊ��ѡ��ǵ���ϯһְ������л��λ�г�ί��ָ�̰��ݣ�Ҳ����δ���������ɸ���ϯ�������ݴ������δ�����в�ѡ������";
		// ʵ���������࣬���ҵ��ýӿ�
		NotifyUtil notify2 = new NotifyUtil(mContext, 2);
		notify2.notify_normail_moreline(pIntent, smallIcon, ticker, title,
				content, true, true, false);
	}

}
