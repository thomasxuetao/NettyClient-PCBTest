package com.thomas.data;

import java.io.Serializable;

import org.joda.time.Instant;

public class I单张牌数据 implements Serializable{
	
	private int tilesetid;//牌副码
	private int tilecodeid;	//牌码
	private int neighbor1;
	private int neighbor2;
	private Instant neighborTime;
	private Instant directionTime;
	private int direction; //4个姿势:向上，向下，直立和运动
	private Instant zDirectionTime;
	private int zdirection;
	private int iPower; //功率
	private boolean offline;//是否超时，true代表超时，false代表没超时
	private int badType; //0，好牌；1 邻居坏；2 姿势坏； 3 都坏; 4 不上报; 5 杠走的牌；6 财神牌


	public I单张牌数据(){
		tilecodeid = 0;
		tilesetid = 0;
		neighbor1 = 0;
		neighbor2 = 0;
		neighborTime = new Instant();
		directionTime = new Instant();
		zDirectionTime = new Instant();
		zdirection = 0;
		offline = false;
		direction = 0;
		iPower = 0;
		setBadType(0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;				
		}
		return (this.tilecodeid == ((I单张牌数据) obj).tilecodeid && 
				this.tilesetid == ((I单张牌数据) obj).tilesetid);
	}
	
	@Override
	public I单张牌数据 clone(){
		I单张牌数据 ret = new I单张牌数据();
		ret.setTilecodeid(getTilecodeid());
		ret.setTilesetid(getTilesetid());
		ret.setNeighbor1(getNeighbor1());
		ret.setNeighbor2(getNeighbor2());
		ret.setNeighborTime(getNeighborTime());
		ret.setDirection(getDirection());
		ret.setDirectionTime(getDirectionTime());
		ret.setZdirection(getZdirection());
		ret.setzDirectionTime(getzDirectionTime());
		ret.setOffline(getOffline());
		ret.setPower(getPower());
		ret.setBadType(getBadType());
		
		return ret;
	}
	
	public int getTilesetid() {
		return tilesetid;
	}

	public void setTilesetid(int tilesetid) {
		this.tilesetid = tilesetid;
	}

	public int getTilecodeid() {
		return tilecodeid;
	}

	public void setTilecodeid(int tilecodeid) {
		this.tilecodeid = tilecodeid;
	}

	public int getNeighbor1() {
		return neighbor1;
	}

	public void setNeighbor1(int neighbor1) {
		this.neighbor1 = neighbor1;
	}

	public int getNeighbor2() {
		return neighbor2;
	}

	public void setNeighbor2(int neighbor2) {
		this.neighbor2 = neighbor2;
	}

	public Instant getNeighborTime() {
		return neighborTime;
	}

	public void setNeighborTime(Instant neighborTime) {
		this.neighborTime = neighborTime;
	}

	public Instant getDirectionTime() {
		return directionTime;
	}

	public void setDirectionTime(Instant directionTime) {
		this.directionTime = directionTime;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public boolean getOffline() {
		return offline;
	}

	public void setOffline(boolean offline) {
		this.offline = offline;
	}


	@Override
	public String toString() {
		StringBuffer strtmp = new StringBuffer();
//		strtmp.append(tilesetid);
//		strtmp.append("/");
		strtmp.append(Integer.toHexString(tilecodeid));
		strtmp.append("/");
//		strtmp.append(neighbor1);
//		strtmp.append("/");
//		strtmp.append(neighbor2);
//		strtmp.append("/");
//		strtmp.append(direction);
//		strtmp.append("/");
//		strtmp.append(iPower);
//		strtmp.append("##");
//		strtmp.append(zDirectionTime);
//		strtmp.append(neighborTime);

		return strtmp.toString();
		
	
	}

	public Instant getzDirectionTime() {
		return zDirectionTime;
	}

	public void setzDirectionTime(Instant zTime) {
		this.zDirectionTime = zTime;
	}

	public int getZdirection() {
		return zdirection;
	}

	public void setZdirection(int zdirection) {
		this.zdirection = zdirection;
	}

	public int getPower() {
		return iPower;
	}
	

	public void setPower(int iPower) {
		this.iPower = iPower;
	}

	public int getBadType() {
		return badType;
	}

	public void setBadType(int badType) {
		this.badType = badType;
	}
	
}

