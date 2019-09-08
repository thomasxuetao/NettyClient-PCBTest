package com.thomas.data;

import org.joda.time.Instant;

/**
 * 汇总牌堆所有状态的数据结构，每次牌堆发生变化都发送一次该数据结构。
 */
public class I牌堆数据 {

	public I牌堆数据(TilesObject tiles) {
		Rows rows = (Rows) ((tiles instanceof Rows) ? tiles : null);
		this.set牌堆中牌的总行数(rows.getRowCount());
		this.set行数据(new I牌堆行数据[rows.getRowCount()]);
		this.setTileSetId(tiles.getiTileSetId());
		for (int i = 0; i < rows.getRowCount(); i++) {
			Row row = rows.getRowItem(i);
			this.get行数据()[i] = new I牌堆行数据();
			this.set牌堆中总牌数(this.get牌堆中总牌数() + row.getTileCount());
			this.get行数据()[i].set行中总牌数(row.getTileCount());
			this.get行数据()[i].set牌数据(new I单张牌数据[row.getTileCount()]);
			for (int j = 0; j < row.getTileCount(); j++) {
				this.get行数据()[i].get牌数据()[j] = new I单张牌数据();
				this.get行数据()[i].get牌数据()[j].setTilecodeid(row.getTileItem(j));
				this.get行数据()[i].get牌数据()[j].setNeighbor1(((j - 1) < 0) ? 0 : row
						.getTileItem(j - 1));
				this.get行数据()[i].get牌数据()[j].setNeighbor2(((j + 1) >= row
						.getTileCount()) ? 0 : row.getTileItem(j + 1));
				this.get行数据()[i].get牌数据()[j].setNeighborTime(tiles.GetTileNeighborUpdateTime(row
						.getTileItem(j)));
				this.get行数据()[i].get牌数据()[j].setDirectionTime(tiles.GetTileDirectionUpdateTime(row
						.getTileItem(j)));
				this.get行数据()[i].get牌数据()[j].setTilesetid(tiles.getiTileSetId());

				this.get行数据()[i].get牌数据()[j].setOffline(tiles.GetTileTimeoutFlag(row
						.getTileItem(j)));
				this.get行数据()[i].get牌数据()[j].setDirection(tiles.GetTileDirection(row
						.getTileItem(j)));
				this.get行数据()[i].get牌数据()[j].setZdirection(tiles.GetTileZDirection(row
						.getTileItem(j)));
				this.get行数据()[i].get牌数据()[j].setzDirectionTime(tiles.GetTileZDirectionUpdateTime(row
						.getTileItem(j)));
				this.get行数据()[i].get牌数据()[j].setPower(tiles.GetTilePower(row.getTileItem(j)));
				
			}
		}
	}

	private I牌堆行数据[] private行数据;

	public final I牌堆行数据[] get行数据() {
		return private行数据;
	}

	public final void set行数据(I牌堆行数据[] value) {
		private行数据 = value;
	}

	private int private牌堆中总牌数;

	public final int get牌堆中总牌数() {
		return private牌堆中总牌数;
	}

	public final void set牌堆中总牌数(int value) {
		private牌堆中总牌数 = value;
	}

	private int private牌堆中牌的总行数;

	public final int get牌堆中牌的总行数() {
		return private牌堆中牌的总行数;
	}

	public final void set牌堆中牌的总行数(int value) {
		private牌堆中牌的总行数 = value;
	}

	public final I牌堆行数据 getItem(int 索引) {
		return get行数据()[索引];
	}

	public final void setItem(int 索引, I牌堆行数据 value) {
		get行数据()[索引] = value;
	}

//	private TilesObject privateTileSet;
	private int tileSetId;

	private void setTileSetId(int id){
		this.tileSetId = id;
	}
	public int getTileSetId(){
		return tileSetId;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		
		I牌堆数据 target = (I牌堆数据) ((obj instanceof I牌堆数据) ? obj : null);

		if (target == null) {
			return false;
		}
		if (target.get牌堆中牌的总行数() != this.get牌堆中牌的总行数()) {
			return false;
		}
		if (target.get牌堆中总牌数() != this.get牌堆中总牌数()) {
			return false;
		}
		if ((target.get行数据() != null) && (this.get行数据() != null)) {
			if (target.get行数据().length != this.get行数据().length) {
				return false;
			}
			for (int i = 0; i < target.get行数据().length; i++) {
				if (!target.get行数据()[i].equals(this.get行数据()[i])) {
					return false;
				}
			}
		} else if (target.get行数据() != this.get行数据()) {
			return false;
		}

		return true;
	}
	
	/**
	 * 获取牌堆的方向
	 * @return
	 */
	public int getDirection(){
		int iDirection = 0;
		int up=0,down=0,stand=0,move=0;
		for(int i = 0; i < get牌堆中牌的总行数();i++){
			switch(private行数据[i].getDirection()){
			case ConstantValue.UPSIDE:
				up++;
				break;
			case ConstantValue.DOWNSIDE:
				down++;
				break;
			default:
				break;
			}
		}
		if(up == get牌堆中牌的总行数()){
			return ConstantValue.UPSIDE;
		}else if(down == get牌堆中牌的总行数()){
			return ConstantValue.DOWNSIDE;
		}else{
			return 0;
		}
	}
	
	/**
	 * 获取最晚的更新时间Z
	 * @return
	 */
	public Instant getDirectionUpdateTime(){
		Instant ret = private行数据[0].getDirectionUpdateTime();
		for(int i = 1; i < get牌堆中牌的总行数();i++){
			if(private行数据[i].getDirectionUpdateTime().isAfter(ret)){
				ret = private行数据[i].getDirectionUpdateTime();
			}
		}
		return ret;
	}
	
	/**
	 * 获取最晚的更新时间
	 * @return
	 */
	public Instant getNeighborUpdateTime(){
		Instant ret = private行数据[0].getNeighborUpdateTime();
		for(int i = 1; i < get牌堆中牌的总行数();i++){
			if(private行数据[i].getNeighborUpdateTime().isAfter(ret)){
				ret = private行数据[i].getNeighborUpdateTime();
			}
		}
		return ret;
	}
	
	
	public int getValidtilesNums(){
		int nums = 0;
		for (int i = 0; i < this.get牌堆中牌的总行数(); i++) {
			nums += get行数据()[i].getValidtilesNums();
		}
		return nums;
	}

	public final I单张牌数据 getSingleItem(int id) {
		for (int i = 0; i < this.get牌堆中牌的总行数(); i++) {
			for(int j = 0; j < getItem(i).get行中总牌数();j++){
				if(getItem(i).getItem(j).getTilecodeid() == id){
					return getItem(i).getItem(j);
				}
			}
		}
		return null;
	}

	public void resetZtime(){
		for (int i = 0; i < this.get牌堆中牌的总行数(); i++) {
			for(int j = 0; j < getItem(i).get行中总牌数();j++){
				getItem(i).getItem(j).setzDirectionTime(new Instant(0));
			}
		}
	}

	@Override
	public String toString() {
		StringBuffer retstr = new StringBuffer();
		for (int i = 0; i < this.get牌堆中牌的总行数(); i++) {
			retstr.append(this.get行数据()[i].toString());
		}

		return retstr.toString();
	}
}