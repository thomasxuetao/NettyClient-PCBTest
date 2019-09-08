package com.thomas.logic;

import java.util.ArrayList;
import java.util.LinkedList;

import com.thomas.data.ConstantValue;
import com.thomas.data.I单张牌数据;
import com.thomas.data.I牌堆行数据;

public class PlayerTileData {
	//抓牌正序反序
	private boolean zhuapaiorder;
	//玩家的顺序号
	private int playerindex;
	// 序列化的牌墙上剩下的牌
	private ArrayList<I单张牌数据> resultTilelist;
	// 四家牌，从庄家按出牌顺序
	private ArrayList<LinkedList<I单张牌数据>> currentTiles;
	// 四家吃碰杠的牌，从庄家按出牌顺序
	private ArrayList<LinkedList<I单张牌数据>> currentCpgTiles;
		
	// 四家待抓牌
	private ArrayList<LinkedList<I单张牌数据>> nextTiles;
	// 会子牌
	private ArrayList<I单张牌数据> huiziTilelist;
	//当前谁出的什么牌，按索引 0 庄家，以此类推
	private ArrayList<I单张牌数据> currentPlayerTiles; 

	public PlayerTileData(){
//		resultTiles = new ArrayList<>();
		resultTilelist = new ArrayList<>();

		currentTiles = new ArrayList<LinkedList<I单张牌数据>>();
		for(int i = 0; i < 4; i++){
			LinkedList<I单张牌数据> tmp = new LinkedList<>();
			currentTiles.add(tmp);
		}

		nextTiles = new ArrayList<LinkedList<I单张牌数据>>();
		for(int i = 0; i < 4; i++){
			LinkedList<I单张牌数据> tmp = new LinkedList<>();
			nextTiles.add(tmp);
		}
		
		currentCpgTiles = new ArrayList<LinkedList<I单张牌数据>>();
		for(int i = 0; i < 4; i++){
			LinkedList<I单张牌数据> tmp = new LinkedList<>();
			currentCpgTiles.add(tmp);
		}
		
		huiziTilelist = new ArrayList<>();
		currentPlayerTiles = new ArrayList<>();
		for(int i = 0; i < 4; i++){
			currentPlayerTiles.add(null);
		}
		setZhuapaiorder(true);
		setPlayerindex(0);
		
	}
	

	public ArrayList<I单张牌数据> getCurrentPlayerTiles() {
		return currentPlayerTiles;
	}

	public void setCurrentPlayerTiles(ArrayList<I单张牌数据> currentPlayerTiles) {
		this.currentPlayerTiles = currentPlayerTiles;
	}

	public void reset(){
//		resultTiles.clear();
		resultTilelist.clear();

		for(int i = 0; i < 4; i++){
			currentTiles.get(i).clear();
			nextTiles.get(i).clear();
			currentCpgTiles.get(i).clear();
		}
		huiziTilelist.clear();
		currentPlayerTiles.clear();
		for(int i = 0; i < 4; i++){
			currentPlayerTiles.add(null);
		}
		setZhuapaiorder(true);
		setPlayerindex(0);
	}

	public boolean getZhuapaiorder() {
		return zhuapaiorder;
	}

	public void setZhuapaiorder(boolean zhuapaiorder) {
		this.zhuapaiorder = zhuapaiorder;
	}

	public int getPlayerindex() {
		return playerindex;
	}

	public void setPlayerindex(int playerindex) {
		this.playerindex = playerindex;
	}

//	public ArrayList<I牌堆行数据> getResultTiles() {
//		return resultTiles;
//	}
//
//	public void setResultTiles(ArrayList<I牌堆行数据> resultTiles) {
//		this.resultTiles = resultTiles;
//	}

	public ArrayList<I单张牌数据> getResultTilelist() {
		return resultTilelist;
	}

	public void setResultTilelist(ArrayList<I单张牌数据> resultTilelist) {
		this.resultTilelist = resultTilelist;
	}

	public ArrayList<LinkedList<I单张牌数据>> getCurrentTiles() {
		return currentTiles;
	}

	public void setCurrentTiles(ArrayList<LinkedList<I单张牌数据>> currentTiles) {
		this.currentTiles = currentTiles;
	}

	public ArrayList<LinkedList<I单张牌数据>> getNextTiles() {
		return nextTiles;
	}

	public void setNextTiles(ArrayList<LinkedList<I单张牌数据>> nextTiles) {
		this.nextTiles = nextTiles;
	}

	public ArrayList<I单张牌数据> getHuiziTilelist() {
		return huiziTilelist;
	}

	public void setHuiziTilelist(ArrayList<I单张牌数据> huiziTilelist) {
		this.huiziTilelist = huiziTilelist;
	}

	public ArrayList<LinkedList<I单张牌数据>> getCurrentCpgTiles() {
		return currentCpgTiles;
	}

	public void setCurrentCpgTiles(ArrayList<LinkedList<I单张牌数据>> currentCpgTiles) {
		this.currentCpgTiles = currentCpgTiles;
	}
	
}
