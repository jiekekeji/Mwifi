package com.jk.mwifi.service;

import java.util.List;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.util.Log;

import com.jk.mwifi.utils.SPUtils;
import com.jk.mwifi.wifi.WifiAdmin;
import com.jk.mwifi.wifi.WifiConnect;
import com.jk.mwifi.wifi.WifiConnect.WifiCipherType;
import com.jk.mywifi.custome.dialog.WifiConnDialog;

/**
 * 检测附近的wifi热点
 * 
 * @author hujiushou
 * 
 */
public class CheckApService extends IntentService {

	public static final String TAG = CheckApService.class.getSimpleName();

	public static boolean isStop = false;

	public static String SPECIFY_SSID;

	public static String SPECIFY_PWD;

	public static WifiCipherType SPECIFY_TYPE;

	// Wifi管理类
	private WifiAdmin mWifiAdmin;

	// wifi连接类
	private WifiConnect mWifiConnect;

	// 指定的ap是否已连接
	public static boolean isConnectting = false;

	// 网络信息的监听者
	private BroadcastReceiver wifiConnectReceiver;

	public CheckApService() {
		super("CheckApService");
	}

	@Override
	public void onCreate() {

		isStop = false;
		isConnectting = false;
		SPECIFY_SSID = (String) SPUtils.get(this, "SPECIFY_SSID", "wanheng");
		SPECIFY_PWD = (String) SPUtils.get(this, "SPECIFY_PWD",
				"WANHENGTECH755");

		String type = (String) SPUtils.get(this, "SPECIFY_TYPE",
				"WIFICIPHER_WPA");

		if (type.equals(WifiConnect.WifiCipherType.WIFICIPHER_WPA.toString())) {
			SPECIFY_TYPE = WifiConnect.WifiCipherType.WIFICIPHER_WPA;
		}

		if (type.equals(WifiConnect.WifiCipherType.WIFICIPHER_WEP.toString())) {
			SPECIFY_TYPE = WifiConnect.WifiCipherType.WIFICIPHER_WEP;
		}

		if (type.equals(WifiConnect.WifiCipherType.WIFICIPHER_NOPASS.toString())) {
			SPECIFY_TYPE = WifiConnect.WifiCipherType.WIFICIPHER_NOPASS;
		}

		if (type.equals(WifiConnect.WifiCipherType.WIFICIPHER_INVALID
				.toString())) {
			SPECIFY_TYPE = WifiConnect.WifiCipherType.WIFICIPHER_INVALID;
		}

		Log.i(TAG, "type==" + type);

		mWifiAdmin = new WifiAdmin(this);
		mWifiConnect = new WifiConnect(mWifiAdmin.mWifiManager);

		wifiConnectReceiver = new WifiConnectReceiver();
		IntentFilter filter = new IntentFilter();

		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		this.registerReceiver(wifiConnectReceiver, filter);

		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		while (!isStop) {
			Log.i("currentSSID", mWifiAdmin.getSSID());
			Log.i("SPECIFY_SSID", "\"" + SPECIFY_SSID + "\"");
			getWifiListInfo();
		}

	}

	// 得到扫描结果
	private void getWifiListInfo() {
		Log.i(TAG, "getWifiListInfo");
		mWifiAdmin.startScan();
		try {
			Thread.sleep(3000);
			List<ScanResult> tmpList = mWifiAdmin.getWifiList();
			if (tmpList != null) {
				// 指定wifi在可用范围内
				for (ScanResult sr : tmpList) {
					Log.i(TAG, sr.SSID + isConnectting);
					if (SPECIFY_SSID.equals(sr.SSID) && !isConnectting) {
						Log.i(TAG, "连接指定wifi");
						connectToSpecifyAp();
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	// 连接到指定的ap
	private void connectToSpecifyAp() {
		Log.i(TAG, SPECIFY_SSID + SPECIFY_PWD + SPECIFY_TYPE);
		// 连接到指定网络
		if (mWifiAdmin.connect(SPECIFY_SSID, SPECIFY_PWD, SPECIFY_TYPE)) {
			isConnectting = true;
		}
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		isStop = true;
		this.unregisterReceiver(wifiConnectReceiver);
		super.onDestroy();
	}

	// 用于接收网络连接情况，判断指定网络是否连接成功
	class WifiConnectReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
				NetworkInfo notewokInfo = manager.getActiveNetworkInfo();
				if (notewokInfo != null) {

					Log.i("currentSSID", mWifiAdmin.getSSID());
					Log.i("SPECIFY_SSID", "\"" + SPECIFY_SSID + "\"");
					if (!mWifiAdmin.getSSID()
							.equals("\"" + SPECIFY_SSID + "\"")) {
						if (WifiConnDialog.isConnectting) {
							return;
						}
						isConnectting = false;
					}
				}

			}
		}
	}
}
