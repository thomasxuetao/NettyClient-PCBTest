package com.thomas.logic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFuture;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;

import com.thomas.client.ClientMain;
import com.thomas.data.ConstantValue;
import com.thomas.data.I单张牌数据;
import com.thomas.data.I牌堆数据;
import com.thomas.data.I牌堆行数据;
import com.thomas.data.LogHelper;
import com.thomas.data.Ulti;

public class Logic {
	//牌副ID
	private int tileSetid; 

	//当前状态
	private int state;
	public int getState() {
		return state;
	}


	//各个状态的引用
	private XiPai xiPai;
	private DongPai dongPai;
	private ZhuaPai zhuaPai;
	private ChuPai chuPai;
	//坏牌的个数
	private int badTilesCount;
	//坏牌数组
	private ArrayList<I单张牌数据> badTilesList;
	//控制牌上报快慢计数
	private int ctrlcount;
	
	public Logic(){
		tileSetid = 0;
		setBadTilesCount(0);
		state = ConstantValue.IdleState;
		badTilesList = new ArrayList<>();
		xiPai = new XiPai();
		dongPai = new DongPai();
		zhuaPai = new ZhuaPai();
		chuPai = new ChuPai();
		ctrlcount = 0;
	}
	
	/**
	 * Bridge过来的牌数据，是完整的一副牌数据，对于偶尔不上报的默认状态不变，也带过来了
	 * @param data
	 */
	public void changeTiles(I牌堆数据 data){
		if(tileSetid == 0){
			tileSetid = data.getTileSetId();
		}

		
		//根据牌总数-坏牌个数 判断是否有坏牌,坏牌市什么？大于1个则提示
		int iValidTileNums = data.getValidtilesNums();
		badTilesCount = ClientMain.TotalTileNums - iValidTileNums;
		LogHelper.log.info("总牌数：" + ClientMain.TotalTileNums + "有效牌个数：" + iValidTileNums);
		System.out.println("总牌数：" + ClientMain.TotalTileNums + "有效牌个数：" + iValidTileNums);
		//		if(badTilesCount > 1){
//			System.out.println("坏牌大于1个:" + badTilesCount);
//			return;
//		}
		if(badTilesCount > 0){
			badTilesList.clear();
			for(int i = 0; i < ConstantValue.tileCodeArrays.length; i++){
				boolean bfind = false;
				for(int j = 0; j < data.get牌堆中牌的总行数(); j++){
					for(int k = 0; k < data.getItem(j).get行中总牌数(); k++){
						if(data.getItem(j).getItem(k).getTilecodeid() == ConstantValue.tileCodeArrays[i]){
							bfind = true;
							break;
						}
					}
					if(bfind){
						break;
					}
				}
				if(!bfind){
					I单张牌数据 badtile = new I单张牌数据();
					badtile.setTilecodeid(ConstantValue.tileCodeArrays[i]);
					badtile.setTilesetid(data.getTileSetId());
					badtile.setBadType(4);
					badTilesList.add(badtile);
				}
			}
		}

		//检查一下状态，数组太多，说明无序
		if(data.get牌堆中牌的总行数() > ClientMain.TotalTileNums-15 && Ulti.getLineCountOfSome(data, 5) == 0 ){
			state = ConstantValue.IdleState;
			xiPai.reset();
			dongPai.reset();
			zhuaPai.reset();
			chuPai.reset();
			badTilesList.clear();
			//Z时间复位
			data.resetZtime();
		}

		/**
		 * 开始判断状态
		 * 如果是空闲状态，说明程序刚刚启动，只有判断出洗牌的状态才能正式开始
		 * 其他的状态都无法判断出牌的实际布局，只能放弃。
		 * 如果是其他状态，则按照状态迁移图进行判断。
		 */
//		state = 1;
		switch (state) {
		//空闲状态，只能从判断是否是洗牌开始
		case ConstantValue.IdleState:
			xiPai.changeTiles(data);
			if(xiPai.isRunning()){
				state = ConstantValue.XiPaiing;
			}
			break;
		//洗牌中状态，判断是否洗牌完成
		case ConstantValue.XiPaiing:
			xiPai.changeTiles(data);
			if(xiPai.isCompleted()){//洗牌完成
				state = ConstantValue.XiPaied;		
				//给牌发命令，奇数牌发FD，偶数牌发FE
				ctrlcount = 0;
				if(tileSetid % 2 == 0){
					sendCmdToMachine(0XFE);
				}else{
					sendCmdToMachine(0XFD);
				}
				
				//根据坏牌调用不同函数
				if(badTilesCount == 0){
					xiPai.analyzeTiles();
				}else{
					xiPai.analyzeTilesOne();
				}

				//刷新界面，显示8个数组
				ArrayList<I牌堆行数据> tmplines = Ulti.cloneArrayList(xiPai.getResultTiles());
				ClientMain.frameWin.refresh(xiPai.getResultTiles(),data.getTileSetId());
				//给动牌/抓牌赋值，下一步两个动作都要判断
				dongPai.setOldResultTiles(Ulti.cloneArrayList(tmplines));
				zhuaPai.setOldResultTiles(Ulti.cloneArrayList(tmplines));
			}
			break;
		//洗牌完成，等待动牌 or 抓牌
		case ConstantValue.XiPaied:
			//是不是抓牌？4张牌站立或者说有任何牌站立了
			zhuaPai.changeTiles(data);
			if(zhuaPai.isRunning()){
				state = ConstantValue.ZhuaPaiing;
			}else{//还没有抓牌，是不是动牌？
				dongPai.changeTiles(data);
				if(dongPai.isCompleted()){
					if(badTilesCount == 0){
						dongPai.analyzeTiles();
					}else{
						dongPai.analyzeTilesOne();
					}

					//刷新界面
					ArrayList<I牌堆行数据> tmplines = Ulti.cloneArrayList(dongPai.getResultTiles());
					ClientMain.frameWin.refresh(dongPai.getResultTiles(),data.getTileSetId());
					//赋值
					zhuaPai.setOldResultTiles(Ulti.cloneArrayList(tmplines));
				}
			}
			break;
		//四张牌站立抓牌，等待抓牌完成
		case ConstantValue.ZhuaPaiing:
			zhuaPai.changeTiles(data);
			if(zhuaPai.isCompleted()){
				state = ConstantValue.ZhuaPaied;
				if(badTilesCount == 0){
				//	zhuaPai.analyzeTiles2();
					zhuaPai.analyzeTilesOne();
				}else{
					zhuaPai.analyzeTilesOne();
				}
				//刷新界面
				ClientMain.frameWin.refresh(zhuaPai.getPlayerTileData(),data.getTileSetId());
				//赋值
				chuPai.setOldPlayerTileData(zhuaPai.getPlayerTileData());
				chuPai.initdata();
				chuPai.setCachestandData(zhuaPai.getCachestandData());
			}
			break;
		//抓牌完成，等待出牌
		case ConstantValue.ZhuaPaied:
			chuPai.changeTiles(data);
			if(chuPai.isRunning()){
				state = ConstantValue.ChuPaiing;
				//刷新第一次出牌
				chuPai.changeTiles(data);
				//分析牌
				chuPai.analyzeTiles();
				//刷新出牌情况
				ClientMain.frameWin.refresh(chuPai.getPlayerTileData(),data.getTileSetId());
			}
			break;
		case ConstantValue.ChuPaiing:
			chuPai.changeTiles(data);
			//分析牌
			chuPai.analyzeTiles();
			//刷新出牌情况
			ClientMain.frameWin.refresh(chuPai.getPlayerTileData(),data.getTileSetId());
			
			if(chuPai.isCompleted()){
				state = ConstantValue.ChuPaied;
			}
			break;
		case ConstantValue.ChuPaied:
			state = ConstantValue.IdleState;
			//本局结束，切换AB牌
			xiPai.reset();
			dongPai.reset();
			zhuaPai.reset();
			chuPai.reset();
			
			break;
		default:
			break;
		}
		
		//控制牌计数
		ctrlcount++;
		if(ctrlcount == 20 && state == ConstantValue.XiPaied){
			//给牌发命令，
			if(tileSetid % 2 == 0){
				sendCmdToMachine(0X1);
			}else{
				sendCmdToMachine(0X0);
			}
		}
		if(ctrlcount == 40 && state == ConstantValue.XiPaied){
			//给牌发命令，奇数牌发FE，偶数牌发FD
			if(tileSetid % 2 == 0){
				sendCmdToMachine(0XFD);
			}else{
				sendCmdToMachine(0XFE);
			}
		}
		
		
		LogHelper.log.info("state:" + state);
		System.out.println("state:" + state);
		
	}
	
	
	public int getBadTilesCount() {
		return badTilesCount;
	}

	public void setBadTilesCount(int badTilesCount) {
		this.badTilesCount = badTilesCount;
	}

	public int getTileSetid() {
		return tileSetid;
	}

	public void setTileSetid(int tileSetid) {
		this.tileSetid = tileSetid;
	}

	public ArrayList<I单张牌数据> getBadTilesList() {
		return badTilesList;
	}

	public void setBadTilesList(ArrayList<I单张牌数据> badTilesList) {
		this.badTilesList = badTilesList;
	}

	/**
	 * 给牌发快慢上报命令
	 */
	public void sendCmdToMachine(int cmdcode){
//		try {
//			ChannelFuture cf = ClientMain.loginWin.getChannelFuture();
//			ByteBuf cmdBuf = cf.channel().alloc().buffer();
//			cmdBuf.writeShort(ConstantValue.USERTYPE);
//			cmdBuf.writeShort(ConstantValue.CTRLCARD);
//			cmdBuf.writeShort(ConstantValue.NULLREC);
//			cmdBuf.writeBytes(ConstantValue.strUserName.getBytes());// 10 bytes
//			cmdBuf.writeByte(cmdcode);
//			cmdBuf.writeInt(0xFBFEBFEF);
//			System.out.println("send cmd: " + cmdBuf.toString(CharsetUtil.UTF_8));
//			System.out.println("send cmd: " + ByteBufUtil.hexDump(cmdBuf));
//			cf.channel().writeAndFlush(cmdBuf);
//			
//			//	ctrlcount = 0;
//		} catch (Exception e) {
//			System.out.println(e.toString());
//			LogHelper.log.error(e.toString());
//		}
		
	}
	
}
