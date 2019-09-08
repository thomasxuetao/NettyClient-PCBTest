package com.thomas.data;

public class ConstantValue {

	/*
	 * 5701 客户端管理一级，服务器时间管理表牌主机管理表配牌器管理表 5702 客户端管理二级，牌管理表群管理表 5703 客户端管理二级，牌配码
	 * 5704 客户端管理三级，用户管理 5705 算法执行玩家-客户端 5706 配牌器 5707 牌主机
	 */

	public static final int ADMIN = 0x1;
	public static final int MANAGER = 0x2;
	public static final int WORKER = 0x3;
	public static final int GROUPER = 0x4;
	public static final int PLAYER = 0x5;
	public static final int CODER = 0x6;
	public static final int MACHINE = 0x7;

	// 当前客户端的类型，用于定制化界面
	public static int USERTYPE = PLAYER;

	// 当前登录的用户授权码
	public static String strUserName;

	/*
	 * 6800 客户端牌主机配牌器登录服务器 6801 特殊密码管理表 6802 服务器时间管理表 6803 牌主机管理表 6804 配牌器管理表
	 * 6805 牌管理表 6806 群管理表 6807 用户管理表 6810 给牌配码 6811 牌唤醒 6812 牌休眠
	 */

	public static final int LOGINCMD = 0x0; // 所有用户登录的命令
	// 1
	public static final int PASSWORDCMD = 0x1; // 用户密码管理
	public static final int PASSWORDCMD2 = 0x71;
	public static final int SETTIMECMD = 0x2; // 时间管理，客户端断则休眠并停止计时
	public static final int SETTIMECMD2 = 0x72;
	public static final int MACHINECMD = 0x3; // 主机管理
	public static final int MACHINECMD2 = 0x73;
	public static final int CODERCMD = 0x4; // 配牌器管理
	public static final int CODERCMD2 = 0x74;

	// 2 厂家
	public static final int CARDCMD = 0x5;// 牌管理
	public static final int CARDCMD2 = 0x75;
	public static final int GROUPCMD = 0x6;// 群管理
	public static final int GROUPCMD2 = 0x76;

	// 3厂家配码
	public static final int CONFIGCODECMD = 0x10;// 配码
	public static final int CONFIGCODECMD2 = 0x70;// 没有实际的命令

	// 4 群主
	public static final int GROUPUSERCMD = 0x7;// 群内用户管理
	public static final int GROUPUSERCMD2 = 0x77;

	// 5 玩家，牌上报命令
	public static final int PLAYERCMD = 0x13;
	
	public static final int CTRLCARD = 0x14;

	// 6 牌唤醒
	public static final int WAKEUPCARD = 0x11;

	// 7 牌休眠
	public static final int SLEEPCARD = 0x12;

	// 8 server上报当前登录的配牌器
	public static final int REPCODER = 0x60;
	public static final int QUERYCODER = 0x61;

	// 9 server上报当前登录的主机
	public static final int REPMACHINE = 0x62;
	public static final int QUERYMACHINE = 0x63;

	// 10 server上报玩家剩余时间
	public static final int REPPLAYERTIME = 0x64;

	/*
	 * 7900 不动表内容 7901 新增记录 7902 修改记录 7903 删除记录 7904 表查询
	 */
	public static final int NULLREC = 0x0;
	public static final int ADDREC = 0x1;
	public static final int MODIREC = 0x2;
	public static final int DELREC = 0x3;
	public static final int QRYREC = 0x4;

	/**
	 * 麻将姿势
	 */
	public static final int UPSIDE = 1;
	public static final int DOWNSIDE = 2;
	public static final int STAND1 = 3;
	public static final int STAND2 = 4;
	public static final int STAND3 = 5;
	public static final int STAND4 = 6;
	public static final int MOVE = 7;

	// 牌状态
	public static final int IdleState = 0;
	public static final int XiPaiing = 1;
	public static final int XiPaied = 2;
	public static final int DongPaiing = 3;
	public static final int DongPaied = 4;
	public static final int ZhuaPaiing = 5;
	public static final int ZhuaPaied = 6;
	public static final int ChuPaiing = 7;
	public static final int ChuPaied = 8;

	// 牌参数配置
//	public static int TotalTileNums = 136;
//	public static int LineTilesNums = 17;
//	public static int PlayerTilesNums = 13;

	//
	// public static String[] cardPicStrings = { "1tong", "2tong", "3tong",
	// "4tong", "5tong", "6tong", "7tong", "8tong", "9tong", "1tong",
	// "2tong", "3tong", "4tong", "5tong", "6tong", "7tong", "8tong",
	// "9tong", "1tong", "2tong", "3tong", "4tong", "5tong", "6tong",
	// "7tong", "8tong", "9tong", "1tong", "2tong", "3tong", "4tong",
	// "5tong", "6tong", "7tong", "8tong", "9tong", "1wan", "2wan",
	// "3wan", "4wan", "5wan", "6wan", "7wan", "8wan", "9wan", "1wan",
	// "2wan", "3wan", "4wan", "5wan", "6wan", "7wan", "8wan", "9wan",
	// "1wan", "2wan", "3wan", "4wan", "5wan", "6wan", "7wan", "8wan",
	// "9wan", "1wan", "2wan", "3wan", "4wan", "5wan", "6wan", "7wan",
	// "8wan", "9wan", "1tiao", "2tiao", "3tiao", "4tiao", "5tiao",
	// "6tiao", "7tiao", "8tiao", "9tiao", "1tiao", "2tiao", "3tiao",
	// "4tiao", "5tiao", "6tiao", "7tiao", "8tiao", "9tiao", "1tiao",
	// "2tiao", "3tiao", "4tiao", "5tiao", "6tiao", "7tiao", "8tiao",
	// "9tiao", "1tiao", "2tiao", "3tiao", "4tiao", "5tiao", "6tiao",
	// "7tiao", "8tiao", "9tiao", "dong", "nan", "xi", "bei", "zhong",
	// "fa", "bai", "dong", "nan", "xi", "bei", "zhong", "fa", "bai",
	// "dong", "nan", "xi", "bei", "zhong", "fa", "bai", "dong", "nan",
	// "xi", "bei", "zhong", "fa", "bai", "chun", "xia", "qiu", "dong",
	// "mei", "lan", "zhu", "ju" };

	public static String[] cardPicStrings = { "1tong", "1tong", "1tong",
			"1tong", "2tong", "2tong", "2tong", "2tong", "3tong", "3tong",
			"3tong", "3tong", "4tong", "4tong", "4tong", "4tong", "5tong",
			"5tong", "5tong", "5tong", "6tong", "6tong", "6tong", "6tong",
			"7tong", "7tong", "7tong", "7tong", "8tong", "8tong", "8tong",
			"8tong", "9tong", "9tong", "9tong", "9tong", "1wan", "1wan",
			"1wan", "1wan", "2wan", "2wan", "2wan", "2wan", "3wan", "3wan",
			"3wan", "3wan", "4wan", "4wan", "4wan", "4wan", "5wan", "5wan",
			"5wan", "5wan", "6wan", "6wan", "6wan", "6wan", "7wan", "7wan",
			"7wan", "7wan", "8wan", "8wan", "8wan", "8wan", "9wan", "9wan",
			"9wan", "9wan", "1tiao", "1tiao", "1tiao", "1tiao", "2tiao",
			"2tiao", "2tiao", "2tiao", "3tiao", "3tiao", "3tiao", "3tiao",
			"4tiao", "4tiao", "4tiao", "4tiao", "5tiao", "5tiao", "5tiao",
			"5tiao", "6tiao", "6tiao", "6tiao", "6tiao", "7tiao", "7tiao",
			"7tiao", "7tiao", "8tiao", "8tiao", "8tiao", "8tiao", "9tiao",
			"9tiao", "9tiao", "9tiao", "dong", "dong", "dong", "dong", "nan",
			"nan", "nan", "nan", "xi", "xi", "xi", "xi", "bei", "bei", "bei",
			"bei", "zhong", "zhong", "zhong", "zhong", "fa", "fa", "fa", "fa",
			"bai", "bai", "bai", "bai", "chun", "xia", "qiu",
			"dong2", "mei", "lan", "zu", "ju" };
	
	public static String[] cardPicStrings2 = { "饼11", "饼12", "饼13", "饼14",
			"饼21", "饼22", "饼23", "饼24", "饼31", "饼32", "饼33", "饼34", "饼41",
			"饼42", "饼43", "饼44", "饼51", "饼52", "饼53", "饼54", "饼61", "饼62",
			"饼63", "饼64", "饼71", "饼72", "饼73", "饼74", "饼81", "饼82", "饼83",
			"饼84", "饼91", "饼92", "饼93", "饼94",

			"万11", "万12", "万13", "万14", "万21", "万22", "万23", "万24", "万31",
			"万32", "万33", "万34", "万41", "万42", "万43", "万44", "万51", "万52",
			"万53", "万54", "万61", "万62", "万63", "万64", "万71", "万72", "万73",
			"万74", "万81", "万82", "万83", "万84", "万91", "万92", "万93", "万94",

			"条11", "条12", "条13", "条14", "条21", "条22", "条23", "条24", "条31",
			"条32", "条33", "条34", "条41", "条42", "条43", "条44", "条51", "条52",
			"条53", "条54", "条61", "条62", "条63", "条64", "条71", "条72", "条73",
			"条74", "条81", "条82", "条83", "条84", "条91", "条92", "条93", "条94",

			"东1", "东2", "东3", "东4", "南1", "南2", "南3", "南4", "西1", "西2", "西3", "西4", "北1",
			"北2", "北3", "北4", "中1", "中2", "中3", "中4", "发1", "发2", "发3", "发4", "白1", "白2",
			"白3", "白4", "春", "夏", "秋", "冬", "梅", "兰", "竹", "菊" };
	
	public static String[] cardPicStrings3 = { "一饼1", "一饼2", "一饼3", "一饼4",
			"二饼1", "二饼2", "二饼3", "二饼4", "三饼1", "三饼2", "三饼3", "三饼4", "四饼1",
			"四饼2", "四饼3", "四饼4", "五饼1", "五饼2", "五饼3", "五饼4", "六饼1", "六饼2",
			"六饼3", "六饼4", "七饼1", "七饼2", "七饼3", "七饼4", "八饼1", "八饼2", "八饼3",
			"八饼4", "九饼1", "九饼2", "九饼3", "九饼4",

			"一万1", "一二万", "一三万", "一四万", "二万1", "二万2", "二三万", "二四万", "三万1",
			"三万2", "三万3", "三四万", "四万1", "四万2", "四万3", "四万4", "五万1", "五万2",
			"五万3", "五万4", "六万1", "六万2", "六万3", "六万4", "七万1", "七万2", "七万3",
			"七万4", "八万1", "八万2", "八万3", "八万4", "九万1", "九万2", "九万3", "九万4",

			"一条1", "一二条", "一三条", "一四条", "二条1", "二条2", "二三条", "二四条", "三条1",
			"三条2", "三条3", "三四条", "四条1", "四条2", "四条3", "四条4", "五条1", "五条2",
			"五条3", "五条4", "六条1", "六条2", "六条3", "六条4", "七条1", "七条2", "七条3",
			"七条4", "八条1", "八条2", "八条3", "八条4", "九条1", "九条2", "九条3", "九条4",

			"东1", "东2", "东3", "东4", "南1", "南2", "南3", "南4", "西1", "西2", "西3", "西4", "北1",
			"北2", "北3", "北4", "中1", "中2", "中3", "中4", "发1", "发2", "发3", "发4", "白1", "白2",
			"白3", "白4", "春", "夏", "秋", "冬", "梅", "兰", "竹", "菊" };

//	public static String[] cardNameStrings = { "饼11", "饼12", "饼13", "饼14",
//			"万11", "万12", "万13", "万14", "条11", "条12", "条13", "条14", "饼91",
//			"饼92", "饼93", "饼94", "万91", "万92", "万93", "万94", "条91", "条92",
//			"条93", "条94", "饼21", "饼22", "饼23", "饼24", "万21", "万22", "万23",
//			"万24", "条21", "条22", "条23", "条24", "饼81", "饼82", "饼83", "饼84",
//			"万81", "万82", "万83", "万84", "条81", "条82", "条83", "条84", "饼31",
//			"饼32", "饼33", "饼34", "万31", "万32", "万33", "万34", "条31", "条32",
//			"条33", "条34", "饼71", "饼72", "饼73", "饼74", "万71", "万72", "万73",
//			"万74", "条71", "条72", "条73", "条74", "饼41", "饼42", "饼43", "饼44",
//			"万41", "万42", "万43", "万44", "条41", "条42", "条43", "条44", "饼51",
//			"饼52", "饼53", "饼54", "万51", "万52", "万53", "万54", "条51", "条52",
//			"条53", "条54", "饼61", "饼62", "饼63", "饼64", "万61", "万62", "万63",
//			"万64", "条61", "条62", "条63", "条64", "东1", "东2", "东3", "东4", "南1",
//			"南2", "南3", "南4", "西1", "西2", "西3", "西4", "北1", "北2", "北3", "北4",
//			"中1", "中2", "中3", "中4", "发1", "发2", "发3", "发4", "白1", "白2", "白3",
//			"白4", "春", "夏", "秋", "冬", "梅", "兰", "竹", "菊" };
//	
//	public static String[] cardNameStrings2 = { "1tong", "1tong", "1tong", "1tong",
//		"1wan", "1wan", "1wan", "1wan", "1tiao", "1tiao", "1tiao", "1tiao", "9tong",
//		"9tong", "9tong", "9tong", "9wan", "9wan", "9wan", "9wan", "9tiao", "9tiao",
//		"9tiao", "9tiao", "2tong", "2tong", "2tong", "2tong", "2wan", "2wan", "2wan",
//		"2wan", "2tiao", "2tiao", "2tiao", "2tiao", "8tong", "8tong", "8tong", "8tong",
//		"8wan", "8wan", "8wan", "8wan", "8tiao", "8tiao", "8tiao", "8tiao", "3tong",
//		"3tong", "3tong", "3tong", "3wan", "3wan", "3wan", "3wan", "3tiao", "3tiao",
//		"3tiao", "3tiao", "7tong", "7tong", "7tong", "7tong", "7wan", "7wan", "7wan",
//		"7wan", "7tiao", "7tiao", "7tiao", "7tiao", "4tong", "4tong", "4tong", "4tong",
//		"4wan", "4wan", "4wan", "4wan", "4tiao", "4tiao", "4tiao", "4tiao", "5tong",
//		"5tong", "5tong", "5tong", "5wan", "5wan", "5wan", "5wan", "5tiao", "5tiao",
//		"5tiao", "5tiao", "6tong", "6tong", "6tong", "6tong", "6wan", "6wan", "6wan",
//		"6wan", "6tiao", "6tiao", "6tiao", "6tiao", "dong", "dong", "dong", "dong", "nan",
//		"nan", "nan", "nan", "xi", "xi", "xi", "xi", "bei", "bei", "bei", "bei",
//		"zhong", "zhong", "zhong", "zhong", "fa", "fa", "fa", "fa", "bai", "bai", "bai",
//		"bai", "chun", "xia", "qiu", "dong2", "mei", "lan", "zu", "ju" };

	// public static String[] cardNameStrings = { "一饼", "二饼", "三饼", "四饼", "五饼",
	// "六饼", "七饼", "八饼", "九饼", "一饼", "二饼", "三饼", "四饼", "五饼", "六饼", "七饼",
	// "八饼", "九饼", "一饼", "二饼", "三饼", "四饼", "五饼", "六饼", "七饼", "八饼", "九饼",
	// "一饼", "二饼", "三饼", "四饼", "五饼", "六饼", "七饼", "八饼", "九饼", "一万", "二万",
	// "三万", "四万", "五万", "六万", "七万", "八万", "九万", "一万", "二万", "三万", "四万",
	// "五万", "六万", "七万", "八万", "九万", "一万", "二万", "三万", "四万", "五万", "六万",
	// "七万", "八万", "九万", "一万", "二万", "三万", "四万", "五万", "六万", "七万", "八万",
	// "九万", "一条", "二条", "三条", "四条", "五条", "六条", "七条", "八条", "九条", "一条",
	// "二条", "三条", "四条", "五条", "六条", "七条", "八条", "九条", "一条", "二条", "三条",
	// "四条", "五条", "六条", "七条", "八条", "九条", "一条", "二条", "三条", "四条", "五条",
	// "六条", "七条", "八条", "九条", "东", "南", "西", "北", "中", "發", "白", "东",
	// "南", "西", "北", "中", "發", "白", "东", "南", "西", "北", "中", "發", "白",
	// "东", "南", "西", "北", "中", "發", "白", "春", "夏", "秋", "冬", "梅", "兰",
	// "竹", "菊" };


	public static int[] tileCodeArrays = {
		0x01,0x04,0x10,0x40,0x02,0x05,0x08,0x11,
		0x14,0x20,0x41,0x44,0x50,0x80,0x03,0x06,
		0x09,0x0C,0x12,0x15,0x18,0x21,0x24,0x30,
		0x42,0x45,0x48,0x51,0x54,0x60,0x81,0x84,
		0x90,0xC0,0x07,0x0A,0x0D,0x13,0x16,0x19,
		0x1C,0x22,0x25,0x28,0x31,0x34,0x43,0x46,
		0x49,0x4C,0x52,0x55,0x58,0x61,0x64,0x70,
		0x82,0x85,0x88,0x91,0x94,0xA0,0xC1,0xC4,
		0xD0,0x0B,0x0E,0x17,0x1A,0x1D,0x23,0x26,
		0x29,0x2C,0x32,0x35,0x38,0x47,0x4A,0x4D,
		0x53,0x56,0x59,0x5C,0x62,0x65,0x68,0x71,
		0x74,0x83,0x86,0x89,0x8C,0x92,0x95,0x98,
		0xA1,0xA4,0xB0,0xC2,0xC5,0xC8,0xD1,0xD4,
		0xE0,0x0F,0x1B,0x1E,0x27,0x2A,0x2D,0x33,
		0x36,0x39,0x3C,0x4B,0x4E,0x57,0x5A,0x5D,
		0x63,0x66,0x69,0x6C,0x72,0x75,0x78,0x87,
		0x8A,0x8D,0x93,0x96,0x99,0x9C,0xA2,0xA5};
//		0xA8,0xB1,0xB4,0xC3,0xC6,0xC9,0xCC,0xD2};
}
