package com.thomas.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.joda.time.Instant;
import com.thomas.data.ConstantValue;
import com.thomas.data.TileSec;
import com.thomas.logic.Bridge;

public class ClientMain {
	public static ClientLogin loginWin = new ClientLogin();
	public static ExcelReader tileInfo = new ExcelReader();
	public static ClientFrame frameWin;
	public static int offlinetimes = 3000;
	public static Instant CurDateTime = new Instant();
	public static TileSec tileSecurity = null;
	/**
	 * 配置文件
	 */
	public static Map<String, String> configMap = ConfigDialog.getConfigXml();
	
	public static int TotalTileNums = Integer.parseInt(configMap.get("选牌"));
	public static String TiaopaiType = configMap.get("挑牌");
	public static ArrayList<String> huapaiArrayList = new ArrayList<>();
	public static int PlayerTilesNums = Integer.parseInt(configMap.get("玩家牌数"));
	public static int LineTilesNums = TotalTileNums/8;
	public static boolean SameLineNums = (TotalTileNums%8 == 0) ? true : false;

	//财神处理
	public static int caishen = Integer.parseInt(configMap.get("财神"));
	public static int caishenOrder = 0;
	public static int caishenShaizi = 0;
	public static int caishenFixed = 0;
	public static int caishenLast = 0;
	public static ArrayList<String> caishenArrayList = new ArrayList<>();
	public static TestDialog testDialog;
	
	static{
		
		updateConfigPara();
		
		try {
			tileSecurity = new TileSec();
		} catch (Exception e) {
			
		}
		
	}
	
	public static void updateConfigPara(){
		TotalTileNums = Integer.parseInt(configMap.get("选牌"));
		TiaopaiType = configMap.get("挑牌");
		PlayerTilesNums = Integer.parseInt(configMap.get("玩家牌数"));
		LineTilesNums = TotalTileNums/8;
		SameLineNums = (TotalTileNums%8 == 0) ? true : false;
		ArrayList<String> Huapais = new ArrayList<>(Arrays.asList( configMap.get("自选花牌").split(",")));
		huapaiArrayList.clear();
		for(String tilename : Huapais){
			huapaiArrayList.add(getTileNameByPicName(tilename));
		}
		caishen = Integer.parseInt(configMap.get("财神"));
		if(caishen == 1){
			caishenOrder = 0;
			caishenShaizi = 0;
			caishenFixed = 0;
			caishenLast = 0;
			if(Integer.parseInt(configMap.get("顺摸财神")) == 1){
				caishenOrder = 1;
			}else if(Integer.parseInt(configMap.get("色子定财神")) == 1){
				caishenShaizi = 1;
			}else if(Integer.parseInt(configMap.get("固定财神")) == 1){
				caishenFixed = 1;
				ArrayList<String> caishens = new ArrayList<>(Arrays.asList( configMap.get("自选财神").split(",")));
				caishenArrayList.clear();
				for(String tilename : caishens){
					caishenArrayList.add(getTileNameByPicName(tilename));
				}
				
			}else if(Integer.parseInt(configMap.get("后财神")) == 1){
				caishenLast = 1;
			}
		}
		
	}



	public static void initFrame() {
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(ClientFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(ClientFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(ClientFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(ClientFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
//		frameWin = new ClientFrame();
		testDialog = new TestDialog();
		testDialog.setVisible(true);
	}
	
	public static Instant updateTime(){
		CurDateTime = Instant.now();
		return CurDateTime;
	}

	public ClientMain() {

	}

	public static void main(String[] args) throws Exception {
		// start the login Frame
		if (ConstantValue.USERTYPE == ConstantValue.PLAYER) {
			new Thread(Bridge.runnerA).start();
			new Thread(Bridge.runnerB).start();
		}

//		Properties prop = new Properties();
//		try {
//			InputStream in = new BufferedInputStream(new FileInputStream(
//					"cfg.txt"));
//			prop.load(in); // /加载属性列表
//			Iterator<String> it = prop.stringPropertyNames().iterator();
//			while (it.hasNext()) {
//				String key = it.next();
//				if(key.equals("offlinetimes")){
//					offlinetimes = Integer.parseInt(prop.getProperty(key));
//				}
//				System.out.println(key + ":" + prop.getProperty(key));
//				
//			}
//			in.close();
//
//		} catch (Exception e) {
//			System.out.println(e);
//		}
		
	}
	
	/**
	 * 根据牌ID获取界面名称"饼11"
	 */
	public static String getTileNameById(int id){
		for (Map<String, String> map : tileInfo.list) {
			for (Entry<String, String> entry : map.entrySet()) {
				if(entry.getKey().equals("编码") && Integer.parseInt(entry.getValue(),16)==id){//find it!
					return map.get("界面名");
				}
			}
		}
		return null;
	}
	
	/**
	 * 根据id获取图片名称
	 * @param id
	 * @return
	 */
	public static String getTileNameById2(int id){
		String strName = ClientMain.getTileNameById(id);//饼1
		int k = 0;
		if(strName != null){
			for(k = 0; k < ConstantValue.cardPicStrings2.length;k++){
				if(strName.equals(ConstantValue.cardPicStrings2[k])){
					break;
				}
			}
		}
		
		return ConstantValue.cardPicStrings[k];
	}
	
	public static String getTileNameByPicName(String picName) {
		if (picName == null) {
			return "";
		}
		for (int i = 0; i < ClientPlayerPanel.cardPics.length; i++) {
			if (String.valueOf(ClientPlayerPanel.cardPics[i]).equals(picName)) {
				return ConstantValue.cardPicStrings[i];
			}
		}
		return "";
	}
	
	public static String getPicNameByTileName(String name) {
		if (name == null) {
			return "";
		}
		for (int i = 0; i < ConstantValue.cardPicStrings.length; i++) {
			if (ConstantValue.cardPicStrings[i].equals(name)) {
				return String.valueOf(ClientPlayerPanel.cardPics[i]);
			}
		}
		return "";
	}
	
	public static ArrayList<String> getCaiShen(String name) {
		ArrayList<String> list = new ArrayList<>();
		if (name == null) {
			return list;
		}
		int index1 = -1;// 牌的图片名称索引
		int index2 = -1;// 牌的图片名称索引
		for (int i = 0; i < ConstantValue.cardPicStrings.length; i++) {
			if (ConstantValue.cardPicStrings[i].equals(name)) {
				index1 = ClientPlayerPanel.cardPics[i];
				break;
			}
		}
		if (index1 == -1) {
			return list;
		}
		String 升级财神 = configMap.get("升级财神");
		String 双财神 = configMap.get("双财神");
		String 上下 = configMap.get("上下");
		String 上上下下 = configMap.get("上上下下");
		if (升级财神 != null && 升级财神.equals("1") && 上下 != null) {
			if (上下.equals("上")) {
				if (index1 == 9) {
					index1 = 1;
				} else if (index1 == 18) {
					index1 = 10;
				} else if (index1 == 27) {
					index1 = 19;
				} else if (index1 == 31) {
					index1 = 28;
				} else if (index1 == 34) {
					index1 = 32;
				} else {
					index1++;
				}
			} else if (上下.equals("下")) {
				if (index1 == 1) {
					index1 = 9;
				} else if (index1 == 10) {
					index1 = 18;
				} else if (index1 == 19) {
					index1 = 27;
				} else if (index1 == 28) {
					index1 = 31;
				} else if (index1 == 32) {
					index1 = 34;
				} else {
					index1--;
				}
			}
			list.add(getTileNameByPicName(String.valueOf(index1)));
		} else if (双财神 != null && 双财神.equals("1") && 上上下下 != null) {
			if (上上下下.equals("下下")) {
				if (index1 == 1) {
					index1 = 8;
					index2 = 9;
				} else if (index1 == 2) {
					index1 = 9;
					index2 = 1;
				} else if (index1 == 10) {
					index1 = 17;
					index2 = 18;
				} else if (index1 == 11) {
					index1 = 18;
					index2 = 10;
				} else if (index1 == 19) {
					index1 = 26;
					index2 = 27;
				} else if (index1 == 20) {
					index1 = 27;
					index2 = 19;
				} else if (index1 == 28) {
					index1 = 30;
					index2 = 31;
				} else if (index1 == 29) {
					index1 = 31;
					index2 = 28;
				} else if (index1 == 32) {
					index1 = 33;
					index2 = 34;
				} else if (index1 == 33) {
					index1 = 34;
					index2 = 32;
				} else {
					index2 = index1 - 1;
					index1 -= 2;
				}
			} else if (上上下下.equals("上上")) {
				if (index1 == 8) {
					index1 = 9;
					index2 = 1;
				} else if (index1 == 9) {
					index1 = 1;
					index2 = 2;
				} else if (index1 == 17) {
					index1 = 18;
					index2 = 10;
				} else if (index1 == 18) {
					index1 = 10;
					index2 = 11;
				} else if (index1 == 26) {
					index1 = 27;
					index2 = 19;
				} else if (index1 == 27) {
					index1 = 19;
					index2 = 20;
				} else if (index1 == 30) {
					index1 = 31;
					index2 = 28;
				} else if (index1 == 31) {
					index1 = 28;
					index2 = 29;
				} else if (index1 == 33) {
					index1 = 34;
					index2 = 32;
				} else if (index1 == 34) {
					index1 = 32;
					index2 = 33;
				} else {
					index1 ++;
					index2 = index1 + 1;
				}
			} else if (上上下下.equals("上下")) {
				if (index1 == 1) {
					index1 = 9;
					index2 = 2;
				} else if (index1 == 9) {
					index1 = 8;
					index2 = 1;
				} else if (index1 == 10) {
					index1 = 18;
					index2 = 11;
				} else if (index1 == 18) {
					index1 = 17;
					index2 = 10;
				} else if (index1 == 19) {
					index1 = 27;
					index2 = 20;
				} else if (index1 == 27) {
					index1 = 26;
					index2 = 19;
				} else if (index1 == 28) {
					index1 = 31;
					index2 = 29;
				} else if (index1 == 31) {
					index1 = 30;
					index2 = 28;
				} else if (index1 == 32) {
					index1 = 34;
					index2 = 33;
				} else if (index1 == 34) {
					index1 = 33;
					index2 = 32;
				} else {
					index1 --;
					index2 = index1 + 2;
				}
			} else if (上上下下.equals("一上")) {
				if (index1 == 9) {
					index2 = 1;
				} else if (index1 == 18) {
					index2 = 10;
				} else if (index1 == 27) {
					index2 = 19;
				} else if (index1 == 31) {
					index2 = 28;
				} else if (index1 == 34) {
					index2 = 32;
				} else {
					index2 = index1 + 1;
				}
			} else if (上上下下.equals("一下")) {
				index2 = index1;
				if (index1 == 1) {
					index1 = 9;
				} else if (index1 == 10) {
					index1 = 18;
				} else if (index1 == 19) {
					index1 = 27;
				} else if (index1 == 28) {
					index1 = 31;
				} else if (index1 == 32) {
					index1 = 34;
				} else {
					index1--;
				}
			}
			list.add(getTileNameByPicName(String.valueOf(index1)));
			list.add(getTileNameByPicName(String.valueOf(index2)));
		}
		return list;
	}
}
