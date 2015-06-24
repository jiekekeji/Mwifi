package com.jk.mwifi.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.jk.mwifi.service.CheckApService;

public class App extends Application {

	public static final String TAG = App.class.getSimpleName();

	@Override
	public void onCreate() {
		super.onCreate();

		checkService();

	}

	// 如果wifi开关已打开
	private void checkService() {

		if (((WifiManager) getSystemService(Context.WIFI_SERVICE))
				.isWifiEnabled()) {
			Log.i(TAG, "开启服务");
			// 开启服务
			startService(new Intent(this, CheckApService.class));
		}
	}
}
