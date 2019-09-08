package com.thomas.client;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

import javax.swing.JOptionPane;
import com.thomas.data.ConstantValue;
import com.thomas.data.I牌堆数据;
import com.thomas.data.LogHelper;
import com.thomas.data.Ulti;
import com.thomas.logic.Bridge;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * @author
 * @version EchoClientHandler.java
 */
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
	private int playerMachine = 0;
	private int playerAid = 0;
	private int playerBid = 0;
	private int curTilesetid = 0;
	public int cardnum = 0;
	public static String time = "";
	/**
	 * 服务器的连接被建立后调用 建立连接后该 channelActive() 方法被调用一次
	 * 
	 * @param ctx
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		System.out.println("client and server connection is ok! ");
		// 当被通知该 channel 是活动的时候就发送信息
		LogHelper.log.info("client and server connection is ok! ");
		// 等待用户在界面触发登录命令
		// 如果是自动登录的情况，则直接登录
//		if (!ClientMain.loginWin.isVisible()) {
			// ClientMain.loginWin.userLogin(ConstantValue.strUserName);
//		}
	}

	/**
	 * 从服务器接收到数据调用
	 * 
	 * @param ctx
	 * @param in
	 * @throws UnsupportedEncodingException
	 */
	@Override
	public void channelRead0(ChannelHandlerContext ctx, ByteBuf in)
			throws UnsupportedEncodingException {

//		System.out.println("服务器发来消息: " + in.toString(CharsetUtil.UTF_8));
//		System.out.println("服务器发来消息: " + ByteBufUtil.hexDump(in));
//		LogHelper.log.info("服务器发来消息(String): " + in.toString(CharsetUtil.UTF_8));
//		LogHelper.log.info("服务器发来消息(HEX): " + ByteBufUtil.hexDump(in));

		int usertype=0,cmdcode=0,cmdtype=0;
		byte[] username = new byte[10];
		if(in.readableBytes() >= 16){
			usertype = in.readShort();
			cmdcode = in.readShort();
			cmdtype = in.readShort();
			in.readBytes(username);
		}else {
			return;
		}
//		String strUserName = new String(username);
//		if (!strUserName.equals(ConstantValue.strUserName)) {
//			JOptionPane.showMessageDialog(null, "用户不匹配！！！", "错误",
//					JOptionPane.ERROR_MESSAGE);
//			return;
//		}

		// if(in.readableBytes() == 0){
		// JOptionPane.showMessageDialog(null, "命令执行失败！！！", "错误",
		// JOptionPane.ERROR_MESSAGE);
		// return;
		// }
		//

		int iresult = 0;
		switch (cmdcode) {
		case ConstantValue.LOGINCMD:// 登录命令
			iresult = (int) in.readShort();
			if (iresult == 1) {
				ClientMain.loginWin.hideWindow();
				ClientMain.initFrame();
				if (ClientMain.frameWin != null) {
					ClientMain.frameWin.showWindow();
					ClientMain.frameWin.onQueryCmd();
					ClientMain.frameWin.setStateLabel("", 1, 0);
				}
			} else if (iresult == 2) {
				JOptionPane.showMessageDialog(null, "登录密码错误！！！", "错误",
						JOptionPane.ERROR_MESSAGE);
			} else if (iresult == 3) {
				JOptionPane.showMessageDialog(null, "该用户已经登录！！！", "错误",
						JOptionPane.ERROR_MESSAGE);
			} else if (iresult == 4) {
				JOptionPane.showMessageDialog(null, "该用户没有权限登录！！！", "错误",
						JOptionPane.ERROR_MESSAGE);
			}
			break;
		case ConstantValue.PASSWORDCMD:// 配置命令
		case ConstantValue.SETTIMECMD:
		case ConstantValue.MACHINECMD:
		case ConstantValue.CODERCMD:
		case ConstantValue.CARDCMD:
		case ConstantValue.GROUPCMD:
		case ConstantValue.GROUPUSERCMD:
			iresult = (int) in.readShort();
			if (iresult > 0) {
				JOptionPane.showMessageDialog(null, "命令执行成功！！！", "信息",
						JOptionPane.INFORMATION_MESSAGE);
				// 更新内存 or 重新查询一下
				if (ClientMain.frameWin != null) {
					ClientMain.frameWin.onQueryCmd();
				}
			} else {
				JOptionPane.showMessageDialog(null, "命令执行失败！！！", "错误",
						JOptionPane.ERROR_MESSAGE);
			}
			break;
		// 查询用户密码
		case ConstantValue.PASSWORDCMD2:
			processPassword(in);
			break;
		// 查询时间
		case ConstantValue.SETTIMECMD2:
			processSettime(in);
			break;
		// 查询主机
		case ConstantValue.MACHINECMD2:
			processMachine(in);
			break;
		// 查询配码器
		case ConstantValue.CODERCMD2:
			processCoder(in);
			break;
		// 查询牌管理
		case ConstantValue.CARDCMD2:
			processCard(in);
			break;
		// 群管理
		case ConstantValue.GROUPCMD2:
			processGroup(in);
			break;
		// 群用户
		case ConstantValue.GROUPUSERCMD2:
			processGroupUser(in);
			break;
		// 上报配牌器
		case ConstantValue.REPCODER:
			int coderid = in.readByte()&0xFF;
			int state = in.readShort();
			if (ClientMain.frameWin != null) {
				if (state == 1) {
					ClientMain.frameWin.refresh(state);
				} else {
					ClientMain.frameWin.refresh(state);
				}
			}
			TestDialog.state = state;
			if (ClientMain.testDialog != null) {
				ClientMain.testDialog.setState1(state);
			}
			break;
		// 配置牌应答
		case ConstantValue.CONFIGCODECMD:
			int cardsetcode = in.readByte()&0xFF;// (cardsetcode);
			int cardcode = in.readByte()&0xFF;// (cardcode);
			int voltage1 = in.readByte() & 0xFF; //xx.(xx/255)
			int voltage2 = in.readByte() & 0xFF;
			int voltage = voltage1*255 + voltage2;
			String strVol = String.format("%1.2f", (float)((float)voltage/255));
			if (ClientMain.frameWin != null) {
				ClientMain.frameWin.refreshConfigCard(cardcode, strVol);
			}
			if (ClientMain.testDialog != null) {
				ClientMain.testDialog.ReceiveStep2(voltage);
			}
			break;
		// 上报玩家时间
		case ConstantValue.REPPLAYERTIME:
			int time = in.readShort();
			int hour = time / 60;
			int min = time % 60;
			int timestate = time >0 ? 1 : 0;
			ClientHandler.time = hour + "小时" + min + "分钟";
			if (ClientMain.frameWin != null) {
				ClientMain.frameWin.setStateLabel("剩余时间：" + String.valueOf(hour) + "小时" + String.valueOf(min) + "分钟", 1, 0);
			}
			break;
		// 上报主机列表
		case ConstantValue.REPMACHINE:
			playerMachine = in.readByte()&0xFF;
			int mstate = in.readShort();
						
			if (ClientMain.frameWin != null) {
				if (mstate == 1) {
					ClientMain.frameWin.refresh(mstate);
				} else {
					ClientMain.frameWin.refresh(mstate);
				}
			}
			TestDialog.mstate = mstate;
			if (ClientMain.testDialog != null) {
				ClientMain.testDialog.setState2(mstate);
			}
			//处理AB牌
			if (ClientMain.frameWin != null) {
				Vector dataVector = ClientMain.frameWin.machinetableModel.getDataVector();
				// 所有的
				for (int i = 0; i < dataVector.size(); i++) {
					Vector tmpVector = (Vector) dataVector.elementAt(i);
					String strmachine = (String) tmpVector.elementAt(1);
					String strA = (String) tmpVector.elementAt(2);
					playerAid = Integer.parseInt(strA);
					String strB = (String) tmpVector.elementAt(3);
					playerBid = Integer.parseInt(strB);
					if (strmachine.equals(String.valueOf(playerMachine))) {
						break;
					}
				}
			}
			break;
		// 唤醒牌应答
		case ConstantValue.WAKEUPCARD:
			in.readByte();
			int result = in.readShort();// (result);
			if (result != 1) {// failed
				JOptionPane.showMessageDialog(null, "命令执行失败！！！", "错误",
						JOptionPane.ERROR_MESSAGE);
			}else{
				if (ClientMain.frameWin != null) {
					ClientMain.frameWin.refresh(ConstantValue.WAKEUPCARD, null);
				}
			}
			//如果是牌显示测试界面，需要下发快速上报命令
			if (true) {
				try {
					Thread.sleep(4000);
					ChannelFuture cf = ClientMain.loginWin.getChannelFuture();
					ByteBuf cmdBuf = cf.channel().alloc().buffer();
					cmdBuf.writeShort(ConstantValue.USERTYPE);
					cmdBuf.writeShort(ConstantValue.CTRLCARD);
					cmdBuf.writeShort(ConstantValue.NULLREC);
					cmdBuf.writeBytes(ConstantValue.strUserName.getBytes());// 10 bytes
					cmdBuf.writeByte(0xFF);
					cmdBuf.writeInt(0xFBFEBFEF);
					cf.channel().writeAndFlush(cmdBuf);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			break;
		// 休眠牌的应答
		case ConstantValue.SLEEPCARD:
			in.readByte();
			result = in.readShort();// (result);
			if (result != 1) {// failed
				JOptionPane.showMessageDialog(null, "命令执行失败！！！", "错误",
						JOptionPane.ERROR_MESSAGE);
			}else{
				if (ClientMain.frameWin != null) {
					ClientMain.frameWin.refresh(ConstantValue.SLEEPCARD, null);
				}
			}
			if (ClientMain.testDialog != null) {
				ClientMain.testDialog.ReceiveStep4();
			}
			break;
		// 玩家上报牌处理
		case ConstantValue.PLAYERCMD:
			processPlayerCard(in);
			break;
		// 控制快慢上报的应答
		case ConstantValue.CTRLCARD:
			in.readByte();
			result = in.readShort();// (result);
			if (result != 1) {// failed
				JOptionPane.showMessageDialog(null, "命令执行失败！！！", "错误",
						JOptionPane.ERROR_MESSAGE);
			} else {
				;
			}
			break;
		default:
			break;
		}
//		ReferenceCountUtil.release(in);
	}

	/**
	 * 捕获异常时调用
	 * 
	 * @param ctx
	 * @param cause
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// 记录错误日志并关闭 channel
		cause.printStackTrace();
		LogHelper.log.info("客户端出现异常", cause);
		LogHelper.log.info(cause.toString());
		ctx.close();
		if (ClientMain.frameWin != null) {
			ClientMain.frameWin.setStateLabel("", 0, 1);
		}
	}

	/**
	 * 
	 * 处理查询用户密码信息并显示 字节数10,2,2
	 */
	public void processPassword(ByteBuf cmdbody)
			throws UnsupportedEncodingException {
		Vector rowData = new Vector();

		int ilen = cmdbody.readableBytes();
		if (ilen % 12 != 0) {
			return;
		}
		int isum = ilen / 12;
		for (int i = 0; i < isum; i++) {
			Vector row = new Vector();
			row.addElement(Integer.toString(i + 1));
			byte[] username = new byte[10];
			cmdbody.readBytes(username);
			row.addElement(new String(username, "utf-8"));

			int iusergrade = cmdbody.readShort();
			row.addElement(Integer.toString(iusergrade));

			rowData.addElement(row);
		}
		ClientMain.frameWin.refresh(ConstantValue.PASSWORDCMD2, rowData);
	}

	/**
	 * 
	 * 处理查询时间信息并显示 字节数4 2
	 */
	public void processSettime(ByteBuf cmdbody)
			throws UnsupportedEncodingException {
		Vector rowData = new Vector();

		int ilen = cmdbody.readableBytes();
		if (ilen % 6 != 0) {
			return;
		}
		int isum = ilen / 6;
		for (int i = 0; i < isum; i++) {
			Vector row = new Vector();
			row.addElement(Integer.toString(i + 1));
			int time = cmdbody.readInt();
			row.addElement(Integer.toString(time));
			int flag = cmdbody.readShort();
			row.addElement(Integer.toString(flag));

			rowData.addElement(row);
		}
		ClientMain.frameWin.refresh(ConstantValue.SETTIMECMD2, rowData);
	}

	/**
	 * 主机信息显示
	 * 
	 * @param cmdbody
	 * @throws UnsupportedEncodingException
	 */
	public void processMachine(ByteBuf cmdbody)
			throws UnsupportedEncodingException {
		Vector rowData = new Vector();

		int ilen = cmdbody.readableBytes();
		if (ilen % 18 != 0) {
			return;
		}
		int isum = ilen / 18;
		for (int i = 0; i < isum; i++) {
			Vector row = new Vector();
			row.addElement(Integer.toString(i + 1));

			row.addElement(Integer.toString(cmdbody.readShort()));
			row.addElement(Integer.toString(cmdbody.readShort()));
			row.addElement(Integer.toString(cmdbody.readShort()));
			row.addElement(Integer.toString(cmdbody.readShort()));
			row.addElement(Integer.toString(cmdbody.readShort()));
			row.addElement(Integer.toString(cmdbody.readShort()));
			row.addElement(Integer.toString(cmdbody.readShort()));
			row.addElement(Integer.toString(cmdbody.readShort()));
			row.addElement(Integer.toString(cmdbody.readShort()));

			rowData.addElement(row);
		}
		ClientMain.frameWin.refresh(ConstantValue.MACHINECMD2, rowData);
		

	}

	/**
	 * 配牌器显示
	 * 
	 * @param cmdbody
	 * @throws UnsupportedEncodingException
	 */
	public void processCoder(ByteBuf cmdbody)
			throws UnsupportedEncodingException {
		Vector rowData = new Vector();

		int ilen = cmdbody.readableBytes();
		if (ilen % 2 != 0) {
			return;
		}
		int isum = ilen / 2;
		for (int i = 0; i < isum; i++) {
			Vector row = new Vector();
			row.addElement(Integer.toString(i + 1));
			int flag = cmdbody.readShort();
			row.addElement(Integer.toString(flag));

			rowData.addElement(row);
		}
		if (ClientMain.frameWin != null) {
			ClientMain.frameWin.refresh(ConstantValue.CODERCMD2, rowData);
		}
	}

	/**
	 * 配置牌显示
	 * 
	 * @param cmdbody
	 * @throws UnsupportedEncodingException
	 */
	public void processCard(ByteBuf cmdbody)
			throws UnsupportedEncodingException {
		Vector rowData = new Vector();

		int ilen = cmdbody.readableBytes();
		if (ilen % 102 != 0) {
			return;
		}
		int isum = ilen / 102;
		for (int i = 0; i < isum; i++) {
			Vector row = new Vector();
			row.addElement(Integer.toString(i + 1));

			row.addElement(Integer.toString(cmdbody.readShort()));

			byte[] tmpbuf = new byte[20];
			cmdbody.readBytes(tmpbuf);
			row.addElement(new String(tmpbuf, CharsetUtil.UTF_8).trim());

			tmpbuf = new byte[20];
			cmdbody.readBytes(tmpbuf);
			row.addElement(new String(tmpbuf, CharsetUtil.UTF_8).trim());
			tmpbuf = new byte[20];
			cmdbody.readBytes(tmpbuf);
			row.addElement(new String(tmpbuf, CharsetUtil.UTF_8).trim());
			tmpbuf = new byte[20];
			cmdbody.readBytes(tmpbuf);
			row.addElement(new String(tmpbuf, CharsetUtil.UTF_8).trim());

			tmpbuf = new byte[20];
			cmdbody.readBytes(tmpbuf);
			row.addElement(new String(tmpbuf, CharsetUtil.UTF_8).trim());

			rowData.addElement(row);
		}
		ClientMain.frameWin.refresh(ConstantValue.CARDCMD2, rowData);
	}

	/**
	 * 配置群显示
	 * 
	 * @param cmdbody
	 * @throws UnsupportedEncodingException
	 */
	public void processGroup(ByteBuf cmdbody)
			throws UnsupportedEncodingException {
		Vector rowData = new Vector();

		int i = 0;
		while (cmdbody.readableBytes() > 0) {
			Vector row = new Vector();
			row.addElement(Integer.toString(i + 1));

			row.addElement(Integer.toString(cmdbody.readShort()));

			byte[] tmpbuf = new byte[10];
			cmdbody.readBytes(tmpbuf);
			row.addElement(new String(tmpbuf, CharsetUtil.UTF_8));

			row.addElement(Integer.toString(cmdbody.readShort()));

			row.addElement(Integer.toString(cmdbody.readShort()));

			int ilen = cmdbody.readShort();

			tmpbuf = new byte[ilen];
			cmdbody.readBytes(tmpbuf);
			row.addElement(new String(tmpbuf, CharsetUtil.UTF_8));

			rowData.addElement(row);
		}
		ClientMain.frameWin.refresh(ConstantValue.GROUPCMD2, rowData);
	}

	/**
	 * 配置群用户显示
	 * 
	 * @param cmdbody
	 * @throws UnsupportedEncodingException
	 */
	public void processGroupUser(ByteBuf cmdbody)
			throws UnsupportedEncodingException {
		String strGroupId = "";
		Vector dataVector = ClientMain.frameWin.grouptableModel.getDataVector();
		// 所有的
		for (int i = 0; i < dataVector.size(); i++) {
			Vector tmpVector = (Vector) dataVector.elementAt(i);
			strGroupId = (String) tmpVector.elementAt(1);
			String strUserPass = (String) tmpVector.elementAt(2);
			if (strUserPass.equals(ConstantValue.strUserName)) {
				break;
			}
		}

		Vector rowData = new Vector();

		int i = 0;
		while (cmdbody.readableBytes() > 0) {

			int igroupid = cmdbody.readShort();
			if (Integer.parseInt(strGroupId) != igroupid) {
				byte[] tmpbuf = new byte[16];
				cmdbody.readBytes(tmpbuf);
				continue;
			}
			Vector row = new Vector();
			row.addElement(Integer.toString(i + 1));

			row.addElement(Integer.toString(igroupid));

			byte[] tmpbuf = new byte[10];
			cmdbody.readBytes(tmpbuf);
			row.addElement(new String(tmpbuf, CharsetUtil.UTF_8));

			row.addElement(Integer.toString(cmdbody.readShort()));

			row.addElement(Integer.toString(cmdbody.readShort()));

			row.addElement(Integer.toString(cmdbody.readShort()));

			rowData.addElement(row);
		}
		ClientMain.frameWin.refresh(ConstantValue.GROUPUSERCMD2, rowData);
	}

	/**
	 * 玩家牌处理
	 * 
	 * @param cmdbody
	 */
	public void processPlayerCard(ByteBuf cmdbody) {
		/**
		 * 字节（1）主机号，1字节16进制 字节（1）牌副码，2字节16进制 字节（1）牌自己码，1字节16进制 字节（1）左邻居，1字节16进制
		 * 字节（1）右邻居，1字节16进制 字节（1）X，1字节16进制 字节（1）Y ，1字节16进制 字节（1）Z ，1字节16进制, R 1字节
		 */

		//主机信息匹配判断
		int sMachineid = cmdbody.readByte()&0xFF;
		if(sMachineid != playerMachine){
			return;
		}
		int inum = cmdbody.readableBytes();
		if(inum != 10){
			return;
		}
		
		byte[] tmpbuf = new byte[10];
		cmdbody.readBytes(tmpbuf);
		
		int sTileSetid=0,bTileValue=0,bNeighbor1=0, bNeighbor2=0, iDirection=0, iZDirection=0, iPower=0;
		byte[] databuf = new byte[8];
		databuf = dencryptData(tmpbuf);

		// 解密后数据
		sTileSetid = databuf[0] & 0xFF;
		bTileValue = databuf[1] & 0xFF;
		bNeighbor1 = databuf[2] & 0xFF;
		bNeighbor2 = databuf[3] & 0xFF;
		//5E 67 6A 73

		// 判断姿势
		int ix = databuf[4];
		int iy = databuf[5];
		int iz = databuf[6];

		iDirection = Ulti.getTileDirection(ix, iy, iz);
		iZDirection = Ulti.getZDirection(iz);
		iPower = 0 - databuf[7];// power
		
		if (ClientMain.testDialog != null) {
			ClientMain.testDialog.ReceiveStep3(sTileSetid, bTileValue, bNeighbor1, bNeighbor2, iDirection, iZDirection, iPower);
		}
		
//		int sTileSetid = cmdbody.readByte()&0xFF;
//		int bTileValue = cmdbody.readByte()&0xFF;
//		int bNeighbor1 = cmdbody.readByte()&0xFF;
//		int bNeighbor2 = cmdbody.readByte()&0xFF;
//		
//		//判断姿势
//		int ix = cmdbody.readByte();
//		int iy = cmdbody.readByte();
//		int iz = cmdbody.readByte();
//		int iDirection = Ulti.getTileDirection(ix, iy, iz);
//		int iZDirection = Ulti.getZDirection(iz);
//		int iPower = 0-cmdbody.readByte();//power 
//		
		//测试用。。。。
//		processPlayerTest(bTileValue,bNeighbor1,bNeighbor2,ix,databuf[5] & 0xFF,databuf[6] & 0xFF,iPower);
//
//		//每600ms更新一下时间
//		if((sTileSetid&0xFF) == 0x2E && (bTileValue&0xFF) == 0x37 && (bNeighbor1&0xFF) == 0x3A && (bNeighbor2&0xFF) == 0x5B){
//			//update time
//			ClientMain.updateTime();
////			if(curTilesetid == playerAid)
//			{
//				Bridge.tilesReferenceA.CheckAndRemoveTimeout(ClientMain.offlinetimes);
//				//发送给算法部分
//				I牌堆数据 data = new I牌堆数据(Bridge.tilesReferenceA);
//				Bridge.TilesChangedA(data);		
//			}//else
//			{
//				Bridge.tilesReferenceB.CheckAndRemoveTimeout(ClientMain.offlinetimes);
//				//发送给算法部分
//				I牌堆数据 data = new I牌堆数据(Bridge.tilesReferenceB);
//				Bridge.TilesChangedB(data);		
//			}				
//			ClientMain.frameWin.repaint();
//		}
//		
//		
//		
//		if(sTileSetid == playerAid){
//			curTilesetid = playerAid;
////			// 有效并且变化,通知算法部分并绘制图形界面
////			if ((sTileSetid&0xFF) == 0x2E && (bTileValue&0xFF) == 0x37 && (bNeighbor1&0xFF) == 0x3A && (bNeighbor2&0xFF) == 0x5B /*&& Bridge.tilesReferenceA.getChanged()*/) {
////				//检查超时情况
////				Bridge.tilesReferenceA.CheckAndRemoveTimeout(ClientMain.offlinetimes);
////				//变化了才刷新
////		//		if(Bridge.tilesReferenceA.getChanged()){
////					//发送给算法部分
////					I牌堆数据 data = new I牌堆数据(Bridge.tilesReferenceA);
////					Bridge.TilesChangedA(data);		
////		//			LogHelper.log.info(data.toString());
////	//			}
////			}else{
//				// 更新这个牌
//				Bridge.tilesReferenceA.UpdateSingleTile(bTileValue,
//						bNeighbor1, bNeighbor2, iDirection, iZDirection, iPower); 
//				//设置牌副id
//				Bridge.tilesReferenceA.setiTileSetId(sTileSetid);
////			}						
//		}else if(sTileSetid == playerBid){
//			curTilesetid = playerBid;
////			// 有效并且变化,通知算法部分并绘制图形界面
////			if ((sTileSetid&0xFF) == 0x2E && (bTileValue&0xFF) == 0x37 && (bNeighbor1&0xFF) == 0x3A && (bNeighbor2&0xFF) == 0x5B /*&& Bridge.tilesReferenceA.getChanged()*/) {
////				//检查超时情况
////				Bridge.tilesReferenceB.CheckAndRemoveTimeout(ClientMain.offlinetimes);
////				//变化了才刷新
////				//		if(Bridge.tilesReferenceA.getChanged()){
////					//发送给算法部分
////					I牌堆数据 data = new I牌堆数据(Bridge.tilesReferenceB);
////					Bridge.TilesChangedB(data);		
////					//			LogHelper.log.info(data.toString());
////				//}
////			}else{
//				// 更新这个牌
//				Bridge.tilesReferenceB.UpdateSingleTile(bTileValue,
//						bNeighbor1, bNeighbor2, iDirection, iZDirection, iPower); 
//				//设置牌副id
//				Bridge.tilesReferenceB.setiTileSetId(sTileSetid);
////			}	
//		}else {
//			LogHelper.log.info("当前牌副ID不合法！！！");
//			return;
//		}
	}
	
	/**
	 * 对牌报文进行解密
	 * @param cmdbody
	 */
	public byte[] dencryptData(byte[] tmpbuf){
		
		byte seed1 = tmpbuf[4];
		byte seed2 = tmpbuf[5];
		
		//seed2 减去 0x13
		seed2 = (byte)((seed2 - (byte)0x13)&0xFF);
		
		//seed2异或所有的数据
		for(int i = 0; i < 10; i++){
			if(i != 5){
				tmpbuf[i] = (byte) (tmpbuf[i] ^ seed2);
			}
		}
		
		//see1减去0x57
		seed1 = tmpbuf[4];
		seed1 = (byte)((seed1 - (byte)0x57)&0xFF);
		
		//seed1异或初最后一位所有数据
		for(int i = 0; i < 10; i++){
			if(i != 9){
				tmpbuf[i] = (byte) (tmpbuf[i] ^ seed1);
			}
		}
		
		//牌码-3，邻居1-1，邻居2 -2 
		byte[] retbuf = new byte[8];
		retbuf[0] = tmpbuf[0];
		retbuf[1] = (byte)(tmpbuf[1] - (byte)0x3);
		retbuf[2] = (byte)(tmpbuf[2] - (byte)0x1);
		retbuf[3] = (byte)(tmpbuf[3] - (byte)0x2);
		retbuf[4] = tmpbuf[6];
		retbuf[5] = tmpbuf[7];
		retbuf[6] = tmpbuf[8];
		retbuf[7] = tmpbuf[9];
		
		return retbuf;

	}

	/**
	 * 测试用
	 * @param cmdbody
	 */
	public void processPlayerTest(int bTileValue, int neigh1,int neigh2,int ix,int iy,int iz,int iR) {
	
		StringBuffer str = new StringBuffer();
		if((bTileValue&0xFF) == 0x37 && (neigh1&0xFF) == 0x3A && (neigh2&0xFF) == 0x5B){
			str.append("\n");
			str.append(cardnum);
			str.append("--");
			if (ClientMain.frameWin != null) {
				ClientMain.frameWin.refreshTest(str);
			}
//			LogHelper.log.info(str.toString());
			cardnum = 0;
			return;
		}
		cardnum++;

		if(bTileValue == 0xF1 || bTileValue == 0xF2 ||bTileValue == 0xF3){
			str.append(Integer.toHexString(bTileValue));
			str.append("/");	
			str.append(Integer.toHexString(neigh1));
			str.append("/");	
			str.append(Integer.toHexString(neigh2));
			str.append("/");
			str.append(ix);
			str.append("/");
//			int ipower =  ((iy<<8) & 0xFF00) | (iz & 0xFF);
//			str.append(Integer.toHexString(ipower));
			str.append(Integer.toHexString(iy));
			str.append(Integer.toHexString(iz));
			str.append("\n");
		}else{
//			DateTime dateTime = new DateTime();
//			int second = dateTime.getSecondOfMinute();  
//			int milsec = dateTime.getMillisOfSecond();
//			str.append(second);
//			str.append(":");
//			str.append(milsec);
//			str.append("");
			str.append(Integer.toHexString(bTileValue));
			str.append("/");	
			str.append(Integer.toHexString(neigh1));
			str.append("/");	
			str.append(Integer.toHexString(neigh2));
			str.append("/");
			str.append(ix);
			str.append(",");
			str.append((byte)iy);
			str.append(",");
			str.append((byte)iz);
			str.append(",");
			str.append(iR);
			str.append("\t");
//			str.append(",");
//			str.append(Ulti.getZDirection(iz));
//			str.append(",");
//			str.append(ClientMain.CurDateTime);
//			str.append("\t");
		}

		if (ClientMain.frameWin != null) {
			ClientMain.frameWin.refreshTest(str);
		}
//		LogHelper.log.info(str.toString());
	}
	

}
