package com.jk.mwifi;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.jk.mwifi.service.CheckApService;
import com.jk.mwifi.utils.SPUtils;
import com.jk.mwifi.wifi.WifiConnect;
import com.jk.mwifi.wifi.WifiConnect.WifiCipherType;

public class ApSettingActivity extends Activity {

	private String ssid;
	private WifiCipherType type;
	private String pswd;
	private EditText ssidEt;
	private EditText pswdEt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ap_setting);
		getActionBar().setTitle("设置默认连接热点信息");
		initView();
	}

	private void initView() {
		ssidEt = (EditText) findViewById(R.id.ap_ssid);
		ssidEt.setText((String) SPUtils.get(this, "SPECIFY_SSID", "wanheng"));
		((RadioGroup) findViewById(R.id.gp))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.rbnt1:
							type = WifiConnect.WifiCipherType.WIFICIPHER_WEP;
							break;
						case R.id.rbnt2:
							type = WifiConnect.WifiCipherType.WIFICIPHER_WPA;
							break;
						case R.id.rbnt3:
							type = WifiConnect.WifiCipherType.WIFICIPHER_NOPASS;
							break;
						case R.id.rbnt4:
							type = WifiConnect.WifiCipherType.WIFICIPHER_INVALID;
							break;

						}

					}
				});

		pswdEt = (EditText) findViewById(R.id.ap_pswd);
		((Button) findViewById(R.id.confire))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (checkInput()) {
							SPUtils.put(ApSettingActivity.this, "SPECIFY_SSID",
									ssid);
							SPUtils.put(ApSettingActivity.this, "SPECIFY_TYPE",
									type.toString());
							SPUtils.put(ApSettingActivity.this, "SPECIFY_PWD",
									pswd);

							CheckApService.SPECIFY_SSID = ssid;
							CheckApService.SPECIFY_TYPE = type;
							CheckApService.SPECIFY_PWD = pswd;

							Toast.makeText(ApSettingActivity.this, "设置成功",
									Toast.LENGTH_SHORT).show();
							CheckApService.isConnectting = false;
							CheckApService.isStop = false;

							ApSettingActivity.this.finish();
						}
					}

				});

	}

	private boolean checkInput() {
		ssid = ssidEt.getText().toString().trim();
		if (TextUtils.isEmpty(ssid)) {
			Toast.makeText(this, "ap名称未输入", Toast.LENGTH_SHORT).show();
			return false;
		}

		pswd = pswdEt.getText().toString().trim();
		if (TextUtils.isEmpty(pswd)) {
			Toast.makeText(this, "ap密码未输入", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (type == null) {
			Toast.makeText(this, "ap加密类型未选择", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}
}
