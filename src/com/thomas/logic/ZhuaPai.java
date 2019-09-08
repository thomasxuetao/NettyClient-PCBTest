package com.thomas.logic;

import java.util.ArrayList;
import java.util.Collections;

import org.joda.time.Instant;

import com.thomas.client.ClientMain;
import com.thomas.data.ConstantValue;
import com.thomas.data.I单张牌数据;
import com.thomas.data.I牌堆数据;
import com.thomas.data.I牌堆行数据;
import com.thomas.data.LogHelper;
import com.thomas.data.Ulti;

public class ZhuaPai implements PaiInterface{

	//洗牌的结果
	private ArrayList<I牌堆行数据> oldResultTiles;	
	//抓牌的结果
	PlayerTileData resultData;
	
	//缓存判断数据
	private ArrayList<I单张牌数据> cachestandData;

	private ArrayList<I牌堆行数据> cacheTiles4;
	private ArrayList<I牌堆行数据> cacheTiles8;
	private ArrayList<I牌堆行数据> cacheTiles12;
	//current state
	private int state;
	private I牌堆数据 reportedTiles;
	private ArrayList<Instant> cacheTimes;
	private int badlineindex;
	
	public ZhuaPai(){
		oldResultTiles = new ArrayList<>();
		cachestandData = new ArrayList<>();
		cacheTiles4 = new ArrayList<>();
		cacheTiles8 = new ArrayList<>();
		cacheTiles12 = new ArrayList<>();
		for(int i = 0; i < 20; i++){
			cacheTiles8.add(null);
			cacheTiles12.add(null);
		}
		state = ConstantValue.IdleState;
		resultData = new PlayerTileData();
		cacheTimes = new ArrayList<>();
		for(int i = 0; i < 12; i++){
			cacheTimes.add(null);
		}
		badlineindex = 0;
	}
	
	/**
	 * 牌
	 */
	public void changeTiles(I牌堆数据 data) {
		reportedTiles = data;
		
		if(state == ConstantValue.ZhuaPaiing){
			//缓存站立过的牌
			for(int i = 0; i < reportedTiles.get牌堆中牌的总行数(); i++){
				for(int j = 0; j < reportedTiles.getItem(i).get行中总牌数(); j++){
					//站立 or 向上 or 挪动过
					if( !cachestandData.contains(reportedTiles.getItem(i).getItem(j))
						&&	tileZhuaChanged(reportedTiles.getItem(i).getItem(j)) ){
						cachestandData.add(reportedTiles.getItem(i).getItem(j));
					}
				}

			}
			//缓存4、8、12牌
			for(int i = 0; i < reportedTiles.get牌堆中牌的总行数(); i++){
//				if(reportedTiles.getItem(i).get行中总牌数() != 4 && reportedTiles.getItem(i).get行中总牌数() != 8 && 
//						reportedTiles.getItem(i).get行中总牌数() != 12 ){
//					continue;
//				}
				if(reportedTiles.getItem(i).getDirection() == ConstantValue.STAND1){
					if((reportedTiles.getItem(i).get行中总牌数() == 4) 
							&& !cacheTiles4.contains(reportedTiles.getItem(i))){
						cacheTiles4.add(reportedTiles.getItem(i));
					}else if((reportedTiles.getItem(i).get行中总牌数() == 8)){
						for(int j = 0; j < cacheTiles4.size(); j++){
							if(reportedTiles.getItem(i).contains(cacheTiles4.get(j))){
								cacheTiles8.set(j, reportedTiles.getItem(i));
								break;
							}
						}
					}else if((reportedTiles.getItem(i).get行中总牌数() == 12)){
						for(int j = 0; j < cacheTiles8.size(); j++){
							if(reportedTiles.getItem(i).contains(cacheTiles8.get(j))){
								cacheTiles12.set(j, reportedTiles.getItem(i));
								break;
							}
						}
					}
				}
			}
			//缓存好4行牌的变化时间{1， count/2 ,count-2}
			I单张牌数据 newone,oldone;
			for(int i = badlineindex; i <= badlineindex+6; i=i+2){
				int index = 0;
				if(i % 2 == 0){
					index = i/2;
				}else{
					index = (i-1)/2;
				}
				
				I牌堆行数据 line = oldResultTiles.get(i);
				if(cacheTimes.get(index*3) == null){
					oldone = line.getItem(1);
					newone = reportedTiles.getSingleItem(oldone.getTilecodeid());
					if((oldone.getNeighbor1() == newone.getNeighbor1() && oldone.getNeighbor2() == newone.getNeighbor2())
							||(oldone.getNeighbor1() == newone.getNeighbor2() && oldone.getNeighbor2() == newone.getNeighbor1())){
						
					}else{
						cacheTimes.set(index*3, newone.getNeighborTime());
					}					
				}
				
				if(cacheTimes.get(index*3+1) == null){
					oldone = line.getItem(line.get行中总牌数()/2 + 1);
					newone = reportedTiles.getSingleItem(oldone.getTilecodeid());
					if((oldone.getNeighbor1() == newone.getNeighbor1() && oldone.getNeighbor2() == newone.getNeighbor2())
							||(oldone.getNeighbor1() == newone.getNeighbor2() && oldone.getNeighbor2() == newone.getNeighbor1())){
						
					}else{
						cacheTimes.set(index*3+1, newone.getNeighborTime());
					}					
				}
				
				if(cacheTimes.get(index*3+2) == null){
					oldone = line.getItem(line.get行中总牌数()-2);
					newone = reportedTiles.getSingleItem(oldone.getTilecodeid());
					if((oldone.getNeighbor1() == newone.getNeighbor1() && oldone.getNeighbor2() == newone.getNeighbor2())
							||(oldone.getNeighbor1() == newone.getNeighbor2() && oldone.getNeighbor2() == newone.getNeighbor1())){
						
					}else{
						cacheTimes.set(index*3+2, newone.getNeighborTime());
					}					
				}
				
			}
		}
		
		
	}
	


	
	/**
	 * 是否正在抓牌，标记是 出现 4张牌站立且成邻居
	 */
	public boolean isRunning() {
		int istandnum = 0;
		for(int i = 0; i < reportedTiles.get牌堆中牌的总行数(); i++){
			for(int j = 0; j < reportedTiles.getItem(i).get行中总牌数();j++){
				if(reportedTiles.getItem(i).getItem(j).getDirection() == ConstantValue.STAND1){
					istandnum++;
				}
			}
			if(istandnum >= 4){
				state = ConstantValue.ZhuaPaiing;
				changeTiles(reportedTiles);
				LogHelper.log.info("ZhuaPai::isRunning:开始抓牌！");
				//看看坏牌在哪一行
				int k = 0,m = 0;
				for(k = 0; k < oldResultTiles.size(); k++){
					for(m = 0; m <oldResultTiles.get(k).get行中总牌数(); m++){
						if(oldResultTiles.get(k).getItem(m).getBadType() > 0){
							break;
						}
					}
					if(m != oldResultTiles.get(k).get行中总牌数()){
						break;
					}
				}
				if(k % 2 == 0){
					badlineindex = 1;
				}else{
					badlineindex = 0;
				}
				
				return true;
			}else{
				istandnum = 0;
			}
		}
		
		return false;
	}

	
	/**
	 * 抓牌是否完成
	 * 出现 13*4+1张牌曾经站立过
	 */
	public boolean isCompleted() {
		int istandnum = 0;
//		for(int i = 0; i < reportedTiles.get牌堆中牌的总行数(); i++){
//			for(int j = 0; j < reportedTiles.getItem(i).get行中总牌数();j++){
//				if(reportedTiles.getItem(i).getItem(j).getDirection() == ConstantValue.STAND1){
//					istandnum++;
//				}
//			}
			//13张站立并且 曾经站立牌总数大于等于13*4
			if(/*istandnum >= ConstantValue.PlayerTilesNums ||*/ cachestandData.size() >= ClientMain.PlayerTilesNums*4+1){
				state = ConstantValue.ZhuaPaied;
				LogHelper.log.info("ZhuaPai::isCompleted:抓牌完成！");
				return true;
			}
//		}
		
		return false;
	}
	
	/**
	 * 头尾，站立或者有新邻居
	 * 中间，站立或者有新邻居或者孤立
	 *     一侧空另一侧不空，另一侧是头尾默认不抓走；另一侧被抓走则他也被抓走；如果另一侧也是单侧空则被抓走
	 * @param tmptile
	 * @return true: false:
	 */
	public boolean tileZhuaChanged(I单张牌数据 tmptile){
		I单张牌数据 curtile = tmptile;
		I单张牌数据 oldtile = getTileFromTileLine(tmptile);
		if(curtile == null || oldtile == null){
			return false;
		}
		
		//站立或者向上
		if(curtile.getDirection() == ConstantValue.STAND1 || curtile.getDirection() == ConstantValue.UPSIDE){
			return true;
		}
		
		//原来牌墙的邻居
		ArrayList<Integer> oldNeighbors = new ArrayList<>();
		oldNeighbors.add(oldtile.getNeighbor1());
		oldNeighbors.add(oldtile.getNeighbor2());
		Collections.sort(oldNeighbors);
		//新邻居
		ArrayList<Integer> curNeighbors = new ArrayList<>();
		curNeighbors.add(curtile.getNeighbor1());
		curNeighbors.add(curtile.getNeighbor2());
		Collections.sort(curNeighbors);
		
		
		//头尾牌 判断，是否有新邻居
		if((oldNeighbors.get(0) == 0 && oldNeighbors.get(1) != 0) || 
				oldNeighbors.get(0) != 0 && oldNeighbors.get(1) == 0){
			//现在有新邻居，变化
			if((!oldNeighbors.contains(curNeighbors.get(0)) && curNeighbors.get(0) != 0) ||
					(!oldNeighbors.contains(curNeighbors.get(1)) && curNeighbors.get(1) != 0)){
				return true;
			}
		}else{//中间牌
			//新邻居
			if((!oldNeighbors.contains(curNeighbors.get(0)) && curNeighbors.get(0) != 0) ||
					(!oldNeighbors.contains(curNeighbors.get(1)) && curNeighbors.get(1) != 0)){
				return true;
			}
			//或者变成孤立
			if(curNeighbors.get(0) == 0 && curNeighbors.get(1) == 0){
				return true;
			}
			//一侧空，看另一侧牌
			if((curNeighbors.get(0) == 0 && curNeighbors.get(1) != 0) || 
					curNeighbors.get(0) != 0 && curNeighbors.get(1) == 0){
				I单张牌数据 curneighbortile = reportedTiles.getSingleItem(curNeighbors.get(0));
				if(curneighbortile == null){
					curneighbortile = reportedTiles.getSingleItem(curNeighbors.get(1));
				}
				if(curneighbortile == null){//孤立了？
					return true;
				}
				//另一侧是头尾，默认不抓走
				I单张牌数据 oldneighbortile = getTileFromTileLine(curneighbortile);
				if((oldneighbortile.getNeighbor1() == 0 && oldneighbortile.getNeighbor2() != 0)||
						(oldneighbortile.getNeighbor1() != 0 && oldneighbortile.getNeighbor2() == 0)){
					return false;
				}else{//中间牌
					if(cachestandData.contains(oldneighbortile)){
						return true;
					}else{
						if(curneighbortile.getNeighbor1() == 0 || curneighbortile.getNeighbor2() == 0){
							return true;
						}
					}
				}
			}
		}
		
		
		
		return false;
	}
	
	/**
	 * 从原始牌墙获取该牌
	 * @param tile
	 * @return
	 */
	public I单张牌数据 getTileFromTileLine(I单张牌数据 tile){
		for(int k = 0; k < oldResultTiles.size(); k++){
			for(int m = 0; m <oldResultTiles.get(k).get行中总牌数(); m++){
				if(oldResultTiles.get(k).getItem(m).equals(tile)){
					return oldResultTiles.get(k).getItem(m);
				}
			}
		}
		return null;
	}
	

/**
	 * 分析抓牌的情况
	 * 基于功率判断自家对家情况
	 *//*
	public void analyzeTiles() {
		//正序反序，左右互换，4种可能，分别查找4.8.12在其中的位置
		int index = 0;
		for(index = 0; index < cacheTiles12.size();index++){
			if(cacheTiles12.get(index) != null){
				break;
			}
		}
		if(index == cacheTiles12.size()){
			LogHelper.log.info("ZhuaPai::analyzeTiles:无法找到4，8，12张牌的数组！");
			return;
		}
		
		//按顺序整理抓的牌
		ArrayList<I单张牌数据> tileArrayList = new ArrayList<>();
		tileArrayList.addAll(Ulti.tilesLineToArrayList(cacheTiles4.get(index)));
		ArrayList<I单张牌数据> tmpArrayList = new ArrayList<>();
		tmpArrayList = Ulti.tilesLineToArrayList(cacheTiles8.get(index));
		tmpArrayList.removeAll(tileArrayList);
		tileArrayList.addAll(tmpArrayList);
		tmpArrayList = Ulti.tilesLineToArrayList(cacheTiles12.get(index));
		tmpArrayList.removeAll(tileArrayList);
		tileArrayList.addAll(tmpArrayList);
		//在牌堆中按顺序找，理论上0123 ，，，，， 16，17，18，19
		ArrayList<I单张牌数据> tileList = Ulti.tilesLineArrayToArrayList(oldResultTiles,false,true);
		ArrayList<Integer> orderArrayList = new ArrayList<>();
		for(I单张牌数据 tmpTile : tileArrayList){
			if(tileList.contains(tmpTile)){
				orderArrayList.add(tileList.indexOf(tmpTile));
			}
		}
		if(isLineOk(orderArrayList) == 1){//正序
			resultData.setZhuapaiorder(true);
		}else if(isLineOk(orderArrayList) == 2){//反序
			resultData.setZhuapaiorder(false);
		}else{
			Collections.swap(oldResultTiles, 2, 6);
			Collections.swap(oldResultTiles, 3, 7);
			tileList.clear();
			tileList = Ulti.tilesLineArrayToArrayList(oldResultTiles,false,true);
			orderArrayList.clear();
			for(I单张牌数据 tmpTile : tileArrayList){
				if(tileList.contains(tmpTile)){
					orderArrayList.add(tileList.indexOf(tmpTile));
				}
			}
			if(isLineOk(orderArrayList) == 1){
				resultData.setZhuapaiorder(true);
			}else if(isLineOk(orderArrayList) == 2){
				resultData.setZhuapaiorder(false);
			}else{
				LogHelper.log.info("ZhuaPai::analyzeTiles:无法找到适配的牌序组合!");
				return;
			}
		}
		
		//在牌序列中查找，找到抓牌的头部，然后从头部开始形成一个序列
		if(resultData.getZhuapaiorder()){
			if(cachestandData.contains(tileList.get(tileList.size()-1))){
				int i = 0;
				for(i = tileList.size()-1; i >= 0; i--){
					if(!cachestandData.contains(tileList.get(i))){
						break;
					}
				}
				for(int j = i+1; j < tileList.size();j++){
					resultData.getResultTilelist().add(tileList.get(j));
				}
				for(int j = 0; j <= i;j++){
					resultData.getResultTilelist().add(tileList.get(j));
				}

			}else{
				int i = 0;
				for(i = 0; i < tileList.size(); i++){
					if(cachestandData.contains(tileList.get(i))){
						break;
					}
				}
				for(int j = i; j < tileList.size();j++){
					resultData.getResultTilelist().add(tileList.get(j));
				}
				for(int j = 0; j < i;j++){
					resultData.getResultTilelist().add(tileList.get(j));
				}
			}
			
		}else{//反序
			if(cachestandData.contains(tileList.get(0))){
				int i = 0;
				for(i = 0; i < tileList.size(); i++){
					if(!cachestandData.contains(tileList.get(i))){
						break;
					}
				}
				for(int j = i-1; j >= 0;j--){
					resultData.getResultTilelist().add(tileList.get(j));
				}
				for(int j = tileList.size()-1; j >= i;j--){
					resultData.getResultTilelist().add(tileList.get(j));
				}

			}else{
				int i = 0;
				for(i = tileList.size()-1; i >= 0; i--){
					if(cachestandData.contains(tileList.get(i))){
						break;
					}
				}
				for(int j = i; j >= 0;j--){
					resultData.getResultTilelist().add(tileList.get(j));
				}
				for(int j = tileList.size()-1; j > i;j--){
					resultData.getResultTilelist().add(tileList.get(j));
				}
			}
		}
	
		//计算4家牌，仅计算12张，跳牌的根据互相邻居获得
		int iround = 0, icur = 0;
		for(icur = 0; icur < resultData.getResultTilelist().size(); icur+=4){
			if(!cachestandData.contains(resultData.getResultTilelist().get(icur))){
				break;
			}

			resultData.getCurrentTiles().get(iround%4).add(resultData.getResultTilelist().get(icur));
			resultData.getCurrentTiles().get(iround%4).add(resultData.getResultTilelist().get(icur+1));
			resultData.getCurrentTiles().get(iround%4).add(resultData.getResultTilelist().get(icur+2));
			resultData.getCurrentTiles().get(iround%4).add(resultData.getResultTilelist().get(icur+3));
			iround++;
			if(iround == 12){
				break;
			}
		}
		for(int i = 0; i < icur+4; i++){
			resultData.getResultTilelist().remove(0);
		}
		
		//根据功率确定玩家,设置玩家的index
		checkThePlayer();
		
		//继续根据跳牌规则分配4家牌，跳牌规则可配置
		//1. 庄家1、5，后面依次是 2，3，4
		for(int i = 0; i < resultData.getResultTilelist().size(); i++){
			I单张牌数据 tile = resultData.getResultTilelist().get(i);
			if(i == 0 || i== 4){
				resultData.getCurrentTiles().get(0).add(tile);
			}else if(i == 1){
				resultData.getCurrentTiles().get(1).add(tile);				
			}else if(i == 2){
				resultData.getCurrentTiles().get(2).add(tile);
			}else if(i == 3){
				resultData.getCurrentTiles().get(3).add(tile);				
			}else{
				break;
			}
		}
		for(int i = 0; i < 5; i++){
			resultData.getResultTilelist().remove(0);
		}
		
		//计算4家待抓的牌，从庄家的下家开始，逐个分配
		for(int i = 0; i < resultData.getResultTilelist().size(); i++){
			I单张牌数据 tile = resultData.getResultTilelist().get(i);
			if(i%4 == 0){//庄家的下家先抓
				resultData.getNextTiles().get(1).add(tile);
			}else if(i%4 == 1){
				resultData.getNextTiles().get(2).add(tile);
			}else if(i%4 == 2){
				resultData.getNextTiles().get(3).add(tile);
			}else if(i%4 == 3){ //庄家
				resultData.getNextTiles().get(0).add(tile);
			}
		}
		
		//计算牌墙剩下的牌情况,加上null
		tileList = Ulti.tilesLineArrayToArrayList(oldResultTiles, true, true);
		int begin = tileList.indexOf(resultData.getResultTilelist().get(0));
		int end = tileList.indexOf(resultData.getResultTilelist().get(resultData.getResultTilelist().size()-1));
		resultData.getResultTilelist().clear();
		if(begin < end){
			for(int i = begin; i <= end; i++){
				resultData.getResultTilelist().add(tileList.get(i));
			}
		}else{
			for(int i = begin; i < tileList.size(); i++){
				resultData.getResultTilelist().add(tileList.get(i));
			}
			for(int i = 0; i <= end; i++){
				resultData.getResultTilelist().add(tileList.get(i));
			}
		}
		
	}
	
	*//**
	 * 分析牌
	 * 基于无序状态
	 *//*
	public void analyzeTiles2(){
		//根据自家12张牌顺序，判断出两个方向的牌，判断出抓牌方向
		int index = 0;
		for(index = 0; index < cacheTiles12.size();index++){
			if(cacheTiles12.get(index) != null){
				break;
			}
		}
		if(index == cacheTiles12.size()){
			LogHelper.log.info("ZhuaPai::analyzeTiles:无法找到4，8，12张牌的数组！");
			return;
		}
		
		//按顺序整理抓的牌
		ArrayList<I单张牌数据> tileArrayList = new ArrayList<>();
		tileArrayList.addAll(Ulti.tilesLineToArrayList(cacheTiles4.get(index)));
		ArrayList<I单张牌数据> tmpArrayList = new ArrayList<>();
		tmpArrayList = Ulti.tilesLineToArrayList(cacheTiles8.get(index));
		tmpArrayList.removeAll(tileArrayList);
		tileArrayList.addAll(tmpArrayList);
		tmpArrayList = Ulti.tilesLineToArrayList(cacheTiles12.get(index));
		tmpArrayList.removeAll(tileArrayList);
		tileArrayList.addAll(tmpArrayList);
		//判断抓牌的方向[1,2,201,202,217,218]
		ArrayList<Integer> sortIntegers = new ArrayList<>();
		for(int i = 0;i < 12; i++){
			for(int j = 0; j < 8; j=j+2){
				ArrayList<I单张牌数据> tmp = Ulti.tilesLineToArrayList(oldResultTiles.get(j));
				if(tmp.contains(tileArrayList.get(i))){
					sortIntegers.add(j*100 + tmp.indexOf(tileArrayList.get(i)));
					break;
				}
			}
		}
		//获取最多个数的元素，确定方向
		index = getMaxnumIntegers(sortIntegers)*100;
		int begin=-1,end=-1;
		for(int i = 0; i < sortIntegers.size();i++){
			if(sortIntegers.get(i) >= index){
				if(begin == -1){
					begin = sortIntegers.get(i);
				}else{
					if(Math.abs(begin - sortIntegers.get(i)) > 1){
						end = sortIntegers.get(i);
						break;
					}
				}
			}
		}
		
		if(begin == -1 || end == -1){
			LogHelper.log.info("ZhuaPai::analyzeTiles:无法判断抓牌的方向！");
			return;
		}
		
		if(begin - end > 0){
			resultData.setZhuapaiorder(true);
		}else{
			resultData.setZhuapaiorder(false);
		}
		
		//获取两堆牌顺序
		begin = -1; end = -1;
		if(Math.abs(sortIntegers.get(0) - sortIntegers.get(1)) == 1){//起始牌没跨牌堆
			begin = sortIntegers.get(0)/100;
			for(int i = 2; i < sortIntegers.size();i++){
				int itmp = sortIntegers.get(i)/100;
				if(itmp != begin){//另一牌行
					end = itmp;break;
				}
			}
		}else{
			begin = sortIntegers.get(0)/100;
			end = sortIntegers.get(1)/100;
			int itmp = sortIntegers.get(2)/100;
			if(itmp == begin){
				begin = end;
				end = itmp;
			}
		}
		if(begin == -1 || end == -1){
			LogHelper.log.info("ZhuaPai::analyzeTiles:无法判断抓的两个牌行的方向！");
			return;
		}
		
		//判断其他排行是否被抓？如果被抓，根据抓牌方向看看是头还是尾？就能判断出前后
		int start = -1;
		for(int i = 0; i < 8; i=i+2){
			if(i==begin || i== end){
				continue;
			}
			if(resultData.getZhuapaiorder() && cachestandData.contains(oldResultTiles.get(i).getItem(0))){
				start = i;
				break;
			}
			if(!resultData.getZhuapaiorder() && cachestandData.contains(oldResultTiles.get(i).getItem(oldResultTiles.get(i).get行中总牌数()-1))){
				start = i;
				break;
			}
		}
		
		//根据计算结果重新排列牌堆的顺序，至少出来2家
		ArrayList<I牌堆行数据> tmpResultTiles = new ArrayList<>();
		tmpResultTiles.addAll(oldResultTiles);
		oldResultTiles.clear();
		if(start == -1){
			oldResultTiles.add(tmpResultTiles.get(begin));
			oldResultTiles.add(tmpResultTiles.get(begin+1));
			oldResultTiles.add(tmpResultTiles.get(end));
			oldResultTiles.add(tmpResultTiles.get(end+1));
			//根据默认的功率排序
			if(begin == 0){
				oldResultTiles.add(tmpResultTiles.get(4));
				oldResultTiles.add(tmpResultTiles.get(5));
				oldResultTiles.add(tmpResultTiles.get(12-4-begin-end));
				oldResultTiles.add(tmpResultTiles.get(16-5-begin-end-2));
			}else if(end == 0){
				oldResultTiles.add(tmpResultTiles.get(12-4-begin-end));
				oldResultTiles.add(tmpResultTiles.get(16-5-begin-end-2));				
				oldResultTiles.add(tmpResultTiles.get(4));
				oldResultTiles.add(tmpResultTiles.get(5));
			}else if(begin == 4){
				oldResultTiles.add(tmpResultTiles.get(0));
				oldResultTiles.add(tmpResultTiles.get(1));
				oldResultTiles.add(tmpResultTiles.get(12-0-begin-end));
				oldResultTiles.add(tmpResultTiles.get(16-1-begin-end-2));
			}else if(end == 4){
				oldResultTiles.add(tmpResultTiles.get(12-0-begin-end));
				oldResultTiles.add(tmpResultTiles.get(16-1-begin-end-2));
				oldResultTiles.add(tmpResultTiles.get(0));
				oldResultTiles.add(tmpResultTiles.get(1));
			}else{
				for(int i = 0; i < 8; i=i+2){
					if(i== begin || i==end){
						continue;
					}
					oldResultTiles.add(tmpResultTiles.get(i));
					oldResultTiles.add(tmpResultTiles.get(i+1));
				}
				LogHelper.log.info("ZhuaPai::analyzeTiles:根据默认排序更新牌堆错误！");
			}
		}else{
			oldResultTiles.add(tmpResultTiles.get(start));
			oldResultTiles.add(tmpResultTiles.get(start+1));
			oldResultTiles.add(tmpResultTiles.get(begin));
			oldResultTiles.add(tmpResultTiles.get(begin+1));
			oldResultTiles.add(tmpResultTiles.get(end));
			oldResultTiles.add(tmpResultTiles.get(end+1));
			oldResultTiles.add(tmpResultTiles.get(12-start-begin-end));
			oldResultTiles.add(tmpResultTiles.get(16-start-1-begin-1-end-1));
		}
		
		
		//在牌序列中查找，找到抓牌的头部，然后从头部开始形成一个序列
		ArrayList<I单张牌数据> tileList = null;
		if (resultData.getZhuapaiorder()) {// 正序
			tileList = Ulti.tilesLineArrayToArrayList(oldResultTiles,false,true);
			int i = 0;
			for (i = 0; i < tileList.size(); i++) {
				if (cachestandData.contains(tileList.get(i))) {
					break;
				}
			}
			for (int j = i; j < tileList.size(); j++) {
				resultData.getResultTilelist().add(tileList.get(j));
			}
			for (int j = 0; j < i; j++) {
				resultData.getResultTilelist().add(tileList.get(j));
			}

		} else {// 反序
			tileList = Ulti.tilesLineArrayToArrayList(oldResultTiles,false,false);
			int i = 0;
			for (i = tileList.size() - 1; i >= 0; i--) {
				if (cachestandData.contains(tileList.get(i))) {
					break;
				}
			}
			for (int j = i; j >= 0; j--) {
				resultData.getResultTilelist().add(tileList.get(j));
			}
			for (int j = tileList.size() - 1; j > i; j--) {
				resultData.getResultTilelist().add(tileList.get(j));
			}
		}

		//计算4家牌，仅计算12张，跳牌的根据互相邻居获得
		int iround = 0, icur = 0;
		for(icur = 0; icur < resultData.getResultTilelist().size(); icur+=4){
			if(!cachestandData.contains(resultData.getResultTilelist().get(icur))){
				break;
			}

			resultData.getCurrentTiles().get(iround%4).add(resultData.getResultTilelist().get(icur));
			resultData.getCurrentTiles().get(iround%4).add(resultData.getResultTilelist().get(icur+1));
			resultData.getCurrentTiles().get(iround%4).add(resultData.getResultTilelist().get(icur+2));
			resultData.getCurrentTiles().get(iround%4).add(resultData.getResultTilelist().get(icur+3));
			iround++;
			if(iround == 12){
				break;
			}
		}
		for(int i = 0; i < icur+4; i++){
			resultData.getResultTilelist().remove(0);
		}
		
		//根据功率确定玩家,设置玩家的index
		checkThePlayer();
		
		//继续根据跳牌规则分配4家牌，跳牌规则可配置
		//1. 庄家1、5，后面依次是 2，3，4
		for(int i = 0; i < resultData.getResultTilelist().size(); i++){
			I单张牌数据 tile = resultData.getResultTilelist().get(i);
			if(i == 0 || i== 4){
				resultData.getCurrentTiles().get(0).add(tile);
			}else if(i == 1){
				resultData.getCurrentTiles().get(1).add(tile);				
			}else if(i == 2){
				resultData.getCurrentTiles().get(2).add(tile);
			}else if(i == 3){
				resultData.getCurrentTiles().get(3).add(tile);				
			}else{
				break;
			}
		}
		for(int i = 0; i < 5; i++){
			resultData.getResultTilelist().remove(0);
		}
		
		//计算4家待抓的牌，从庄家的下家开始，逐个分配
		for(int i = 0; i < resultData.getResultTilelist().size(); i++){
			I单张牌数据 tile = resultData.getResultTilelist().get(i);
			if(i%4 == 0){//庄家的下家先抓
				resultData.getNextTiles().get(1).add(tile);
			}else if(i%4 == 1){
				resultData.getNextTiles().get(2).add(tile);
			}else if(i%4 == 2){
				resultData.getNextTiles().get(3).add(tile);
			}else if(i%4 == 3){ //庄家
				resultData.getNextTiles().get(0).add(tile);
			}
		}
		
		//计算牌墙剩下的牌情况,加上null
		tileList = Ulti.tilesLineArrayToArrayList(oldResultTiles, true,true);
		begin = tileList.indexOf(resultData.getResultTilelist().get(0));
		end = tileList.indexOf(resultData.getResultTilelist().get(resultData.getResultTilelist().size()-1));
		resultData.getResultTilelist().clear();
		if(begin < end){
			for(int i = begin; i <= end; i++){
				resultData.getResultTilelist().add(tileList.get(i));
			}
		}else{
			for(int i = begin; i < tileList.size(); i++){
				resultData.getResultTilelist().add(tileList.get(i));
			}
			for(int i = 0; i <= end; i++){
				resultData.getResultTilelist().add(tileList.get(i));
			}
		}

	}*/
	

	/**
	 * 分析抓牌的情况，支持1张坏牌
	 * 根据排行首尾第二张牌 和 中间牌 邻居变化时间判断顺序
	 * 根据顺序，从头、尾找是否站立，如果先没有站立，找到站立了，那就是开始位置
	 * 如果头部就是站立，可能是开始，还要看是否有上面那种情况
	 */
	public void analyzeTilesOne() {
		//根据排行首尾第二张牌 和 中间牌 邻居变化时间判断顺序
		ArrayList<Integer> sorttime = new ArrayList<>();
		for(int i = 0; i < 4;i++){
			sorttime.add(0);
		}
		for(int i = 0; i < 12; i=i+3){
			ArrayList<Instant> tmptimes = getTimesNumbers(i);//获取3个数字
			if(tmptimes.size() < 2){
				continue;
			}else if(tmptimes.size() == 2){// 
				if(tmptimes.get(0).isAfter(tmptimes.get(1))){
					sorttime.set(i/3, 1);//正序
				}else{
					sorttime.set(i/3, 2);
				}
			}else{//3
				if(tmptimes.get(0).isAfter(tmptimes.get(1)) && tmptimes.get(1).isAfter(tmptimes.get(2))){
					sorttime.set(i/3, 1);//正序
				}else if(tmptimes.get(0).isBefore(tmptimes.get(1)) && tmptimes.get(1).isBefore(tmptimes.get(2))){
					sorttime.set(i/3, 2);
				}
			}
		}
		int max = Collections.max(sorttime);
		int index = sorttime.indexOf(max);

		if(max == 1){
			resultData.setZhuapaiorder(true);
		}else if(max == 2){
			resultData.setZhuapaiorder(false);
		}else{
			LogHelper.log.error("ZhuaPai：analyzeTilesOne：无法获得抓牌的顺序");
			return;
		}
		//是否存在冲突的情况，以最新时间为准
		for(int i = 0; i < 4; i++){
			if(sorttime.get(i) != 0 && sorttime.get(i) != max){
				if(!whichTimeisNewer(index, i)){
					resultData.setZhuapaiorder(true);
				}
			}
		}
		
			
		//根据抓牌正反在牌序列中查找头部
		int start=-1,begin = -1,end = -1;
		if (!resultData.getZhuapaiorder()) {
			for(int i = 0; i < 8; i=i+2){
				//没动过
				if((!cachestandData.contains(oldResultTiles.get(i).getItem(0)) || 
						!cachestandData.contains(oldResultTiles.get(i+1).getItem(0))) && 
						(!cachestandData.contains(oldResultTiles.get(i).getItem(oldResultTiles.get(i).get行中总牌数()-1)) ||
						!cachestandData.contains(oldResultTiles.get(i+1).getItem(oldResultTiles.get(i+1).get行中总牌数()-1)))){
					continue;
				}
				//头没动过，尾动过，是开始
				else if((!cachestandData.contains(oldResultTiles.get(i).getItem(0)) || 
						!cachestandData.contains(oldResultTiles.get(i+1).getItem(0))) && 
						(cachestandData.contains(oldResultTiles.get(i).getItem(oldResultTiles.get(i).get行中总牌数()-1)) ||
						cachestandData.contains(oldResultTiles.get(i+1).getItem(oldResultTiles.get(i+1).get行中总牌数()-1)))){
					start = i;
				}
				//头动了，尾没动，是结束
				else if((cachestandData.contains(oldResultTiles.get(i).getItem(0)) || 
						cachestandData.contains(oldResultTiles.get(i+1).getItem(0))) && 
						(!cachestandData.contains(oldResultTiles.get(i).getItem(oldResultTiles.get(i).get行中总牌数()-1)) ||
						!cachestandData.contains(oldResultTiles.get(i+1).getItem(oldResultTiles.get(i+1).get行中总牌数()-1)))){
					end = i;
				}
				//抓完了，可能是头/尾，也可能是中间，综合判断
				else if((cachestandData.contains(oldResultTiles.get(i).getItem(0)) || 
						cachestandData.contains(oldResultTiles.get(i+1).getItem(0))) && 
						(cachestandData.contains(oldResultTiles.get(i).getItem(oldResultTiles.get(i).get行中总牌数()-1)) ||
						cachestandData.contains(oldResultTiles.get(i+1).getItem(oldResultTiles.get(i+1).get行中总牌数()-1)))){
					begin = i;
				}
			}
			
		}else{
			for(int i = 0; i < 8; i=i+2){
				//没动过
				if((!cachestandData.contains(oldResultTiles.get(i).getItem(0)) || 
						!cachestandData.contains(oldResultTiles.get(i+1).getItem(0))) && 
						(!cachestandData.contains(oldResultTiles.get(i).getItem(oldResultTiles.get(i).get行中总牌数()-1)) ||
						!cachestandData.contains(oldResultTiles.get(i+1).getItem(oldResultTiles.get(i+1).get行中总牌数()-1)))){
					continue;
				}
				//头没动过，尾动过，是开始
				else if((cachestandData.contains(oldResultTiles.get(i).getItem(0)) || 
						cachestandData.contains(oldResultTiles.get(i+1).getItem(0))) && 
						(!cachestandData.contains(oldResultTiles.get(i).getItem(oldResultTiles.get(i).get行中总牌数()-1)) ||
						!cachestandData.contains(oldResultTiles.get(i+1).getItem(oldResultTiles.get(i+1).get行中总牌数()-1)))){
					start = i;
				}
				//头动了，尾没动，是结束
				else if((!cachestandData.contains(oldResultTiles.get(i).getItem(0)) || 
						!cachestandData.contains(oldResultTiles.get(i+1).getItem(0))) && 
						(cachestandData.contains(oldResultTiles.get(i).getItem(oldResultTiles.get(i).get行中总牌数()-1)) ||
						cachestandData.contains(oldResultTiles.get(i+1).getItem(oldResultTiles.get(i+1).get行中总牌数()-1)))){
					end = i;
				}
				//抓完了，可能是头/尾，也可能是中间，综合判断
				else if((cachestandData.contains(oldResultTiles.get(i).getItem(0)) || 
						cachestandData.contains(oldResultTiles.get(i+1).getItem(0))) && 
						(cachestandData.contains(oldResultTiles.get(i).getItem(oldResultTiles.get(i).get行中总牌数()-1)) ||
						cachestandData.contains(oldResultTiles.get(i+1).getItem(oldResultTiles.get(i+1).get行中总牌数()-1)))){
					begin = i;
				}
			}
		}
		//获取抓牌顺序
		boolean border = false;
		if(begin != -1){
			if(start == -1){//begin ,end
				
			}else if(end == -1){ //start, begin
				end = begin;
				begin = start;
			}else if(start != -1 && end != -1){ // start begin ,end
				border = true;
			}else{
				LogHelper.log.error("ZhuaPai：analyzeTilesOne：无法获得抓牌的开始和结束");
				return;
			}
			
		}else{//start end
			begin = start;
		}
		if(begin == -1 || end == -1){
			LogHelper.log.error("ZhuaPai：analyzeTilesOne：无法获得抓牌的开始和结束");
			return;
		}
		
		// 根据计算结果重新排列牌堆的顺序，至少出来2家
		ArrayList<I牌堆行数据> tmpResultTiles = new ArrayList<>();
		tmpResultTiles.addAll(oldResultTiles);
		oldResultTiles.clear();
		if (!border) {
			oldResultTiles.add(tmpResultTiles.get(begin));
			oldResultTiles.add(tmpResultTiles.get(begin + 1));
			oldResultTiles.add(tmpResultTiles.get(end));
			oldResultTiles.add(tmpResultTiles.get(end + 1));
			// 根据默认的功率排序
			/*if (begin == 0) {
				oldResultTiles.add(tmpResultTiles.get(4));
				oldResultTiles.add(tmpResultTiles.get(5));
				oldResultTiles.add(tmpResultTiles.get(12 - 4 - begin - end));
				oldResultTiles
						.add(tmpResultTiles.get(16 - 5 - begin - end - 2));
			} else if (end == 0) {
				oldResultTiles.add(tmpResultTiles.get(12 - 4 - begin - end));
				oldResultTiles
						.add(tmpResultTiles.get(16 - 5 - begin - end - 2));
				oldResultTiles.add(tmpResultTiles.get(4));
				oldResultTiles.add(tmpResultTiles.get(5));
			} else if (begin == 4) {
				oldResultTiles.add(tmpResultTiles.get(0));
				oldResultTiles.add(tmpResultTiles.get(1));
				oldResultTiles.add(tmpResultTiles.get(12 - 0 - begin - end));
				oldResultTiles
						.add(tmpResultTiles.get(16 - 1 - begin - end - 2));
			} else if (end == 4) {
				oldResultTiles.add(tmpResultTiles.get(12 - 0 - begin - end));
				oldResultTiles
						.add(tmpResultTiles.get(16 - 1 - begin - end - 2));
				oldResultTiles.add(tmpResultTiles.get(0));
				oldResultTiles.add(tmpResultTiles.get(1));
			} else */{
				for (int i = 0; i < 8; i = i + 2) {
					if (i == begin || i == end) {
						continue;
					}
					oldResultTiles.add(tmpResultTiles.get(i));
					oldResultTiles.add(tmpResultTiles.get(i + 1));
				}
				LogHelper.log.info("ZhuaPai::analyzeTiles:根据默认排序更新牌堆错误！");
			}
		} else {
			oldResultTiles.add(tmpResultTiles.get(start));
			oldResultTiles.add(tmpResultTiles.get(start + 1));
			oldResultTiles.add(tmpResultTiles.get(begin));
			oldResultTiles.add(tmpResultTiles.get(begin + 1));
			oldResultTiles.add(tmpResultTiles.get(end));
			oldResultTiles.add(tmpResultTiles.get(end + 1));
			oldResultTiles.add(tmpResultTiles.get(12 - start - begin - end));
			oldResultTiles.add(tmpResultTiles.get(16 - start - 1 - begin - 1
					- end - 1));
		}
		
		//在牌序列中查找，找到抓牌的头部，然后从头部开始形成一个序列
		ArrayList<I单张牌数据> tileList = null;
		if (resultData.getZhuapaiorder()) {
			tileList = Ulti.tilesLineArrayToArrayList(oldResultTiles,false,true);
			int i = 0;
			for (i = 0; i < tileList.size(); i++) {
				if (cachestandData.contains(tileList.get(i))) {
					break;
				}
			}
			for (int j = i; j < tileList.size(); j++) {
				resultData.getResultTilelist().add(tileList.get(j));
			}
			for (int j = 0; j < i; j++) {
				resultData.getResultTilelist().add(tileList.get(j));
			}

		} else {// 反序
			tileList = Ulti.tilesLineArrayToArrayList(oldResultTiles,false,false);
			int i = 0;
			for (i = 0; i < tileList.size(); i++) {
				if (cachestandData.contains(tileList.get(i))) {
					break;
				}
			}
			for (int j = i; j < tileList.size(); j++) {
				resultData.getResultTilelist().add(tileList.get(j));
			}
			for (int j = 0; j < i; j++) {
				resultData.getResultTilelist().add(tileList.get(j));
			}
		}
	
		//计算4家牌，仅计算12张，跳牌的根据互相邻居获得
		int iround = 0, icur = 0;
		for(icur = 0; icur < resultData.getResultTilelist().size(); icur+=4){
			if(!cachestandData.contains(resultData.getResultTilelist().get(icur))){
				break;
			}

			resultData.getCurrentTiles().get(iround%4).add(resultData.getResultTilelist().get(icur));
			resultData.getCurrentTiles().get(iround%4).add(resultData.getResultTilelist().get(icur+1));
			resultData.getCurrentTiles().get(iround%4).add(resultData.getResultTilelist().get(icur+2));
			resultData.getCurrentTiles().get(iround%4).add(resultData.getResultTilelist().get(icur+3));
			iround++;
			if(iround == 12){
				break;
			}
		}
		for(int i = 0; i < icur+4; i++){
			resultData.getResultTilelist().remove(0);
		}
		
		//根据功率确定玩家,设置玩家的index
		checkThePlayer();
		
		//继续根据跳牌规则分配4家牌，跳牌规则可配置
		if(ClientMain.TiaopaiType.equals("隔挑")){
			//1. 庄家1、5，后面依次是 2，3，4
			for(int i = 0; i < resultData.getResultTilelist().size(); i++){
				I单张牌数据 tile = resultData.getResultTilelist().get(i);
				if(i == 0 || i== 4){
					resultData.getCurrentTiles().get(0).add(tile);
				}else if(i == 1){
					resultData.getCurrentTiles().get(1).add(tile);				
				}else if(i == 2){
					resultData.getCurrentTiles().get(2).add(tile);
				}else if(i == 3){
					resultData.getCurrentTiles().get(3).add(tile);				
				}else{
					break;
				}
			}
			//删除跳牌
			for(int i = 0; i < 5; i++){
				resultData.getResultTilelist().remove(0);
			}
		}else if(ClientMain.TiaopaiType.equals("")){
			
		}else{
			
		}
		
		//根据用户选择的财神规则确定未来牌
		//翻开的badtype = 6
		//财神放入 HuiziList
		resultData.getHuiziTilelist().clear();
		if(ClientMain.caishen == 1){
			if(ClientMain.caishenOrder == 1){//顺摸财神
				resultData.getResultTilelist().get(0).setBadType(6);
				ArrayList<String> huinameList = new ArrayList<>();
				//huinameList.add(ClientMain.getTileNameById2(resultData.getResultTilelist().get(0).getTilecodeid()));
				huinameList = ClientMain.getCaiShen(ClientMain.getTileNameById2(resultData.getResultTilelist().get(0).getTilecodeid()));
				ArrayList<I单张牌数据> huilist = Ulti.getSamenameTiles(reportedTiles,huinameList);
				resultData.getHuiziTilelist().add(resultData.getResultTilelist().get(0));
				for(I单张牌数据 tile : huilist){
					if(!resultData.getHuiziTilelist().contains(tile)){
						resultData.getHuiziTilelist().add(tile);
					}
				}
				//头部直接删除财神
				resultData.getResultTilelist().remove(0);
			}else if(ClientMain.caishenLast == 1){//后财神
				//不是最后一个，需要设置type显示
				resultData.getResultTilelist().get(resultData.getResultTilelist().size()-2).setBadType(6);
				ArrayList<String> huinameList = new ArrayList<>();
//				huinameList.add(ClientMain.getTileNameById2(resultData.getResultTilelist().get(resultData.getResultTilelist().size()-2).getTilecodeid()));
				huinameList = ClientMain.getCaiShen(ClientMain.getTileNameById2(resultData.getResultTilelist().get(resultData.getResultTilelist().size()-2).getTilecodeid()));
				ArrayList<I单张牌数据> huilist = Ulti.getSamenameTiles(reportedTiles,huinameList);
				resultData.getHuiziTilelist().add(resultData.getResultTilelist().get(resultData.getResultTilelist().size()-2));
				for(I单张牌数据 tile : huilist){
					if(!resultData.getHuiziTilelist().contains(tile)){
						resultData.getHuiziTilelist().add(tile);
					}
				}
				
				//删除财神
			//	resultData.getResultTilelist().remove(resultData.getResultTilelist().size()-1);
			}else if(ClientMain.caishenFixed == 1){
				//固定财神，仅传递财神牌，其他不动
				ArrayList<I单张牌数据> huilist = Ulti.getSamenameTiles(reportedTiles,ClientMain.caishenArrayList);
				resultData.getHuiziTilelist().addAll(huilist);
				
			}else if(ClientMain.caishenShaizi == 1){
				//通过骰子确定，放到出牌对象里处理
			}
		}
		
		
		//计算4家待抓的牌，从庄家的下家开始，逐个分配
		for(int i = 0; i < resultData.getResultTilelist().size(); i++){
			I单张牌数据 tile = resultData.getResultTilelist().get(i);
			if(i%4 == 0){//庄家的下家先抓
				resultData.getNextTiles().get(1).add(tile);
			}else if(i%4 == 1){
				resultData.getNextTiles().get(2).add(tile);
			}else if(i%4 == 2){
				resultData.getNextTiles().get(3).add(tile);
			}else if(i%4 == 3){ //庄家
				resultData.getNextTiles().get(0).add(tile);
			}
		}
		
		//重新计算牌墙剩下的牌情况,加上null分割
		if(resultData.getZhuapaiorder()){
			tileList = Ulti.tilesLineArrayToArrayList(oldResultTiles, true,true);
		}else{
			tileList = Ulti.tilesLineArrayToArrayList(oldResultTiles, true,false);
		}
		//牌墙牌在null牌墙的索引
		begin = tileList.indexOf(resultData.getResultTilelist().get(0));
		end = tileList.indexOf(resultData.getResultTilelist().get(resultData.getResultTilelist().size()-1));
		resultData.getResultTilelist().clear();
		if(begin < end){
			for(int i = begin; i <= end; i++){
				resultData.getResultTilelist().add(tileList.get(i));
			}
		}else{
			for(int i = begin; i < tileList.size(); i++){
				resultData.getResultTilelist().add(tileList.get(i));
			}
			for(int i = 0; i <= end; i++){
				resultData.getResultTilelist().add(tileList.get(i));
			}
		}

	}
	
	/**
	 * 根据3次抓牌间隔判断该牌序是否合理
	 * @param orderArrayList
	 * @return
	 */
	public int isLineOk(ArrayList<Integer> orderArrayList){
		//检查只要有一个间隔16就可以
		boolean bOK = false;
		for(int i = 0; i < 4; i++){
			for(int j = 4; j < 8; j++){
				if(orderArrayList.get(i) + 16 == orderArrayList.get(j) || 
						orderArrayList.get(i) + 16 == orderArrayList.get(j)+ClientMain.TotalTileNums){
					bOK = true;
					break;
				}
			}
			if(bOK){
				break;
			}
		}
		if(bOK){
			for(int i = 4; i < 8; i++){
				for(int j = 8; j < 12; j++){
					if(orderArrayList.get(i) + 16 == orderArrayList.get(j) || 
							orderArrayList.get(i) + 16 == orderArrayList.get(j)+ClientMain.TotalTileNums){
						return 1;
						
					}
				}
			}
			
		}
		//反序判断
		for(int i = 0; i < 4; i++){
			for(int j = 4; j < 8; j++){
				if(orderArrayList.get(i)  == orderArrayList.get(j)+16 || 
						orderArrayList.get(i)  == orderArrayList.get(j)-ClientMain.TotalTileNums+16){
					bOK = true;
					break;
				}
			}
			if(bOK){
				break;
			}
		}
		if(bOK){
			for(int i = 4; i < 8; i++){
				for(int j = 8; j < 12; j++){
					if(orderArrayList.get(i) == orderArrayList.get(j)+16 || 
							orderArrayList.get(i) + 16 == orderArrayList.get(j)-ClientMain.TotalTileNums+16){
						return 2;
						
					}
				}
			}
			
		}
		
		
		return 0;
	}
	
	/**
	 * 
	 * @param integers
	 * @return
	 */
	public int getMaxnumIntegers(ArrayList<Integer> integers){
		int[] tmpint = new int[8];
		for(Integer i : integers){
			if(i >= 600){
				tmpint[6]++;
			}else if(i >= 400 && i < 600){
				tmpint[4]++;
			}else if(i >= 200 && i < 400){
				tmpint[2]++;
			}else{
				tmpint[0]++;
			}
		}
		for(int i =0; i < 8; i++){
			if(tmpint[i] >= 3){
				return i;
			}
		}
		return 0;
	}
	
	public ArrayList<Instant> getTimesNumbers(int start){
		ArrayList<Instant> rettime = new ArrayList<>();
		int num = 0;
		for(int i = start; i < start+3; i++){
			if(cacheTimes.get(i) != null){
				rettime.add(cacheTimes.get(i));
				num++;
			}
		}
		return rettime;
	}
	
	public boolean whichTimeisNewer(int a, int b){
		int num = 0;
		long atime = 0, btime = 0;
		for(int i = a; i < a+3; i++){
			if(cacheTimes.get(i) != null){
				num++;
				atime += cacheTimes.get(i).getMillis();
			}
		}
		atime = atime/num;
		
		num = 0;
		for(int i = b; i < b+3; i++){
			if(cacheTimes.get(i) != null){
				num++;
				btime += cacheTimes.get(i).getMillis();
			}
		}
		btime = btime/num;
		
		if(atime > btime){
			return true;
		}else{
			return false;
		}
	}

	
	@Override
	public void setOldResultTiles(ArrayList<I牌堆行数据> oldResultTiles) {

		this.oldResultTiles = oldResultTiles;
	}

	@Override
	public ArrayList<I牌堆行数据> getResultTiles() {
		return null;
	}
	
	public PlayerTileData getPlayerTileData() {
		return resultData;
	}
	
	public ArrayList<I单张牌数据> getCachestandData() {
		return cachestandData;
	}


	public void reset() {
		oldResultTiles.clear();
		cachestandData.clear();
		cacheTiles4.clear();
		for(int i = 0; i < 20; i++){
			cacheTiles8.set(i,null);
			cacheTiles12.set(i,null);
		}
		state = ConstantValue.IdleState;
		resultData.reset();
		for(int i = 0; i < 12; i++){
			cacheTimes.set(i, null);
		}
	}


	/**
	 * 根据功率获取玩家的位置
	 * 按玩家，下家，对家，上家 顺序排列
	 * 
	 */
	public void checkThePlayer(){
		//找一下坏牌的位置，需要去掉
		int badindex = -1;
		for(int i = 0; i < resultData.getCurrentTiles().size(); i++){
			for(int j = 0; j < resultData.getCurrentTiles().get(i).size(); j++){
				if(resultData.getCurrentTiles().get(i).get(j).getBadType() > 0){
					badindex = j;
					break;
				}
			}
			if(badindex != -1){
				break;
			}
		}
		
		ArrayList<Integer> powerList = new ArrayList<>();
		int ipower = 0;
		StringBuffer stringBuffer = new StringBuffer();
		for(int i = 0; i < resultData.getCurrentTiles().size(); i++){
			for(int j = 0; j < resultData.getCurrentTiles().get(i).size(); j++){
				if(j == badindex){
					continue;
				}
				ipower += reportedTiles.getSingleItem(resultData.getCurrentTiles().get(i).get(j).getTilecodeid()).getPower();
				stringBuffer.append(resultData.getCurrentTiles().get(i).get(j).getTilecodeid());
				stringBuffer.append("\\");
				stringBuffer.append( reportedTiles.getSingleItem(resultData.getCurrentTiles().get(i).get(j).getTilecodeid()).getPower());
				stringBuffer.append(" ");
			}

			stringBuffer.append("\n");
			stringBuffer.append(ipower);
			stringBuffer.append("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
			
			powerList.add(ipower);
			ipower = 0;
		}
		
	//	ClientMain.frameWin.refreshTest(stringBuffer);
		
		int iMax = Collections.max(powerList);
		int iMin = Collections.min(powerList);
		int min=0,max=0,a=4,b=4;
		min = powerList.indexOf(iMin);
		
		resultData.setPlayerindex(min);		
		
	}

	@Override
	public void analyzeTiles() {
		// TODO Auto-generated method stub
		
	}


	
}
