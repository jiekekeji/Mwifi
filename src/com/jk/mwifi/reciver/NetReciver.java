package com.jk.mwifi.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.jk.mwifi.service.CheckApService;

public class NetReciver extends BroadcastReceiver {

	public static final String TAG = NetReciver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i("NetReciver", intent.getAction());

		// 判断wifi是打开还是关闭
		if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {

			int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
			switch (wifiState) {
			case WifiManager.WIFI_STATE_DISABLED:
				Log.i("NetReciver", "wifi已关闭");

				// 开启服务
				Log.i(TAG, "关闭服务");
				context.stopService(new Intent(context, CheckApService.class));

				break;

			case WifiManager.WIFI_STATE_DISABLING:
				Log.i("NetReciver", "wifi正在关闭");

				break;

			case WifiManager.WIFI_STATE_ENABLED:
				Log.i("NetReciver", "wifi已开启");

				// 开启服务
				Log.i(TAG, "开启服务");
				context.startService(new Intent(context, CheckApService.class));

				break;

			case WifiManager.WIFI_STATE_ENABLING:
				Log.i("NetReciver", "wifi正在开启");

				break;

			case WifiManager.WIFI_STATE_UNKNOWN:
				Log.i("NetReciver", "// 未知的状态");

				break;

			}
		}

	}
}
