package com.thomas.data;

import java.io.Serializable;
import java.util.ArrayList;

import org.joda.time.Instant;

/**
 * 代表每行牌数据的数据结构。
 */
public class I牌堆行数据  implements Serializable{

	/**
	 * 
	 */
	private I单张牌数据[] private牌数据;
	private int private行中总牌数 = 0;
	
	public I牌堆行数据(){
		private行中总牌数 = 0;
		private牌数据 = new I单张牌数据[0];
	}

	public final I单张牌数据[] get牌数据() {
		return private牌数据;
	}

	public final void set牌数据(I单张牌数据[] value) {
		private牌数据 = value;
	}


	public final int get行中总牌数() {
		return private行中总牌数;
	}

	public final void set行中总牌数(int value) {
		private行中总牌数 = value;
	}

	public final I单张牌数据 getItem(int 索引) {
		return get牌数据()[索引];
	}

	public final void setItem(int 索引, I单张牌数据 value) {
		get牌数据()[索引] = value;
	}
	
	public final ArrayList<Integer> getTilecodeArrayList(){
		ArrayList<Integer> tilecodelist = new ArrayList<Integer>();
		for(int i = 0; i < private牌数据.length; i++){
			tilecodelist.add(private牌数据[i].getTilecodeid());
		}
		return tilecodelist;
	}
	
	public I牌堆行数据 connetTileLines(I牌堆行数据 a){		
		I牌堆行数据 ret = new I牌堆行数据();
		
		private行中总牌数 = a.get行中总牌数()+get行中总牌数();
		I单张牌数据[] tmp = new I单张牌数据[private行中总牌数];
		for(int i = 0; i < private牌数据.length; i++){
			tmp[i] = private牌数据[i];
		}
		for(int i = 0; i < a.get行中总牌数(); i++){
			tmp[i + private牌数据.length] = a.getItem(i);
		}
		
		ret.set行中总牌数(private行中总牌数);
		ret.set牌数据(tmp);

		return ret;
	}
	
	public I牌堆行数据 connetOneTile(I单张牌数据 a){		
		I牌堆行数据 ret = new I牌堆行数据();
		
		private行中总牌数 = 1 + get行中总牌数();
		I单张牌数据[] tmp = new I单张牌数据[private行中总牌数];
		for(int i = 0; i < private牌数据.length; i++){
			tmp[i] = private牌数据[i];
		}
		
		tmp[private牌数据.length] = a;
		
		ret.set行中总牌数(private行中总牌数);
		ret.set牌数据(tmp);

		return ret;
	}


	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		I牌堆行数据 target = (I牌堆行数据) ((obj instanceof I牌堆行数据) ? obj : null);

		if (target.get行中总牌数() != this.get行中总牌数()) {
			return false;
		}
		if ((target.get牌数据() != null) && (this.get牌数据() != null)) {
			if (target.get牌数据().length != this.get牌数据().length) {
				return false;
			}
			for (int i = 0; i < this.get牌数据().length; i++) {
				if (target.getItem(i).equals(this.getItem(i)) || target.getItem(i).equals(this.getItem(get牌数据().length-1-i))) {
				
				}else{
					return false;
				}
			}
		} else if (target.get牌数据() != this.get牌数据()) {
			return false;
		}

		return true;
	}


	/**
	 * 判断是否以某行开始
	 * @param prefix
	 * @param toffset
	 * @return
	 */
	public boolean startWith(I牌堆行数据 prefix,int toffset){
		I单张牌数据[] ta = private牌数据;
		int to= toffset;
		I单张牌数据[] pa = prefix.get牌数据();
		int po = 0;
		int pc = prefix.get行中总牌数();
		if((toffset < 0) || (toffset > get行中总牌数() - pc)){
			return false;
		}
		while(--pc >= 0){
			if(!ta[to++].equals(pa[po++])){
				return false;
			}
		}
		
		return true;
	}
	
	public boolean startWith(I牌堆行数据 prefix){
		return startWith(prefix, 0);
	}
	
	/**
	 * 获取牌行的方向
	 * @return
	 */
	public int getDirection(){
		int iDirection = 0;
		int up=0,down=0,stand=0,move=0;
		for(int i = 0; i < get行中总牌数();i++){
			switch(private牌数据[i].getDirection()){
			case ConstantValue.UPSIDE:
				up++;
				break;
			case ConstantValue.DOWNSIDE:
				down++;
				break;
			case ConstantValue.STAND1:
				stand++;
				break;
			default:
				break;
			}
		}
		if(up == get行中总牌数()){
			return ConstantValue.UPSIDE;
		}else if(down == get行中总牌数()){
			return ConstantValue.DOWNSIDE;
		}else if(stand == get行中总牌数()){
			return ConstantValue.STAND1;
		}else{
			return 0;
		}
	}
	
	/**
	 * 获取最晚的更新时间
	 * @return
	 */
	public Instant getDirectionUpdateTime(){
		Instant ret = private牌数据[0].getzDirectionTime();
		for(int i = 1; i < get行中总牌数();i++){
			if(private牌数据[i].getzDirectionTime().isAfter(ret)){
				ret = private牌数据[i].getzDirectionTime();
			}
		}
		return ret;
	}
	
	/**
	 * 获取最晚的更新时间
	 * @return
	 */
	public Instant getNeighborUpdateTime(){
		Instant ret = private牌数据[0].getNeighborTime();
		for(int i = 1; i < get行中总牌数();i++){
			if(private牌数据[i].getNeighborTime().isAfter(ret)){
				ret = private牌数据[i].getNeighborTime();
			}
		}
		return ret;
	}
	
	
	/**
	 *  revert the line
	 */
	public I牌堆行数据 revert(){
		I牌堆行数据 ret = new I牌堆行数据();
		int ilen = get行中总牌数();
		I单张牌数据[] result = new I单张牌数据[ilen];
		for(int i = 0; i < ilen; i++){
			result[ilen-1-i] = private牌数据[i].clone();
		}
		
		ret.set行中总牌数(ilen);
		ret.set牌数据(result);
		return ret;
	}
	
	@Override
	public I牌堆行数据 clone(){
		I牌堆行数据 ret = new I牌堆行数据();
		int ilen = get行中总牌数();
		I单张牌数据[] result = new I单张牌数据[ilen];
		for(int i = 0; i < ilen; i++){
			result[i] = private牌数据[i].clone();
		}
		ret.set行中总牌数(ilen);
		ret.set牌数据(result);
	
		return ret;
	}
	
	/**
	 * 某个牌行的牌包括另一个牌行，不关心顺序
	 * @param prefix
	 * @return
	 */
	public boolean contains(I牌堆行数据 prefix){
		if(prefix == null){
			return false;
		}
		I单张牌数据[] ta = private牌数据;
		I单张牌数据[] pa = prefix.get牌数据();
		if(get行中总牌数() < prefix.get行中总牌数()){
			return false;
		}
		int num = 0;
		for(int i = 0; i < pa.length; i++){
			for(int j = 0; j < ta.length; j++){
				if(ta[j].equals(pa[i])){
					num++;
					break;
				}
			}
		}
		if(num == pa.length){
			return true;
		}
		
		return false;
	}
	
	public int getValidtilesNums(){
		int nums = 0;
		for(int i = 0; i < get行中总牌数();i++){
			if(!get牌数据()[i].getOffline()){
				nums++;
			}
		}
		return nums;
	}
		
	
	@Override
	public String toString() {
		StringBuffer retstr = new StringBuffer();
		for (int i = 0; i < get行中总牌数(); i++) {
			retstr.append(this.get牌数据()[i].toString());
		}
		return retstr.toString();
	}
	
}