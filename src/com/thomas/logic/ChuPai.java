package com.thomas.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import org.joda.time.Instant;
import com.thomas.client.ClientMain;
import com.thomas.data.ConstantValue;
import com.thomas.data.I单张牌数据;
import com.thomas.data.I牌堆数据;
import com.thomas.data.I牌堆行数据;
import com.thomas.data.Ulti;

public class ChuPai implements PaiInterface{
	//抓牌的数据结果
	private PlayerTileData oldPlayerTileData;
	//出牌的新数据结果
	private PlayerTileData resultData;

	//从抓牌开始。。曾经站立牌缓存
	private ArrayList<I单张牌数据> cachestandData;
	//已经出的牌缓存判断
	private ArrayList<I单张牌数据> chuTileList;
	//新出现的字面向上的牌
	private ArrayList<I单张牌数据> newChuTileList;
	//新出现字面不再向上的牌，可能是误操作碰倒了
	private ArrayList<I单张牌数据> newReturnTileList;
	private HashMap<Integer, Integer> newReturnTileMap;

	//当前玩家的出牌控制结构,从庄家0开始，1，2，3，共4个元素
	private ArrayList<OnePlayer> currentplayerlist;
	//当前出牌索引记录
	private ArrayList<Integer> playerchuList;
	//没有入手的不确定归属的牌列表
	private ArrayList<I单张牌数据> notsureTileList;
	//是否调整过牌行顺序
	private boolean ifSwitchedTileline;
	
	//上牌
	private I牌堆数据 reportedTiles;
	
	public ChuPai(){
		resultData = new PlayerTileData();
		chuTileList = new ArrayList<>();
		cachestandData = new ArrayList<>();
		newChuTileList = new ArrayList<>();
		newReturnTileList = new ArrayList<>();
		currentplayerlist = new ArrayList<>();	
		playerchuList = new ArrayList<>();
		newReturnTileMap = new HashMap<>();
		notsureTileList = new ArrayList<>();
		ifSwitchedTileline = false;
	}
	
	public void initdata(){
		resultData.setResultTilelist(oldPlayerTileData.getResultTilelist());//牌墙
		resultData.setCurrentTiles(oldPlayerTileData.getCurrentTiles());//当前牌
		resultData.setNextTiles(oldPlayerTileData.getNextTiles());//未来牌
		resultData.setPlayerindex(oldPlayerTileData.getPlayerindex());
		resultData.setZhuapaiorder(oldPlayerTileData.getZhuapaiorder());
		resultData.setHuiziTilelist(oldPlayerTileData.getHuiziTilelist());
		for(int i = 0; i < 4; i++){
			OnePlayer one = new OnePlayer(i);
			one.addCachelist(oldPlayerTileData.getCurrentTiles().get(i));
			currentplayerlist.add(one);
		}
	}
	
	/**
	 * 牌更新
	 */
	public void changeTiles(I牌堆数据 data) {
		reportedTiles = data;
		// 处理骰子定财神，轮询牌墙，找到字面向上的，并且邻居没有变化
		if (ClientMain.caishen == 1 && ClientMain.caishenShaizi == 1
				&& resultData.getHuiziTilelist().size() == 0) {
			I单张牌数据 huitile = null;
			int icount = 0;
			for (I单张牌数据 tmptile : resultData.getResultTilelist()) {
				if (tmptile == null) {
					continue;
				}
				I单张牌数据 curtile = reportedTiles.getSingleItem(tmptile.getTilecodeid());
				if (curtile == null) {
					continue;
				}
				icount++;
				if(icount < 3){//跳过前2张牌
					continue;
				}
				if (curtile.getDirection() == ConstantValue.UPSIDE) {
					// 置位
					huitile = tmptile;
					tmptile.setBadType(6);
					break;
				}
			}
			// 根据选择的几种方式确定财神牌
			if (huitile != null) {
				//色子定财神，翻开就是财神
				ArrayList<String> huinameList = new ArrayList<>();
				huinameList = ClientMain.getCaiShen(ClientMain.getTileNameById2(huitile.getTilecodeid()));
				ArrayList<I单张牌数据> huilist = Ulti.getSamenameTiles(reportedTiles,huinameList);
				resultData.getHuiziTilelist().add(huitile);
				for(I单张牌数据 tile : huilist){
					if(!resultData.getHuiziTilelist().contains(tile)){
						resultData.getHuiziTilelist().add(tile);
					}
				}
				
			}
		}
				
		//把字面向上的缓存
		for(int i = 0; i < reportedTiles.get牌堆中牌的总行数();i++){
			for(int j = 0; j < reportedTiles.getItem(i).get行中总牌数();j++){
				//头尾财神翻开的牌，不计算出牌；色子财神翻开两边邻居不动也不算出牌。
				if(resultData.getHuiziTilelist().size() > 0 && resultData.getHuiziTilelist().get(0).equals(reportedTiles.getItem(i).getItem(j))){
					continue;
				}
				//新的字面向上的牌
				if(reportedTiles.getItem(i).getItem(j).getDirection() == ConstantValue.UPSIDE &&
						!chuTileList.contains(reportedTiles.getItem(i).getItem(j))){
					chuTileList.add(reportedTiles.getItem(i).getItem(j));
					newChuTileList.add(reportedTiles.getItem(i).getItem(j));
				}
				//缓存站立的牌，也就是牌墙上被抓走的牌
				if(reportedTiles.getItem(i).getItem(j).getDirection() == ConstantValue.STAND1 && 
						!cachestandData.contains(reportedTiles.getItem(i).getItem(j))){
					cachestandData.add(reportedTiles.getItem(i).getItem(j));
				}
			}
		}
		
		//新的取消字面向上的牌，不再向上且不和已出牌成邻居
		for(I单张牌数据 tmptile : chuTileList){
			if(tmptile == null || reportedTiles.getSingleItem(tmptile.getTilecodeid()) == null){
				continue;
			}
			I单张牌数据 newtile= reportedTiles.getSingleItem(tmptile.getTilecodeid());
			//如果邻居是向上的牌，不返回
			if(((reportedTiles.getSingleItem(newtile.getNeighbor1()) != null && reportedTiles.getSingleItem(newtile.getNeighbor1()).getDirection() == ConstantValue.UPSIDE))
			|| ((reportedTiles.getSingleItem(newtile.getNeighbor2()) != null && reportedTiles.getSingleItem(newtile.getNeighbor2()).getDirection() == ConstantValue.UPSIDE))){
				continue;
			}
			//返回了计数
			if(newtile.getDirection() != ConstantValue.UPSIDE){
				if(!newReturnTileMap.containsKey(tmptile.getTilecodeid())){
					newReturnTileMap.put(tmptile.getTilecodeid(), 1);
				}else{
					int times = newReturnTileMap.get(tmptile.getTilecodeid())+1;
					newReturnTileMap.put(tmptile.getTilecodeid(), times);
				}
			}else{//否则清空
				if(newReturnTileMap.containsKey(tmptile.getTilecodeid())){
					newReturnTileMap.remove(tmptile.getTilecodeid());
				}
			}
		}
		//连续5次站立才认为确实站立了
		for (Integer key : newReturnTileMap.keySet()) {
			if(newReturnTileMap.get(key) >= 5){
				newReturnTileList.add(reportedTiles.getSingleItem(key));
			}
		}
		for(I单张牌数据 tile : newReturnTileList){
			newReturnTileMap.remove(tile.getTilecodeid());
		}
		chuTileList.removeAll(newReturnTileList);
		
		
		
		
		//相同牌成邻居且字面向上,3张或4张
//		for(I单张牌数据 tmptile : chuTileList){
//			if(pengArrayList.contains(tmptile)){
//				continue;
//			}
//			I牌堆行数据 tileline = Ulti.getLineByTile(reportedTiles, tmptile);
//			if(tileline.get行中总牌数() < 3){
//				continue;
//			}
//			String strname = ClientMain.getTileNameById2(tmptile.getTilecodeid());
//			ArrayList<I单张牌数据> tmpArrayList = new ArrayList<>();
//			for(int i = 0; i < tileline.get行中总牌数(); i++){
//				String strname2 = ClientMain.getTileNameById2(tileline.getItem(i).getTilecodeid());
//				if(strname.equals(strname2)){
//					tmpArrayList.add(tileline.getItem(i));
//				}
//			}
//			if(tmpArrayList.size() >= 3 ){
//				pengArrayList.addAll(tmpArrayList);
//				newpengArrayList.addAll(tmpArrayList);
//
//			}	
//		}
		
		
	}

	/**
	 * 出牌开始：庄家的牌字面向上
	 */
	public boolean isRunning() {
		boolean running = false;
		for(I单张牌数据 tmptile : oldPlayerTileData.getCurrentTiles().get(0)){
			if(chuTileList.contains(tmptile)){
				//给庄家一个默认抓牌，便于控制结构
				currentplayerlist.get(0).setZhuaTile(tmptile);
				running = true;
				break;
			}
		}
		return running;
	}

	/**
	 * 数组个数大于130
	 */
	public boolean isCompleted() {
		if(resultData.getResultTilelist().size() == 0){
			return true;
		}
		if(reportedTiles.get牌堆中牌的总行数() > ClientMain.TotalTileNums-15 && Ulti.getLineCountOfSome(reportedTiles, 5) == 0 ){
			return true;
		}

		return false;
	}

	

	/**
	 * 分析牌
	 * 1.新增向上的牌： 以出牌为准，可能一家牌，可能多家，理论上一家
	 *   挨个处理牌，从玩家集合删除，更新出牌家到当前
	 *   碰，出了3张，2张一样，且与上一次出牌相同
	 *   杠，出了4张，3张一样，且与上一次出牌相同
	 *   如果不属于任何一家，则是牌墙，不处理
	 * 2.取消非向上的牌，在玩家搜索，返回去
	 * 3.判断牌墙牌是否被抓走，通过曾经站立判断，
	 *   成邻居判断谁家，没有邻居则入当前出牌家
	 *   
	 * 4.最后根据当前出牌索引更新待抓牌
	 */
	public void analyzeTiles() {
		//没牌了，返回
		if(resultData.getResultTilelist().size() == 0){
			return;
		}
		//是否是正常下家出牌
		boolean normalchuOrder = true;
		//判断每家有几个
		ArrayList<Integer> chuplayernums = getChulistNums();
		//正常出牌
		for(int i = 0; i < newChuTileList.size();i++){
			I单张牌数据 tmptile = newChuTileList.get(i);
			if(tmptile == null){
				continue;
			}
			int index = 0;
			for (index = 0; index < 4; index++) {
				if (resultData.getCurrentTiles().get(index).contains(tmptile)) {	
					//当前出牌玩家
					playerchuList.add(index);
					//从当前玩家删除
					resultData.getCurrentTiles().get(index).remove(tmptile);
					//花牌
					if(!ClientMain.huapaiArrayList.contains(ClientMain.getTileNameById2(tmptile.getTilecodeid()))){
						//加入控制结果
						currentplayerlist.get(index).addChuTiles(tmptile); 
					}

					//庄家第一次出牌
					//按顺序出牌 && 当前家只有一个出牌，判定正常出牌
					if( (playerchuList.size() == 1 && index == 0)
						||((getPrePlayerIndex()+1)%4 == getLastPlayerIndex() && chuplayernums.get(index) == 1)){
						normalchuOrder = true;
						
					}else{//不是下家，是吃碰杠或者误碰情况，在后面控制器处理
						normalchuOrder = false;
					}
					break;
				}
			}
			//不属于任何一家,属于牌墙，仅移动出牌家指针，不做其他处理
			if(index == 4){
				//看看当前牌属于牌头还是牌尾？牌头需要index+1，牌尾是杠牌不需要
				if(isTileHeadOrTail(tmptile)){//牌头
					//当前出牌玩家+1
					playerchuList.add((getLastPlayerIndex()+1)%4);
					//加入控制结果,花牌
					if(ClientMain.huapaiArrayList.contains(ClientMain.getTileNameById2(tmptile.getTilecodeid()))){
						LinkedList<I单张牌数据> tmplist = new LinkedList<>();
						tmplist.addAll(resultData.getCurrentTiles().get(getLastPlayerIndex()));
						if(!tmplist.contains(tmptile)){
							tmplist.add(tmptile);
						}
						currentplayerlist.get(getLastPlayerIndex()).addCachelist(tmplist);
						
					}else{
						currentplayerlist.get(getLastPlayerIndex()).setZhuaTile(tmptile); 
						currentplayerlist.get(getLastPlayerIndex()).addChuTiles(tmptile); 
					}
				}else{
					//加入控制结果,花牌
					if(ClientMain.huapaiArrayList.contains(ClientMain.getTileNameById2(tmptile.getTilecodeid()))){
						LinkedList<I单张牌数据> tmplist = new LinkedList<>();
						tmplist.addAll(resultData.getCurrentTiles().get(getLastPlayerIndex()));
						if(!tmplist.contains(tmptile)){
							tmplist.add(tmptile);
						}
						currentplayerlist.get(getLastPlayerIndex()).addCachelist(tmplist);
						
					}else{
						currentplayerlist.get(getLastPlayerIndex()).addChuTiles(tmptile); 
					}
				}
			}
		}
		
		// 处理误操作的牌
		for (I单张牌数据 tile : newReturnTileList) {
			for (OnePlayer one : currentplayerlist) {
				// 属于谁家？
				if (one.ifTileInCachelist(tile)) {
					// 加入当前玩家手里牌
					if(!resultData.getCurrentTiles().get(one.getIndex()).contains(tile)){
						resultData.getCurrentTiles().get(one.getIndex()).add(tile);
					}
					
					//玩家已出牌删除
					if(resultData.getCurrentPlayerTiles().get(one.getIndex()) != null && 
							resultData.getCurrentPlayerTiles().get(one.getIndex()).equals(tile)){
						resultData.getCurrentPlayerTiles().set(one.getIndex(),null);
					}
					//从控制结构删除已出牌
					if (one.getChuTileList().contains(tile)) {
						one.getChuTileList().remove(tile);
					}
					//如果没有出牌，那么抓的牌可能不对
					if(one.getChuTileList().size() == 0 && one.getZhuaTile() != null){
						one.setZhuaTile(null);
					}
					//当前出牌玩家索引删除
					deletePlayerIndex(one.getIndex());
					
					break;
				}
			}
		}
		newReturnTileList.clear();
				
		//看看抓牌是否到了临界点，是否需要调整最后两排的顺序
		if(!ifSwitchedTileline && resultData.getResultTilelist().size() > 0 && resultData.getResultTilelist().get(0) == null &&
				(Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums-1)+Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums)) == 4){
			for(int j = 1; j < resultData.getResultTilelist().size(); j++){
				//判断排行的第一张牌是否站立？
				//null开始，牌行长度差不多满行，下一张牌被抓走
				if(resultData.getResultTilelist().get(j) == null && 
//					Ulti.getLineByTile(reportedTiles, resultData.getResultTilelist().get(j+1)).get行中总牌数() >= ClientMain.LineTilesNums-1 && 
					(cachestandData.contains(resultData.getResultTilelist().get(j+1)) || /*neighborChanged(resultData.getResultTilelist().get(j+1)) ||*/
					reportedTiles.getSingleItem(resultData.getResultTilelist().get(j+1).getTilecodeid()).getDirection() == ConstantValue.UPSIDE)){
					//exchange
					ArrayList<I单张牌数据> tmpList = new ArrayList<>();
					int inull2 = 0;
					for(int m = j; m < resultData.getResultTilelist().size(); m++){
						if(m != j && resultData.getResultTilelist().get(m) == null){
							inull2 = m;
							break;
						}
						tmpList.add(resultData.getResultTilelist().get(m));
					}
					for(int m = 0; m < j; m++){
						tmpList.add(resultData.getResultTilelist().get(m));
					}
					if(inull2 != 0){
						for(int m = inull2; m < resultData.getResultTilelist().size(); m++){
							tmpList.add(resultData.getResultTilelist().get(m));
						}						
					}
					resultData.getResultTilelist().clear();
					resultData.getResultTilelist().addAll(tmpList);
					ifSwitchedTileline = true;
					break;
				}
			}
		}
		

		//如果本次没有出牌，根据邻居更新玩家手中的牌
		//处理牌墙，如果和玩家成邻居就更新牌墙和计算未来牌
		int standnum = 0, allstandnum = 0;
		if(newChuTileList.size() == 0){	
			//处理牌墙误抓的牌,没入手最后一个，跟牌墙成邻居了，返回牌墙
//			if(notsureTileList.size() > 0){
//				I单张牌数据 tmptile = notsureTileList.get(notsureTileList.size()-1);
//				I单张牌数据 curtmptile = reportedTiles.getSingleItem(tmptile.getTilecodeid());
//				//和没有站立的牌成邻居
//				if((curtmptile.getNeighbor1() != 0 && !cachestandData.contains(reportedTiles.getSingleItem(curtmptile.getNeighbor1())))
//					|| (curtmptile.getNeighbor2() != 0 && !cachestandData.contains(reportedTiles.getSingleItem(curtmptile.getNeighbor2())))){
//					//加到牌墙头
//					resultData.getResultTilelist().add(0, curtmptile);
//					//不确定牌删除
//					notsureTileList.remove(tmptile);
//					//玩家删除
//					for (int index = 0; index < 4; index++) {
//						if (resultData.getCurrentTiles().get(index).contains(tmptile)) {	
//							resultData.getCurrentTiles().get(index).remove(tmptile);
//							break;
//						}
//					}
//					
//				}
//			}
			
			//处理牌墙有成邻居的
			for(int j = 0; j < resultData.getResultTilelist().size(); j++){
				I单张牌数据 tmptile  = resultData.getResultTilelist().get(j);
				if(tmptile == null){
					continue;
				}
				boolean find = false;
				if(cachestandData.contains(tmptile) || /*neighborChanged(tmptile) ||*/
						reportedTiles.getSingleItem(tmptile.getTilecodeid()).getDirection() == ConstantValue.UPSIDE){//抓走了
					//看看和谁成邻居？
					for(int i = 0; i < 4; i++){
						LinkedList<I单张牌数据> tmplist = resultData.getCurrentTiles().get(i);
						if(Ulti.isTileNeighborsTilesets(reportedTiles, tmptile, tmplist)){
							if(!resultData.getCurrentTiles().get(i).contains(tmptile) ){
								//加入当前牌
								resultData.getCurrentTiles().get(i).add(tmptile);
							}
							//更新控制结果
							currentplayerlist.get(i).setZhuaTile(tmptile); //加入控制结果
							standnum++;
							find = true;
							break;
						}
					}
					//每找到邻居，要么出牌了，要么就没成邻居
					if(!find){
						break;
					}
				}else {//牌墙数据没动
					break;
				}
			}
			
			// 从牌墙删除抓走的牌
			for (int i = 0; i < standnum; i++) {
				if (resultData.getResultTilelist().get(i) == null) {
					i--;
				}
				resultData.getResultTilelist().remove(0);
			}
			
			//根据邻居更新
			updateCurPlayerTiles();
			
			//更新碰杠牌显示
			updatePengGangTiles();
	
			return;
		}
		newChuTileList.clear();
		
		//从排尾开始，找翻开的牌，也就是财神，可能需要调整牌行的顺序
		//后财神 或者 骰子确定
		if(!ifSwitchedTileline && (ClientMain.caishenLast == 1 || ClientMain.caishenShaizi == 1) &&
				Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums) == 4 &&
				resultData.getHuiziTilelist().size() > 0){
			int itilecount = 0;
			for(int k = resultData.getResultTilelist().size()-1; k >= 1; k=k-2){
				I单张牌数据 tmptile  = resultData.getResultTilelist().get(k);
				I单张牌数据 tmptile2  = resultData.getResultTilelist().get(k-1);
				if(tmptile == null || tmptile2 == null){
					k=k+1;//跳过null
					continue;
				}
				//计数经过了多少牌
				itilecount += 2;
				//遇到翻开的
				if(tmptile.getBadType() == 6 || tmptile2.getBadType() == 6){
					if(itilecount > 2*ClientMain.LineTilesNums+1 && itilecount < 4*ClientMain.LineTilesNums){//超过一个牌行，交换
						int start=-1,mid = -1,end=-1;
						for(int j = 0; j < resultData.getResultTilelist().size(); j++){
							//null开始，牌行长度差不多满行，下一张牌被抓走
							if(resultData.getResultTilelist().get(j) == null){
								if(start == -1){
									start = j;
								}else{
									if(mid == -1){
										mid = j;
									}else{
										end = j;
									}
								}
							}
						}
						if(start != -1 && mid != -1){
							ArrayList<I单张牌数据> tmpList = new ArrayList<>();
							for(int m = 0; m < start; m++){
								tmpList.add(resultData.getResultTilelist().get(m));
							}
							if(end != -1){
								for(int m = mid; m < end; m++){
									tmpList.add(resultData.getResultTilelist().get(m));
								}
							}else{
								for(int m = mid; m < resultData.getResultTilelist().size(); m++){
									tmpList.add(resultData.getResultTilelist().get(m));
								}
							}
							for(int m = start; m < mid; m++){
								tmpList.add(resultData.getResultTilelist().get(m));
							}
							if(end != -1){
								for(int m = end; m < resultData.getResultTilelist().size(); m++){
									tmpList.add(resultData.getResultTilelist().get(m));
								}
							}
							resultData.getResultTilelist().clear();
							resultData.getResultTilelist().addAll(tmpList);
						}
						ifSwitchedTileline = true;
						break;
					}
					
				}else{
					break;
				}
			}
		}
		
		//看看排尾是否被杠走，可能需要调整两个完整牌行的先后顺序
		//从排尾开始找，找到大于一个牌行，然后继续有badtype==5的，就需要调整到最后
		if(!ifSwitchedTileline && (Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums-1)+Ulti.getLineCountOfSome(reportedTiles, ClientMain.LineTilesNums)) == 4 &&
				(resultData.getCurrentCpgTiles().get(0).size() > 0 ||
					resultData.getCurrentCpgTiles().get(1).size() > 0 ||
					resultData.getCurrentCpgTiles().get(2).size() > 0 ||
					resultData.getCurrentCpgTiles().get(3).size() > 0)){//还有4个完整的牌行
			int itilecount = 0;
			for(int k = resultData.getResultTilelist().size()-1; k >= 1; k=k-2){
				I单张牌数据 tmptile  = resultData.getResultTilelist().get(k);
				I单张牌数据 tmptile2  = resultData.getResultTilelist().get(k-1);
				if(tmptile == null || tmptile2 == null){
					k=k+1;//跳过null
					continue;
				}
				//计数经过了多少牌
				itilecount += 2;
//				if(itilecount > 2*ClientMain.LineTilesNums){
//					break;
//				}
				//遇到被杠的
//				if(tmptile.getBadType() == 5 || tmptile2.getBadType() == 5){
				if((cachestandData.contains(tmptile) || reportedTiles.getSingleItem(tmptile.getTilecodeid()).getDirection() == ConstantValue.UPSIDE)
						||(cachestandData.contains(tmptile2) || reportedTiles.getSingleItem(tmptile2.getTilecodeid()).getDirection() == ConstantValue.UPSIDE)){
					if(itilecount > 2*ClientMain.LineTilesNums+1 && itilecount < 4*ClientMain.LineTilesNums){//超过一个牌行，交换
						int start=-1,mid = -1,end=-1;
						for(int j = 0; j < resultData.getResultTilelist().size(); j++){
							//null开始，牌行长度差不多满行，下一张牌被抓走
							if(resultData.getResultTilelist().get(j) == null){
								if(start == -1){
									start = j;
								}else{
									if(mid == -1){
										mid = j;
									}else{
										end = j;
									}
								}
							}
						}
						if(start != -1 && mid != -1){
							ArrayList<I单张牌数据> tmpList = new ArrayList<>();
							for(int m = 0; m < start; m++){
								tmpList.add(resultData.getResultTilelist().get(m));
							}
							if(end != -1){
								for(int m = mid; m < end; m++){
									tmpList.add(resultData.getResultTilelist().get(m));
								}
							}else{
								for(int m = mid; m < resultData.getResultTilelist().size(); m++){
									tmpList.add(resultData.getResultTilelist().get(m));
								}
							}
							for(int m = start; m < mid; m++){
								tmpList.add(resultData.getResultTilelist().get(m));
							}
							if(end != -1){
								for(int m = end; m < resultData.getResultTilelist().size(); m++){
									tmpList.add(resultData.getResultTilelist().get(m));
								}
							}
							resultData.getResultTilelist().clear();
							resultData.getResultTilelist().addAll(tmpList);
						}
						ifSwitchedTileline = true;
						break;
					}
					
				}
			}
			
		}
		
		//处理牌尾的杠牌
		ArrayList<I单张牌数据> gangtiles = new ArrayList<>();
		for(int j = resultData.getResultTilelist().size()-1; j >= 1; j=j-2){
			I单张牌数据 tmptile  = resultData.getResultTilelist().get(j);
			I单张牌数据 tmptile2  = resultData.getResultTilelist().get(j-1);
			if(tmptile == null || tmptile2 == null){
				j=j+1;//跳过null
				continue;
			}
			//如果两个都没被抓走，break
			boolean find = false;
			if(cachestandData.contains(tmptile2) || (reportedTiles.getSingleItem(tmptile2.getTilecodeid()).getDirection() == ConstantValue.UPSIDE && tmptile2.getBadType() != 6)){//抓走了
				if(tmptile2.getBadType() == 5){
					find = true;
				}else{
					gangtiles.add(tmptile2);
					//看看和谁成邻居？
					for(int i = 0; i < 4; i++){
						LinkedList<I单张牌数据> tmplist = resultData.getCurrentTiles().get(i);
						if(Ulti.isTileNeighborsTilesets(reportedTiles, tmptile2, tmplist)){
							//更新控制结果
							currentplayerlist.get(i).setGangTile(tmptile2); //加入控制结果
							find = true;
							break;
						}
					}
					//每找到邻居，要么出牌了，要么就没成邻居
					if(!find){
						//出牌，暂时不用处理
						if(reportedTiles.getSingleItem(tmptile2.getTilecodeid()).getDirection() == ConstantValue.UPSIDE){
							//花牌
							if(ClientMain.huapaiArrayList.contains(ClientMain.getTileNameById2(tmptile2.getTilecodeid()))){
								LinkedList<I单张牌数据> tmplist = new LinkedList<>();
								tmplist.addAll(resultData.getCurrentTiles().get(getLastPlayerIndex()));
								if(!tmplist.contains(tmptile2)){
									tmplist.add(tmptile2);
								}
								currentplayerlist.get(getLastPlayerIndex()).addCachelist(tmplist);
								
							}else{
								currentplayerlist.get(getLastPlayerIndex()).addChuTiles(tmptile2); 
								currentplayerlist.get(getLastPlayerIndex()).setGangTile(tmptile2);
							}
							
						}else{//没出牌，放入当前家集合
							//可疑牌加入
							notsureTileList.add(tmptile2);
							
							if(!resultData.getCurrentTiles().get(getLastPlayerIndex()).contains(tmptile2)/* &&
									currentplayerlist.get(getLastPlayerIndex()).getZhuaTile() == null &&
									currentplayerlist.get(getLastPlayerIndex()).getChuTileList().size() > 1*/){
								//加入当前牌
								resultData.getCurrentTiles().get(getLastPlayerIndex()).add(tmptile2);
								//更新控制结果
								currentplayerlist.get(getLastPlayerIndex()).setGangTile(tmptile2); //加入控制结果
							}
						}
					}
				}
				
				
				//继续看看下张牌
				find = false;
				if(cachestandData.contains(tmptile) || (reportedTiles.getSingleItem(tmptile.getTilecodeid()).getDirection() == ConstantValue.UPSIDE && tmptile.getBadType() != 6)){//抓走了
					if(tmptile.getBadType() == 5){
						find = true;
					}else{
						gangtiles.add(tmptile);
						//看看和谁成邻居？
						for(int i = 0; i < 4; i++){
							LinkedList<I单张牌数据> tmplist = resultData.getCurrentTiles().get(i);
							if(Ulti.isTileNeighborsTilesets(reportedTiles, tmptile, tmplist)){
								//更新控制结果
								currentplayerlist.get(i).setGangTile(tmptile); //加入控制结果
								find = true;
								break;
							}
						}
						//每找到邻居，要么出牌了，要么就没成邻居
						if(!find){
							//出牌，暂时不用处理
							if(reportedTiles.getSingleItem(tmptile.getTilecodeid()).getDirection() == ConstantValue.UPSIDE){
								//花牌
								if(ClientMain.huapaiArrayList.contains(ClientMain.getTileNameById2(tmptile.getTilecodeid()))){
									LinkedList<I单张牌数据> tmplist = new LinkedList<>();
									tmplist.addAll(resultData.getCurrentTiles().get(getLastPlayerIndex()));
									if(!tmplist.contains(tmptile)){
										tmplist.add(tmptile);
									}
									currentplayerlist.get(getLastPlayerIndex()).addCachelist(tmplist);
									
								}else{
									currentplayerlist.get(getLastPlayerIndex()).addChuTiles(tmptile); 
									currentplayerlist.get(getLastPlayerIndex()).setGangTile(tmptile);
								}
							}else{//没出牌，放入当前家集合
								//可疑牌加入
								notsureTileList.add(tmptile);
								
								if(!resultData.getCurrentTiles().get(getLastPlayerIndex()).contains(tmptile)/* &&
										currentplayerlist.get(getLastPlayerIndex()).getZhuaTile() == null &&
										currentplayerlist.get(getLastPlayerIndex()).getChuTileList().size() > 1*/){
									//加入当前牌
									resultData.getCurrentTiles().get(getLastPlayerIndex()).add(tmptile);
									//更新控制结果
									currentplayerlist.get(getLastPlayerIndex()).setGangTile(tmptile); //加入控制结果
								}
							}
						}
					}
				}else{
					break;
				}	
			}else{//上面的没被抓走，后面肯定也没抓
				break;
			}
		}
		//置位牌尾的数据，为了显示判断用
		for (I单张牌数据 tmptile : gangtiles) {
			int i = resultData.getResultTilelist().indexOf(tmptile);
			resultData.getResultTilelist().remove(tmptile);
			tmptile.setBadType(5);
			resultData.getResultTilelist().add(i, tmptile);
		}
		
		
		//看牌墙上的是否被抓走，首先计算下有多少被抓走了
		//如果不是顺序出牌，比如杠碰情况，不处理牌墙，防止误操作
		standnum = 0;allstandnum = 0;
		for(int j = 0; j < resultData.getResultTilelist().size(); j++){
			I单张牌数据 tmptile  = resultData.getResultTilelist().get(j);
			if(tmptile == null){
				continue;
			}
			if(cachestandData.contains(tmptile) || neighborChanged(tmptile) ||
					reportedTiles.getSingleItem(tmptile.getTilecodeid()).getDirection() == ConstantValue.UPSIDE){//抓走了
				allstandnum++;
			}else{
				break;
			}
		}
		//看牌墙上的是否被抓走，站立或者失去邻居， 与某家形成邻居
		for(int j = 0; j < resultData.getResultTilelist().size(); j++){
			I单张牌数据 tmptile  = resultData.getResultTilelist().get(j);
			if(tmptile == null){
				continue;
			}
			boolean find = false;
			//到杠走的牌了，应该结束了
			if(tmptile.getBadType() == 5){
				break;
			}
			if(normalchuOrder && currentplayerlist.get(getLastPlayerIndex()).getZhuaTile() == null &&
				(cachestandData.contains(tmptile) || neighborChanged(tmptile) ||
				reportedTiles.getSingleItem(tmptile.getTilecodeid()).getDirection() == ConstantValue.UPSIDE)){//抓走了
				//站立++
				standnum++;
				//看看和谁成邻居？
				for(int i = 0; i < 4; i++){
					LinkedList<I单张牌数据> tmplist = resultData.getCurrentTiles().get(i);
					if(Ulti.isTileNeighborsTilesets(reportedTiles, tmptile, tmplist)){
						if(!resultData.getCurrentTiles().get(i).contains(tmptile) ){
							//加入当前牌
							resultData.getCurrentTiles().get(i).add(tmptile);
						}
						//更新控制结果
						currentplayerlist.get(i).setZhuaTile(tmptile); //加入控制结果

						find = true;
						break;
					}
				}
				//每找到邻居，要么出牌了，要么就没成邻居
				if(!find){
					//出牌，暂时不用处理
					if(reportedTiles.getSingleItem(tmptile.getTilecodeid()).getDirection() == ConstantValue.UPSIDE){
						//更新控制结果
						currentplayerlist.get(getLastPlayerIndex()).setZhuaTile(tmptile); //加入控制结果
					}else{//没出牌，放入当前家集合???
						//可疑牌加入
						notsureTileList.add(tmptile);
						//看看前一家是否缺牌？牌墙必须大于1张站立才行
						if(getPrePlayerIndex() != -1 &&
							!resultData.getCurrentTiles().get(getPrePlayerIndex()).contains(tmptile) &&
							resultData.getCurrentTiles().get(getPrePlayerIndex()).size() % 3 == 0 &&
							allstandnum > 1 && standnum == 1 && normalchuOrder){
							//加入当前牌
							resultData.getCurrentTiles().get(getPrePlayerIndex()).add(tmptile);
							//更新控制结果
							currentplayerlist.get(getPrePlayerIndex()).setZhuaTile(tmptile); //加入控制结果
						}else if(getLastPlayerIndex() != -1 &&
								!resultData.getCurrentTiles().get(getLastPlayerIndex()).contains(tmptile) &&
								resultData.getCurrentTiles().get(getLastPlayerIndex()).size() % 3 == 0 &&
								normalchuOrder){
							//加入当前牌
							resultData.getCurrentTiles().get(getLastPlayerIndex()).add(tmptile);
							//更新控制结果
							currentplayerlist.get(getLastPlayerIndex()).setZhuaTile(tmptile); //加入控制结果
						}else{
							for(int k = 0; k < 4; k++){
								if(resultData.getCurrentTiles().get(k).size() % 3 == 0){
									//加入当前牌
									resultData.getCurrentTiles().get(k).add(tmptile);
									//更新控制结果
									currentplayerlist.get(k).setZhuaTile(tmptile); //加入控制结果
								}
							}
						}						
					}
				}else{
					
				}
			}else {//牌墙数据没动
				break;
			}
		}
		
		//抓完牌根据邻居更新
		updateCurPlayerTiles();
		
		//更新碰杠牌显示
		updatePengGangTiles();
				
		// 从牌墙删除抓走的牌
		for (int i = 0; i < standnum; i++) {
			if (resultData.getResultTilelist().get(i) == null) {
				i--;
			}
			resultData.getResultTilelist().remove(0);
		}

		// 计算待抓牌
		for (int i = 0; i < resultData.getNextTiles().size(); i++) {
			resultData.getNextTiles().get(i).clear();
		}

		ArrayList<I单张牌数据> tmpresult = new ArrayList<>();
		for (int i = 0; i < resultData.getResultTilelist().size(); i++) {
			I单张牌数据 tile = resultData.getResultTilelist().get(i);
			if (tile != null) {
				tmpresult.add(tile);
			}
		}
		for (int i = 0; i < tmpresult.size(); i++) {
			I单张牌数据 tile = tmpresult.get(i);
			if(tile.getBadType() == 6){//跳过翻开的财神
				continue;
			}
			if(tile.getBadType() == 5){//到杠牌了，停止
				break;
			}
			if (i % 4 == 0) {// 当前出牌索引的下一家开始
				resultData.getNextTiles().get((getLastPlayerIndex() + 1) % 4)
						.add(tile);
			} else if (i % 4 == 1) {
				resultData.getNextTiles().get((getLastPlayerIndex() + 2) % 4)
						.add(tile);
			} else if (i % 4 == 2) {
				resultData.getNextTiles().get((getLastPlayerIndex() + 3) % 4)
						.add(tile);
			} else if (i % 4 == 3) { //
				resultData.getNextTiles().get((getLastPlayerIndex() + 4) % 4)
						.add(tile);
			}
		}

		// 保存各家出牌记录
		for (int i = 0; i < 4; i++) {
			//如果有直接抓打的情况需要保存
			if(currentplayerlist.get(i).getCurState() == 1 && currentplayerlist.get(i).getChuTileList().get(0).equals(currentplayerlist.get(i).getZhuaTile())){
				LinkedList<I单张牌数据> tmplist = new LinkedList<>();
				tmplist.addAll(resultData.getCurrentTiles().get(i));
				if(!tmplist.contains(currentplayerlist.get(i).getChuTileList().get(0))){
					tmplist.addAll(currentplayerlist.get(i).getChuTileList());
				}
				currentplayerlist.get(i).addCachelist(tmplist);
			}else{
				currentplayerlist.get(i).addCachelist(resultData.getCurrentTiles().get(i));
			}
		}

		// 判断一下控制结果，处理吃碰杠的情况，对于出完牌的reset
		for (int i = 0; i < 4; i++) {
			//按照最后出牌的在最后的顺序处理
			int index = (getLastPlayerIndex()+1+i)%4;
			int state = currentplayerlist.get(index).getCurState();
			System.out.println(index + ":" + state);
			if (state == 1) {// 正常抓打
				// 出牌显示
				for (int j = 0; j < 4; j++) {
					resultData.getCurrentPlayerTiles().set(j, null);
				}
				//如果是花牌，不显示
				//如果是花牌，不判断出牌
				if(!ClientMain.huapaiArrayList.contains(ClientMain.getTileNameById2(currentplayerlist.get(index).getChuTileList().get(0).getTilecodeid()))){
					resultData.getCurrentPlayerTiles().set(
							index,currentplayerlist.get(index).getChuTileList().get(0));

				}
				
				currentplayerlist.get(index).reset();

			} else if (state == 2) {// 碰牌
				ArrayList<I单张牌数据> penglist = currentplayerlist.get(index)
						.getPengpaiList();
				if (penglist == null || penglist.size() != 2) {
					continue;
				}
				// 在出牌cache中找另一张相同名字的牌
				I单张牌数据 another = getAnotherPengTile(penglist);
				if (another == null) {
					continue;
				}
				penglist.add(another);
				// 加入碰牌显示
				resultData.getCurrentCpgTiles().get(index).addAll(penglist);
				// 显示出牌
				for (int j = 0; j < 4; j++) {
					resultData.getCurrentPlayerTiles().set(j, null);
				}
				resultData.getCurrentPlayerTiles().set(index,
						currentplayerlist.get(index).getPengpaiChuPai());
				// 复位
				currentplayerlist.get(index).reset();

			} else if (state == 3) {// 吃牌
				// 显示出牌
				for (int j = 0; j < 4; j++) {
					resultData.getCurrentPlayerTiles().set(j, null);
				}
				resultData.getCurrentPlayerTiles().set(index,
						currentplayerlist.get(index).getChipaiChuPai());
				//
				currentplayerlist.get(index).reset();
			} else if (state == 4) {// 杠牌
				//获取出的牌
				ArrayList<I单张牌数据> penglist = currentplayerlist.get(index).getGangpaiList();
				if (penglist.size() == 0) {
					continue;
				}
				//根据出的牌找可能的杠牌
				int ifind = 0;
				ArrayList<I单张牌数据> penglistnew = new ArrayList<>();
				if(penglist.size() > 0){
					//碰自家的3张牌，找自家的3张牌
					for(I单张牌数据 tile : penglist){
						for(I单张牌数据 tmptile : resultData.getCurrentCpgTiles().get(index)){
							if(ClientMain.getTileNameById2(tmptile.getTilecodeid()).equals(ClientMain.getTileNameById2(tile.getTilecodeid()))){
								int m = resultData.getCurrentCpgTiles().get(index).indexOf(tmptile);
								if(!resultData.getCurrentCpgTiles().get(index).contains(tmptile)){
									resultData.getCurrentCpgTiles().get(index).add(m, tmptile);
									ifind++;
									break;
								}
							}
						}
					}
					//花牌
					for(I单张牌数据 tile : penglist){
						if(ClientMain.huapaiArrayList.contains(ClientMain.getTileNameById2(tile.getTilecodeid()))){
							if(!resultData.getCurrentCpgTiles().get(index).contains(tile)){
								resultData.getCurrentCpgTiles().get(index).add(tile);
								ifind++;
							}
							
						}
					}
					//碰别人家的牌，碰牌了，找出牌cache中一张牌 或者 自家4张出牌向上
					penglistnew = get3PengTiles(penglist);
					// 在出牌cache中找另一张相同名字的牌
					if(penglistnew.size() == 3){
						I单张牌数据 another = getAnotherPengTile(penglistnew);
						if (another != null) {
							penglistnew.add(another);
							// 加入						
							for(I单张牌数据 tile : penglistnew){
								resultData.getCurrentCpgTiles().get(index).remove(tile);
								resultData.getCurrentCpgTiles().get(index).add(tile);
							}
							ifind++;
						}
					}else if(penglistnew.size() == 4){//自家4张牌向上出
						for(I单张牌数据 tile : penglistnew){
							resultData.getCurrentCpgTiles().get(index).remove(tile);
							resultData.getCurrentCpgTiles().get(index).add(tile);
						}
						
						ifind++;
					}
				}
				

				
				// 显示出牌
				for (int j = 0; j < 4; j++) {
					resultData.getCurrentPlayerTiles().set(j, null);
				}
				//设置最后一个出牌为当前出牌
				if(currentplayerlist.get(index).getChuTileList().size() > 0){
					resultData.getCurrentPlayerTiles().set(index,
							penglist.get(penglist.size()-1));
				}
				
				if(ifind > 0){
					currentplayerlist.get(index).reset();
				}				
				//复位
				// 超时，reset
				if (currentplayerlist.get(index).ifResetPlayer()) {
					currentplayerlist.get(index).reset();
				}

			} else if (state == -1) {// 动作没完成
				// 超时，reset
				if (currentplayerlist.get(index).ifResetPlayer()) {
					currentplayerlist.get(index).reset();
				}
				//先打了一张牌 && 顺序出牌，这时可以直接显示出牌
				if(currentplayerlist.get(index).getChuTileList().size() == 1 && normalchuOrder){
					for (int j = 0; j < 4; j++) {
						resultData.getCurrentPlayerTiles().set(j, null);
					}
					resultData.getCurrentPlayerTiles().set(index,currentplayerlist.get(index).getChuTileList().get(0));
				}
			} else {// == 0 没动作等待中

			}

		}
		
		//根据当前成邻居的数组，看看是否存在错误的牌
		for(int i = 0; i < reportedTiles.get牌堆中牌的总行数(); i++){
			I牌堆行数据 tmpline = reportedTiles.getItem(i);
			if(tmpline.getDirection() == ConstantValue.STAND1){
				int iowner = isLineBelongOneplayer(tmpline);
				if(iowner == -1){
					continue;
				}
				ArrayList<I单张牌数据> tilelist = getNotownerTiles(iowner,tmpline);
				if(tilelist.size() == 0){
					continue;
				}
				resultData.getCurrentTiles().get(iowner).addAll(tilelist);
				for(I单张牌数据 tile : tilelist){
					for(int j = 0; j < 4; j++){
						if(resultData.getCurrentTiles().get(j).contains(tile) && j != iowner){
							resultData.getCurrentTiles().get(j).remove(tile);
							break;
						}
					}
				}
			}
		}
		
		
		
	}
	
	/**
	 * 当前行是否属于某家，大多数属于某家
	 * @param line
	 * @return
	 */
	public int isLineBelongOneplayer(I牌堆行数据 line){
		int[] owners = new int[4];
		for(int i = 0; i < line.get行中总牌数(); i++){
			for(int j = 0; j < 4; j++){
				if(resultData.getCurrentTiles().get(j).contains(line.getItem(i))){
					owners[j]++;
				}
			}
		}

		for(int i = 0; i < 4; i++){
			if(owners[i] > line.get行中总牌数()/2){
				return i;
			}
		}
		
		return -1;
	}
	
	
	public ArrayList<I单张牌数据> getNotownerTiles(int index, I牌堆行数据 line){
		ArrayList<I单张牌数据> retList = new ArrayList<>();
		for(int i = 0; i < line.get行中总牌数(); i++){
			for(int j = 0; j < 4; j++){
				if(resultData.getCurrentTiles().get(j).contains(line.getItem(i)) && j != index){
					retList.add(line.getItem(i));
				}
			}
		}
		return retList;
	}

	/**
	 * 根据出牌获取3张碰牌
	 * @param penglist
	 * @return
	 */
	public ArrayList<I单张牌数据> get3PengTiles(ArrayList<I单张牌数据> penglist){
		ArrayList<I单张牌数据> retList = new ArrayList<>();
		for (int i = 0; i < penglist.size() - 1; i++) {
			I单张牌数据 atile = penglist.get(i);
			String anameString = ClientMain.getTileNameById2(atile
					.getTilecodeid());
			for (int j = i + 1; j < penglist.size(); j++) {
				I单张牌数据 btile = penglist.get(j);
				String bnameString = ClientMain.getTileNameById2(btile
						.getTilecodeid());
				if (anameString.equals(bnameString) && !retList.contains(atile)) {
					retList.add(atile);
				}
				if (anameString.equals(bnameString) && !retList.contains(btile)) {
					retList.add(btile);
				}
			}
		}
		return retList;
	}


	/**
	 *  一张坏牌的情况出牌
	 */
	@Override
	public void analyzeTilesOne() {
		//正常出牌,如果遇到坏牌，在出牌list中不存在的
		boolean chuOrder = true;
		//正常出牌
		for(int i = 0; i < newChuTileList.size();i++){
			I单张牌数据 tmptile = newChuTileList.get(i);
			int index = 0;
			for (index = 0; index < 4; index++) {
				if (resultData.getCurrentTiles().get(index).contains(tmptile)) {			
					//当前出牌玩家
					playerchuList.add(index);
					//从当前玩家删除
					resultData.getCurrentTiles().get(index).remove(tmptile);
					//加入控制结果
					currentplayerlist.get(index).addChuTiles(tmptile); 
					//出牌显示
					for(int j = 0; j < 4; j++){
						resultData.getCurrentPlayerTiles().set(j,null);
					}
					//下家出牌 && 当前家还没有出牌，正常出牌
					if((playerchuList.size() == 1 || (getPrePlayerIndex()+1)%4 == getLastPlayerIndex()) &&
							currentplayerlist.get(index).getChuTileList().size() == 1){
						chuOrder = true;
						resultData.getCurrentPlayerTiles().set(index,tmptile);							
					}else{//不是下家，是吃碰杠或者误碰情况，在后面控制器处理
						chuOrder = false;
					}
					
					break;
				}
			}
			//不属于任何一家,属于牌墙，仅移动出牌家指针，不做其他处理
			if(index == 4){
				//当前出牌玩家+1
				playerchuList.add((getLastPlayerIndex()+1)%4);
				//出牌
				for(int j = 0; j < 4; j++){
					resultData.getCurrentPlayerTiles().set(j,null);
				}
				resultData.getCurrentPlayerTiles().set(getLastPlayerIndex(),tmptile);
			}
		}
		
		// 处理误操作的牌
		for (I单张牌数据 tile : newReturnTileList) {
			for (OnePlayer one : currentplayerlist) {
				// 属于谁家？
				if (one.ifTileInCachelist(tile)) {
					// 加入当前牌
					if(!resultData.getCurrentTiles().get(one.getIndex()).contains(tile)){
						resultData.getCurrentTiles().get(one.getIndex()).add(tile);
					}
					
					//已出牌删除
					if(resultData.getCurrentPlayerTiles().get(one.getIndex()).equals(tile)){
						resultData.getCurrentPlayerTiles().set(one.getIndex(),null);
					}
					//从控制结构删除已出牌
					if (one.getChuTileList().contains(tile)) {
						one.getChuTileList().remove(tile);
					}
					//如果没有出牌，那么抓的牌可能不对
					if(one.getChuTileList().size() == 0 && one.getZhuaTile() != null){
//						for(int i = 0; i < 4; i++){
//							if(currentplayerlist.get(i).getChuTileList().size() == 1){
//								one.setZhuaTile(one.getZhuaTile());
//							}
//						}
						one.setZhuaTile(null);
					}
					break;
				}
			}
		}
		newReturnTileList.clear();
				
		//看看是否到了临界点，是否需要调整最后两排的顺序
		if(resultData.getResultTilelist().get(0) == null){
			for(int j = 1; j < resultData.getResultTilelist().size(); j++){
				if((resultData.getResultTilelist().get(j) == null && 
				   reportedTiles.getSingleItem(resultData.getResultTilelist().get(j+1).getTilecodeid()) != null && 
					(cachestandData.contains(resultData.getResultTilelist().get(j+1)) || 
					reportedTiles.getSingleItem(resultData.getResultTilelist().get(j+1).getTilecodeid()).getDirection() == ConstantValue.UPSIDE))
					|| (reportedTiles.getSingleItem(resultData.getResultTilelist().get(j+1).getTilecodeid()) == null &&
						(cachestandData.contains(resultData.getResultTilelist().get(j+2)) || 
						reportedTiles.getSingleItem(resultData.getResultTilelist().get(j+2).getTilecodeid()).getDirection() == ConstantValue.UPSIDE))){
					//exchange
					ArrayList<I单张牌数据> tmpList = new ArrayList<>();
					int inull2 = 0;
					for(int m = j; m < resultData.getResultTilelist().size(); m++){
						if(m != j && resultData.getResultTilelist().get(m) == null){
							inull2 = m;
							break;
						}
						tmpList.add(resultData.getResultTilelist().get(m));
					}
					for(int m = 0; m < j; m++){
						tmpList.add(resultData.getResultTilelist().get(m));
					}
					for(int m = inull2; m < resultData.getResultTilelist().size(); m++){
						tmpList.add(resultData.getResultTilelist().get(m));
					}
					resultData.getResultTilelist().clear();
					resultData.getResultTilelist().addAll(tmpList);
					break;
				}
				//如果下一行的第一个是坏牌，需要判断第二个
				if(reportedTiles.getSingleItem(resultData.getResultTilelist().get(j+1).getTilecodeid()) == null &&
						(cachestandData.contains(resultData.getResultTilelist().get(j+2)) || 
						reportedTiles.getSingleItem(resultData.getResultTilelist().get(j+2).getTilecodeid()).getDirection() == ConstantValue.UPSIDE)){
					
				}
			}
		}
		
		//如果本次没有出牌，不处理牌墙，但是根据邻居更新玩家手中的牌
		if(newChuTileList.size() == 0){
			//根据邻居更新
			updateCurPlayerTiles();

			return;
		}
		newChuTileList.clear();
		
		//看牌尾是否被抓走，杠牌
		for(int j = resultData.getResultTilelist().size()-1; j >= 0; j--){
			I单张牌数据 tmptile  = resultData.getResultTilelist().get(j);
			if(tmptile == null){
				continue;
			}
			boolean find = false;
			if(cachestandData.contains(tmptile) || reportedTiles.getSingleItem(tmptile.getTilecodeid()).getDirection() == ConstantValue.UPSIDE){//抓走了
				//看看和谁成邻居？
				for(int i = 0; i < 4; i++){
					LinkedList<I单张牌数据> tmplist = resultData.getCurrentTiles().get(i);
					if(Ulti.isTileNeighborsTilesets(reportedTiles, tmptile, tmplist)){
						//更新控制结果
						currentplayerlist.get(i).setGangTile(tmptile); //加入控制结果
						find = true;
						break;
					}
				}
				//每找到邻居，要么出牌了，要么就没成邻居
				if(!find){
					//出牌，暂时不用处理
					if(reportedTiles.getSingleItem(tmptile.getTilecodeid()).getDirection() == ConstantValue.UPSIDE){
						
					}else{//没出牌，放入当前家集合
						if(!resultData.getCurrentTiles().get(getLastPlayerIndex()).contains(tmptile)/* &&
								currentplayerlist.get(getLastPlayerIndex()).getZhuaTile() == null &&
								currentplayerlist.get(getLastPlayerIndex()).getChuTileList().size() > 1*/){
							//加入当前牌
							resultData.getCurrentTiles().get(getLastPlayerIndex()).add(tmptile);
							//更新控制结果
							currentplayerlist.get(getLastPlayerIndex()).setGangTile(tmptile); //加入控制结果
						}
					}
				}
			}else {//牌墙数据没动
				break;
			}
		}
		

		//看牌墙上的是否被抓走，站立或者失去邻居， 与某家形成邻居
		int standnum = 0, allstandnum = 0;
		for(int j = 0; j < resultData.getResultTilelist().size(); j++){
			I单张牌数据 tmptile  = resultData.getResultTilelist().get(j);
			if(tmptile == null){
				continue;
			}
			if(cachestandData.contains(tmptile) || reportedTiles.getSingleItem(tmptile.getTilecodeid()).getDirection() == ConstantValue.UPSIDE){//抓走了
				allstandnum++;
			}
		}
		
		for(int j = 0; j < resultData.getResultTilelist().size(); j++){
			I单张牌数据 tmptile  = resultData.getResultTilelist().get(j);
			if(tmptile == null){
				continue;
			}
			boolean find = false;
			if(cachestandData.contains(tmptile) || reportedTiles.getSingleItem(tmptile.getTilecodeid()).getDirection() == ConstantValue.UPSIDE){//抓走了
				//站立++
				standnum++;
				//看看和谁成邻居？
				for(int i = 0; i < 4; i++){
					LinkedList<I单张牌数据> tmplist = resultData.getCurrentTiles().get(i);
					if(Ulti.isTileNeighborsTilesets(reportedTiles, tmptile, tmplist)){
						if(!resultData.getCurrentTiles().get(i).contains(tmptile) ){
							//加入当前牌
							resultData.getCurrentTiles().get(i).add(tmptile);
							//更新控制结果
							currentplayerlist.get(i).setZhuaTile(tmptile); //加入控制结果
							
						}
						find = true;
						break;
					}
				}
				//每找到邻居，要么出牌了，要么就没成邻居
				if(!find){
					//出牌，暂时不用处理
					if(reportedTiles.getSingleItem(tmptile.getTilecodeid()).getDirection() == ConstantValue.UPSIDE){
						
					}else{//没出牌，放入当前家集合???
						//看看前一家是否缺牌？牌墙必须大于1张站立才行
						if(!resultData.getCurrentTiles().get(getPrePlayerIndex()).contains(tmptile) &&
							resultData.getCurrentTiles().get(getPrePlayerIndex()).size() % 3 == 0 &&
							allstandnum > 1 && standnum == 1 && chuOrder){
							//加入当前牌
							resultData.getCurrentTiles().get(getPrePlayerIndex()).add(tmptile);
							//更新控制结果
							currentplayerlist.get(getPrePlayerIndex()).setZhuaTile(tmptile); //加入控制结果
						}else if(!resultData.getCurrentTiles().get(getLastPlayerIndex()).contains(tmptile) &&
								resultData.getCurrentTiles().get(getLastPlayerIndex()).size() % 3 == 0 &&
								chuOrder){
							//加入当前牌
							resultData.getCurrentTiles().get(getLastPlayerIndex()).add(tmptile);
							//更新控制结果
							currentplayerlist.get(getLastPlayerIndex()).setZhuaTile(tmptile); //加入控制结果
						}else{
							for(int k = 0; k < 4; k++){
								if(resultData.getCurrentTiles().get(k).size() % 3 == 0){
									//加入当前牌
									resultData.getCurrentTiles().get(k).add(tmptile);
									//更新控制结果
									currentplayerlist.get(k).setZhuaTile(tmptile); //加入控制结果
								}
							}
						}
						
					}
				}else{//确定时刻，矫正其他家牌个数
					/*int m = -1,n = -1;
					for(int k = 0; k < 4; k++){
						if(k == getLastPlayerIndex()){
							continue;
						}
						//找到多牌的
						if(resultData.getCurrentTiles().get(k).size() % 3 == 2){
							m = k;
						}
						if(resultData.getCurrentTiles().get(k).size() % 3 == 0){
							n = k;
						}
					}
					if(m != -1 && n != -1){//确实存在
						if(currentplayerlist.get(m).getCachedList().size() > 1){
							LinkedList<I单张牌数据> lasttiles = currentplayerlist.get(m).getCachedList().get(currentplayerlist.get(m).getCachedList().size()-1);
							LinkedList<I单张牌数据> pretiles = currentplayerlist.get(m).getCachedList().get(currentplayerlist.get(m).getCachedList().size()-2);
							for(I单张牌数据 tile : lasttiles){
								if(!pretiles.contains(tile)){
									//加入当前牌
									resultData.getCurrentTiles().get(n).add(tile);
									
								}
							}
						}
						
					}*/
				}
			}else {//牌墙数据没动
				break;
			}
		}
		
		//抓完牌根据邻居更新
		updateCurPlayerTiles();
				
		// 从牌墙删除抓走的牌
		for (int i = 0; i < standnum; i++) {
			if (resultData.getResultTilelist().get(i) == null) {
				i--;
			}
			resultData.getResultTilelist().remove(0);
		}

		// 计算待抓牌
		for (int i = 0; i < resultData.getNextTiles().size(); i++) {
			resultData.getNextTiles().get(i).clear();
		}

		ArrayList<I单张牌数据> tmpresult = new ArrayList<>();
		for (int i = 0; i < resultData.getResultTilelist().size(); i++) {
			I单张牌数据 tile = resultData.getResultTilelist().get(i);
			if (tile != null) {
				tmpresult.add(tile);
			}
		}
		for (int i = 0; i < tmpresult.size(); i++) {
			I单张牌数据 tile = tmpresult.get(i);
			if (i % 4 == 0) {// 当前出牌索引的下一家开始
				resultData.getNextTiles().get((getLastPlayerIndex() + 1) % 4)
						.add(tile);
			} else if (i % 4 == 1) {
				resultData.getNextTiles().get((getLastPlayerIndex() + 2) % 4)
						.add(tile);
			} else if (i % 4 == 2) {
				resultData.getNextTiles().get((getLastPlayerIndex() + 3) % 4)
						.add(tile);
			} else if (i % 4 == 3) { //
				resultData.getNextTiles().get((getLastPlayerIndex() + 4) % 4)
						.add(tile);
			}
		}

		// 保存各家出牌记录
		for (int i = 0; i < 4; i++) {
			currentplayerlist.get(i).addCachelist(resultData.getCurrentTiles().get(i));
		}

		// 判断一下控制结果，处理吃碰杠的情况，对于出完牌的reset
		for (int i = 0; i < 4; i++) {
			if (currentplayerlist.get(i).getCurState() == 1) {// 正常抓打
				currentplayerlist.get(i).reset();
			} else if (currentplayerlist.get(i).getCurState() == 2) {// 碰牌
				int index = currentplayerlist.get(i).getIndex();
				ArrayList<I单张牌数据> penglist = currentplayerlist.get(i).getPengpaiList();
				if (penglist == null) {
					continue;
				}
				// 在出牌cache中找另一张相同名字的牌
				I单张牌数据 another = getAnotherPengTile(penglist);
				if (another == null) {
					continue;
				}
				penglist.add(another);
				// 加入碰牌显示
				resultData.getCurrentCpgTiles().get(index).addAll(penglist);
				//显示出牌
				for(int j = 0; j < 4; j++){
					resultData.getCurrentPlayerTiles().set(j,null);
				}
				resultData.getCurrentPlayerTiles().set(index,currentplayerlist.get(i).getPengpaiChuPai());
				//复位
				currentplayerlist.get(i).reset();

			} else if (currentplayerlist.get(i).getCurState() == 3) {// 吃牌
				int index = currentplayerlist.get(i).getIndex();
				//显示出牌
				for(int j = 0; j < 4; j++){
					resultData.getCurrentPlayerTiles().set(j,null);
				}
				resultData.getCurrentPlayerTiles().set(index,currentplayerlist.get(i).getChipaiChuPai());
				//
				currentplayerlist.get(i).reset();
			} else if (currentplayerlist.get(i).getCurState() == 4) {// 杠牌
				int index = currentplayerlist.get(i).getIndex();
				ArrayList<I单张牌数据> penglist = currentplayerlist.get(i)
						.getGangpaiList();
				if (penglist == null) {
					continue;
				}
				// 在出牌cache中找另一张相同名字的牌
				I单张牌数据 another = getAnotherPengTile(penglist);
				if (another == null) {
					continue;
				}
				penglist.add(another);
				// 加入
				resultData.getCurrentCpgTiles().get(index).addAll(penglist);
				//
				currentplayerlist.get(i).reset();

			} else if (currentplayerlist.get(i).getCurState() == -1) {// 动作没完成
				//超时，reset
				if(currentplayerlist.get(i).ifResetPlayer()){
					currentplayerlist.get(i).reset();
				}
			} else {// == 0 没动作等待中

			}

		}
	}
	
	/**
	 * 判断该抓的牌是牌头还是牌尾？
	 * @param tile
	 * @return ture 头，false 尾
	 */
	public boolean isTileHeadOrTail(I单张牌数据 tile){
//		for (int i = 0; i < resultData.getNextTiles().size(); i++) {
//			for(int j = 0; j < resultData.getNextTiles().get(i).size(); j++){
//				if(resultData.getNextTiles().get(i).get(j).equals(tile) && j < 2){
//					return true;
//				}
//			}
//		}
		for (int i = 0; i < resultData.getResultTilelist().size(); i++) {
			I单张牌数据 tmptile = resultData.getResultTilelist().get(i);
			if (tmptile != null && tile.equals(tmptile) && i < 2) {
				return true;
			}
		}
		return false;
	}
	


	/**
	 * 从牌墙结果中获取tile
	 * @return
	 */
	public I单张牌数据 getTileFromResult(I单张牌数据 tile){
		for(int i = 0; i < resultData.getResultTilelist().size(); i++){
			if(resultData.getResultTilelist().get(i) == null){
				continue;
			}
			if(resultData.getResultTilelist().get(i).equals(tile)){
				return resultData.getResultTilelist().get(i);
				
			}
		}
		return null;
	}
	
	/**
	 * 根据2张牌，在出牌列表找另一个相同的牌
	 * @param alist
	 * @return
	 */
	public I单张牌数据  getAnotherPengTile(ArrayList<I单张牌数据> alist){
		if(alist.size() == 0){
			return null;
		}

		for(int i = chuTileList.size()-1; i >= 0; i--){
			I单张牌数据 tmp = chuTileList.get(i);
			if(alist.contains(tmp)){
				continue;
			}
			String tmpString = ClientMain.getTileNameById2(tmp.getTilecodeid());
			if(tmpString.equals(ClientMain.getTileNameById2(alist.get(0).getTilecodeid()))){
				return tmp;
			}
		}
		return null;
	}
	

	/**
	 * 判断牌墙上的牌邻居是否变化,抓牌总要有个孤立的过程
	 * 1. 出现新的邻居
	 * 2. 如果无邻居，原来2个邻居
	 * 
	 * @param tmptile
	 * @return
	 */
	public boolean neighborChanged(I单张牌数据 tmptile){
		I单张牌数据 curtile = reportedTiles.getSingleItem(tmptile.getTilecodeid());
		I单张牌数据 oldtile = tmptile;
		if(curtile == null || oldtile == null){
			return false;
		}
		
		ArrayList<Integer> oldNeighbors = new ArrayList<>();
		//原来牌墙的邻居
		oldNeighbors.add(oldtile.getNeighbor1());
		oldNeighbors.add(oldtile.getNeighbor2());

		ArrayList<Integer> curNeighbors = new ArrayList<>();
		//原来牌墙的邻居
		curNeighbors.add(curtile.getNeighbor1());
		curNeighbors.add(curtile.getNeighbor2());
	
		//原来靠边，可能孤立，所以需要用有新邻居判断，肯定动了
		if(oldNeighbors.get(0) == 0 || oldNeighbors.get(1) == 0){
			if((!oldNeighbors.contains(curNeighbors.get(0)) && curNeighbors.get(0) != 0) ||
					(!oldNeighbors.contains(curNeighbors.get(1)) && curNeighbors.get(1) != 0)){
				return true;
			}
		}
		
		//原来2个邻居，现在孤立，动过
		if(oldNeighbors.get(0) != 0 && oldNeighbors.get(1) != 0){
			if(curNeighbors.get(0) == 0 && curNeighbors.get(1) == 0){
				return true;
			}
		}
			
//		//只要有一个邻居没动，就不动
//		for(Integer old : oldNeighbors){
//			for(Integer cur : curNeighbors){
//				if(old == cur && old != 0){
//					return false;
//				}
//			}
//		}
		
		return false;
	}
	
	/**
	 * 获取出牌中，每家出了几张
	 * @return
	 */
	public ArrayList<Integer> getChulistNums(){
		//判断有几
		ArrayList<Integer> arrayList = new ArrayList<>();
		for(int i = 0; i < 4; i++){
			arrayList.add(0);
		}
		for(int i = 0; i < newChuTileList.size();i++){
			I单张牌数据 tmptile = newChuTileList.get(i);
			if(tmptile == null){
				continue;
			}
			for (int index = 0; index < 4; index++) {
				if (resultData.getCurrentTiles().get(index).contains(tmptile)) {
					int itime = arrayList.get(index);
					arrayList.set(index, itime+1);
					break;
				}
			}
		}
		return arrayList;
	}
	
	/**
	 * 根据与当前牌成邻居更新玩家牌堆
	 * @param tilelist
	 * @return
	 */
	public void updateCurPlayerTiles(){
		LinkedList<I单张牌数据> retList = new LinkedList<>();
		ArrayList<Integer> integers = new ArrayList<>();
		ArrayList<Integer> newintegers = new ArrayList<>();
		//已出牌删除
		for(int i = 0; i < 4; i++){
			LinkedList<I单张牌数据> tilelist = resultData.getCurrentTiles().get(i);
			//原牌
			for (Iterator<I单张牌数据> iterator = tilelist.iterator(); iterator.hasNext();) {
				I单张牌数据 tile = iterator.next();
			    if (reportedTiles.getSingleItem(tile.getTilecodeid()).getDirection() == ConstantValue.UPSIDE) {
			    	iterator.remove();
			    }
			}
		}
		//根据邻居更新
		for(int i = 0; i < 4; i++){
			LinkedList<I单张牌数据> tilelist = resultData.getCurrentTiles().get(i);
			//原牌
			for(I单张牌数据 tile : tilelist){
				if(!notsureTileList.contains(tile)){
					integers.add(tile.getTilecodeid());					
				}
			}
			//看看是否有新邻居
			for(I单张牌数据 tile : tilelist){
				//可疑牌不计算邻居
				if(notsureTileList.contains(tile)){
					continue;					
				}
				I单张牌数据 newtile = reportedTiles.getSingleItem(tile.getTilecodeid());
				if(newtile == null){
					continue;
				}
				if(newtile.getNeighbor1() != 0 && !integers.contains(newtile.getNeighbor1()) 
						/*&& cachestandData.contains(reportedTiles.getSingleItem(newtile.getNeighbor1()))*/){
					integers.add(newtile.getNeighbor1());
					newintegers.add(newtile.getNeighbor1());
				}
			
				if(newtile.getNeighbor2() != 0 && !integers.contains(newtile.getNeighbor2())
						/*&& cachestandData.contains(reportedTiles.getSingleItem(newtile.getNeighbor2()))*/){
					integers.add(newtile.getNeighbor2());
					newintegers.add(newtile.getNeighbor2());
				}
			}
			
			//把其他的玩家删除可能新增的
			if(newintegers.size() > 0){				
				//新增牌
				for(Integer integer:newintegers){
					I单张牌数据 newtile = reportedTiles.getSingleItem(integer);
					if(newtile != null && newtile.getDirection() != ConstantValue.UPSIDE 
							&& !resultData.getCurrentTiles().get(i).contains(newtile)){
						resultData.getCurrentTiles().get(i).add(newtile);
						retList.add(newtile);
					}
					//可疑列表删除
					if(notsureTileList.contains(newtile)){
						notsureTileList.remove(newtile);
					}
				}
				//从其他删除
				for(int j = 0; j < 4; j++){
					if(j == i){
						continue;
					}
					resultData.getCurrentTiles().get(j).removeAll(retList);
				}
				
				
			}
			integers.clear();
			newintegers.clear();
			retList.clear();
		}
	}
	
	/**
	 * 根据3、4张牌成邻居，更新碰杠牌.
	 * 
	 */
	public void updatePengGangTiles(){
		//判断异常2张相同的数据
		ArrayList<Integer> badList = new ArrayList<>();
		for(int i = 0;i < 4; i++){
			int inum = 0;
			ArrayList<Integer> tmpList = new ArrayList<>();
			for(I单张牌数据 tmptile : resultData.getCurrentCpgTiles().get(i)){
				if(ClientMain.huapaiArrayList.contains(ClientMain.getTileNameById2(tmptile.getTilecodeid()))){
					continue;
				}
				for(I单张牌数据 tmptile2 : resultData.getCurrentCpgTiles().get(i)){
					if(ClientMain.huapaiArrayList.contains(ClientMain.getTileNameById2(tmptile2.getTilecodeid()))){
						continue;
					}
					if(ClientMain.getTileNameById2(tmptile.getTilecodeid()).equals(ClientMain.getTileNameById2(tmptile2.getTilecodeid()))){
						tmpList.add(tmptile2.getTilecodeid());
						inum++;
					}else{
						if(inum > 0 && inum <= 2 && !badList.contains(tmpList.get(0))){
							badList.addAll(tmpList);
						}
						inum = 0;
						tmpList.clear();
						break;
					}
				}
			}
			removeCurrentCpgTile(i,badList);
			badList.clear();
		}
		
		//删除花牌后面重新加
		for(int i = 0;i < 4; i++){
			for(I单张牌数据 tmptile : resultData.getCurrentCpgTiles().get(i)){
				if(ClientMain.huapaiArrayList.contains(ClientMain.getTileNameById2(tmptile.getTilecodeid()))){
					badList.add(tmptile.getTilecodeid());
				}
			}
			removeCurrentCpgTile(i,badList);
			badList.clear();
		}
		
		
		//刷新碰杠牌
		for(int i = 0; i < reportedTiles.get牌堆中牌的总行数(); i++){
			boolean bfind = false;
			for(int j = 0; j < reportedTiles.getItem(i).get行中总牌数(); j++){
				I单张牌数据 tile = reportedTiles.getItem(i).getItem(j);
				//曾经站立过  并且 姿势是放倒的，处理该行
				if(tile != null && /*cachestandData.contains(tile) &&*/ 
						(reportedTiles.getSingleItem(tile.getTilecodeid()).getDirection() == ConstantValue.UPSIDE
						|| reportedTiles.getSingleItem(tile.getTilecodeid()).getDirection() == ConstantValue.DOWNSIDE)){
					
				}else{
					bfind = true;
					break;
				}
			}
			if(!bfind){//处理该行
				String oldName = "";
				String curName = "";
				ArrayList<Integer> tmpList = new ArrayList<>();
				for(int k = 0; k < reportedTiles.getItem(i).get行中总牌数(); k++){
					I单张牌数据 tile = reportedTiles.getItem(i).getItem(k);
					if(tile == null){
						continue;
					}
					oldName = curName;
					curName = ClientMain.getTileNameById2(tile.getTilecodeid());
					tmpList.add(tile.getTilecodeid());
					
					if(!"".equals(oldName) && !curName.equals(oldName)){//新
						tmpList.remove(tmpList.size()-1);
						getOwnerAndDisplay(tmpList);
						tmpList.clear();
						tmpList.add(tile.getTilecodeid());
					}
					//最后一对
					if(k == reportedTiles.getItem(i).get行中总牌数()-1){
						getOwnerAndDisplay(tmpList);
					}
				}
			}
			
		}
		
	}
	
	public void getOwnerAndDisplay(ArrayList<Integer> tilelist){
		if(tilelist.size() == 0){
			return;
		}else if(tilelist.size() < 3){//只判断花牌
			for(Integer tileid : tilelist){
				if(ClientMain.huapaiArrayList.contains(ClientMain.getTileNameById2(tileid))){
					//获取玩家显示
					int oid = getTileOwner(tileid);
					if(oid != -1){
						for(Integer id : tilelist){
							if(!resultData.getCurrentCpgTiles().get(oid).contains(reportedTiles.getSingleItem(id))){
								resultData.getCurrentCpgTiles().get(oid).add(reportedTiles.getSingleItem(id));
							}
							if(resultData.getCurrentTiles().get(oid).contains(reportedTiles.getSingleItem(id))){
								resultData.getCurrentTiles().get(oid).remove(reportedTiles.getSingleItem(id));
							}
						}
						
					}
				}
			}
			
		}else if(tilelist.size() == 3){//碰都更新下
			int oid = getTilelistOwner(tilelist);
			if(oid != -1){
				removeCurrentCpgTile(oid,tilelist);
				for(Integer id : tilelist){
				//	resultData.getCurrentCpgTiles().get(oid).remove(reportedTiles.getSingleItem(id));
					resultData.getCurrentCpgTiles().get(oid).add(reportedTiles.getSingleItem(id));
					if(resultData.getCurrentTiles().get(oid).contains(reportedTiles.getSingleItem(id))){
						resultData.getCurrentTiles().get(oid).remove(reportedTiles.getSingleItem(id));
					}
				}
				
			}
		}else if(tilelist.size() == 4){//杠都更新下
			int oid = getTilelistOwner(tilelist);

			if(oid != -1){
				removeCurrentCpgTile(oid,tilelist);
				for(Integer id : tilelist){
				//	resultData.getCurrentCpgTiles().get(oid).remove(reportedTiles.getSingleItem(id));
					resultData.getCurrentCpgTiles().get(oid).add(reportedTiles.getSingleItem(id));
					if(resultData.getCurrentTiles().get(oid).contains(reportedTiles.getSingleItem(id))){
						resultData.getCurrentTiles().get(oid).remove(reportedTiles.getSingleItem(id));
					}
				}
				
			}
		}
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public int getTileOwner(int id){
		for (OnePlayer one : currentplayerlist) {
			// 属于谁家？
			if (one.ifTileInCachelist(reportedTiles.getSingleItem(id))) {
				return one.getIndex();
			}
		}
		return -1;
	}
	
	/**
	 * 获取牌组的所属家
	 * @param ids
	 * @return
	 */
	public int getTilelistOwner(ArrayList<Integer> ids){
		int[] owners = new int[4];
		for(Integer id : ids){
			int oid = getTileOwner(id);
			if(oid != -1){
				owners[oid]++;
			}
		}
		
		for(int i = 0; i < 4; i++){
			if(owners[i] >= 2){
				return i;
			}
		}
		
		return -1;
	}
	
	public void removeCurrentCpgTile(int index, ArrayList<Integer> ids) {
		Iterator<I单张牌数据> it = resultData.getCurrentCpgTiles().get(index)
				.iterator();

		while (it.hasNext()) {
			I单张牌数据 tile = it.next();

			if (ids.contains(tile.getTilecodeid())) {
				it.remove();
			}
		}

	}
	
	public void reset() {
		resultData.reset();
		chuTileList.clear();
		newChuTileList.clear();
		newReturnTileList.clear();
		cachestandData.clear();
		currentplayerlist.clear();
		playerchuList.clear();
		newReturnTileMap.clear();
		notsureTileList.clear();
		ifSwitchedTileline = false;
	}

	
	@Override
	public void setOldResultTiles(ArrayList<I牌堆行数据> oldResultTiles) {
		
	}

	@Override
	public ArrayList<I牌堆行数据> getResultTiles() {

		return null;
	}

	public PlayerTileData getOldPlayerTileData() {
		return oldPlayerTileData;
	}

	public void setOldPlayerTileData(PlayerTileData oldPlayerTileData) {
		this.oldPlayerTileData = oldPlayerTileData;
	}

	public PlayerTileData getPlayerTileData() {
		return resultData;
	}
	public void setCachestandData(ArrayList<I单张牌数据> cachestandData) {
		this.cachestandData = cachestandData;
	}
	
	public int getLastPlayerIndex(){
		if(playerchuList.size() > 0){
			return playerchuList.get(playerchuList.size()-1);
		}
		return -1;
	}

	public int getPrePlayerIndex(){
		if(playerchuList.size() > 1){
			for(int i = playerchuList.size()-1; i >= 0; i--){
				if(playerchuList.get(i) != getLastPlayerIndex()){
					return playerchuList.get(i);
				}
			}
		}
		
		return -1;
	}
	
	public void deletePlayerIndex(int index){
		if(playerchuList.size() > 1){
			for(int i = playerchuList.size()-1; i >= 0; i--){
				if(playerchuList.get(i) == index){
					playerchuList.remove(i);
					return;
				}
			}
		}
	}


}

/**
 * 单个玩家牌记录
 * @author Administrator
 * 抓一张，打一张
 * 不抓，打三张，吃碰
 * 抓1，打四张，杠
 * so 判断牌个数，决定当前玩家是否完成
 */
class OnePlayer{
	private int index; //玩家索引
	private I单张牌数据 zhuaTile;
	private I单张牌数据 gangTile;
	private ArrayList<I单张牌数据> chuTileList;
	private ArrayList<LinkedList<Integer>> cachedList;
	private ArrayList<I单张牌数据> chuTileListbak;
	private Instant duration; //间隔时间，防止状态卡死
	
	public OnePlayer(int i){
		setIndex(i);
		zhuaTile = null;
		gangTile = null;
		chuTileList = new ArrayList<>();
		cachedList = new ArrayList<>();
		duration = null;
		chuTileListbak = new ArrayList<>();
	}
	
	/**
	 * 当前状态
	 * 0等待
	 * 1  抓出完成
	 * 2 吃碰杠完成
	 * -1 待完成
	 * @return
	 */
	public int getCurState(){
		//正常抓打
		if(zhuaTile != null && gangTile == null && chuTileList.size() == 1){
			return 1;
		}
		ArrayList<String> names = new ArrayList<>();
		//碰牌
		if(zhuaTile == null && gangTile == null && chuTileList.size() == 3){
			for(I单张牌数据 tmptile:chuTileList){
				String nameString = ClientMain.getTileNameById2(tmptile.getTilecodeid());
			//	if(!names.contains(nameString)){
					names.add(nameString);
			//	}
			}
			if(ifMatchPengpai(names)){
				return 2;
			}
		}		
		//吃牌,至少2个花色一样
		if(zhuaTile == null && gangTile == null && chuTileList.size() == 3){
			for(I单张牌数据 tmptile:chuTileList){
				String nameString = ClientMain.getTileNameById2(tmptile.getTilecodeid());
//				if(!names.contains(nameString)){
					names.add(nameString);
//				}
			}
			if(ifMatchChipai(names)){ //
				return 3;
			}
		}

		//杠牌
		if(gangTile != null/* &&  chuTileList.size() >= 4*/){
//			for(I单张牌数据 tmptile:chuTileList){
//				String nameString = ClientMain.getTileNameById2(tmptile.getTilecodeid());
//				if(!names.contains(nameString)){
//					names.add(nameString);
//				}
//			}
//			if(names.size() == 2){
//				return 4;
//			}
			return 4;
		}
		//等待状态
		if(zhuaTile == null && gangTile == null && chuTileList.size() == 0){
			return 0;
		}
		//未完成
		return -1;
	}

	public void setZhuaTile(I单张牌数据 zhuaTile) {
		this.zhuaTile = zhuaTile;
		if(duration == null){
			duration = new Instant();
		}
	}
	
	public void setGangTile(I单张牌数据 gangTile) {
		this.gangTile = gangTile;
		if(duration == null){
			duration = new Instant();
		}
	}

	public I单张牌数据 getZhuaTile() {
		return zhuaTile;
	}

	public I单张牌数据 getGangTile() {
		return gangTile;
	}

	public ArrayList<I单张牌数据> getChuTileList() {
		return chuTileList;
	}

	public ArrayList<LinkedList<Integer>> getCachedList() {
		return cachedList;
	}


	/**
	 * 添加出牌，如果出牌属于{梅兰竹菊春夏秋冬}则不添加，仅添加杠牌到当前牌堆
	 * @param data
	 */
	public void addChuTiles(I单张牌数据 data){
		if(!chuTileList.contains(data)){
			chuTileList.add(data);
			if(duration == null){
				duration = new Instant();
			}
		}
	}

	public void addCachelist(LinkedList<I单张牌数据> data){
		
//		if(cachedList.size() > 0){
//			if(!data.equals(cachedList.get(cachedList.size()-1))){
//				cachedList.add(Ulti.tileListToIntegerList(data));
//			}
//		}else{
			cachedList.add(Ulti.tileListToIntegerList(data));
//		}
	}

	/**
	 * 是否超时需要reset？
	 * @return
	 */
	public boolean ifResetPlayer(){
		if(duration == null){
			return false;
		}
		
		Instant nowInstant = new Instant();
		if(nowInstant.getMillis() - duration.getMillis() > 15000){
			return true;
		}
		return false;
	}

	
	/**
	 * 查找某张牌是否在历史缓存中
	 * @param tile
	 * @return
	 */
	public boolean ifTileInCachelist(I单张牌数据 tile){
		if(tile == null){
			return false;
		}
		for(LinkedList<Integer> tilelist : cachedList){
			if(tilelist.contains(tile.getTilecodeid())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 吃牌，筒万条，至少2个类别一样且相差1、2
	 * @param names
	 * @return
	 */
	public boolean ifMatchChipai(ArrayList<String> names){
		
		for(int i = 0; i < 2; i++){
			if(names.get(i).equals("dong") || names.get(i).equals("nan") || names.get(i).equals("xi") || 
					names.get(i).equals("bei") || names.get(i).equals("zhong") || names.get(i).equals("fa") || 
					names.get(i).equals("bai") || names.get(i).equals("mei") || names.get(i).equals("lan") || 
					names.get(i).equals("zu") || names.get(i).equals("ju") || names.get(i).equals("chun") || 
					names.get(i).equals("xia") || names.get(i).equals("qiu") || names.get(i).equals("dong2")){
				continue;
			}

			int aint = Integer.parseInt(names.get(i).substring(0,1));
			String aString = names.get(i).substring(1);
			for(int j = i+1; j < 3; j++){
				if(names.get(j).equals("dong") || names.get(j).equals("nan") || names.get(j).equals("xi") || 
						names.get(j).equals("bei") || names.get(j).equals("zhong") || names.get(j).equals("fa") || 
						names.get(j).equals("bai") || names.get(j).equals("mei") || names.get(j).equals("lan") || 
						names.get(j).equals("zu") || names.get(j).equals("ju") || names.get(j).equals("chun") || 
						names.get(j).equals("xia") || names.get(j).equals("qiu") || names.get(j).equals("dong2")){
					continue;
				}
				int bint = Integer.parseInt(names.get(j).substring(0,1));
				String bString = names.get(j).substring(1);
				if((Math.abs(aint-bint) == 2 || Math.abs(aint-bint) == 1) && aString.equals(bString)){
					return true;
				}
				
			}
		}
		return false;
	}
	
	/**
	 * 碰牌，出的牌有2个一样，如果3个一样则不是碰牌
	 * @param names
	 * @return
	 */
	public boolean ifMatchPengpai(ArrayList<String> names){
//		if(names.get(0).equals(names.get(1)) && names.get(0).equals(names.get(2))){
//			return false;
//		}
		for(int i = 0; i < 2; i++){
			String aString = names.get(i);
			for (int j = i + 1; j < 3; j++) {
				String bString = names.get(j);
				if (aString.equals(bString)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 获取碰牌的2张牌
	 * @return
	 */
	public ArrayList<I单张牌数据> getPengpaiList(){
		ArrayList<I单张牌数据> retList = new ArrayList<>();
		for(int i = 0; i < 2; i++){
			I单张牌数据 atile = chuTileList.get(i);
			String anameString = ClientMain.getTileNameById2(atile.getTilecodeid());
			for (int j = i + 1; j < 3; j++) {
				I单张牌数据 btile = chuTileList.get(j);
				String bnameString = ClientMain.getTileNameById2(btile.getTilecodeid());
				if(anameString.equals(bnameString)){
					retList.add(atile);
					retList.add(btile);
					return retList;
				}
			}
		}
		return null;
	}
	
	public I单张牌数据 getPengpaiChuPai(){
		ArrayList<I单张牌数据> retList = (ArrayList<I单张牌数据>) chuTileList.clone();
		retList.removeAll(getPengpaiList());
		if(retList.size() > 0){
			return retList.get(0);
		}
		
		return null;
	}
	
	
	public I单张牌数据 getChipaiChuPai(){
		if(chuTileList.size() > 0){
			return chuTileList.get(chuTileList.size()-1);
		}
		
		return null;
	}
	
//	public ArrayList<I单张牌数据> getGangpaiList(){
//		int size = chuTileList.size();
//		ArrayList<I单张牌数据> retList = new ArrayList<>();
//		for(int i = 0; i < size-1; i++){
//			I单张牌数据 atile = chuTileList.get(i);
//			String anameString = ClientMain.getTileNameById2(atile.getTilecodeid());
//			for (int j = i + 1; j < size; j++) {
//				I单张牌数据 btile = chuTileList.get(j);
//				String bnameString = ClientMain.getTileNameById2(btile.getTilecodeid());
//				if(anameString.equals(bnameString) && !retList.contains(atile)){
//					retList.add(atile);
//					
//				}
//				if(anameString.equals(bnameString) && !retList.contains(btile)){
//					retList.add(btile);
//				}
//			}
//		}
//		return retList;
//	}
	
	/**
	 * 发现有杠牌情况，获取杠牌,根据出牌的相同的获取
	 * 1. 抓一张，杠自家3 => 抓打了
	 * 2. 别人出，杠3张	=> 碰牌
	 * 3. 抓一张，暗杠4张 =>如果翻开1张就是抓打了，否则找4张字面向下相同的
	 * 4. 花牌，杠
	 * 
	 * @return
	 */
	public ArrayList<I单张牌数据> getGangpaiList() {
		ArrayList<I单张牌数据> retList = new ArrayList<>();
		int size = chuTileListbak.size();
		retList.addAll(chuTileListbak);
		retList.addAll(chuTileList);
		return retList;

		
//		for (int i = 0; i < size - 1; i++) {
//			I单张牌数据 atile = chuTileListbak.get(i);
//			String anameString = ClientMain.getTileNameById2(atile
//					.getTilecodeid());
//			for (int j = i + 1; j < size; j++) {
//				I单张牌数据 btile = chuTileListbak.get(j);
//				String bnameString = ClientMain.getTileNameById2(btile
//						.getTilecodeid());
//				if (anameString.equals(bnameString) && !retList.contains(atile)) {
//					retList.add(atile);
//				}
//				if (anameString.equals(bnameString) && !retList.contains(btile)) {
//					retList.add(btile);
//				}
//			}
//		}
//		if(retList.size() > 0){
//			return retList;
//		}
		
	}
	

	
	public void reset(){
		chuTileListbak.addAll(chuTileList);
		zhuaTile = null;
		gangTile = null;
		chuTileList.clear();
		duration = null;
	}
	


	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	
}
