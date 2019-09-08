package com.thomas.client;

import java.util.Observable;
import java.util.Observer;

public class TileSetStatus implements Observer {
	public static TileSetStatus tileSetStatusInstance = null;

	public static TileSetStatus SelectTileSet(String name) {
		if (tileSetStatusInstance == null) {
			tileSetStatusInstance = new TileSetStatus();
			tileSetStatusInstance.setName(name);
		}
		return tileSetStatusInstance;
	}

	private String privateName;

	public final String getName() {
		return privateName;
	}

	private void setName(String value) {
		privateName = value;
	}

	/*
	 * 串口数据响应函数 输入：串口对象，解析后的消息对象 输出：
	 */
	public void update(Observable arg0, Object arg1) {
		if (ClientMain.testDialog != null) {
			ClientMain.testDialog.ReceiveStep1();
		}
	}
}