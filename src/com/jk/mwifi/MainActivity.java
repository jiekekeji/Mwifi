package com.jk.mwifi;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.jk.mwifi.adapter.MyListViewAdapter;
import com.jk.mwifi.custome.view.MyListView;
import com.jk.mwifi.wifi.WifiAdmin;
import com.jk.mywifi.custome.dialog.OnNetworkChangeListener;
import com.jk.mywifi.custome.dialog.WifiConnDialog;
import com.jk.mywifi.custome.dialog.WifiStatusDialog;

public class MainActivity extends Activity implements OnItemClickListener {

	protected static final String TAG = MainActivity.class.getSimpleName();

	// 刷新wifi扫描结果
	private static final int REFRESH_CONN = 100;

	private static final int REQ_SET_WIFI = 200;

	// Wifi管理类
	private WifiAdmin mWifiAdmin;

	// 扫描结果列表
	private List<ScanResult> list = new ArrayList<ScanResult>();

	// 显示扫描结果列表
	private MyListView mListView;

	// 定时刷新列表
	protected boolean isUpdate = true;

	// wifi信息适配
	private MyListViewAdapter mAdapter;

	// 网络信息的监听者
	private BroadcastReceiver wifiConnectReceiver;

	private Handler mHandler = new MyHandler(this);

	private static class MyHandler extends Handler {

		private WeakReference<MainActivity> reference;

		public MyHandler(MainActivity activity) {
			this.reference = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {

			MainActivity activity = reference.get();

			switch (msg.what) {
			case REFRESH_CONN:

				activity.getWifiListInfo();
				activity.mAdapter.setDatas(activity.list);
				activity.mAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}

			super.handleMessage(msg);
		}
	}

	private OnNetworkChangeListener mOnNetworkChangeListener = new OnNetworkChangeListener() {

		@Override
		public void onNetWorkDisConnect() {
			getWifiListInfo();
			mAdapter.setDatas(list);
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public void onNetWorkConnect() {
			getWifiListInfo();
			mAdapter.setDatas(list);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mAdapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initData();
		initView();
		refreshWifiStatusOnTime();
	}

	private void initView() {
		mListView = (MyListView) findViewById(R.id.listview);
		mAdapter = new MyListViewAdapter(this, list);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

	}

	private void initData() {
		mWifiAdmin = new WifiAdmin(MainActivity.this);
		// 获得Wifi列表信息
		getWifiListInfo();
	}

	// 得到扫描结果
	private void getWifiListInfo() {
		Log.i(TAG, "getWifiListInfo");
		mWifiAdmin.startScan();
		List<ScanResult> tmpList = mWifiAdmin.getWifiList();
		if (tmpList == null) {
			list.clear();
		} else {
			list = tmpList;
		}
	}

	// 定时刷新列表,10秒重新获取一次扫描结果
	private void refreshWifiStatusOnTime() {
		new Thread() {
			public void run() {
				while (isUpdate) {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mHandler.sendEmptyMessage(REFRESH_CONN);
				}
			}
		}.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isUpdate = false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		int position = pos - 1;

		ScanResult scanResult = list.get(position);

		String desc = "";
		String descOri = scanResult.capabilities;
		if (descOri.toUpperCase().contains("WPA-PSK")) {
			desc = "WPA";
		}
		if (descOri.toUpperCase().contains("WPA2-PSK")) {
			desc = "WPA2";
		}
		if (descOri.toUpperCase().contains("WPA-PSK")
				&& descOri.toUpperCase().contains("WPA2-PSK")) {
			desc = "WPA/WPA2";
		}

		if (desc.equals("")) {
			isConnectSelf(scanResult);
			return;
		}
		isConnect(scanResult);

	}

	private void isConnectSelf(ScanResult scanResult) {
		if (mWifiAdmin.isConnect(scanResult)) {

			// 已连接，显示连接状态对话框
			WifiStatusDialog mStatusDialog = new WifiStatusDialog(this,
					R.style.PopDialog, scanResult, mOnNetworkChangeListener);
			mStatusDialog.show();

		} else {
			boolean iswifi = mWifiAdmin.connectSpecificAP(scanResult);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (iswifi) {
				Toast.makeText(this, "连接成功！", 0).show();
			} else {
				Toast.makeText(this, "连接失败！", 0).show();
			}
		}
	}

	private void isConnect(ScanResult scanResult) {
		if (mWifiAdmin.isConnect(scanResult)) {
			// 已连接，显示连接状态对话框
			WifiStatusDialog mStatusDialog = new WifiStatusDialog(this,
					R.style.PopDialog, scanResult, mOnNetworkChangeListener);
			mStatusDialog.show();
		} else {
			// 未连接显示连接输入对话框
			WifiConnDialog mDialog = new WifiConnDialog(this,
					R.style.PopDialog, scanResult, mOnNetworkChangeListener);
			mDialog.show();
		}
	}
}
