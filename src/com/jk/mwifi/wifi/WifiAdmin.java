package com.jk.mwifi.wifi;

import java.net.Inet4Address;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import com.jk.mwifi.wifi.WifiConnect.WifiCipherType;

/**
 * Class Name: WifiAdmin.java<br>
 * Function:Wifi连接管理工具类<br>
 */
public class WifiAdmin {

	// 定义一个WifiManager对象
	public WifiManager mWifiManager;

	// 扫描出的网络连接列表
	private List<ScanResult> mWifiList;

	// 网络连接列表
	private List<WifiConfiguration> mWifiConfigurations;

	WifiLock mWifiLock;

	public WifiAdmin(Context context) {
		// 取得WifiManager对象
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
	}

	/**
	 * Function:关闭wifi<br>
	 * www.javaapk.com更改
	 * 
	 * @author ZYT DateTime 2014-5-15 上午12:17:37<br>
	 * @return<br>
	 */
	public boolean closeWifi() {
		if (mWifiManager.isWifiEnabled()) {
			return mWifiManager.setWifiEnabled(false);
		}
		return false;
	}

	/**
	 * Gets the Wi-Fi enabled state.检查当前wifi状态
	 * 
	 * @return One of {@link WifiManager#WIFI_STATE_DISABLED},
	 *         {@link WifiManager#WIFI_STATE_DISABLING},
	 *         {@link WifiManager#WIFI_STATE_ENABLED},
	 *         {@link WifiManager#WIFI_STATE_ENABLING},
	 *         {@link WifiManager#WIFI_STATE_UNKNOWN}
	 * @see #isWifiEnabled()
	 */
	public int checkState() {
		return mWifiManager.getWifiState();
	}

	// 锁定wifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// 解锁wifiLock
	public void releaseWifiLock() {
		// 判断是否锁定
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// 创建一个wifiLock
	public void createWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("test");
	}

	// 得到配置好的网络:曾经连接过的wifi信息
	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfigurations;
	}

	// 指定配置好的网络进行连接
	public void connetionConfiguration(int index) {
		if (index > mWifiConfigurations.size()) {
			return;
		}
		// 连接配置好指定ID的网络
		mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId,
				true);
	}

	public void startScan() {

		// openWifi();
		mWifiManager.startScan();
		// 得到扫描结果
		mWifiList = mWifiManager.getScanResults();
		// 得到配置好的网络连接
		mWifiConfigurations = mWifiManager.getConfiguredNetworks();

	}

	// 得到扫描后的网络列表
	public List<ScanResult> getWifiList() {
		return mWifiList;
	}

	// 查看扫描结果
	public StringBuffer lookUpScan() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mWifiList.size(); i++) {
			sb.append("Index_" + new Integer(i + 1).toString() + ":");
			// 将ScanResult信息转换成一个字符串包
			// 其中把包括：BSSID、SSID、capabilities、frequency、level
			sb.append((mWifiList.get(i)).toString()).append("\n");
		}
		return sb;
	}

	public String getMacAddress() {
		return (mWifiManager.getConnectionInfo() == null) ? "NULL"
				: mWifiManager.getConnectionInfo().getMacAddress();
	}

	/**
	 * Return the basic service set identifier (BSSID) of the current access
	 * point. The BSSID may be {@code null} if there is no network currently
	 * connected.
	 * 
	 * @return the BSSID, in the form of a six-byte MAC address:
	 *         {@code XX:XX:XX:XX:XX:XX}
	 */
	public String getBSSID() {
		return (mWifiManager.getConnectionInfo() == null) ? "NULL"
				: mWifiManager.getConnectionInfo().getBSSID();
	}

	public int getIpAddress() {
		return (mWifiManager.getConnectionInfo() == null) ? 0 : mWifiManager
				.getConnectionInfo().getIpAddress();
	}

	/**
	 * Each configured network has a unique small integer ID, used to identify
	 * the network when performing operations on the supplicant. This method
	 * returns the ID for the currently connected network.
	 * 
	 * @return the network ID, or -1 if there is no currently connected network
	 */
	public int getNetWordId() {
		return (mWifiManager.getConnectionInfo() == null) ? 0 : mWifiManager
				.getConnectionInfo().getNetworkId();
	}

	/**
	 * Function: 得到wifiInfo的所有信息<br>
	 * 
	 * @author ZYT DateTime 2014-5-14 上午11:03:32<br>
	 * @return<br>
	 */
	public String getWifiInfo() {
		return (mWifiManager.getConnectionInfo() == null) ? "NULL"
				: mWifiManager.getConnectionInfo().toString();
	}

	public int getConnNetId() {
		// result.SSID;
		return mWifiManager.getConnectionInfo().getNetworkId();
	}

	/**
	 * 得到当前连接的SSID
	 * 
	 * @return
	 */
	public String getSSID() {
		return mWifiManager.getConnectionInfo().getSSID();
	}

	// 添加一个网络并连接
	public void addNetWork(WifiConfiguration configuration) {
		int wcgId = mWifiManager.addNetwork(configuration);
		mWifiManager.enableNetwork(wcgId, true);
	}

	// 断开指定ID的网络
	public void disConnectionWifi(int netId) {
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}

	/**
	 * Function: 打开wifi功能<br>
	 * 
	 * @author ZYT DateTime 2014-5-14 上午11:01:11<br>
	 * @return true:打开成功；false:打开失败<br>
	 */
	public boolean openWifi() {
		boolean bRet = true;
		if (!mWifiManager.isWifiEnabled()) {
			bRet = mWifiManager.setWifiEnabled(true);
		}
		return bRet;
	}

	/**
	 * Function: 提供一个外部接口，传入要连接的无线网 <br>
	 * 
	 * @author ZYT DateTime 2014-5-13 下午11:46:54<br>
	 * @param SSID
	 *            SSID
	 * @param Password
	 * @param Type
	 * <br>
	 *            没密码：{@linkplain WifiCipherType#WIFICIPHER_NOPASS}<br>
	 *            WEP加密： {@linkplain WifiCipherType#WIFICIPHER_WEP}<br>
	 *            WPA加密： {@linkplain WifiCipherType#WIFICIPHER_WPA}
	 * @return true:连接成功；false:连接失败<br>
	 */
	public boolean connect(String SSID, String Password, WifiCipherType Type) {
		if (!this.openWifi()) {
			return false;
		}
		// 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
		// 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
		while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
			try {
				// 为了避免程序一直while循环，让它睡个100毫秒在检测……
				Thread.currentThread();
				Thread.sleep(100);
			} catch (InterruptedException ie) {
			}
		}

		System.out.println("WifiAdmin#connect==连接结束");

		WifiConfiguration wifiConfig = createWifiInfo(SSID, Password, Type);
		//
		if (wifiConfig == null) {
			return false;
		}

		WifiConfiguration tempConfig = this.isExsits(SSID);

		int tempId = wifiConfig.networkId;
		if (tempConfig != null) {
			tempId = tempConfig.networkId;
			mWifiManager.removeNetwork(tempConfig.networkId);
		}

		int netID = mWifiManager.addNetwork(wifiConfig);

		// 断开连接
		mWifiManager.disconnect();
		// 重新连接
		// netID = wifiConfig.networkId;
		// 设置为true,使其他的连接断开
		boolean bRet = mWifiManager.enableNetwork(netID, true);
		mWifiManager.reconnect();
		return bRet;
	}

	// 查看以前是否也配置过这个网络
	private WifiConfiguration isExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	private WifiConfiguration createWifiInfo(String SSID, String Password,
			WifiCipherType Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == WifiCipherType.WIFICIPHER_WEP) {
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == WifiCipherType.WIFICIPHER_WPA) {

			// 修改之后配置
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);

		} else {
			return null;
		}
		return config;
	}

	/**
	 * Function:判断扫描结果是否连接上<br>
	 * 
	 * @author ZYT DateTime 2014-5-14 上午11:31:40<br>
	 * @param result
	 * @return<br>
	 */
	public boolean isConnect(ScanResult result) {
		if (result == null) {
			return false;
		}

		WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
		String g2 = "\"" + result.SSID + "\"";
		if (mWifiInfo.getSSID() != null && mWifiInfo.getSSID().endsWith(g2)) {
			return true;
		}
		return false;
	}

	/**
	 * Function: 将int类型的IP转换成字符串形式的IP<br>
	 * 
	 * @author ZYT DateTime 2014-5-14 下午12:28:16<br>
	 * @param ip
	 * @return<br>
	 */
	public String ipIntToString(int ip) {
		try {
			byte[] bytes = new byte[4];
			bytes[0] = (byte) (0xff & ip);
			bytes[1] = (byte) ((0xff00 & ip) >> 8);
			bytes[2] = (byte) ((0xff0000 & ip) >> 16);
			bytes[3] = (byte) ((0xff000000 & ip) >> 24);
			return Inet4Address.getByAddress(bytes).getHostAddress();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Function:信号强度转换为字符串<br>
	 * 
	 * @author ZYT DateTime 2014-5-14 下午2:14:42<br>
	 * @param level
	 * <br>
	 */
	public static String singlLevToStr(int level) {

		String resuString = "无信号";

		if (Math.abs(level) > 100) {
		} else if (Math.abs(level) > 80) {
			resuString = "弱";
		} else if (Math.abs(level) > 70) {
			resuString = "强";
		} else if (Math.abs(level) > 60) {
			resuString = "强";
		} else if (Math.abs(level) > 50) {
			resuString = "较强";
		} else {
			resuString = "极强";
		}
		return resuString;
	}

	/**
	 * 添加到网络
	 * 
	 * @param wcg
	 */
	public boolean addNetwork(WifiConfiguration wcg) {
		if (wcg == null) {
			return false;
		}
		int wcgID = mWifiManager.addNetwork(wcg);
		boolean b = mWifiManager.enableNetwork(wcgID, true);
		mWifiManager.saveConfiguration();
		return b;
	}

	public boolean connectSpecificAP(ScanResult scan) {
		List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
		boolean networkInSupplicant = false;
		boolean connectResult = false;
		// 重新连接指定AP
		mWifiManager.disconnect();
		for (WifiConfiguration w : list) {
			// 将指定AP 名字转化
			// String str = convertToQuotedString(info.ssid);
			if (w.BSSID != null && w.BSSID.equals(scan.BSSID)) {
				connectResult = mWifiManager.enableNetwork(w.networkId, true);
				// mWifiManager.saveConfiguration();
				networkInSupplicant = true;
				break;
			}
		}
		if (!networkInSupplicant) {
			WifiConfiguration config = CreateWifiInfo(scan, "");
			connectResult = addNetwork(config);
		}

		return connectResult;
	}

	// 然后是一个实际应用方法，只验证过没有密码的情况：
	public WifiConfiguration CreateWifiInfo(ScanResult scan, String Password) {
		// Password="ultrapower2013";
		// deleteExsits(info.ssid);
		WifiConfiguration config = new WifiConfiguration();
		config.hiddenSSID = false;
		config.status = WifiConfiguration.Status.ENABLED;

		if (scan.capabilities.contains("WEP")) {
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);

			config.SSID = "\"" + scan.SSID + "\"";

			config.wepTxKeyIndex = 0;
			config.wepKeys[0] = Password;
			// config.preSharedKey = "\"" + SHARED_KEY + "\"";
		} else if (scan.capabilities.contains("PSK")) {
			//
			config.SSID = "\"" + scan.SSID + "\"";
			config.preSharedKey = "\"" + Password + "\"";
		} else if (scan.capabilities.contains("EAP")) {
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.SSID = "\"" + scan.SSID + "\"";
			config.preSharedKey = "\"" + Password + "\"";
		} else {
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

			config.SSID = "\"" + scan.SSID + "\"";
			// config.BSSID = info.mac;
			config.preSharedKey = null;
			//
		}

		return config;
	}

}
