package com.thomas.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.joda.time.Instant;
import com.thomas.client.ClientMain;

public class TilesObject implements Rows, Row {

	// 牌副id
	private int iTileSetId;
	// 牌堆信息
	private HashMap<Integer, SingleTile> m_tiles = new HashMap<Integer, SingleTile>();
	private int[][] m_tileValues = null;
	private LinkedList<Integer> _TilesReported = new LinkedList<Integer>();

	//
	private HashMap<Integer, ArrayList<Integer>> tileDirectionInfo = new HashMap<>();
	
	public int getiTileSetId() {
		return iTileSetId;
	}

	public void setiTileSetId(int iTileSetId) {
		this.iTileSetId = iTileSetId;
	}


	public final TilesObject clone() {
		TilesObject result = new TilesObject();
		result.m_tiles = Ulti.DeepCopy(this.m_tiles);
		result.m_tileValues = Ulti.DeepCopy(this.m_tileValues);
		return result;
	}

	// /#region Tile 相关操作
	public final SingleTile GetTileByValue(int targetTileValue) {
		if (!this.m_tiles.containsKey(targetTileValue)) {
			return null;
		}
		return this.m_tiles.get(targetTileValue);
	}

	public final boolean ExistsTileValue(int targetTileValue) {
		if (this.m_tiles.containsKey(targetTileValue)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * insert new tile into m_tiles 
	 * @param newTileValue
	 * @param neighbor1
	 * @param neighbor2
	 * @param direction
	 * @return
	 */
	private boolean InsertTile(int newTileValue, int neighbor1, int neighbor2,
			int direction, int zdirection) {
		if (!this.TileValid(newTileValue)) {
			return false;
		}
		if (this.ExistsTileValue(newTileValue)) {
			return false;
		}
		this.m_tiles.put(newTileValue, new SingleTile(newTileValue, neighbor1,
				neighbor2, direction, zdirection));
		return true;
	}

	public final boolean TileValid(int newTileValue) {
		// if ((Bridge.ValidTiles.length <= newTileValue)
		// || (Bridge.ValidTiles[newTileValue] == 0)) {
		// return false;
		// }
		return true;
	}

	/**
	 * 
	 * 重新组合建立牌堆得到当前的状态是否有效，及有效状态时的牌堆数据。
	 */
	private void RebuildValueArray() {

		boolean resultFlag = true;

		LinkedList<Integer[]> result = new LinkedList<Integer[]>();
		ArrayList beginsAndEnds = new ArrayList();

		// * 循环每一张牌，更新其相应的状态

		for (SingleTile st : this.m_tiles.values()) {
			if (st.getType() == SingleTile.TileType.Regular) {
				continue;
			} else if (beginsAndEnds.contains(st.getValue())) {
				continue;
			} else {
				beginsAndEnds.add(st.getValue());

				LinkedList<Integer> row = new LinkedList<Integer>();
				SingleTile currentTile = st;
				int currentValue = st.getValue();
				int prevTileValue = 0;
				int tempValue = currentValue;

				while (true) {
					row.offer(currentValue);

					currentValue = currentTile
							.GetTheOtherNeighbor(prevTileValue);

					if (currentValue == 0) {
						break;
					}
					if (currentValue == -1) {
						resultFlag = false;
						break;
					}

					prevTileValue = tempValue;
					tempValue = currentValue;
					currentTile = this.GetTileByValue(currentValue);

					if (currentTile == null) {
						resultFlag = false;
						break;
					}

					if (!currentTile.HasNeighbor(prevTileValue)) {
						resultFlag = false;
						break;
					}

					if (currentTile.getType() == SingleTile.TileType.End) {
						if (!beginsAndEnds.contains(currentValue)) {
							beginsAndEnds.add(currentValue);
						}
						row.offer(currentValue);
						break;
					}
				}

//				int[] tmpres1 = new int[row.size()];
				int[] tmpres2 = new int[row.size()];

//				LinkedList<Integer> to = Ulti.ReverList(row);
//				tmpres1 = Ulti.IntegerListToIntArray(to);
				tmpres2 = Ulti.IntegerListToIntArray(row);

//				if (tmpres2[0] > tmpres1[0]) {
//					Integer[] tmp1 = Ulti.IntArrayToIntegerArray(tmpres1);
//					result.offer(tmp1);
//				} else {
					Integer[] tmp2 = Ulti.IntArrayToIntegerArray(tmpres2);
					result.offer(tmp2);
//				}
			}
		}

		this.setValid(resultFlag);
		
		//把result里面数据按牌行包括的牌个数牌序
		Collections.sort(result,new TileNodeComparator());

		this.m_tileValues = Ulti.IntegerListsToArrays(result);

		// 几张牌 != 数组生成的个数
		if (this.m_tiles.size() != Ulti.TotalCount(m_tileValues)) {
			this.setValid(false);
		}
	}
	
	public class TileNodeComparator implements Comparator<Integer[]> {
//		  @Override
		  public int compare(Integer[] o1, Integer[] o2 ) {
		          return (o2.length -o1.length ); 
		  }
	}


	public final String DumpTiles() {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\nDumping tiles:\r\n");

		Iterator iter = this.m_tiles.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			SingleTile tmpSingleTile = (SingleTile) entry.getValue();
			sb.append(String.format("0x%02X:0x%02X:0x%02X, ", tmpSingleTile,
					tmpSingleTile.getNeighbor1(), tmpSingleTile.getNeighbor2()));
		}
		sb.append("\r\nDumping values:\r\n");

		for (int i = 0; i < this.m_tileValues.length; i++) {
			for (int j = 0; j < this.m_tileValues[i].length; j++) {
				sb.append(String.format("0x%02X, ", this.m_tileValues[i][j]));
			}
			sb.append("\r\n");
		}
		return sb.toString();
	}


	/**
	 * 重要函数！
	 * 根据上报的牌更新当前牌堆，邻居和姿势变化的牌才更新并上报算法 
	 * 邻居和姿势不变化也需要更新牌的时间戳，判断牌超时
	 * 本次加上了麻将的姿势字段
	 */
	public final void UpdateSingleTile(int tileValue, int firstNeighbor,
			int secondNeighbor, int direction, int zdirection, int iPower) {
		// 容错性校验
		if ((tileValue == firstNeighbor) || (tileValue == secondNeighbor)
				|| !this.TileValid(tileValue)) {
			return;
		}
		if (!(this.TileValid(firstNeighbor) || firstNeighbor == 0)) {
			return;
		}
		if (!(this.TileValid(secondNeighbor) || secondNeighbor == 0)) {
			return;
		}

		if (this.ExistsTileValue(tileValue)) {
			// 判断合理性
			if (this.ExistsTileValue(firstNeighbor)
					&& !this.GetTileByValue(firstNeighbor).HasNeighbor(0)
					&& !this.GetTileByValue(firstNeighbor).HasNeighbor(
							tileValue)) {
				firstNeighbor = 0;
			}
			if (this.ExistsTileValue(secondNeighbor)
					&& !this.GetTileByValue(secondNeighbor).HasNeighbor(0)
					&& !this.GetTileByValue(secondNeighbor).HasNeighbor(
							tileValue)) {
				secondNeighbor = 0;
			}
			
			this.SetTileChangeStatus(tileValue, false);
//			this.setChanged(false);
			// 邻居变化
			if (!this.GetTileByValue(tileValue).MatchNeighbors(firstNeighbor,
					secondNeighbor)) {
				 if ((firstNeighbor != 0)
				 && !this._TilesReported.contains(firstNeighbor)) {
					 return;
				 }
				 if ((secondNeighbor != 0)
				 && !this._TilesReported.contains(secondNeighbor)) {
					 return;
				 }
//				 //善变情况
//				 if(GetTileByValue(tileValue).MatchPreNeighbors(firstNeighbor,
//							secondNeighbor)){
//					 return;
//				 }
				 //update neighbor time
				this.PrepareChangeTile(tileValue, firstNeighbor,
						secondNeighbor, direction, zdirection,iPower);
				//update neighbors
				this.GetTileByValue(tileValue).setNeighbors(firstNeighbor,
						secondNeighbor);
				//update time
				this.GetTileByValue(tileValue).setNeighborTime(ClientMain.CurDateTime);
				this.SetTileChangeStatus(tileValue, true);
//				this.setChanged(true);
			} 
			// 姿势变化，根据新姿势更新。
			if (!this.GetTileByValue(tileValue).MatchDirection(direction)) {

				//处理姿势闪变的情况，新姿势是静止，跟前1/2比较，如果1运动2相同的静止，则取2的时间
//				if(direction != ConstantValue.MOVE && GetTileByValue(tileValue).getCurDirection() == ConstantValue.MOVE
//						&& direction == GetTileByValue(tileValue).getPreDirection()){
//					GetTileByValue(tileValue).setCurDirection(direction);
//					GetTileByValue(tileValue).setDirectionTime(GetTileByValue(tileValue).getPreDirectionTime());
//					this.SetTileChangeStatus(tileValue, true);
////					this.setChanged(true);
//				}else{
				
					this.GetTileByValue(tileValue).setPreDirection(
							GetTileByValue(tileValue).getCurDirection());
					this.GetTileByValue(tileValue).setCurDirection(direction);
					this.GetTileByValue(tileValue).setDirectionTime(ClientMain.CurDateTime);
					this.SetTileChangeStatus(tileValue, true);
//					this.setChanged(true);
//				}
			} 

			// Z时间根据新姿势更新。
			if (true/*GetTileByValue(tileValue).getCurZDirection() != zdirection*/) {
				//缓存和判断状态
				if(tileDirectionInfo.containsKey(tileValue)){
					ArrayList<Integer> direcList = tileDirectionInfo.get(tileValue);
					direcList.add(zdirection);
					tileDirectionInfo.put(tileValue, direcList);
				}else{
					ArrayList<Integer> direcList = new ArrayList<Integer>();
					direcList.add(zdirection);
					tileDirectionInfo.put(tileValue, direcList);
				}

				//看看是否需要更新Z时间，过滤 212,202跳变，过滤 11002丢数据情况
				if (zdirection == ConstantValue.DOWNSIDE) {
					ArrayList<Integer> direcList = tileDirectionInfo.get(tileValue);
					if(direcList != null && direcList.size() >= 3 
						&& direcList.get(direcList.size()-1) == ConstantValue.DOWNSIDE
						&& direcList.get(direcList.size()-2) == ConstantValue.UPSIDE	
						&& direcList.get(direcList.size()-3) == ConstantValue.DOWNSIDE){
						
						//GetTileByValue(tileValue).setZdirectionTime(ClientMain.CurDateTime);
					}else if(direcList != null && direcList.size() >= 3 
							&& direcList.get(direcList.size()-1) == ConstantValue.DOWNSIDE
							&& direcList.get(direcList.size()-2) == 0	
							&& direcList.get(direcList.size()-3) == ConstantValue.DOWNSIDE){
						
					}else if(direcList != null && direcList.size() >= 4 
							&& direcList.get(direcList.size()-1) == ConstantValue.DOWNSIDE
							&& direcList.get(direcList.size()-2) == 0	
							&& direcList.get(direcList.size()-3) == 0	
							&& direcList.get(direcList.size()-4) == ConstantValue.DOWNSIDE){
						
					}else if(direcList != null && direcList.size() >= 5 
							&& direcList.get(direcList.size()-1) == ConstantValue.DOWNSIDE
							&& direcList.get(direcList.size()-2) == 0	
							&& direcList.get(direcList.size()-3) == 0
							&& direcList.get(direcList.size()-4) == 0
							&& direcList.get(direcList.size()-5) == ConstantValue.DOWNSIDE){
						
					}else if(direcList != null && direcList.size() >= 3 
							&& direcList.get(direcList.size()-1) == ConstantValue.DOWNSIDE
							&& direcList.get(direcList.size()-2) == ConstantValue.DOWNSIDE){
						
					}else{
						GetTileByValue(tileValue).setZdirectionTime(ClientMain.CurDateTime);
					}
					
					//原始值赋值，防止没有up的情况
//					if(GetTileByValue(tileValue).getZDirectionTime().getMillis() == 0){
//						GetTileByValue(tileValue).setZdirectionTime(ClientMain.CurDateTime);
//					}
				}
				
//				StringBuffer tmpBuffer = new StringBuffer();
//				tmpBuffer.append(Integer.toHexString(tileValue));
//				tmpBuffer.append(",");
//				tmpBuffer.append(GetTileByValue(tileValue).getCurZDirection());
//				tmpBuffer.append(",");
//				tmpBuffer.append(zdirection);
//				tmpBuffer.append(",");
//				tmpBuffer.append(ClientMain.CurDateTime);
//				tmpBuffer.append("\n");
//				ClientMain.frameWin.refreshTest(tmpBuffer);
				
				GetTileByValue(tileValue).setCurZDirection(zdirection);
				
			}

			// 存在的牌，更新一下时间戳
			GetTileByValue(tileValue).setUpdateTime(ClientMain.CurDateTime);
			GetTileByValue(tileValue).SetTimeoutFlag(false);
			GetTileByValue(tileValue).setPower(iPower);

		} else {// 新增的牌，插入
			this._TilesReported.add(tileValue);
			this.InsertTile(tileValue, firstNeighbor, secondNeighbor, direction, zdirection);// 插入新数据
			this.SetTileChangeStatus(tileValue, true);// 牌变化
			this.GetTileByValue(tileValue).setPower(iPower);
//			this.GetTileByValue(tileValue).setUpdateTime(ClientMain.CurDateTime); //设置时间
//			this.GetTileByValue(tileValue).setNeighborTime(ClientMain.CurDateTime);
//			this.GetTileByValue(tileValue).setDirectionTime(ClientMain.CurDateTime);
//			this.GetTileByValue(tileValue).setZdirectionTime(ClientMain.CurDateTime);
//			this.setChanged(true); // 牌堆变化
		}

		// 根据数据重新生成数组
//		if (GetTileByValue(tileValue).getTileChanged()) {
//			RebuildValueArray();
//		}
	}
	
	/**
	 * 判断牌的超时不上报情况
	 * 输入：超时毫秒数，从配置文件读取。以600为一个周期
	 */
	public boolean CheckAndRemoveTimeout(int TimeOutMillSecond) {

		Instant now = new Instant();
		boolean flag = false;
		setChanged(false);

		Iterator<Map.Entry<Integer, SingleTile>> iter = m_tiles.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, SingleTile> entry = iter.next();
			SingleTile tmpSingleTile = (SingleTile) entry.getValue();
			Instant tileTimeInstant = tmpSingleTile.getUpdateTime();
			long diffMilSecond = now.getMillis() - tileTimeInstant.getMillis();

			if (diffMilSecond > TimeOutMillSecond) {
				this.IsolateTile(tmpSingleTile.getValue());
				this.SetTileChangeStatus(tmpSingleTile.getValue(), true);
				iter.remove();//删除，保持孤立
				LogHelper.log.info(tmpSingleTile.getValue()
						+ " 已超时掉线. (最后更新时间: "
						+ tileTimeInstant.toString() + " 检测时间: "
						+ now.toString() + " 间隔: " + diffMilSecond + "毫秒)");
				flag = true;
			}
			//检查是否变化
			if(tmpSingleTile.getTileChanged()){
				setChanged(true);
			}
		}
		
		if(flag){
	//		RebuildValueArray();
			LogHelper.log.info("======================================================");
		}
		RebuildValueArray();

		return flag;
	}

	/**
	 * 孤立没有超时上报的牌
	 * @param targetTile
	 */
	private void IsolateTile(int targetTile) {
		if (this.ExistsTileValue(targetTile)) {
			SingleTile target = this.GetTileByValue(targetTile);
			//将邻居都置位0
			if (this.ExistsTileValue(target.getNeighbor1())) {
				this.GetTileByValue(target.getNeighbor1()).ReplaceNeighbor(
						targetTile, 0);
			}
			if (this.ExistsTileValue(target.getNeighbor2())) {
				this.GetTileByValue(target.getNeighbor2()).ReplaceNeighbor(
						targetTile, 0);
			}
			target.setNeighbors(0, 0);
			//设置超时标记
			target.SetTimeoutFlag(true);
			//更新一下
			this.UpdateSingleTile(targetTile,
						this.GetTileByValue(targetTile).getNeighbor1(),
						this.GetTileByValue(targetTile).getNeighbor2(),
						this.GetTileByValue(targetTile).getCurDirection(),
						this.GetTileByValue(targetTile).getCurZDirection(),
						this.GetTileByValue(targetTile).getPower());
				
				
		}
	}
	
	
	/**
	 * 
	 * 更新牌数据前准备 注意是把邻居牌的邻居处理一下
	 */
	private void PrepareChangeTile(int tileValue, int newFirstNeighbor,
			int newSecondNeighbor, int newDirection, int newZDiretion, int iPower) {
		if (this.ExistsTileValue(tileValue)) {
			int old1=0,old2=0;
			SingleTile oldTile = this.GetTileByValue(tileValue);
			SingleTile newTile = new SingleTile(tileValue, newFirstNeighbor,
					newSecondNeighbor, newDirection,newZDiretion);

			if (this.ExistsTileValue(oldTile.getNeighbor1())) {
				this.GetTileByValue(oldTile.getNeighbor1()).ReplaceNeighbor(
						tileValue, 0);
				old1 = oldTile.getNeighbor1();
			}
			if (this.ExistsTileValue(oldTile.getNeighbor2())) {
				this.GetTileByValue(oldTile.getNeighbor2()).ReplaceNeighbor(
						tileValue, 0);
				old2 = oldTile.getNeighbor2();
			}

			if (this.ExistsTileValue(newTile.getNeighbor1())) {
				this.GetTileByValue(newTile.getNeighbor1()).ReplaceNeighbor(0,
						tileValue);
			}
			if (this.ExistsTileValue(newTile.getNeighbor2())) {
				this.GetTileByValue(newTile.getNeighbor2()).ReplaceNeighbor(0,
						tileValue);
			}
			//更新邻居的时间
			if(old1 != 0 && old1 != newFirstNeighbor &&	old1 != newSecondNeighbor){
				GetTileByValue(old1).setNeighborTime(ClientMain.CurDateTime);
			}
			if(old2 != 0 && old2 != newFirstNeighbor &&	old2 != newSecondNeighbor){
				GetTileByValue(old2).setNeighborTime(ClientMain.CurDateTime);
			}
			if(newFirstNeighbor != 0 && GetTileByValue(newFirstNeighbor) != null && old1 != newFirstNeighbor && old2 != newFirstNeighbor){
				GetTileByValue(newFirstNeighbor).setNeighborTime(ClientMain.CurDateTime);
			}
			if(newSecondNeighbor != 0 && GetTileByValue(newSecondNeighbor) != null && old1 != newSecondNeighbor && old2 != newSecondNeighbor){
				GetTileByValue(newSecondNeighbor).setNeighborTime(ClientMain.CurDateTime);
			}
			
		}
	}


	public final boolean GetTileChangeStatus(int tile) {
		return this.GetTileByValue(tile).tileChanged;
	}

	public final void SetTileChangeStatus(int tile, boolean status) {
		this.GetTileByValue(tile).tileChanged = status;
	}

	public final boolean GetTileTimeoutFlag(int TileValue) {
		SingleTile target = this.GetTileByValue(TileValue);
		if (target != null) {
			return target.getIsTimeout();
		} else {
			return false;
		}
	}

	// 获取牌邻居更新时间
	public final Instant GetTileNeighborUpdateTime(int TileValue) {
		SingleTile target = this.GetTileByValue(TileValue);
		if (target != null) {
			return target.getNeighborTime();
		} else {
			return null;
		}
	}

	// 获取牌姿势更新时间
	public final Instant GetTileDirectionUpdateTime(int TileValue) {
		SingleTile target = this.GetTileByValue(TileValue);
		if (target != null) {
			return target.getDirectionTime();
		} else {
			return null;
		}
	}
	
	// 获取牌姿势更新时间
		public final Instant GetTileZDirectionUpdateTime(int TileValue) {
			SingleTile target = this.GetTileByValue(TileValue);
			if (target != null) {
				return target.getZDirectionTime();
			} else {
				return null;
			}
		}

	// 获取牌更新时间
	public final Instant GetTileUpdateTime(int TileValue) {
		SingleTile target = this.GetTileByValue(TileValue);
		if (target != null) {
			return target.getUpdateTime();
		} else {
			return null;
		}
	}

	// 获取姿势
	public final int GetTileDirection(int tile) {
		return this.GetTileByValue(tile).getCurDirection();
	}

	// 获取Z姿势
	public final int GetTileZDirection(int tile) {
		return this.GetTileByValue(tile).getCurZDirection();
	}
	
	//get tile power
	public final int GetTilePower(int tile){
		return this.GetTileByValue(tile).getPower();
	}
		
	// 牌有效
	private boolean privateValid = true;

	public final boolean getValid() {
		return privateValid;
	}

	private void setValid(boolean value) {
		privateValid = value;
	}

	// 牌变化
	private boolean privateChanged = true;

	public final boolean getChanged() {
		return privateChanged;
	}

	private void setChanged(boolean value) {
		privateChanged = value;
	}

	// 用以标示当前选中的要进读取操作的行
	private int privateCurrentSelectedRow;

	private int getCurrentSelectedRow() {
		return privateCurrentSelectedRow;
	}

	private void setCurrentSelectedRow(int value) {
		privateCurrentSelectedRow = value;
	}

	/**
	 * #region Rows, Row 接口成员实现
	 */
	public Row getRowItem(int index) {
		if (index >= ((Rows) this).getRowCount()) {
			throw new IndexOutOfBoundsException("指定的行索引超过了牌堆实际总行数。指定的index = "
					+ (new Integer(index)).toString() + "。");
		}
		this.setCurrentSelectedRow(index);
		return this;
	}

	public int getRowCount() {
		if (this.m_tileValues == null) {
			return 0;
		} else {
			return this.m_tileValues.length;
		}
	}

	/**
	 * 
	 * 
	 */
	public int getTileItem(int index) {
		if (index >= ((Row) this).getTileCount()) {
			throw new IndexOutOfBoundsException("指定的行索引超过了当前行实际总牌数。指定的index = "
					+ (new Integer(index)).toString()
					+ "，CurrentSelectedRow = "
					+ (new Integer(this.getCurrentSelectedRow())).toString()
					+ "。");
		}
		return this.m_tileValues[this.getCurrentSelectedRow()][index];
	}

	/**
	 * 
	 */
	public int getTileCount() {
		if (this.m_tileValues[this.getCurrentSelectedRow()] == null) {
			return 0;
		} else {
			return this.m_tileValues[this.getCurrentSelectedRow()].length;
		}
	}

	/**
	 * 单张牌类
	 * 
	 * */
	public static class SingleTile {
		// 牌头尾还是中间
		public enum TileType {
			End, Regular;

			public int getValue() {
				return this.ordinal();
			}

			public static TileType forValue(int value) {
				return values()[value];
			}
		}

		public final TileType getType() {
			if ((this.curNeighbors[0] == 0) || (this.curNeighbors[1] == 0)) {
				return TileType.End;
			} else {
				return TileType.Regular;
			}
		}

		// 牌值
		private int tileValue;
		// 是否变化，不管邻居还是姿势变化
		private boolean tileChanged;

		// 邻居信息
		private int[] curNeighbors = new int[2];
		private int[] preNeighbors = new int[2];
		// 邻居变化时间戳
		private Instant neighborTime = null;

		// 姿势信息
		private int curDirection;
		// 姿势变化时间戳
		private Instant directionTime = null;
		

		// Z姿势信息
		private int curZDirection;
		// Z姿势变化时间戳
		private Instant zdirectionTime = null;

		// 前1姿势信息
		private int preDirection;

//		private Instant preDirectionTime = null;
		
		//power
		private int iPower= 0;

		// 最近变化时间戳
		private Instant privateUpdateTime = null;

		// 是否超时
		private boolean isTimeout;

		public final int getValue() {
			return tileValue;
		}

		private void setValue(int value) {
			tileValue = value;
		}

		public final boolean getTileChanged() {
			return tileChanged;
		}

		public final void setTileChanged(boolean value) {
			tileChanged = value;
		}

		public final int getNeighbor1() {
			return this.curNeighbors[0];
		}

		public final int getNeighbor2() {
			return this.curNeighbors[1];
		}

		public final int getPreviousNeighbor1() {
			return this.preNeighbors[0];
		}

		public final int getPreviousNeighbor2() {
			return this.preNeighbors[1];
		}

		public final boolean getIsTimeout() {
			return isTimeout;
		}

		private void setIsTimeout(boolean value) {
			isTimeout = value;
		}

		public Instant getNeighborTime() {
			return neighborTime;
		}

		public void setNeighborTime(Instant privateNeighborTime) {
			this.neighborTime = privateNeighborTime;
//			LogHelper.log.info(tileValue + ":" + neighborTime);
		}

		public Instant getDirectionTime() {
			return directionTime;
		}

		public void setDirectionTime(Instant privateDirectionTime) {
			this.directionTime = privateDirectionTime;
		}

		public Instant getUpdateTime() {
			return privateUpdateTime;
		}

		public void setUpdateTime(Instant privateUpdateTime) {
			this.privateUpdateTime = privateUpdateTime;
			setIsTimeout(false);
		}

		public int getCurDirection() {
			return curDirection;
		}

		public void setCurDirection(int privateDirection) {
			this.curDirection = privateDirection;
		}


		public int getPreDirection() {
			return preDirection;
		}

		public void setPreDirection(int preDirection) {
			this.preDirection = preDirection;
		}

//		public Instant getPreDirectionTime() {
//			return preDirectionTime;
//		}
//
//		public void setPreDirectionTime(Instant preDirectionTime) {
//			this.preDirectionTime = preDirectionTime;
//		}



//		public final SingleTile DeepCopy() {
//			SingleTile result = new SingleTile();
//			result.setValue(this.getValue());
//			result.curNeighbors = Ulti.DeepCopy(this.curNeighbors);
//			result.preNeighbors = Ulti.DeepCopy(this.preNeighbors);
//			result.setCurDirection(this.getCurDirection());
//			result.setCurZDirection(this.getCurZDirection());
//			result.setPreDirection(this.getPreDirection());
//			result.setTileChanged(this.getTileChanged());
//			result.setIsTimeout(this.getIsTimeout());
//			result.setNeighborTime(this.getNeighborTime());
//			result.setDirectionTime(this.getDirectionTime());
//			result.setZdirectionTime(this.getZDirectionTime());
//			result.setUpdateTime(this.getUpdateTime());
//			return result;
//		}

		private SingleTile() {
		}

		/**
		 * constructor 
		 * @param Value
		 * @param Neighbor1
		 * @param Neighbor2
		 * @param Direction
		 */
		public SingleTile(int Value, int Neighbor1, int Neighbor2, int Direction, int zDirection) {
			this.setValue(Value);
			this.setNeighbors(Neighbor1, Neighbor2);
			this.setCurDirection(Direction);
			this.setPreDirection(Direction);
			this.setCurZDirection(zDirection);
			this.setNeighborTime(ClientMain.CurDateTime);
			this.setDirectionTime(ClientMain.CurDateTime);
			this.setUpdateTime(ClientMain.CurDateTime);
//			this.setPreDirectionTime(ClientMain.CurDateTime);
			this.setZdirectionTime(new Instant(0));
			this.setPower(0);
		}

		public final boolean HasNeighbor(int neighbor) {
			if ((neighbor == this.curNeighbors[0])
					|| (neighbor == this.curNeighbors[1])) {
				return true;
			} else {
				return false;
			}
		}

		public final boolean HasPreviousNeighbor(int neighbor) {
			if ((neighbor == this.preNeighbors[0])
					|| (neighbor == this.preNeighbors[1])) {
				return true;
			} else {
				return false;
			}
		}

		public final void setNeighbors(int neighbor1, int neighbor2) {
			this.preNeighbors[0] = this.curNeighbors[0];
			this.preNeighbors[1] = this.curNeighbors[1];
			this.curNeighbors[0] = neighbor1;
			this.curNeighbors[1] = neighbor2;
			this._checkNeighbors();
		}


		public final int GetTheOtherNeighbor(int prevNeighbor) {
			if (this.curNeighbors[0] == prevNeighbor) {
				return this.curNeighbors[1];
			} else if (this.curNeighbors[1] == prevNeighbor) {
				return this.curNeighbors[0];
			} else {
				return -1;
			}
		}

		public final void ReplaceNeighbor(int oldNeighbor, int newNeighbor) {
			if (this.curNeighbors[0] == oldNeighbor) {
				this.preNeighbors[0] = this.curNeighbors[0];
				this.curNeighbors[0] = newNeighbor;
				this._checkNeighbors();
				return;
			}
			if (this.curNeighbors[1] == oldNeighbor) {
				this.preNeighbors[1] = this.curNeighbors[1];
				this.curNeighbors[1] = newNeighbor;
				this._checkNeighbors();
				return;
			}
		}

		public static boolean TilesAreConnecting(SingleTile changingTile,
				SingleTile unchangedTile) {
			if (changingTile.HasNeighbor(unchangedTile.getValue())
					&& !unchangedTile.HasNeighbor(changingTile.getValue())
					&& !changingTile.HasPreviousNeighbor(unchangedTile
							.getValue())) {
				return true;
			}
			return false;
		}

		public final boolean MatchNeighbors(int firstNeighbor,
				int secondNeighbor) {
			if ((this.curNeighbors[0] == firstNeighbor)
					&& (this.curNeighbors[1] == secondNeighbor)) {
				return true;
			}
			if ((this.curNeighbors[0] == secondNeighbor)
					&& (this.curNeighbors[1] == firstNeighbor)) {
				return true;
			}
			return false;
		}
		
		public final boolean MatchPreNeighbors(int firstNeighbor,
				int secondNeighbor) {
			if ((this.preNeighbors[0] == firstNeighbor)
					&& (this.preNeighbors[1] == secondNeighbor)) {
				return true;
			}
			if ((this.preNeighbors[0] == secondNeighbor)
					&& (this.preNeighbors[1] == firstNeighbor)) {
				return true;
			}
			return false;
		}

		public final boolean MatchDirection(int direcion) {
			if (this.curDirection == direcion) {
				return true;
			}
			return false;
		}

		
		public final void SetTimeoutFlag(boolean value) {
			this.setIsTimeout(value);
		}

		private void _checkNeighbors() {
			if (this.curNeighbors[0] == this.curNeighbors[1]) {
				this.curNeighbors[1] = 0;
			}
		}

		public int getCurZDirection() {
			return curZDirection;
		}

		public void setCurZDirection(int curZDirection) {
			this.curZDirection = curZDirection;
		}

		public Instant getZDirectionTime() {
			return zdirectionTime;
		}

		public void setZdirectionTime(Instant zdirectionTime) {
			this.zdirectionTime = zdirectionTime;
		}

		public int getPower() {
			return iPower;
		}

		public void setPower(int iPower) {
			this.iPower = iPower;
		}

		
	}

}