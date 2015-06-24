package com.jk.mwifi.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jk.mwifi.service.CheckApService;

/**
 * 接收开机广播
 * 
 * @author hujiushou
 * 
 */
public class BootReciver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		// if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
		// // 开机完成
		// Log.i("BootReciver", "开机完成");
		// // 开启后台服务
		// Intent i = new Intent(context, CheckApService.class);
		// context.startService(i);
		// }
	}

}
