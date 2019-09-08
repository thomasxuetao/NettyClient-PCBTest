package com.thomas.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.thomas.client.ClientMain;
import com.thomas.logic.Bridge;

public final class Ulti {

	public static String ToString(byte[] orig, String format) {
		String result = "";
		for (byte b : orig) {
			result += String.format(format, b);
		}
		return result;
	}

	public static byte[] Sub(byte[] orig, int startPos, int len) {
		if (orig.length < (startPos + len)) {
			return null;
		}
		byte[] result = new byte[len];
		System.arraycopy(orig, startPos, result, 0, len);
		return result;
	}

	public static String FormatToString(byte[] orig) {
		String result = "";
		for (byte by : orig) {
			result += String.format("%02X", by) + " ";
		}
		return result;
	}

	public static byte[] DeepCopy(byte[] orig) {
		if (orig == null) {
			return null;
		}
		byte[] result = new byte[orig.length];
		result = orig.clone();

		return result;
	}

	public static int[] DeepCopy(int[] orig) {
		if (orig == null) {
			return null;
		}
		int[] result = new int[orig.length];
		result = orig.clone();
		return result;
	}

	public static HashMap<Integer, TilesObject.SingleTile> DeepCopy(
			HashMap<Integer, TilesObject.SingleTile> orig) {
		HashMap<Integer, TilesObject.SingleTile> result = new HashMap<Integer, TilesObject.SingleTile>();

		Iterator iter = orig.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, TilesObject.SingleTile> entry = (Map.Entry<Integer, TilesObject.SingleTile>) iter
					.next();

			Integer key = entry.getKey();
			TilesObject.SingleTile value = entry.getValue();
			result.put(key, value);

		}

		return result;
	}

	public static int[][] DeepCopy(int[][] orig) {
		if (orig == null) {
			return null;
		}
		int[][] result = new int[orig.length][];
		for (int i = 0; i < orig.length; i++) {
			result[i] = orig[i].clone();
		}
		return result;
	}

	public static int TotalCount(int[][] orig) {
		if (orig == null) {
			return 0;
		}
		int result = 0;
		for (int[] row : orig) {
			result += row.length;
		}

		return result;
	}

	public static LinkedList<Integer> ReverList(LinkedList<Integer> orig) {
		LinkedList<Integer> result = new LinkedList<Integer>();
		Iterator<Integer> it = orig.iterator();
		while (it.hasNext()) {
			result.addFirst(it.next());
		}
		return result;
	}

	public static int[] IntegerListToIntArray(List<Integer> list) {
		int[] ret = new int[list.size()];
		for (int i = 0; i < ret.length; i++)
			ret[i] = list.get(i);
		return ret;
	}

	public static int[] IntegerArrayToIntArray(Integer[] orig) {
		int[] ret = new int[orig.length];
		for (int i = 0; i < ret.length; i++)
			ret[i] = orig[i].intValue();
		return ret;
	}

	public static Integer[] IntArrayToIntegerArray(int[] orig) {
		Integer[] ret = new Integer[orig.length];
		for (int i = 0; i < ret.length; i++)
			ret[i] = new Integer(orig[i]);
		return ret;
	}

	/**
	 * 把list数组转换为二维数组
	 * 
	 * @param orig
	 * @return
	 */
	public static int[][] IntegerListsToArrays(List<Integer[]> orig) {
		int[][] result = new int[orig.size()][];
		Iterator<Integer[]> it = orig.iterator();
		int i = 0;
		while (it.hasNext()) {
			Integer[] tmpIntegers = it.next();
			result[i] = DeepCopy(IntegerArrayToIntArray(tmpIntegers));
			i++;
		}

		return result;
	}

	public static boolean contains(int[] array, int v) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == v)
				return true;
		}
		return false;
	}

	static ByteBuffer _intShifter = ByteBuffer.allocate(
			Integer.SIZE / Byte.SIZE).order(ByteOrder.LITTLE_ENDIAN);
	static ByteBuffer _shortShifter = ByteBuffer.allocate(
			Short.SIZE / Byte.SIZE).order(ByteOrder.LITTLE_ENDIAN);
	static ByteBuffer _longShifter = ByteBuffer.allocate(Long.SIZE / Byte.SIZE)
			.order(ByteOrder.LITTLE_ENDIAN);

	public static byte[] shortTobyteArray(short value) {
		_shortShifter.clear();
		_shortShifter.putShort((short) value);
		return revertArray(_shortShifter.array());
	}

	public static byte[] intTobyteArray(int value) {
		_intShifter.clear();
		_intShifter.putInt((int) value);
		return revertArray(_intShifter.array());
	}

	public static short byteArrayToShort(byte[] data) {
		_shortShifter.clear();
		_shortShifter.put(data, 0, Short.SIZE / Byte.SIZE);
		_shortShifter.flip();
		return _shortShifter.getShort();
	}

	public static int byteArrayToInt(byte[] data) {
		_intShifter.clear();
		_intShifter.put(data, 0, Integer.SIZE / Byte.SIZE);
		_intShifter.flip();
		return _intShifter.getInt();
	}

	public static long byteArrayToLong(byte[] data) {
		_longShifter.clear();
		_longShifter.put(data, 0, Long.SIZE / Byte.SIZE);
		_longShifter.flip();
		return _longShifter.getLong();
	}

	public static byte[] revertArray(byte[] data) {
		int ilen = data.length;
		byte[] result = new byte[ilen];
		for (int i = 0; i < ilen; i++) {
			result[ilen - 1 - i] = data[i];
		}

		return result;
	}

	public static boolean isAscend(ArrayList<Integer> a, int n) {
		if (n == 1) {
			return true;
		}
		if (n == 2) {
			return a.get(n - 1) >= a.get(n - 2);
		}
		return isAscend(a, n - 1) && (a.get(n - 1) >= a.get(n - 2));
	}

	/**
	 * 获取牌堆中所有牌行中的牌个数
	 * 
	 * @param data
	 * @return
	 */
	public static int[] getLineCountOfTiles(I牌堆数据 data) {
		int[] ret = new int[ClientMain.LineTilesNums*2];
		for (int i = 0; i < data.get牌堆中牌的总行数(); i++) {
			ret[data.getItem(i).get行中总牌数()]++;
		}
		return ret;
	}

	/**
	 * 获取所有牌行中牌数为num的牌行个数
	 * 
	 * @param data
	 * @param num
	 * @return
	 */
	public static int getLineCountOfSome(I牌堆数据 data, int num) {
		int[] ret = getLineCountOfTiles(data);
		return ret[num];
	}

	/**
	 * 根据牌行获取int数组，用于牌序查找等
	 * 
	 * @param line
	 * @return
	 */
	public static int[] tileLineTointArray(I牌堆行数据 line) {
		int[] ret = new int[line.get行中总牌数()];
		for (int i = 0; i < line.get行中总牌数(); i++) {
			ret[i] = line.getItem(i).getTilecodeid();
		}
		return ret;
	}
	
	/**
	 * 堆数据转换为行list
	 * @param data
	 * @return
	 */
	public static ArrayList<I牌堆行数据> convertTilesToArray(I牌堆数据 data){
		ArrayList<I牌堆行数据> retArrayList = new ArrayList<>();
		for(int i = 0; i < data.get牌堆中牌的总行数(); i++){
			retArrayList.add(data.getItem(i));
		}
		
		return retArrayList;
	}

	/**
	 * 获取牌姿势
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static int getTileDirection(int x, int y, int z) {
		int idirection = 0;
		int MAX1 = 35;
		int MIN = 15;
		if ((z >= MAX1) && Math.abs(x) < MIN && Math.abs(y) < MIN) {
			idirection = ConstantValue.DOWNSIDE;
		} else if ((z <= 0-MAX1) && Math.abs(x) < MIN && Math.abs(y) < MIN) {
			idirection = ConstantValue.UPSIDE;
		} else if (Math.abs(z) < MIN
				&& ((Math.abs(x) >= MAX1 ) || (Math.abs(y) >= MAX1 ))) {
			idirection = ConstantValue.STAND1;
		} else {
			idirection = ConstantValue.MOVE;
		}
		
		return idirection;
	}

	/**
	 * 获取灵敏的Z方向
	 * @param z
	 * @return
	 */
	public static int getZDirection(int z) {
		int idirection = 0;
		
		if (z >= 50 ){
			idirection = ConstantValue.DOWNSIDE;
		}else if(z <= -30){
			idirection = ConstantValue.UPSIDE;
		}else{
			
		}
		return idirection;
	}

	/**
	 * 获取牌堆功率，2行取功率最小的30张牌，算自家牌(no use)
	 * @param data
	 * @return
	 */
	public static int getTilesPower(ArrayList<I牌堆行数据> data){
		int ipower = 0;
		ArrayList<Integer> powerArrayList = new ArrayList<>();
		for(I牌堆行数据 tmp : data){
			for(int i = 0; i < tmp.get行中总牌数(); i++){
				powerArrayList.add(tmp.getItem(i).getPower());
			}
		}
		
		Collections.sort(powerArrayList);
		if(powerArrayList.size() >= ClientMain.LineTilesNums*2 - 4){
			for(int i = 0; i < ClientMain.LineTilesNums*2 - 4; i++){
				ipower += powerArrayList.get(i);
			}
		}
		
		return ipower;
	}
	
	/**
	 * 获取牌行功率，下层牌，去掉最大的num个，返回功率(no use)
	 * @param data
	 * @return
	 */
	public static int getTilesPower(I牌堆行数据 data,int num){
		int ipower = 0;
		ArrayList<Integer> powerArrayList = new ArrayList<>();
		for(int i = 0; i < data.get行中总牌数(); i++){
			powerArrayList.add(data.getItem(i).getPower());
		}
		
		Collections.sort(powerArrayList);
		if(powerArrayList.size() == ClientMain.LineTilesNums){
			for(int i = 0; i < ClientMain.LineTilesNums-num; i++){
				ipower += powerArrayList.get(i);
			}
		}
		return ipower;
	}
	

	
	/**
	 * 加密函数
	 */
	public static final String KEY_SHA = "SHA";
	public static String getSHAResult(String inputStr) {
		BigInteger sha = null;
		byte[] inputData = inputStr.getBytes();
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(KEY_SHA);
			messageDigest.update(inputData);
			sha = new BigInteger(messageDigest.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sha.toString(32);
	}
	
	/**
	 * 将牌行变成按顺序取的list
	 * 按照抓牌的顺序来
	 * 正序：从前往后，从每个数组尾部往前抓
	 * 反序：从后往前，从每个数组头部往后抓
	 */
	public static ArrayList<I单张牌数据> tilesLineArrayToArrayList(ArrayList<I牌堆行数据> array,boolean addnull,boolean order){
		ArrayList<I单张牌数据> retList = new ArrayList<>();
		if(order){
			for(int i = 0; i < array.size(); i = i+2){
				I牌堆行数据 up = array.get(i);
				I牌堆行数据 down= array.get(i+1);
				for(int j = up.get行中总牌数()-1; j >= 0;j--){
					retList.add(up.getItem(j));
					retList.add(down.getItem(j));
				}
				if(addnull){
					retList.add(null);
				}
			}			
		}else{
			for(int i = 0; i < array.size(); i = i+2){
				I牌堆行数据 up = array.get(i);
				I牌堆行数据 down= array.get(i+1);
				for(int j = 0; j < up.get行中总牌数();j++){
					retList.add(up.getItem(j));
					retList.add(down.getItem(j));
				}
				if(addnull){
					retList.add(null);
				}
			}

		}
		return retList;
	}
	
	/**
	 * 行数据转换为list
	 * @param line
	 * @return
	 */
	public static ArrayList<I单张牌数据> tilesLineToArrayList(I牌堆行数据 line){
		ArrayList<I单张牌数据> retList = new ArrayList<>();
		for(int i = 0; i < line.get行中总牌数(); i++){
			
				retList.add(line.getItem(i));
			
		}
		return retList;
	}

	/**
	 * 判断两个牌堆是否邻居都一样，用于排除运动二导致的牌堆变化
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isTwoTilesSameNeighbors(I牌堆数据 a,I牌堆数据 b){
		for(int i = 0; i < a.get牌堆中牌的总行数();i++){
			for(int j = 0; j < a.getItem(i).get行中总牌数();j++){
				I单张牌数据 atmp = a.getItem(i).getItem(j);
				I单张牌数据 btmp = b.getSingleItem(atmp.getTilecodeid());
				if((atmp.getNeighbor1() == btmp.getNeighbor1() && atmp.getNeighbor2() == btmp.getNeighbor2())
						|| (atmp.getNeighbor1() == btmp.getNeighbor2() && atmp.getNeighbor2() == btmp.getNeighbor1())){
					//....
				}else{
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 根据牌获取排行
	 * @param data
	 * @param tile
	 * @return
	 */
	public static I牌堆行数据 getLineByTile(I牌堆数据 data,I单张牌数据 tile){
		for(int i = 0; i < data.get牌堆中牌的总行数(); i++){
			for(int j = 0; j < data.getItem(i).get行中总牌数();j++){
				if(data.getItem(i).getItem(j).equals(tile)){
					return data.getItem(i);
				}
			}
		}
		return null;
	}
	

	
	/**
	 * 判断某个牌是否跟某家牌成邻居
	 * @param a
	 * @param sets
	 * @return
	 */
	public static boolean isTileNeighborsTilesets(I牌堆数据 data, I单张牌数据 a, LinkedList<I单张牌数据> sets){
		for(I单张牌数据 tmp : sets){
			if(data.getSingleItem(tmp.getTilecodeid()).getNeighbor1() == a.getTilecodeid() 
					|| data.getSingleItem(tmp.getTilecodeid()).getNeighbor2() == a.getTilecodeid()){
				return true;
			}
		}
		return false;
	}
	
	
	
	/**
	 * 获取坏牌数据
	 * @param reportedTiles
	 * @return
	 */
	public static ArrayList<I单张牌数据> getBadTiles(I牌堆数据 reportedTiles){
		int tilesetid = reportedTiles.getTileSetId();
		if(tilesetid == Bridge.logicA.getTileSetid()){
			if(Bridge.logicA.getBadTilesList().size() > 0){
				return Bridge.logicA.getBadTilesList();
			}
		}else{
			if(Bridge.logicB.getBadTilesList().size() > 0){
				return Bridge.logicB.getBadTilesList();
			}
		}
		return null;
		
	}

	public static LinkedList<Integer> tileListToIntegerList(LinkedList<I单张牌数据> data){
		 LinkedList<Integer> retIntegers = new LinkedList<>();
		 for(I单张牌数据 tmptile : data){
			 if(tmptile != null){
				 retIntegers.add(tmptile.getTilecodeid());
			 }
		 }
		 return retIntegers;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> deepCopyList(List<T> src) {
		List<T> dest = null;
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(byteOut);
			out.writeObject(src);
			ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
			ObjectInputStream in = new ObjectInputStream(byteIn);
			dest = (List<T>) in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return dest;
	}
	
	public static ArrayList<I牌堆行数据> cloneArrayList(ArrayList<I牌堆行数据> old){
		ArrayList<I牌堆行数据> retArrayList = new ArrayList<>();
		for(I牌堆行数据 line : old){
			retArrayList.add(line.clone());
		}
		return retArrayList;
	}
	
	public static int getRandomInt(){
		int retbyte = 0;
		Random r = new Random();
		retbyte = r.nextInt();
		
		return retbyte;
	}
	
	/**
	 * 获取名字相同的牌列表数据
	 * @param data
	 * @param names
	 * @return
	 */
	public static ArrayList<I单张牌数据> getSamenameTiles(I牌堆数据 data, ArrayList<String> names){
		ArrayList<I单张牌数据> retlist = new ArrayList<>();
		for(int i = 0; i < data.get牌堆中牌的总行数(); i++){
			for(int j = 0; j < data.getItem(i).get行中总牌数();j++){
				if(names.contains(ClientMain.getTileNameById2(data.getItem(i).getItem(j).getTilecodeid()))){
					retlist.add(data.getItem(i).getItem(j));
				}
			}
		}
		
		return retlist;
	}
	
	/**
	 * 加密
	 * @param data 待加密数据
	 * @param key  秘钥
	 * @return 
	 */
	public static byte[] encrypt(byte[] data, byte key) {
        if (data == null || data.length == 0) {
            return data;
        }

        byte[] result = new byte[data.length];

        // 使用密钥字节数组循环加密或解密
        for (int i = 0; i < data.length; i++) {
            // 数据与密钥异或
            result[i] = (byte) (data[i] ^ key);
        }

        return result;
    }

}