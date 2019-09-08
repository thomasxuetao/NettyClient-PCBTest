package com.thomas.client;

import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;
import com.thomas.data.ConstantValue;

/**
 * 表格模型类
 * @author xuetao
 *
 */
public class ClientTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;
	private int cmdcode;
	
	
	public ClientTableModel(int icmdcode){
		this.cmdcode = icmdcode;

		columnIdentifiers = new Vector();
		dataVector = new Vector();
		initialize();
		this.setDataVector();
	}
	

	public void setRowData(Vector rowData) {
		dataVector = rowData;
	}


	
	
	public void setDataVector(){
		super.setDataVector(dataVector, columnIdentifiers);
	}
	
	public void initialize(){
		switch (cmdcode) {
		// 查询密码
		case ConstantValue.PASSWORDCMD2:
		case ConstantValue.PASSWORDCMD:
			columnIdentifiers.addElement("序号");
			columnIdentifiers.addElement("用户密码");
			columnIdentifiers.addElement("用户级别");
			break;
			
		case ConstantValue.SETTIMECMD2:
		case ConstantValue.SETTIMECMD:
			columnIdentifiers.addElement("序号");
			columnIdentifiers.addElement("剩余时间(分钟)");
			columnIdentifiers.addElement("禁用标志");
			break;

		case ConstantValue.MACHINECMD2:
		case ConstantValue.MACHINECMD:
			columnIdentifiers.addElement("序号");
			columnIdentifiers.addElement("牌主机号");
			columnIdentifiers.addElement("A副牌号");
			columnIdentifiers.addElement("B副牌号");
			columnIdentifiers.addElement("频点整");
			columnIdentifiers.addElement("频点小");
			columnIdentifiers.addElement("DEV");
			columnIdentifiers.addElement("RXFilter");
			columnIdentifiers.addElement("Address");
			columnIdentifiers.addElement("Power");
			
			break;

		case ConstantValue.CODERCMD2:
		case ConstantValue.CODERCMD:
			columnIdentifiers.addElement("序号");
			columnIdentifiers.addElement("配牌器号");
			break;

		case ConstantValue.CARDCMD2:
		case ConstantValue.CARDCMD:
			columnIdentifiers.addElement("序号");
			columnIdentifiers.addElement("牌主机号");
			columnIdentifiers.addElement("A副牌颜色");
			columnIdentifiers.addElement("B副牌颜色");
			columnIdentifiers.addElement("牌厂家");
			columnIdentifiers.addElement("牌型号");
			columnIdentifiers.addElement("牌背纹");
			break;

		case ConstantValue.GROUPCMD2:
		case ConstantValue.GROUPCMD:
			columnIdentifiers.addElement("序号");
			columnIdentifiers.addElement("群号");
			columnIdentifiers.addElement("群密码");
			columnIdentifiers.addElement("剩余时间(分钟)");
			columnIdentifiers.addElement("禁用标志");
			columnIdentifiers.addElement("主机列表");
			break;

		case ConstantValue.GROUPUSERCMD2:
		case ConstantValue.GROUPUSERCMD:
			columnIdentifiers.addElement("序号");
			columnIdentifiers.addElement("群号");
			columnIdentifiers.addElement("用户密码");
			columnIdentifiers.addElement("牌主机号");
			columnIdentifiers.addElement("剩余时间(分钟)");
			columnIdentifiers.addElement("禁用标志");
			break;
		case ConstantValue.CONFIGCODECMD:
		case ConstantValue.CONFIGCODECMD2:
			columnIdentifiers.addElement("");
			columnIdentifiers.addElement("");
			columnIdentifiers.addElement("");
			columnIdentifiers.addElement("");
			columnIdentifiers.addElement("");
			columnIdentifiers.addElement("");
			columnIdentifiers.addElement("");
			columnIdentifiers.addElement("");
			columnIdentifiers.addElement("");
//			initConfigCard();
			//setDataVector();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 配置牌的界面初始化
	 */
//	public void initConfigCard(){
//		dataVector.clear();
//		for(int i =0; i < 12; i++){
//			Vector<String> vStrings = new Vector<String>();
//			for(int j = 0;j < 9;j++){
//				vStrings.addElement(ConstantValue.cardNameStrings[i*9+j]);
//			}
//			dataVector.addElement(vStrings);
//		}
//		
//		for(int i =0; i < 4; i++){
//			Vector<String> vStrings = new Vector<String>();
//			for(int j = 0;j < 7;j++){
//				vStrings.addElement(ConstantValue.cardNameStrings[108+i*7+j]);
//			}
//			dataVector.addElement(vStrings);
//		}
//		Vector<String> vStrings = new Vector<String>();
//		for(int j = 0;j < 8;j++){
//			vStrings.addElement(ConstantValue.cardNameStrings[136+j]);
//		}
//		dataVector.addElement(vStrings);
//		
///*		cardVector = new Vector<JButton>();
//		for(int i = 0; i < 136; i++){
//			JButton tmpButton = new JButton();
//			ImageIcon icon = new ImageIcon("res\\"+ConstantValue.cardNameStrings[i]+".JPG");
//			tmpButton.setIcon(icon);
//			cardVector.addElement(tmpButton);
////			jConfigCardPanel.add(tmpButton);
//		}*/
//	}


}
