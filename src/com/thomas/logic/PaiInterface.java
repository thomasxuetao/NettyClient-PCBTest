package com.thomas.logic;

import java.util.ArrayList;

import com.thomas.data.I单张牌数据;
import com.thomas.data.I牌堆数据;
import com.thomas.data.I牌堆行数据;

public interface PaiInterface {
	public void changeTiles(I牌堆数据 data);
	public boolean isRunning();
	public boolean isCompleted();
	public void analyzeTiles();
	public void analyzeTilesOne();
	public void setOldResultTiles(ArrayList<I牌堆行数据> oldResultTiles);
	public ArrayList<I牌堆行数据> getResultTiles();

	public void reset();

}
