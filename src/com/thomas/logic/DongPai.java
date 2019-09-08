package com.thomas.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import com.thomas.client.ClientMain;
import com.thomas.data.ConstantValue;
import com.thomas.data.I单张牌数据;
import com.thomas.data.I牌堆数据;
import com.thomas.data.I牌堆行数据;
import com.thomas.data.LogHelper;
import com.thomas.data.Ulti;

public class DongPai implements PaiInterface{
	//洗牌的结果
	private ArrayList<I牌堆行数据> oldResultTiles;	
	//动牌最新的结果
	private ArrayList<I牌堆行数据> resultTiles;	
	
	private I牌堆数据 reportedTiles;
	private final int SAMETILESNUMBER = 10;//两行具有相同牌的个数
	private final int SAMEORDERNUMBER = 2; //两行具有相同顺序牌的个数
	
	//缓存功率值
	private ArrayList<LinkedList<Integer>> cachePowerData;
	
	//洗牌完成后，计算10个周期
	private int iRunning; 
	//功率差之和
	private int iPowerSum;
	//升上来之和计算次数
	private int iComputeNums;
	
	/**
	 * constructor 
	 */
	public DongPai(){
		resultTiles = new ArrayList<I牌堆行数据>(8);
		oldResultTiles = new ArrayList<I牌堆行数据>();
		cachePowerData = new ArrayList<>();
		iRunning = 0;
		iPowerSum = 0;
		iComputeNums = 0;
		for(int i = 0; i < 8; i++){
			LinkedList<Integer> tmp = new LinkedList<>();
			cachePowerData.add(tmp);
		}
	}
	
	
	public void changeTiles(I牌堆数据 data) {
		reportedTiles = data;
		if(oldResultTiles.size() != 8){
			return;
		}
		//缓存功率
//		processPowerData();
	}

	
	public boolean isRunning() {
		if(isCompleted()){
			return false;
		}
		LogHelper.log.info("Dongpai:isRunning():state:ConstantValue.DongPaiing");
		return true;
	}

	/**
	 * 判断当前状态是否是完成
	 */
	public boolean isCompleted() {
		// 根据是否坏牌情况判断
		if(ClientMain.TotalTileNums == reportedTiles.getValidtilesNums()){
			if(Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 8 && ClientMain.SameLineNums){
				if(reportedTiles.getDirection() == ConstantValue.DOWNSIDE){
					LogHelper.log.info("Dongpai:isCompleted():state:ConstantValue.DongPaied");
					return true;
				}
				
				
				/*else if(Ulti.isTwoTilesSameNeighbors(oldreportedTiles, reportedTiles) && 
						oldreportedTiles.getDirection() == ConstantValue.DOWNSIDE){
						//邻居都没变，就是姿势变了，不认为变动
						LogHelper.log.info("Dongpai:isCompleted():state:ConstantValue.DongPaied");
						return true;
				}*/
			}
			//
			if(!ClientMain.SameLineNums){
				if((Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 4 && 
						Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums+1) == 4)||
						(Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 3 && 
						Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums+1) == 4) ||
						(Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 4 && 
						Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums+1) == 3)){
					return true;
				}
			}
			//有邻居坏的牌，7个数组就可以了
			//单侧邻居坏，或者两侧坏在边上
			if (getOldtileState() == 2) {
				if(Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 7 &&
						reportedTiles.get牌堆中牌的总行数() <= 9){
					return true;
				}
			}
			//两侧邻居坏，在中间情况
			if (getOldtileState() == 3) {
				if(Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 7 &&
						reportedTiles.get牌堆中牌的总行数() <= 10){
					return true;
				}
			}
			
		}else if(ClientMain.TotalTileNums - reportedTiles.getValidtilesNums() == 1){
			//坏牌在两边
			if(Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 7 &&
				Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums-1) == 1 ){
				if(reportedTiles.getDirection() == ConstantValue.DOWNSIDE){
					LogHelper.log.info("Dongpai:isCompleted():state:ConstantValue.DongPaied");
					return true;
				}/*else if(Ulti.isTwoTilesSameNeighbors(oldreportedTiles, reportedTiles) && 
						oldreportedTiles.getDirection() == ConstantValue.DOWNSIDE){
						//邻居都没变，就是姿势变了，不认为变动
						LogHelper.log.info("Dongpai:isCompleted():state:ConstantValue.DongPaied");
						return true;
				}*/
			}
			if((Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 3 && 
					Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums+1) == 4 &&
					Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums-1) == 1) ||
					(Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 5 && 
					Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums+1) == 3 )){
				return true;
			}
			//坏牌在中间
			if(Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 7 &&
					reportedTiles.get牌堆中牌的总行数() == 9){
				if(reportedTiles.getDirection() == ConstantValue.DOWNSIDE){
					LogHelper.log.info("Dongpai:isCompleted():state:ConstantValue.DongPaied");
					return true;
				}/*else if(Ulti.isTwoTilesSameNeighbors(oldreportedTiles, reportedTiles) && 
						oldreportedTiles.getDirection() == ConstantValue.DOWNSIDE){
						//邻居都没变，就是姿势变了，不认为变动
						LogHelper.log.info("Dongpai:isCompleted():state:ConstantValue.DongPaied");
						return true;
				}*/
			}
			if((Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 3 && 
					Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums+1) == 4&&
					reportedTiles.get牌堆中牌的总行数() == 9) ||
					(Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 4 && 
					Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums+1) == 3 &&
					reportedTiles.get牌堆中牌的总行数() == 9)){
				return true;
			}

		}
		LogHelper.log.info("Dongpai:isCompleted():state:ConstantValue.DongPaied, return false!!!");
		return false;
	
	}
	
	

	/**
	 * 动牌到完成状态后计算一下结果
	 */
	public void analyzeTiles() {
		// 取交集，找相同的个数最多的
		//没有坏牌的情况
		if(Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 8 ||
				(Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 4 &&
				Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums+1) == 4)){
			resultTiles.clear();
			for (I牌堆行数据 line : oldResultTiles) {
				for (int i = 0; i < reportedTiles.get牌堆中牌的总行数(); i++) {
					//找到相同牌数多的两行
					if (getSameTilesNum(line, reportedTiles.getItem(i)) >= SAMETILESNUMBER) {
						//顺序牌>2就认为同序
						if (getSameOrderNum(line, reportedTiles.getItem(i)) > SAMEORDERNUMBER){
							resultTiles.add(reportedTiles.getItem(i).clone());
						}else{
							resultTiles.add(reportedTiles.getItem(i).revert());							
						}
						
						break;
					}
				}
			}
			oldResultTiles.clear();
			oldResultTiles.addAll(resultTiles);
		}else{//存在邻居坏的情况
			ArrayList<I牌堆行数据> tmpTiles = new ArrayList<I牌堆行数据>();
			for (int i = 0; i < reportedTiles.get牌堆中牌的总行数(); i++) {
				if(reportedTiles.getItem(i).get行中总牌数() != ClientMain.LineTilesNums){		
					tmpTiles.add(reportedTiles.getItem(i));
					tmpTiles.add(reportedTiles.getItem(i).revert());
				}		
			}
			ArrayList<I牌堆行数据> badTiles = new ArrayList<I牌堆行数据>();
			if(tmpTiles.size() == 4){//两个数组
				for(int m=0;m<2;m++){
					for(int n=2;n<4;n++){
						I牌堆行数据 badline = new I牌堆行数据();
						badline = badline.connetTileLines(tmpTiles.get(m));
						badline = badline.connetTileLines(tmpTiles.get(n));
						badTiles.add(badline);
					}
				}
				for(int m=0;m<2;m++){
					for(int n=2;n<4;n++){
						I牌堆行数据 badline = new I牌堆行数据();
						badline = badline.connetTileLines(tmpTiles.get(n));
						badline = badline.connetTileLines(tmpTiles.get(m));
						badTiles.add(badline);
					}
				}
				
			}
			if(tmpTiles.size() == 6){//3个数组
				for(int m=0;m<2;m++){
					for(int n=2;n<4;n++){
						for(int k = 4; k < 6; k++){
							I牌堆行数据 badline = new I牌堆行数据();
							badline = badline.connetTileLines(tmpTiles.get(m));
							badline = badline.connetTileLines(tmpTiles.get(n));
							badline = badline.connetTileLines(tmpTiles.get(k));
							badTiles.add(badline);							
						}
					}
				}
				for(int m=0;m<2;m++){
					for(int n=2;n<4;n++){
						for(int k = 4; k < 6; k++){
							I牌堆行数据 badline = new I牌堆行数据();
							badline = badline.connetTileLines(tmpTiles.get(m));
							badline = badline.connetTileLines(tmpTiles.get(k));
							badline = badline.connetTileLines(tmpTiles.get(n));
							badTiles.add(badline);							
						}
					}
				}
				for(int m=0;m<2;m++){
					for(int n=2;n<4;n++){
						for(int k = 4; k < 6; k++){
							I牌堆行数据 badline = new I牌堆行数据();
							badline = badline.connetTileLines(tmpTiles.get(n));
							badline = badline.connetTileLines(tmpTiles.get(m));
							badline = badline.connetTileLines(tmpTiles.get(k));
							badTiles.add(badline);							
						}
					}
				}
				for(int m=0;m<2;m++){
					for(int n=2;n<4;n++){
						for(int k = 4; k < 6; k++){
							I牌堆行数据 badline = new I牌堆行数据();
							badline = badline.connetTileLines(tmpTiles.get(n));
							badline = badline.connetTileLines(tmpTiles.get(k));
							badline = badline.connetTileLines(tmpTiles.get(m));
							badTiles.add(badline);							
						}
					}
				}
				for(int m=0;m<2;m++){
					for(int n=2;n<4;n++){
						for(int k = 4; k < 6; k++){
							I牌堆行数据 badline = new I牌堆行数据();
							badline = badline.connetTileLines(tmpTiles.get(k));
							badline = badline.connetTileLines(tmpTiles.get(m));
							badline = badline.connetTileLines(tmpTiles.get(n));
							badTiles.add(badline);							
						}
					}
				}
				for(int m=0;m<2;m++){
					for(int n=2;n<4;n++){
						for(int k = 4; k < 6; k++){
							I牌堆行数据 badline = new I牌堆行数据();
							badline = badline.connetTileLines(tmpTiles.get(k));
							badline = badline.connetTileLines(tmpTiles.get(n));
							badline = badline.connetTileLines(tmpTiles.get(m));
							badTiles.add(badline);							
						}
					}
				}
			}
			//
			int badindex = 0;
			//取交集，找相同的个数最多的
			resultTiles.clear();
			for(int i = 0; i < 8; i++){
				resultTiles.add(null);
			}
			for(int k = 0; k < oldResultTiles.size(); k++){
				I牌堆行数据 line = oldResultTiles.get(k);
				int i = 0;
				for(i = 0; i < reportedTiles.get牌堆中牌的总行数();i++){
					//只匹配完整行
					if(reportedTiles.getItem(i).get行中总牌数() == ClientMain.LineTilesNums && 
							getSameTilesNum(line,reportedTiles.getItem(i)) >= SAMETILESNUMBER){
						if(getSameOrderNum(line,reportedTiles.getItem(i)) > SAMEORDERNUMBER){
							resultTiles.set(k,reportedTiles.getItem(i));
						}else{
							resultTiles.set(k,reportedTiles.getItem(i).revert());
						}
						
						break;
					}
				}
				if(i== reportedTiles.get牌堆中牌的总行数()){
					badindex = k;
				}
			}
			

			//处理坏牌
			ArrayList<Integer> sortnums = new ArrayList<>();
			for(int i = 0; i < badTiles.size(); i++){
				sortnums.add(getSameOrderNum(badTiles.get(i), oldResultTiles.get(badindex)));
			}
			int maxindex = sortnums.indexOf(Collections.max(sortnums));
			resultTiles.set(badindex, badTiles.get(maxindex));
			
			oldResultTiles.clear();
			oldResultTiles.addAll(resultTiles);
		}
		
	}

	/**
	 * 一张坏牌的情况
	 * 分别和洗牌完数据进行比较，找最大交集，
	 * 判断顺序的时候，需要根据坏牌位置进行逐个判断
	 */
	public void analyzeTilesOne(){

		//先把坏牌行可能情况列出了
		ArrayList<I牌堆行数据> badTiles = processBadTiles();
		
		int badindex = 0;
		//取交集，找相同的个数最多的
		resultTiles.clear();
		for(int i = 0; i < 8; i++){
			resultTiles.add(null);
		}
		for(int k = 0; k < oldResultTiles.size(); k++){
			I牌堆行数据 line = oldResultTiles.get(k);
			int i = 0;
			for(i = 0; i < reportedTiles.get牌堆中牌的总行数();i++){
				//只匹配完整行
				if((reportedTiles.getItem(i).get行中总牌数() == ClientMain.LineTilesNums ||
						reportedTiles.getItem(i).get行中总牌数() == ClientMain.LineTilesNums+1) && 
						getSameTilesNum(line,reportedTiles.getItem(i)) >= SAMETILESNUMBER){
					if(getSameOrderNum(line,reportedTiles.getItem(i)) > SAMEORDERNUMBER){
						resultTiles.set(k,reportedTiles.getItem(i));
					}else{
						resultTiles.set(k,reportedTiles.getItem(i).revert());
					}
					
					break;
				}
			}
			if(i== reportedTiles.get牌堆中牌的总行数()){
				badindex = k;
			}
			
		}
		

		//处理坏牌，取可能情况中排序相同最多的那个
		ArrayList<Integer> sortnums = new ArrayList<>();
		for(int i = 0; i < badTiles.size(); i++){
			sortnums.add(getSameOrderNum(badTiles.get(i), oldResultTiles.get(badindex)));
		}
		int maxindex = sortnums.indexOf(Collections.max(sortnums));
		resultTiles.set(badindex, badTiles.get(maxindex));
		
		oldResultTiles.clear();
		oldResultTiles.addAll(resultTiles);
	}
	

	
	/**
	 * 处理坏牌，把坏牌的可能情况罗列出来，供后续分析比较
	 * return : 所有坏牌组合情况
	 */
	public ArrayList<I牌堆行数据> processBadTiles(){
		//找到坏牌
		I单张牌数据 badtile = null;
		if(Ulti.getBadTiles(reportedTiles) != null){
			badtile = Ulti.getBadTiles(reportedTiles).get(0);
		}
		ArrayList<I牌堆行数据> badTiles = new ArrayList<I牌堆行数据>();
		ArrayList<I牌堆行数据> tmpTiles = new ArrayList<I牌堆行数据>();
		//一张坏牌
		if(reportedTiles.getValidtilesNums() == ClientMain.TotalTileNums-1){
			//---------o
			if(reportedTiles.get牌堆中牌的总行数() == 8){
				for(int j = 0; j < reportedTiles.get牌堆中牌的总行数();j++){
					if(reportedTiles.getItem(j).get行中总牌数() < ClientMain.LineTilesNums){
						I牌堆行数据 tmpline = new I牌堆行数据();
						tmpline = tmpline.connetOneTile(badtile);
						tmpline = tmpline.connetTileLines(reportedTiles.getItem(j));
						badTiles.add(tmpline);
						tmpline = new I牌堆行数据();
						tmpline = tmpline.connetTileLines(reportedTiles.getItem(j));
						tmpline = tmpline.connetOneTile(badtile);
						badTiles.add(tmpline);
						tmpline = new I牌堆行数据();
						tmpline = tmpline.connetTileLines(reportedTiles.getItem(j).revert());
						tmpline = tmpline.connetOneTile(badtile);
						badTiles.add(tmpline);
						tmpline = new I牌堆行数据();
						tmpline = tmpline.connetOneTile(badtile);
						tmpline = tmpline.connetTileLines(reportedTiles.getItem(j).revert());
						badTiles.add(tmpline);
						
						return badTiles;
					}
				}
			}else{//----o----
				for(int j = 0; j < reportedTiles.get牌堆中牌的总行数();j++){
					if(reportedTiles.getItem(j).get行中总牌数() < ClientMain.LineTilesNums){
					//	I牌堆行数据 line = reportedTiles.getItem(j).clone();
						tmpTiles.add(reportedTiles.getItem(j));
						tmpTiles.add(reportedTiles.getItem(j).revert());
					}
				}
				for(int m=0;m<2;m++){
					for(int n=2;n<4;n++){
						I牌堆行数据 badline = new I牌堆行数据();
						badline = badline.connetTileLines(tmpTiles.get(m));
						badline = badline.connetOneTile(badtile);
						badline = badline.connetTileLines(tmpTiles.get(n));
						badTiles.add(badline);
					}
				}
				for(int m=0;m<2;m++){
					for(int n=2;n<4;n++){
						I牌堆行数据 badline = new I牌堆行数据();
						badline = badline.connetTileLines(tmpTiles.get(n));
						badline = badline.connetOneTile(badtile);
						badline = badline.connetTileLines(tmpTiles.get(m));
						badTiles.add(badline);
					}
				}
				
				return badTiles;
			}
		}
		
		return badTiles;
	}

	
	/**
	 * 获取两个牌行相同元素的个数，交集
	 * @param a
	 * @param b
	 * @return
	 */
	public int getSameTilesNum(I牌堆行数据 a,I牌堆行数据 b){
		ArrayList<Integer> aa = a.getTilecodeArrayList();
		ArrayList<Integer> bb = b.getTilecodeArrayList();
		
		aa.retainAll(bb);
		return aa.size();
	}
	
	
	/**
	 * 获取两个牌行顺序一致元素最大数
	 * @param a
	 * @param b
	 */
	public int getSameOrderNum(I牌堆行数据 a,I牌堆行数据 b){
		int num = 0;
		int maxnum = 0;
		for(int i = 0;i < a.get行中总牌数();i++){
			for(int j = 0; j < b.get行中总牌数();j++){
				if(a.getItem(i).getTilecodeid() == b.getItem(j).getTilecodeid()){
					int n,m;
					for(m=i,n=j;m<a.get行中总牌数() && n<b.get行中总牌数();m++,n++){
						if(a.getItem(m).getTilecodeid() == b.getItem(n).getTilecodeid()){
							num++;
						}else{
							break;
						}
					}
					if(num > maxnum){
						maxnum = num;
					}
					i += num-1;
					num=0;
				}
			}
		}
		return maxnum;
	}

	
	/**
	 * 0 无坏牌
	 * 
	 * 2 有1邻居坏的
	 * 3 有2 邻居坏的,或坏牌
	 * @return
	 */
	public int getOldtileState(){
		for(int i = 0; i < oldResultTiles.size(); i++){
			I牌堆行数据 tmpline = oldResultTiles.get(i);
			for(int j = 0; j < tmpline.get行中总牌数(); j++){
				
				if((j != 0 && j != tmpline.get行中总牌数()-1) && 
						(tmpline.getItem(j).getNeighbor1() == 0 && tmpline.getItem(j).getNeighbor2() == 0)){
					return 3;
				}
				if((j != 0 && j != tmpline.get行中总牌数()-1) && 
						(tmpline.getItem(j).getNeighbor1() == 0 || tmpline.getItem(j).getNeighbor2() == 0)){
					return 2;
				}
			}
		}
		return 0;
	}

	/**
	 * reset
	 */
	public void reset() {
		resultTiles.clear();
		oldResultTiles.clear();
		iRunning = 0;
		iPowerSum = 0;
		iComputeNums = 0;
		for(LinkedList<Integer> tmp : cachePowerData)
		{
			tmp.clear();
		}
	}
	
	
	
	public ArrayList<I牌堆行数据> getOldResultTiles() {
		return oldResultTiles;
	}


	public void setOldResultTiles(ArrayList<I牌堆行数据> oldResultTiles) {
		this.oldResultTiles = oldResultTiles;
	}


	public ArrayList<I牌堆行数据> getResultTiles() {
		return resultTiles;
	}


	public void setResultTiles(ArrayList<I牌堆行数据> resultTiles) {
		this.resultTiles = resultTiles;
	}

	/**
	 * 根据最小的功率获取自家
	 * 计算4堆牌功率差之和，以备判断升牌操作
	 */
	public void processResultData() {
		// 计算功率
		ArrayList<I牌堆行数据> tmptiles = new ArrayList<>(8);
		tmptiles.addAll(oldResultTiles);
		oldResultTiles.clear();

		int min = 0, max = 0, left = 4, right = 4;
		ArrayList<Integer> powerList = new ArrayList<>();
		for (int i = 0; i < 8; i += 2) {
			powerList.add(Collections.min(cachePowerData.get(i))
					+ Collections.min(cachePowerData.get(i + 1)));
		}
		min = powerList.indexOf(Collections.min(powerList));
		oldResultTiles.add(tmptiles.get(min * 2));
		oldResultTiles.add(tmptiles.get(min * 2 + 1));
		tmptiles.remove(min*2);
		tmptiles.remove(min*2);
		powerList.clear();

		//计算功率差之和
		for (int i = 0; i < 8; i += 2) {
			iPowerSum += Math.abs(cachePowerData.get(i).getFirst() - cachePowerData.get(i + 1).getFirst());
		}
		
		//把其他牌加到后面
		oldResultTiles.addAll(tmptiles);
		/*
		powerList.clear();
		for (int i = 0; i < 8; i += 2) {
			powerList.add(cachePowerData.get(i).getLast()
					+ cachePowerData.get(i + 1).getLast());
		}
		max = powerList.indexOf(Collections.max(powerList));

		for (int i = 0; i < 4; i++) {
			if (i == min || i == max) {
				continue;
			}
			if (left == 4) {
				left = i;
			} else if (right == 4) {
				right = i;
			}
		}

		oldResultTiles.add(tmptiles.get(left * 2));
		oldResultTiles.add(tmptiles.get(left * 2 + 1));
		oldResultTiles.add(tmptiles.get(max * 2));
		oldResultTiles.add(tmptiles.get(max * 2 + 1));
		oldResultTiles.add(tmptiles.get(right * 2));
		oldResultTiles.add(tmptiles.get(right * 2 + 1));
		*/
		return;

	}
	
	/**
	 * 缓存每行的功率值,从第10次开始，缓存10次
	 * 计算最小值获取自家牌，同时计算各最小值的差值和
	 */
	public void processPowerData(){
		iRunning++;
		if(iRunning < 10 || iComputeNums > 7){
			return;
		}
		
		//更新功率值
		int badindex = -1;
		for(I牌堆行数据 tmpline : oldResultTiles){
			for(int i = 0; i < tmpline.get行中总牌数();i++){
				int id = tmpline.getItem(i).getTilecodeid();
				I单张牌数据 tmptile = reportedTiles.getSingleItem(id);
				if(tmptile == null){
					badindex = i;
					continue;
				}
				tmpline.getItem(i).setPower(tmptile.getPower());
			}
		}
		
		
		StringBuffer stringBuffer = new StringBuffer();
		for(int i = 0; i < 8; i++){
			int ipower = getAveragePower(oldResultTiles.get(i),badindex);
//			if(!cachePowerData.get(i).contains(ipower)){
				cachePowerData.get(i).add(ipower);				
//			}
//			Collections.sort(cachePowerData.get(i));
//			if(cachePowerData.get(i).size() > 10){
//				cachePowerData.get(i).removeLast();
//			}
			stringBuffer.append(oldResultTiles.get(i).toString());
			stringBuffer.append("\n");
			stringBuffer.append(cachePowerData.get(i).toString());
			stringBuffer.append("\n");
		
			
		}
		stringBuffer.append("\n");
		
		if(iComputeNums > 0 && iComputeNums < 7){
			stringBuffer.append("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		}
		if(iComputeNums < 7){
			ClientMain.frameWin.refreshTest(stringBuffer);
		}
		
		//取10次,计算玩家自己位置，计算功率差之和
		if(iRunning == 20){
			processResultData();
		}

		//后面就实时计算功率差之和
		if(iRunning > 20 ){
			if(iComputeNums > 0){
				iComputeNums++;
			}else{
				int itmpPowerSum = 0;
				for (int i = 0; i < 8; i += 2) {
					itmpPowerSum += Math.abs(getAveragePower(oldResultTiles.get(i),badindex) - getAveragePower(oldResultTiles.get(i+1),badindex));
				}
				//找到临界点，清空功率值，重新计算
				if(iPowerSum - itmpPowerSum > 150){
					for(int i = 0; i < 8; i++){
						cachePowerData.get(i).clear();			
					}
					iComputeNums = 1;
				}
			}
			//切换点之后取6个周期
			if(iComputeNums == 7){
				int min = 0, max = 0, left = 4, right = 4;
				ArrayList<I牌堆行数据> tmptiles = new ArrayList<>(8);
				ArrayList<Integer> powerList = new ArrayList<>();
				//牌序
				for (int i = 0; i < 8; i++) {
					Collections.sort(cachePowerData.get(i));
				}
				//取2个最小值之和
				for (int i = 0; i < 8; i += 2) {
					powerList.add(cachePowerData.get(i).get(0) + cachePowerData.get(i).get(1)
							+ cachePowerData.get(i + 1).get(0) + cachePowerData.get(i+1).get(1));
				}
				max = powerList.indexOf(Collections.max(powerList));
				ArrayList<Integer> tmppowerList = new ArrayList<>();
				tmppowerList.add(powerList.get(1));
				tmppowerList.add(powerList.get(2));
				tmppowerList.add(powerList.get(3));
				left = tmppowerList.indexOf(Collections.min(tmppowerList))+1;

				for (int i = 0; i < 4; i++) {
					if (i == min || i == max || i == left) {
						continue;
					}
					
					right = i;
					
				}
				tmptiles.addAll(oldResultTiles);
				oldResultTiles.clear();
				oldResultTiles.add(tmptiles.get(min*2));
				oldResultTiles.add(tmptiles.get(min*2+1));
				oldResultTiles.add(tmptiles.get(left * 2));
				oldResultTiles.add(tmptiles.get(left * 2 + 1));
				oldResultTiles.add(tmptiles.get(max * 2));
				oldResultTiles.add(tmptiles.get(max * 2 + 1));
				oldResultTiles.add(tmptiles.get(right * 2));
				oldResultTiles.add(tmptiles.get(right * 2 + 1));
				
			}
		}
	}
	
	/**
	 * 获取某行的功率和
	 * @param tmptiles
	 * @return 功率
	 */
	
	public int getAveragePower(I牌堆行数据 tmptiles, int badindex){
		int ipower = 0, iaveragepower = 0, itimes = 0;
		for (int j = 0; j < tmptiles.get行中总牌数(); j++) {
			if(badindex == j){
				continue;
			}
			ipower += tmptiles.getItem(j).getPower();
		}
		if(badindex == -1){
			iaveragepower = ipower / ClientMain.LineTilesNums;
		}else{
			iaveragepower = ipower / (ClientMain.LineTilesNums-1);			
		}
		for (int j = 0; j < tmptiles.get行中总牌数(); j++) {
			if(badindex == j){
				continue;
			}
			if (tmptiles.getItem(j).getPower() > iaveragepower + 20) {
				ipower -= (tmptiles.getItem(j).getPower()-iaveragepower);
			}
		}
		return ipower;
	}

}
