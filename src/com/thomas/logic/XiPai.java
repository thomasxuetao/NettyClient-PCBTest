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

public class XiPai implements PaiInterface {

	private ArrayList<ArrayList<I牌堆行数据>> cacheTiles;
	private ArrayList<I牌堆行数据> resultTiles;	
	private ArrayList<Integer> weightValue;
	private I牌堆数据 reportedTiles;
	private final int xipaicounts = 25;//600ms一次统计n次
	private Instant startTime;

	public XiPai() {
		cacheTiles = new ArrayList<ArrayList<I牌堆行数据>>();
		cacheTiles.add(null);
		cacheTiles.add(null);
		for (int i = 2; i <= ClientMain.LineTilesNums+1; i++) {
			ArrayList<I牌堆行数据> tmpArrayList = new ArrayList<I牌堆行数据>();
			cacheTiles.add(tmpArrayList);
		}
		resultTiles = new ArrayList<I牌堆行数据>(8);
		weightValue = new ArrayList<Integer>();
		startTime = null;
	}

	/**
	 * 按牌行个数存入缓存
	 * input: data 
	 */
	public void changeTiles(I牌堆数据 data) {
		if(startTime == null){
			startTime = new Instant();
		}
		reportedTiles = data;

		//按数组增长方向排序
		for(int i = 0; i < reportedTiles.get牌堆中牌的总行数() ;i++){
			if(reportedTiles.getItem(i).get行中总牌数() > 1 && reportedTiles.getItem(i).getDirection() == ConstantValue.DOWNSIDE){
				if(isRevertLine(reportedTiles.getItem(i))){
					reportedTiles.setItem(i, reportedTiles.getItem(i).revert());
				}else{
					reportedTiles.setItem(i, reportedTiles.getItem(i).clone());
				}
			}
		}
		//
		
		//缓存数据
		for (int i = 0; i < reportedTiles.get牌堆中牌的总行数(); i++) {
			int n = reportedTiles.getItem(i).get行中总牌数();
			if (n >= 2 && n <= ClientMain.LineTilesNums+1
					&& reportedTiles.getItem(i).getDirection() == ConstantValue.DOWNSIDE) {
				ArrayList<I牌堆行数据> tmp = cacheTiles.get(n);
				if (!tmp.contains(reportedTiles.getItem(i))) {// 是否已经包括了？
					tmp.add(reportedTiles.getItem(i).clone());
					cacheTiles.set(n, tmp);
				}
			}
		}
		//计算并缓存数据趋势
		int wv = 0;
		for (int i = 2; i < cacheTiles.size(); i++) {
			int num = cacheTiles.get(i).size();
			wv += num * (i + 2);
		}
		
		if(!weightValue.contains(wv)){
			weightValue.add(wv);
			LogHelper.log.info("Xipai:changeTiles():weightValue:" + wv);	
		}
	}
	
	/**
	 * 该行是否需要翻转，根据Z变化时间
	 * @param line
	 * @return
	 */
	public boolean isRevertLine(I牌堆行数据 line){
		int isize = line.get行中总牌数();
		if(isize < 1){
			return false;
		}
		int sum = 0;
//		for(int i = 0; i < isize/2; i++){
//			sum += (int) (line.getItem(i).getzDirectionTime().getMillis() - line.getItem(isize-1-i).getzDirectionTime().getMillis());
//		}
		for(int i = 0; i < isize/2; i++){
			sum += (int) (line.getItem(i).getNeighborTime().getMillis() - line.getItem(isize-1-i).getNeighborTime().getMillis());
		}
		
		if(sum >= 0){
			return true;
		}
		
		return false;
	}

	/**
	 * 根据缓存的牌判断是否正在洗牌
	 */
	public boolean isRunning() {
		if(weightValue.size() < xipaicounts){
			return false;
		}
		if (Ulti.isAscend(weightValue, weightValue.size())) {
			LogHelper.log.info("Xipai:isRunning():state:ConstantValue.XiPaiing");
			return true;
		}
		LogHelper.log.info("Xipai:isRunning():state:ConstantValue.IdleState");
		return false;
	}
	
	/**
	 * 判断当前是否洗牌完成
	 * 大于等于7个数组且向下就认为完成
	 */
	public boolean isCompleted() {
		//没有坏牌，正常8个数组；如果邻居单侧、双侧坏了，运动信息坏了，7个数组就可以了
		if(ClientMain.SameLineNums && 
				Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) >= 7 &&
				reportedTiles.getDirection() == ConstantValue.DOWNSIDE) {

				LogHelper.log.info("Xipai:isCompleted():state:ConstantValue.XiPaied");

				//看看是否缺牌
				int tilesetid = reportedTiles.getTileSetId();

				
				//判断一下是否有牌的邻居和运动信息一直没有更新，则认为是坏牌
				for(int i = 0; i < reportedTiles.get牌堆中牌的总行数() ;i++){
					for(int j = 0; j < reportedTiles.getItem(i).get行中总牌数();j++){
						if(reportedTiles.getItem(i).getItem(j).getNeighborTime().isBefore(startTime)){
							//设置坏牌的类型
							reportedTiles.getItem(i).getItem(j).setBadType(1);
							if(tilesetid == Bridge.logicA.getTileSetid()){
								Bridge.logicA.getBadTilesList().add(reportedTiles.getItem(i).getItem(j));
							}else{
								Bridge.logicB.getBadTilesList().add(reportedTiles.getItem(i).getItem(j));
							}
						}
						if(reportedTiles.getItem(i).getItem(j).getDirectionTime().isBefore(startTime)){
							if(reportedTiles.getItem(i).getItem(j).getBadType() == 1){
								reportedTiles.getItem(i).getItem(j).setBadType(3);
							}else{
								reportedTiles.getItem(i).getItem(j).setBadType(2);
							}
							
							if(tilesetid == Bridge.logicA.getTileSetid()){
								Bridge.logicA.getBadTilesList().add(reportedTiles.getItem(i).getItem(j));
							}else{
								Bridge.logicB.getBadTilesList().add(reportedTiles.getItem(i).getItem(j));
							}
						}
					}
				}
								
				return true;
		}
		if(!ClientMain.SameLineNums  &&	reportedTiles.getDirection() == ConstantValue.DOWNSIDE){
			if((Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 4 && 
					Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums+1) == 4)||
					(Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 3 && 
					Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums+1) == 4) ||
					(Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 4 && 
					Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums+1) == 3)){

				LogHelper.log.info("Xipai:isCompleted():state:ConstantValue.XiPaied");
				
				return true;
			}
		}
				

		
		
//		if(ConstantValue.TotalTileNums == reportedTiles.getValidtilesNums()){
//			if(Ulti.getLineCountOfSome(reportedTiles, ConstantValue.LineTilesNums) == 7 &&
//					reportedTiles.getDirection() == ConstantValue.DOWNSIDE) {
//
//					LogHelper.log.info("Xipai:isCompleted():state:ConstantValue.XiPaied");
//					return true;
//			}
//		//有一张坏牌，超时删除或者开始就坏了没有上报
//		}else if(ConstantValue.TotalTileNums - reportedTiles.getValidtilesNums() == 1){
//			if(Ulti.getLineCountOfSome(reportedTiles, ConstantValue.LineTilesNums) == 7 &&
//				Ulti.getLineCountOfSome(reportedTiles, ConstantValue.LineTilesNums-1) == 1 &&
//				reportedTiles.getDirection() == ConstantValue.DOWNSIDE){
//
//					LogHelper.log.info("Xipai:isCompleted():state:ConstantValue.XiPaied");
//					return true;
//				
//			}
//			if(Ulti.getLineCountOfSome(reportedTiles, ConstantValue.LineTilesNums) == 7 &&
//					reportedTiles.get牌堆中牌的总行数() == 9 &&
//					reportedTiles.getDirection() == ConstantValue.DOWNSIDE){
//
//					LogHelper.log.info("Xipai:isCompleted():state:ConstantValue.XiPaied");
//					return true;
//				
//			}
//		}else{
//		}

		return false;
	}

	/**
	 * 
	 * 判定为洗牌结束了，分析当前牌的状态
	 * 牌序调整，找同一牌行，判断上下关系
	 */
	public void analyzeTiles() {
		
		//完整8个数组的情况，获取最后洗牌完成的最终数据
		ArrayList<I牌堆行数据> lasttiles = null;
		if(ClientMain.SameLineNums){
			lasttiles = cacheTiles.get(ClientMain.LineTilesNums);
		}else{
			lasttiles = cacheTiles.get(ClientMain.LineTilesNums);
			lasttiles.addAll(cacheTiles.get(ClientMain.LineTilesNums+1));
		}
		
		if(lasttiles.size() < 7){
			LogHelper.log.info("Xipai:analyzeTiles():搜集的数组个数小于7个!!!");
			return;
		}

		// 判断同一组的2个数组
		ArrayList<I牌堆行数据> lefttiles = new ArrayList<I牌堆行数据>();
		for (I牌堆行数据 line : lasttiles) {
			//没有处理过。。。
			if (!resultTiles.contains(line) && !lefttiles.contains(line)) {
				int n=0;
				for(n = ClientMain.LineTilesNums+1; n > 10; n--){
					I牌堆行数据 sametile = getSameNeighborTimelineTiles(lasttiles, line, n);
					if(sametile != null) {//ok
						resultTiles.add(line);
						resultTiles.add(sametile);
						break;
					}
				}
				
				if(n == 10){//may be null??
					lefttiles.add(line);
				}
			}
		}
		//判断数组的上下关系，根据两个数组姿势变化时间判断，先上后下
		for (int k = 0; k < resultTiles.size()-1; k=k+2) {
			if(isRevertUpdownTiles(resultTiles.get(k),resultTiles.get(k+1))){
				Collections.swap(resultTiles, k, k+1);
			}
		}
		
		//完整的8个数组，也包括了运动信息坏 和 单侧坏在旁边的一种情况处理完毕！！！
		
		//=======================================================
		//7个数组，邻居坏了导致
		//单侧坏 2个数组，在边上还可能完整，上面代码处理了
		//双侧坏 2个数组，或3个数组
		if(Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 7 || 
				(Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 3 && 
				Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums+1) == 4) ||
				(Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 4 && 
				Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums+1) == 3)){
			if(lefttiles.size() == 1){
				I牌堆行数据 tmpline = new I牌堆行数据();

				//先找到对应的牌行
				ArrayList<I牌堆行数据> tmpresult = new ArrayList<I牌堆行数据>();
				for(int i = 0; i < reportedTiles.get牌堆中牌的总行数(); i++){
					if(reportedTiles.getItem(i).get行中总牌数() != ClientMain.LineTilesNums &&
							reportedTiles.getItem(i).get行中总牌数() != ClientMain.LineTilesNums+1){
						tmpresult.add(reportedTiles.getItem(i));							
					}
				}
				//单侧双侧坏的情况
				if(tmpresult.size() == 2){
					//l---------
					//---------l
					//------l----
					if(tmpresult.get(0).getItem(0).getzDirectionTime().isAfter(tmpresult.get(1).getItem(0).getzDirectionTime())){
						tmpline = tmpline.connetTileLines(tmpresult.get(1));
						tmpline = tmpline.connetTileLines(tmpresult.get(0));
					}else{
						tmpline = tmpline.connetTileLines(tmpresult.get(0));
						tmpline = tmpline.connetTileLines(tmpresult.get(1));						
					}
				}else if(tmpresult.size() == 3){//双侧坏牌在中间
					//---o---
					ArrayList<Long> tmpIntegers = new ArrayList<>();
					for(int i = 0;i<3;i++){
						tmpIntegers.add(tmpresult.get(i).getItem(0).getzDirectionTime().getMillis());
					}
					int min = tmpIntegers.indexOf(Collections.min(tmpIntegers));
					int max = tmpIntegers.indexOf(Collections.max(tmpIntegers));
					int mid = 3-min-max;
					tmpline = tmpline.connetTileLines(tmpresult.get(min));
					tmpline = tmpline.connetTileLines(tmpresult.get(mid));
					tmpline = tmpline.connetTileLines(tmpresult.get(max));
				}
				
				if(isRevertUpdownTiles(lefttiles.get(0), tmpline)){
					resultTiles.add(tmpline);
					resultTiles.add(lefttiles.get(0));
				}else{
					resultTiles.add(lefttiles.get(0));
					resultTiles.add(tmpline);
				}

			}
		}
		
	}
	
	
	
	/**
	 * 一张坏牌的情形，分析牌关系
	 * 1. 坏牌在首尾，2. 坏牌在中间
	 * 先处理7个牌行，会剩下一个没有配对的，再在牌堆中找
	 */
	public void analyzeTilesOne(){

		ArrayList<I牌堆行数据> lasttiles = null;
		if(ClientMain.SameLineNums){
			lasttiles = cacheTiles.get(ClientMain.LineTilesNums);
		}else{
			lasttiles = cacheTiles.get(ClientMain.LineTilesNums);
			lasttiles.addAll(cacheTiles.get(ClientMain.LineTilesNums+1));
		}
		// 判断同一组的2个数组
		ArrayList<I牌堆行数据> lefttiles = new ArrayList<I牌堆行数据>();
		for (I牌堆行数据 line : lasttiles) {
			//没有处理过。。。
			if (!resultTiles.contains(line) && !lefttiles.contains(line)) {
				int n=0;
				for(n = ClientMain.LineTilesNums+1; n > 10; n--){
					I牌堆行数据 sametile = getSameNeighborTimelineTiles(lasttiles, line, n);
					if(sametile != null) {//ok
						resultTiles.add(line);
						resultTiles.add(sametile);
						break;
					}
				}
				

				if(n == 10){//may be null??
					lefttiles.add(line);
				}
			}
		}

		// 判断数组的上下关系，根据两个数组姿势变化时间判断，先上后下
		for (int k = 0; k < resultTiles.size() - 1; k = k + 2) {
			if (isRevertUpdownTiles(resultTiles.get(k), resultTiles.get(k + 1))) {
				Collections.swap(resultTiles, k, k + 1);
			}

		}
	
		//剩下一个牌行没有配对,大于1个则有问题！
		if(lefttiles.size() == 1){
			I牌堆行数据 tmpline = new I牌堆行数据();
			int tilesetid = reportedTiles.getTileSetId();
			ArrayList<I单张牌数据> badtiles = Ulti.getBadTiles(reportedTiles);
			
			//先找到对应的非完整牌行
			ArrayList<I牌堆行数据> tmpresult = new ArrayList<I牌堆行数据>();
			for(int i = 0; i < reportedTiles.get牌堆中牌的总行数(); i++){
				if(reportedTiles.getItem(i).get行中总牌数() != ClientMain.LineTilesNums && 
						reportedTiles.getItem(i).get行中总牌数() != ClientMain.LineTilesNums+1){
					tmpresult.add(reportedTiles.getItem(i));
				}
			}
			//坏牌在两边
			if(tmpresult.size() == 1){
				//---------o
				if(tmpresult.get(0).getItem(0).getNeighborTime().isEqual(lefttiles.get(0).getItem(0).getNeighborTime())
						||tmpresult.get(0).getItem(1).getNeighborTime().isEqual(lefttiles.get(0).getItem(1).getNeighborTime())
						||tmpresult.get(0).getItem(2).getNeighborTime().isEqual(lefttiles.get(0).getItem(2).getNeighborTime())
						){
					tmpline = tmpline.connetTileLines(tmpresult.get(0));
					if(badtiles != null){
						tmpline = tmpline.connetOneTile(badtiles.get(0));
					}
				}else{//o----------
					if(badtiles != null){
						tmpline = tmpline.connetOneTile(badtiles.get(0));
					}
					tmpline = tmpline.connetTileLines(tmpresult.get(0));
				}
			}else if(tmpresult.size() == 2){//坏牌在中间,判断Z时间
				//---o--- -o-----  ------o-
				if(tmpresult.get(0).getItem(tmpresult.get(0).get行中总牌数()/2).getzDirectionTime().isAfter(tmpresult.get(1).getItem(tmpresult.get(1).get行中总牌数()/2).getzDirectionTime())){
					tmpline = tmpline.connetTileLines(tmpresult.get(1));
					if(badtiles != null){
						tmpline = tmpline.connetOneTile(badtiles.get(0));
					}
					tmpline = tmpline.connetTileLines(tmpresult.get(0));
				}else{
					tmpline = tmpline.connetTileLines(tmpresult.get(0));
					if(badtiles != null){
						tmpline = tmpline.connetOneTile(badtiles.get(0));
					}
					tmpline = tmpline.connetTileLines(tmpresult.get(1));
				}
				
			}
			//
			if(isRevertUpdownTiles(lefttiles.get(0), tmpline)){
				resultTiles.add(tmpline);
				resultTiles.add(lefttiles.get(0));
			}else{
				resultTiles.add(lefttiles.get(0));
				resultTiles.add(tmpline);
			}

		}else{
			LogHelper.log.info("Xipai:analyzeTilesOne():没有找到残留的数组");
		}

	}
	
	
	
	/**
	 * 获取两个牌行的上下关系，根据最后Z轴从向上变成向下的时间
	 * 返回：true，false（需要交换）
	 */
	public boolean isRevertUpdownTiles(I牌堆行数据 up, I牌堆行数据 down){
		int ab = 0, ba = 0;
		for (int i = 0; i < up.get行中总牌数(); i++) {
			if(up.getItem(i).getTilecodeid() == 0 || down.getItem(i).getTilecodeid() == 0){
				continue;
			}
			if(up.getItem(i).getzDirectionTime().getMillis() == 0 || down.getItem(i).getzDirectionTime().getMillis() == 0){
				continue;
			}
			int itime = (int) (up.getItem(i).getzDirectionTime().getMillis()
					- down.getItem(i).getzDirectionTime().getMillis());
			System.out.println(up.getItem(i).toString());
			System.out.println(down.getItem(i).toString());
			if (itime > 300) {
				ab++;
			} else if(itime < -300){
				ba++;
			}
		}
		if (ab < ba) {// exchange
			return true;
		}
		return false;
	}
	
	
	
	/**
	 * 获取邻居时间一致的两行牌，n个一致
	 * @param tileArrays
	 * @param tile
	 * @return
	 */
	public I牌堆行数据 getSameNeighborTimelineTiles(ArrayList<I牌堆行数据> tileArrays, I牌堆行数据 tile, int n){
		int ieqnum = 0;
		for(int i = 0; i < tileArrays.size(); i++){
			I牌堆行数据 tmpdata = tileArrays.get(i);
			if(tmpdata.equals(tile) || tmpdata.get行中总牌数() != tile.get行中总牌数()){
				continue;
			}
			int j = 0;
			for(j = 0; j < tile.get行中总牌数();j++){
				if(tmpdata.getItem(j).getNeighborTime().isEqual(tile.getItem(j).getNeighborTime())){
					ieqnum++;
				}
			}
			if(ieqnum == n){
				return tileArrays.get(i);
			}
			ieqnum = 0;
		}
		
		for(int i = 0; i < tileArrays.size(); i++){
			I牌堆行数据 tmpdata = tileArrays.get(i);
			if(tmpdata.equals(tile) || tmpdata.get行中总牌数() != tile.get行中总牌数()){
				continue;
			}
			int j = 0;
			for(j = 0; j < tile.get行中总牌数();j++){
				if(tmpdata.getItem(j).getNeighborTime().isEqual(tile.getItem(tile.get行中总牌数()-1-j).getNeighborTime())){
					ieqnum++;
				}
			}
			if(ieqnum == n){
				return tileArrays.get(i);
			}
			ieqnum = 0;
		}
		
		return null;
	}
	
	/**
	 * 获取tile的在前面tiles里面的行数据
	 * @param tiles
	 * @param tile
	 * @return
	 */
	public I牌堆行数据 getPreline(ArrayList<I牌堆行数据> tiles, I牌堆行数据 tile){
		for(int i = 0; i < tiles.size(); i++){
			if(tile.contains(tiles.get(i))){
				return tiles.get(i);
			}
		}
		return null;
	}
	
	/**
	 * 获取tile的在后面tiles里面的行数据
	 * @param tiles
	 * @param tile
	 * @return
	 */
	public I牌堆行数据 getLastline(ArrayList<I牌堆行数据> tiles, I牌堆行数据 tile){
		for(int i = 0; i < tiles.size(); i++){
			if(tiles.get(i).contains(tile)){
				return tiles.get(i);
			}
		}
		return null;
	}
	



	/**
	 * 
	 */
	public void reset() {
		for(int i = 2; i <= ClientMain.LineTilesNums+1; i++){
			cacheTiles.get(i).clear();
		}
		resultTiles.clear();
		weightValue.clear();
		startTime = null;
	}


	
	public int getCacheSize(){
		int isize = 0;
		for (int i = 0; i < cacheTiles.size(); i++) {
			ArrayList<I牌堆行数据> tmptiles = cacheTiles.get(i);
			isize += tmptiles.size();
		}
		return isize;
	}
	
	public ArrayList<I牌堆行数据> getResultTiles() {
		return resultTiles;
	}

	public void setResultTiles(ArrayList<I牌堆行数据> resultTiles) {
		this.resultTiles = resultTiles;
	}

	@Override
	public void setOldResultTiles(ArrayList<I牌堆行数据> oldResultTiles) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 洗牌完成根据功率判断牌墙的分布情况
	 */
	public void checkThePlayer(){
		ArrayList<I牌堆行数据> tmptiles = new ArrayList<>(8);
		tmptiles.addAll(resultTiles);
		resultTiles.clear();
		ArrayList<Integer> powerList = new ArrayList<>();
		for(int i = 0; i < 8; i+=2){
			ArrayList<I牌堆行数据> tmp = new ArrayList<>();
			tmp.add(tmptiles.get(i));
			tmp.add(tmptiles.get(i+1));
			
			powerList.add(Ulti.getTilesPower(tmp));
			tmp.clear();
		}
		//自己家牌
		int iMin = Collections.min(powerList);
		int iMax = Collections.max(powerList);
		int min = powerList.indexOf(iMin);
		int max = powerList.indexOf(iMax);
		resultTiles.add(tmptiles.get(min*2));
		resultTiles.add(tmptiles.get(min*2+1));
		tmptiles.remove(min*2);
		tmptiles.remove(min*2);
		
		//取平均值+20，大于则去掉
		ArrayList<Integer> timeIntegers = new ArrayList<>();
		for(int i = 0; i < 6; i+=2){
			int ipower = 0,iaveragepower = 0,itimes = 0;
			for(int j = 0; j < tmptiles.get(i+1).get行中总牌数();j++){
				ipower += tmptiles.get(i+1).getItem(j).getPower();
			}
			iaveragepower = ipower/ClientMain.LineTilesNums+20;
			for(int j = 0; j < tmptiles.get(i+1).get行中总牌数();j++){
				if(tmptiles.get(i+1).getItem(j).getPower() > iaveragepower){
					itimes++;
				}
			}
			timeIntegers.add(itimes);
			itimes = 0;
		}
		max = Collections.max(timeIntegers);
		timeIntegers.clear();
		for(int i = 0; i < 6; i+=2){
			timeIntegers.add(Ulti.getTilesPower(tmptiles.get(i+1),max));
		}
		iMax = Collections.max(timeIntegers);
		max = timeIntegers.indexOf(iMax);
		
		
		ArrayList<I牌堆行数据> maxfiles = new ArrayList<>();
		maxfiles.add(tmptiles.get(max*2));
		maxfiles.add(tmptiles.get(max*2+1));
		tmptiles.remove(max*2);
		tmptiles.remove(max*2);
		
		//判断上家下家，根据zpower来判断
		int ipower1 = Ulti.getTilesPower(tmptiles.get(1),0);
		int ipower2 = Ulti.getTilesPower(tmptiles.get(3),0);
		if(ipower1 > ipower2){
			resultTiles.add(tmptiles.get(2));
			resultTiles.add(tmptiles.get(3));
			resultTiles.addAll(maxfiles);
			resultTiles.add(tmptiles.get(0));
			resultTiles.add(tmptiles.get(1));
		}else{
			resultTiles.add(tmptiles.get(0));
			resultTiles.add(tmptiles.get(1));
			resultTiles.addAll(maxfiles);
			resultTiles.add(tmptiles.get(2));
			resultTiles.add(tmptiles.get(3));
		}
	

	}


	
	/**
	 * 
	 * @param a
	 * @param b
	 * @return a大于b的次数
	 */
	public int comparePowerTimes(I牌堆行数据 a, I牌堆行数据 b, boolean head){
		int itimes = 0;
		if(head){
			for(int i = 0; i < 8; i++){
				if(a.getItem(i).getPower() > b.getItem(i).getPower()){
					itimes++;
				}else if(a.getItem(i).getPower() < b.getItem(i).getPower()){
					itimes--;
				}
			}
		}else{
			for(int i = a.get行中总牌数()-1; i >= a.get行中总牌数()-8; i--){
				if(a.getItem(i).getPower() > b.getItem(i).getPower()){
					itimes++;
				}else if(a.getItem(i).getPower() < b.getItem(i).getPower()){
					itimes--;
				}
			}
		}
		
		return itimes;
	}

}
