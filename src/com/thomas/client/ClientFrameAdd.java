package com.thomas.client;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import org.apache.commons.lang3.StringUtils;

import com.sun.corba.se.impl.ior.iiop.JavaCodebaseComponentImpl;
import com.sun.org.apache.regexp.internal.recompile;
import com.thomas.data.ConstantValue;

public class ClientFrameAdd extends JFrame {

	private static final long serialVersionUID = 1L;

	private int opertype;// 1 add; 2 modify
	private int icmdcode;
	private Vector dataVector;
	private Vector insideVector;// 数据搜集
	private Vector oldVector;// 判断重复使用
	private JButton okButton, cancleButton;
	private JScrollPane jPanel = new JScrollPane();
	private ClientTableModel tableModel;
	private ClientTable datatable;
	private JPanel jButtonPane = new JPanel();
	private JPanel jCardPanel = new JPanel();
	private Vector<JCheckBox> checkList = new Vector<JCheckBox>();

	public ClientFrameAdd(int cmdcode, Vector data) {
		icmdcode = cmdcode;
		opertype = 1;
		dataVector = new Vector(1);
		oldVector = new Vector();
		oldVector = (Vector) data.clone();

		if (icmdcode == ConstantValue.PASSWORDCMD) {
			buildDataVector(3);
		} else if (icmdcode == ConstantValue.SETTIMECMD) {
			buildDataVector(3);
		} else if (icmdcode == ConstantValue.MACHINECMD) {
			buildDataVector(10);
		} else if (icmdcode == ConstantValue.CODERCMD) {
			buildDataVector(2);
		} else if (icmdcode == ConstantValue.CARDCMD) {
			buildDataVector(7);
		} else if (icmdcode == ConstantValue.GROUPCMD) {
			buildDataVector(6);
		} else if (icmdcode == ConstantValue.GROUPUSERCMD) {
			buildDataVector(6);
			insideVector.setElementAt(getGroupID(), 1);//直接填入ID
		}

		initFrame();
	}

	public void buildDataVector(int count) {
		insideVector = new Vector(count);

		for (int i = 0; i < count; i++) {
			insideVector.addElement("");
		}
		dataVector.addElement(insideVector);
	}

	public ClientFrameAdd(int cmdcode, Vector data, int currow) {
		icmdcode = cmdcode;
		opertype = 2;
		dataVector = new Vector();
		insideVector = new Vector();
		oldVector = new Vector();
		oldVector = (Vector) data.clone();

		insideVector = (Vector) ((Vector) data.elementAt(currow)).clone();
		dataVector.addElement(insideVector);

		initFrame();
	}

	public void initFrame() {
		// try {
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		layoutComponents();

		if (icmdcode == ConstantValue.CARDCMD
				|| icmdcode == ConstantValue.GROUPCMD
				|| icmdcode == ConstantValue.GROUPUSERCMD) {
			this.setSize(800, 700);
		} else {
			this.setSize(800, 300);
		}
		this.setTitle("增加修改对话框");
		this.setLocationRelativeTo(null);
		// this.setResizable(false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(true);

		try {
			enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void layoutComponents() {
		tableModel = new ClientTableModel(icmdcode);
		tableModel.setRowData(dataVector);
		tableModel.setDataVector();
		datatable = new ClientTable(tableModel) {
			@Override
			public boolean isCellEditable(int row, int column) {
				if (opertype == 1) {// add
					if (column == 0
							|| (icmdcode == ConstantValue.GROUPCMD && (column == 5))
							|| (icmdcode == ConstantValue.GROUPUSERCMD && (column == 3||column==1))
							|| (icmdcode == ConstantValue.CARDCMD && (column == 1))) {
						return false;
					} else {
						return true;
					}
				} else {// modify
					if (icmdcode == ConstantValue.PASSWORDCMD) {
						if (column == 0 || column == 2) {
							return false;
						} else {
							return true;
						}
					} else if(icmdcode == ConstantValue.SETTIMECMD){
						if (column == 0) {
							return false;
						} else {
							return true;
						}
					}
					else {
						if (column == 0
								|| column == 1
								|| (icmdcode == ConstantValue.GROUPCMD && (column == 2||column == 5))
								|| (icmdcode == ConstantValue.GROUPUSERCMD && (column == 2||column == 3||column==1))) {
							return false;
						} else {
							return true;
						}
					}
				}
			}
		};
		jPanel = new JScrollPane(datatable);
		// jPanel.setSize(800, 100);
		// jHostPanel.setSize(800, 200);

		okButton = new JButton("确定");
		cancleButton = new JButton("取消");
		jButtonPane.add(okButton);
		jButtonPane.add(cancleButton);

		if(icmdcode == ConstantValue.CARDCMD){
			// 获得可用主机列表
			Vector retVector = processMachineList();

			Collections.sort(retVector);

			for (int i = 0; i < retVector.size(); i++) {
				JCheckBox tmpCheckBox = new JCheckBox(
						(String) retVector.elementAt(i));
				checkList.addElement(tmpCheckBox);
				jCardPanel.add(tmpCheckBox);
			}

			setLayout(new BorderLayout());
			getContentPane().add(BorderLayout.NORTH, jPanel);
			getContentPane().add(BorderLayout.CENTER, jCardPanel);
			getContentPane().add(BorderLayout.SOUTH, jButtonPane);
		}else if (icmdcode == ConstantValue.GROUPCMD) {
			// 获得可用主机列表
			Vector retVector = processCardList();

			Collections.sort(retVector);

			for (int i = 0; i < retVector.size(); i++) {
				JCheckBox tmpCheckBox = new JCheckBox(
						(String) retVector.elementAt(i));
				checkList.addElement(tmpCheckBox);
				jCardPanel.add(tmpCheckBox);
			}

			setLayout(new BorderLayout());
			getContentPane().add(BorderLayout.NORTH, jPanel);
			getContentPane().add(BorderLayout.CENTER, jCardPanel);
			getContentPane().add(BorderLayout.SOUTH, jButtonPane);

		} else if (icmdcode == ConstantValue.GROUPUSERCMD) {
			// 获得可用主机列表
			Vector retVector = processCardListGroup();

			Collections.sort(retVector);

			for (int i = 0; i < retVector.size(); i++) {
				JCheckBox tmpCheckBox = new JCheckBox(
						(String) retVector.elementAt(i));
				checkList.addElement(tmpCheckBox);
				jCardPanel.add(tmpCheckBox);
			}

			setLayout(new BorderLayout());
			getContentPane().add(BorderLayout.NORTH, jPanel);
			getContentPane().add(BorderLayout.CENTER, jCardPanel);
			getContentPane().add(BorderLayout.SOUTH, jButtonPane);

		} else {
			setLayout(new BorderLayout());
			getContentPane().add(BorderLayout.CENTER, jPanel);
			getContentPane().add(BorderLayout.SOUTH, jButtonPane);

		}

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (datatable.isEditing()) {
						datatable.getCellEditor().stopCellEditing();
					}
					// 判断数据合法性&收集数据
					if (!getInputData()) {
						return;
					}

					// 配置数据
					if (opertype == 1) {
						ClientMain.frameWin.onAddCmd(insideVector);
					} else if (opertype == 2) {
						ClientMain.frameWin.onModiCmd(insideVector);
					}

					dispose();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		cancleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					dispose();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * 
	 * @return
	 */
	public boolean getInputData() {
		if (icmdcode == ConstantValue.PASSWORDCMD) {
			String strName = (String) tableModel.getValueAt(0, 1);
			if (strName.trim().length() != 10) {
				JOptionPane.showMessageDialog(null, "输入数据不合法,请输入正确的名称或数字!",
						"错误", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			String strGrade = (String) tableModel.getValueAt(0, 2);
			if(!strGrade.equals("1") || !strGrade.equals("1") || !strGrade.equals("1")){
				JOptionPane.showMessageDialog(null, "级别只能输入1、2、3，代表3级用户!",
						"错误", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			for (int i = 0; i < oldVector.size(); i++) {
				if (((Vector) oldVector.elementAt(i)).elementAt(1).equals(
						strName)) {
					JOptionPane.showMessageDialog(null, "输入数据不合法,用户名重复！", "错误",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}


			insideVector.setElementAt(strName, 1);
			insideVector.setElementAt(strGrade, 2);

		} else if (icmdcode == ConstantValue.SETTIMECMD) {
			String strtime = (String) tableModel.getValueAt(0, 1);
			String strflag = (String) tableModel.getValueAt(0, 2);
			if (Integer.parseInt(strtime) < 0
					|| Integer.parseInt(strtime) > 6000000) {
				JOptionPane.showMessageDialog(null,
						"输入数据不合法,时间最长支持600万分钟（10万小时）！", "错误",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if (Integer.parseInt(strflag) < 0
					|| Integer.parseInt(strflag) > 1) {
				JOptionPane.showMessageDialog(null,
						"输入数据不合法,禁用标志请输入0或者1！", "错误",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}

			insideVector.setElementAt(strtime, 1);
			insideVector.setElementAt(strflag, 2);

		} else if (icmdcode == ConstantValue.MACHINECMD) {
			for (int i = 1; i < 4; i++) {
				String strvalue = (String) tableModel.getValueAt(0, i);
				if (Integer.parseInt(strvalue) < 0
						|| Integer.parseInt(strvalue) > 255) {
					JOptionPane.showMessageDialog(null, "主机、牌副码输入数据不合法,数字不能大于255！",
							"错误", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				insideVector.setElementAt(strvalue, i);
			}
			for (int i = 4; i < 10; i++) {
				String strvalue = (String) tableModel.getValueAt(0, i);
				if (Integer.parseInt(strvalue) < 0
						|| Integer.parseInt(strvalue) > 65535) {
					JOptionPane.showMessageDialog(null, "参数输入数据不合法,数字不能大于65535！",
							"错误", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				insideVector.setElementAt(strvalue, i);
			}
		} else if (icmdcode == ConstantValue.CODERCMD) {
			String strid = (String) tableModel.getValueAt(0, 1);
			if (Integer.parseInt(strid) < 0 || Integer.parseInt(strid) > 255) {
				JOptionPane.showMessageDialog(null, "输入数据不合法,数字不能大于255！",
						"错误", JOptionPane.ERROR_MESSAGE);
				return false;
			}

			insideVector.setElementAt(strid, 1);

		} else if (icmdcode == ConstantValue.CARDCMD) {
			for (int i = 2; i < 7; i++) {
				String strvalue = (String) tableModel.getValueAt(0, i);
				if (strvalue.equals("")) {
					JOptionPane.showMessageDialog(null, "输入数据不合法,请输入数据！", "错误",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}
				{// 字符串补齐20个字符，中文3个字符
					int length = strvalue.length();
					int count = getChineseCount(strvalue);
					if (count * 3 + length > 20) {
						JOptionPane.showMessageDialog(null,
								"输入数据不合法,最多支持20个字符或者6个汉字！", "错误",
								JOptionPane.ERROR_MESSAGE);
						return false;
					}
					String value = StringUtils.rightPad(strvalue,
							20 - 2 * count, " ");

					insideVector.setElementAt(value, i);
				}
			}
			// 搜集选择的主机
			String strmachineString = "";
			int icount = 0;
			for (int i = 0; i < checkList.size(); i++) {
				JCheckBox tmpCheckBox = checkList.elementAt(i);
				if (tmpCheckBox.isSelected()) {
					icount++;
					strmachineString = tmpCheckBox.getText();
				}
			}
			if (icount > 1) {
				JOptionPane.showMessageDialog(null, "请选择一个主机！", "错误",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			insideVector.setElementAt(strmachineString, 1);

		} else if (icmdcode == ConstantValue.GROUPCMD) {
			int icurtime = 0;
			for (int i = 1; i < 5; i++) {
				String strvalue = (String) tableModel.getValueAt(0, i);
				if (strvalue.equals("")) {
					JOptionPane.showMessageDialog(null, "输入数据不合法,请输入数据！", "错误",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}
				if (i == 1 || i == 3 || i == 4) {
					insideVector.setElementAt(strvalue, i);
					if (i == 3) {
						icurtime = Integer.parseInt(strvalue);
					}
				} else if(i == 2){//password 10char
					int length = strvalue.length();
					if (length != 10) {
						JOptionPane.showMessageDialog(null,
								"输入数据不合法,密码需要输入10个字符！", "错误",
								JOptionPane.ERROR_MESSAGE);
						return false;
					}

					insideVector.setElementAt(strvalue, i);
				}else{
					
				}
			}
			// 搜集选择的主机牌
			StringBuilder strCardList = new StringBuilder();
			for (int i = 0; i < checkList.size(); i++) {
				JCheckBox tmpCheckBox = checkList.elementAt(i);
				if (tmpCheckBox.isSelected()) {
					strCardList.append(tmpCheckBox.getText());
					strCardList.append(",");
				}
			}
			
			if (strCardList.length() == 0) {
				JOptionPane.showMessageDialog(null, "请至少选择一个主机！", "错误",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			insideVector.setElementAt(strCardList.toString(), 5);

			// 判断时间是否超额
			Vector dataVector = ClientMain.frameWin.timetableModel
					.getDataVector();
			Vector eleVector = (Vector) dataVector.elementAt(0);
			int iTotalTime = Integer.parseInt((String) eleVector.elementAt(1));
			dataVector = ClientMain.frameWin.grouptableModel.getDataVector();
			int iusedtime = 0;
			for (int k = 0; k < dataVector.size(); k++) {
				eleVector = (Vector) dataVector.elementAt(k);
				// 修改
				if (opertype == 2
						&& ((String) eleVector.elementAt(1))
								.equals((String) insideVector.elementAt(1))) {// modify
					continue;
				}
				iusedtime += Integer.parseInt((String) eleVector.elementAt(3));
			}

			iusedtime += icurtime;
			if (iusedtime > iTotalTime) {
				JOptionPane.showMessageDialog(null, "输入数据不合法,群总计时间已经超过授权总时间！",
						"错误", JOptionPane.ERROR_MESSAGE);
				return false;
			}

		} else if (icmdcode == ConstantValue.GROUPUSERCMD) {
			int icurtime = 0;
			for (int i = 1; i < 6; i++) {
				if (i == 3) {
					continue;
				}
				String strvalue = (String) tableModel.getValueAt(0, i);
				if (strvalue.equals("")) {
					JOptionPane.showMessageDialog(null, "输入数据不合法,请输入数据！", "错误",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}
				if (i == 1 || i == 4 || i == 5) {
					insideVector.setElementAt(strvalue, i);
					if (i == 4) {
						icurtime = Integer.parseInt(strvalue);
					}
				}else if(i == 2){//password 10char
					int length = strvalue.length();
					if (length != 10) {
						JOptionPane.showMessageDialog(null,
								"输入数据不合法,密码需要输入10个字符！", "错误",
								JOptionPane.ERROR_MESSAGE);
						return false;
					}

					insideVector.setElementAt(strvalue, i);
				}
				
			}
			// 搜集选择的主机牌
			String strmachineString = "";
			int icount = 0;
			for (int i = 0; i < checkList.size(); i++) {
				JCheckBox tmpCheckBox = checkList.elementAt(i);
				if (tmpCheckBox.isSelected()) {
					icount++;
					strmachineString = tmpCheckBox.getText();
				}
			}
			if (icount > 1 || icount == 0) {
				JOptionPane.showMessageDialog(null, "请选择一个主机！", "错误",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			

			// 判断时间是否超额
			Vector dataVector = ClientMain.frameWin.grouptableModel
					.getDataVector();
			//total time
			int iTotalTime = 0;
			Vector eleVector;
			for(int i = 0; i < dataVector.size();i++){
				eleVector = (Vector) dataVector.elementAt(i);
				//find the group
				String groupid = (String)eleVector.elementAt(1);
				if(groupid.equals((String)insideVector.elementAt(1))){
					iTotalTime = Integer.parseInt((String)eleVector.elementAt(3));
					break;
				}
			}
			//used time
			dataVector = ClientMain.frameWin.groupusertableModel.getDataVector();
			int iusedtime = 0;
			for (int k = 0; k < dataVector.size(); k++) {
				eleVector = (Vector) dataVector.elementAt(k);
				// 修改
				if (opertype == 2
						&& ((String) eleVector.elementAt(2))
								.equals((String) insideVector.elementAt(2))) {// modify
					continue;
				}
				iusedtime += Integer.parseInt((String) eleVector.elementAt(4));
			}

			iusedtime += icurtime;
			if (iusedtime > iTotalTime) {
				JOptionPane.showMessageDialog(null, "输入数据不合法,群所有用户总计时间已经超过群授权总时间！",
						"错误", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			insideVector.setElementAt(strmachineString, 3);
		}

		return true;
	}

	public int getChineseCount(String str) {
		int count = 0;
		String reg = "[\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(str);
		while (m.find()) {
			for (int i = 0; i <= m.groupCount(); i++) {
				count = count + 1;
			}
		}
		return count;

	}

	/**
	 * 
	 * 
	 * 群新增修改情况下，获取所有可用的主机牌
	 */
	public Vector processCardList() {
		Vector retVector = new Vector();
		Vector dataVector = ClientMain.frameWin.cardtableModel.getDataVector();
		Vector allCardVector = new Vector();
		// 所有的主机牌
		for (int i = 0; i < dataVector.size(); i++) {
			Vector tmpVector = (Vector) dataVector.elementAt(i);
			String strCardId = (String) tmpVector.elementAt(1);
			allCardVector.addElement(strCardId);
		}
		// 已经使用的主机牌
		Vector dataVector2 = ClientMain.frameWin.grouptableModel
				.getDataVector();
		Vector usedCardVector = new Vector();
		for (int i = 0; i < dataVector2.size(); i++) {
			Vector tmpVector = (Vector) dataVector2.elementAt(i);
			// 如果是修改，则要加上自己的牌
			if (opertype == 2) {
				String str1 = (String) datatable.getValueAt(0, 1);
				String str2 = (String) tmpVector.elementAt(1);
				if (str1.equals(str2)) {
					continue;
				}
			}
			String stridlist = (String) tmpVector.elementAt(5);// 主机列表
			String[] strid = stridlist.split(",");
			for (String str : strid) {
				if (!str.equals("")) {
					usedCardVector.addElement(str);
				}
			}
		}

		// 获取可用的主机牌
		allCardVector.removeAll(usedCardVector);

		return allCardVector;

	}

	/**
	 * 群用户获得可用的主机列表
	 * 
	 * @return
	 */
	public Vector processCardListGroup() {
		Vector retVector = new Vector();
		Vector dataVector = ClientMain.frameWin.grouptableModel.getDataVector();
		Vector allCardVector = new Vector();
		// 该群所有的主机牌
		for (int i = 0; i < dataVector.size(); i++) {
			Vector tmpVector = (Vector) dataVector.elementAt(i);
			String strGroupPass = (String) tmpVector.elementAt(2);
			if (strGroupPass.equals(ConstantValue.strUserName)) {
				String stridlist = (String) tmpVector.elementAt(5);// 主机列表
				String[] strid = stridlist.split(",");
				for (String str : strid) {
					if (!str.equals("")) {
						allCardVector.addElement(str);
					}
				}
			}
		}
		// 该群已经使用的主机牌,当前用户已使用主机牌
		Vector dataVector2 = ClientMain.frameWin.groupusertableModel
				.getDataVector();
		Vector usedCardVector = new Vector();
		for (int i = 0; i < dataVector2.size(); i++) {
			Vector tmpVector = (Vector) dataVector2.elementAt(i);
			if (opertype == 2) {
				String str1 = (String) datatable.getValueAt(0, 1);
				String str2 = (String) tmpVector.elementAt(1);
				if (str1.equals(str2)) {
					continue;
				}
			}
			String strMachine = (String) tmpVector.elementAt(3);
			usedCardVector.addElement(strMachine);
		}

		// 获取可用的主机牌
		allCardVector.removeAll(usedCardVector);

		return allCardVector;

	}
	
	/**
	 * 厂家配置牌的时候，获取所有的主机列表
	 * @return
	 */
	public Vector processMachineList(){
		Vector retVector = new Vector();
		Vector dataVector = ClientMain.frameWin.machinetableModel.getDataVector();
		Vector allCardVector = new Vector();
		// 所有的主机牌
		for (int i = 0; i < dataVector.size(); i++) {
			Vector tmpVector = (Vector) dataVector.elementAt(i);
			String strCardId = (String) tmpVector.elementAt(1);
			allCardVector.addElement(strCardId);
		}
		// 已经使用的主机牌
		Vector dataVector2 = ClientMain.frameWin.cardtableModel
				.getDataVector();
		Vector usedCardVector = new Vector();
		for (int i = 0; i < dataVector2.size(); i++) {
			Vector tmpVector = (Vector) dataVector2.elementAt(i);
			
			// 如果是修改，则要加上自己的牌
			if (opertype == 2) {
				String str1 = (String) datatable.getValueAt(0, 1);
				String str2 = (String) tmpVector.elementAt(1);
				if (str1.equals(str2)) {
					continue;
				}
			}
			String strCardId = (String) tmpVector.elementAt(1);
			usedCardVector.addElement(strCardId);
		}

		// 获取可用的主机牌
		allCardVector.removeAll(usedCardVector);

		return allCardVector;
	}
	
	/**
	 * 根据登录的群密码获取群ID
	 * @return
	 */
	public String getGroupID(){
		String strGroupId = "";
		Vector dataVector = ClientMain.frameWin.grouptableModel.getDataVector();
		// 所有的
		for (int i = 0; i < dataVector.size(); i++) {
			Vector tmpVector = (Vector) dataVector.elementAt(i);
			strGroupId = (String) tmpVector.elementAt(1);
			String strUserPass = (String)tmpVector.elementAt(2);
			if(strUserPass.equals(ConstantValue.strUserName)){
				break;
			}
		}
		return strGroupId;
	}

}
