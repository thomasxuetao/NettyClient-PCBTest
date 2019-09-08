package com.thomas.logic;

import java.util.LinkedList;
import com.thomas.client.ClientMain;
import com.thomas.data.*;

public final class Bridge {
	private static Object Lock = new Object();

	//共享队列，存取牌堆数据
	private static LinkedList<I牌堆数据> tilesQueueA = new LinkedList<I牌堆数据>();
	private static LinkedList<I牌堆数据> tilesQueueB = new LinkedList<I牌堆数据>();
	//指向牌堆数据的引用
	public static TilesObject tilesReferenceA = new TilesObject();
	public static TilesObject tilesReferenceB = new TilesObject();
	//取数据线程
	public static TilesRunnerA runnerA = new TilesRunnerA();
	public static TilesRunnerB runnerB = new TilesRunnerB();
	//算法处理对象
	public static Logic logicA = new Logic();
	public static Logic logicB = new Logic();
	

	/**
	 * 取牌数据线程
	 * @author ZTE
	 *
	 */
	private static class TilesRunnerA implements Runnable {
		public void run() {
			while (true) {
				if (HasDataA()) {
					I牌堆数据 tiles = PeekDataA();
//					ClientMain.frameWin.refreshPowerTest(tiles);//刷新功率
					if (ClientMain.frameWin != null) {
						ClientMain.frameWin.refresh(Ulti.convertTilesToArray(tiles), tiles.getTileSetId());
					}
//					logicA.changeTiles(tiles);
				} else {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private static class TilesRunnerB implements Runnable {
		public void run() {
			while (true) {
				if (HasDataB()) {
					I牌堆数据 tiles = PeekDataB();
//					ClientMain.frameWin.refreshPowerTest(tiles);//刷新功率
					ClientMain.frameWin.refresh(Ulti.convertTilesToArray(tiles),tiles.getTileSetId());
//					logicB.changeTiles(tiles);
				} else {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}


	private static void InsertDataA(I牌堆数据 data) {
		synchronized (Lock) {
			tilesQueueA.offer(data);
		}
	}
	private static void InsertDataB(I牌堆数据 data) {
		synchronized (Lock) {
			tilesQueueB.offer(data);
		}
	}

	private static I牌堆数据 PeekDataA() {
		synchronized (Lock) {
			return tilesQueueA.poll();
		}
	}
	
	private static I牌堆数据 PeekDataB() {
		synchronized (Lock) {
			return tilesQueueB.poll();
		}
	}
	
	private static boolean HasDataA() {
		if (tilesQueueA.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean HasDataB() {
		if (tilesQueueB.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static void TilesChangedA(I牌堆数据 tiles) {
		InsertDataA(tiles);
	}
	public static void TilesChangedB(I牌堆数据 tiles) {
		InsertDataB(tiles);
	}

}