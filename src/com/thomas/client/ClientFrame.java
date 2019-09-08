package com.thomas.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.thomas.data.ConstantValue;
import com.thomas.data.I单张牌数据;
import com.thomas.data.I牌堆数据;
import com.thomas.data.I牌堆行数据;
import com.thomas.data.Ulti;
import com.thomas.logic.Bridge;
import com.thomas.logic.PlayerTileData;

public class ClientFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTabbedPane jTabbedpane = new JTabbedPane();// 存放选项卡的组件
	public JTabbedPane getjTabbedpane() {
		return jTabbedpane;
	}
	//	private String[] tabNames = { "密码管理", "服务器时间管理", "牌主机管理", "配牌器管理", "牌管理",
//			"群管理", "配牌管理", "群用户管理", "" };
	private String[] tabNames = { "密码管理", "服务器时间管理", "牌主机管理", "配牌器管理", "牌管理",
			"群管理", "", "", "" };	
	private JScrollPane jPassPanel = new JScrollPane();
	private JScrollPane jTimePanel = new JScrollPane();
	private JScrollPane jMachinePanel = new JScrollPane();
	private JScrollPane jCoderPanel = new JScrollPane();
	private JScrollPane jCardPanel = new JScrollPane();
	private JScrollPane jGroupPanel = new JScrollPane();
	private JPanel jConfigCardPanel = new JPanel();	 	//配牌
	private JPanel jConfigcardPanel2 = new JPanel();
	private JScrollPane jGroupUserPanel = new JScrollPane();
	private TestPanel jPlayerPanelA = new TestPanel();
	private TestPanel jPlayerPanelB = new TestPanel();
	private JScrollPane jPlayerPanel2 = new JScrollPane();
	private ClientPlayerPanel jPlayerPanel3 = new ClientPlayerPanel();
	private JProgressBar progressBar = new JProgressBar();

	private JPanel jButtonPane = new JPanel();// 存放按钮的组件
	private JLabel stateLabel, plainLabel;
	private JButton queryButton, addButton, delButton, modiButton, exitButton,stopButton,configButton; // 各处理按钮
	private JLabel linkLabel,machineLabel; 

	// 开始画所有的界面
	public ClientTableModel passtableModel;
	public ClientTable passTable;
	public ClientTableModel timetableModel;
	public ClientTable timeTable;
	public ClientTableModel machinetableModel;
	public ClientTable machineTable;
	public ClientTableModel codertableModel;
	public ClientTable coderTable;
	public ClientTableModel cardtableModel;
	public ClientTable cardTable;
	public ClientTableModel grouptableModel;
	public ClientTable groupTable;
	public ClientTableModel configcardtableModel;
	public ClientTable configcardTable;
	public ClientTableModel groupusertableModel;
	public ClientTable groupuserTable;
	
	//配牌器界面控件
	private JPanel jConfigSelectJPanel = new JPanel();
	private JLabel jMachineJLabel = new JLabel("待配置的牌主机:");
	private JLabel jCardcolorJLabel = new JLabel("待配置的AB牌:");
	public JComboBox<String> jMachineBox = new JComboBox<String>();
	private JComboBox<String> jCardbBox = new JComboBox<String>();
	private JLabel jProgressLabel = new JLabel("配牌进度:");
	private JLabel jVoltageLabel = new JLabel("电量:");
	private JTextField jVoltageField = new JTextField();
	
	//牌信息
	ExcelReader tileInfo;// = new ExcelReader();

	//测试用
	private JTextArea jTextArea = new JTextArea();
	//当前显示AB牌切换
	private int ABInfo = 0;// 1,A; 2,B

	/**
	 * 当前选中的table,model,row,cmdcode
	 * 
	 */
	public ClientTable curTable;
	public ClientTableModel curTableModel;
	public int curRow;
	// add/del/mod query
	public int curcmdcode, curcmdcode2;
	public int currentProgress = 0;
	public Vector<JButton> cardButtons = new Vector<JButton>();
	public int curcardcode = 0;
	//进度条
	ActionListener listener = new ProgressRunner();
    Timer timer = new Timer(100, listener);
//    public static TestDialog testDialog;
	
    /**
     * 进度条
     * @author 
     *
     */
	class ProgressRunner implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			currentProgress++;
			if(currentProgress > 100){
				currentProgress = 0;
				timer.stop();
				//解锁按钮
				setEnabled(true);
				progressBar.setValue(0);
			}
			progressBar.setValue(currentProgress);
		} 
		
	}

	public ClientFrame() {
//		try {  
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());  
//        } catch (Exception e) {  
//            e.printStackTrace();  
//        }  
		tileInfo = ClientMain.tileInfo;
		layoutComponents();
		getSelectedTableIndex();
		
		
//		this.setSize(1000, 700);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		if (ConstantValue.USERTYPE == ConstantValue.ADMIN) {
			this.setTitle("系统管理");
		} else if (ConstantValue.USERTYPE == ConstantValue.MANAGER) {
			this.setTitle("厂家管理");
		} else if (ConstantValue.USERTYPE == ConstantValue.WORKER) {
			this.setTitle("配牌软件");
		} else if (ConstantValue.USERTYPE == ConstantValue.GROUPER) {
			this.setTitle("群管理软件");
		} else {
			this.setTitle("麻将软件");
		}

//		this.setTitle("客户端");
		// this.setVisible(true);
		this.setLocationRelativeTo(null);
//		this.setResizable(false);
//		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		try {
			enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int option = JOptionPane.showConfirmDialog(ClientFrame.this,
						"是否退出软件? ", "提示 ", JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION){
					if (e.getWindow() == ClientFrame.this) {
						System.exit(0);
					} else {
						return;
					}
				}
			}
		});
	}
	


	private void layoutComponents() {
		passtableModel = new ClientTableModel(ConstantValue.PASSWORDCMD2);
		passTable = new ClientTable(passtableModel);
		jPassPanel = new JScrollPane(passTable);

		timetableModel = new ClientTableModel(ConstantValue.SETTIMECMD2);
		timeTable = new ClientTable(timetableModel);
		jTimePanel = new JScrollPane(timeTable);

		machinetableModel = new ClientTableModel(ConstantValue.MACHINECMD2);
		machineTable = new ClientTable(machinetableModel);
		jMachinePanel = new JScrollPane(machineTable);

		codertableModel = new ClientTableModel(ConstantValue.CODERCMD2);
		coderTable = new ClientTable(codertableModel);
		jCoderPanel = new JScrollPane(coderTable);

		cardtableModel = new ClientTableModel(ConstantValue.CARDCMD2);
		cardTable = new ClientTable(cardtableModel);
		jCardPanel = new JScrollPane(cardTable);

		grouptableModel = new ClientTableModel(ConstantValue.GROUPCMD2);
		groupTable = new ClientTable(grouptableModel);
		jGroupPanel = new JScrollPane(groupTable);

		//配牌器
		configcardtableModel = new ClientTableModel(ConstantValue.CONFIGCODECMD2);
		configcardTable = new ClientTable(configcardtableModel);
		configcardTable.setCellSelectionEnabled(true);
		configcardTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		new JScrollPane(configcardTable);

		jConfigSelectJPanel.add(jMachineJLabel);
		jMachineBox.setPreferredSize(new Dimension(120, 30));
		jConfigSelectJPanel.add(jMachineBox);
		jConfigSelectJPanel.add(jCardcolorJLabel);
		jCardbBox.setPreferredSize(new Dimension(120, 30));
		jConfigSelectJPanel.add(jCardbBox);
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
//		progressBar.setForeground(new Color(255, 0, 0)); 
		jConfigSelectJPanel.add(jProgressLabel);
		jConfigSelectJPanel.add(progressBar);
		jVoltageField.setColumns(10);
		jVoltageField.setEditable(false);
		jConfigSelectJPanel.add(jVoltageLabel);
		jConfigSelectJPanel.add(jVoltageField);
		
		//测试用
		jPlayerPanel2.setViewportView(jTextArea);
//		jPlayerPanel.setLayout(new BoxLayout(jPlayerPanel,BoxLayout.Y_AXIS));
		
		jConfigcardPanel2.setLayout(new java.awt.GridBagLayout());
		//配牌代码
		for(int i = 0; i < 144; i++){
			final String tileName = ConstantValue.cardPicStrings2[i];
			final JButton button = new JButton();
			button.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent arg0) {
					//牌主机号
					if(jMachineBox.getSelectedItem() == null){
						JOptionPane.showMessageDialog(null, "请选择主机！", "信息",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					//牌副码
					if(jCardbBox.getSelectedIndex() == -1){
						JOptionPane.showMessageDialog(null, "请选择AB牌！", "信息",
									JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(
							null, "确认配置该牌吗？", "配置", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE)) {
						return;
					}
					//界面先锁死
					setEnabled(false);
					jVoltageField.setText("");
					timer.start();
					//下发命令
					ChannelFuture cf = ClientMain.loginWin.getChannelFuture();
					ByteBuf cmdBuf = cf.channel().alloc().buffer();
					cmdBuf.writeShort(ConstantValue.USERTYPE);
					cmdBuf.writeShort(curcmdcode); // 当前的命令
					cmdBuf.writeShort(ConstantValue.NULLREC);
					cmdBuf.writeBytes(ConstantValue.strUserName.getBytes());// 10 bytes
					//主机
					int machineid = Integer.parseInt(jMachineBox.getSelectedItem().toString());
					cmdBuf.writeByte(machineid);
					//牌副码
					int index = jCardbBox.getSelectedIndex();
					cmdBuf.writeByte(index);
					
					ArrayList<Integer> tilelist = getTileParameter(tileName, index);

					//当前配置牌ID
					curcardcode = tilelist.get(0);

					//牌码
					cmdBuf.writeByte(curcardcode);
					//测试用
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(0).get("牌中心频点整数部分"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(1).get("牌中心频点小数部分"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(2).get("牌deviation"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(3).get("牌RX Filter"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(4).get("牌工作address"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(5).get("牌TX Power"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(6).get("WOR唤醒间隔"),16));
					cmdBuf.writeShort(tilelist.get(2));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(7).get("T0"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(8).get("T1-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(9).get("T2-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(10).get("T3-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(11).get("T4-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(12).get("T5-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(13).get("T6-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(14).get("T7-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(15).get("T8-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(16).get("T9-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(17).get("T10-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(18).get("T11-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(19).get("T12-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(20).get("T13-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(21).get("T14-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(22).get("T15-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(23).get("T16-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(24).get("T17-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(25).get("T18-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(26).get("T19-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(27).get("T20-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(28).get("T21-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(29).get("T22-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(30).get("T23-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(31).get("T24-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(32).get("T25-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(33).get("T26-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(34).get("T27-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(35).get("T28-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(36).get("T29-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(37).get("T30-A"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(38).get("T1-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(39).get("T2-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(40).get("T3-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(41).get("T4-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(42).get("T5-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(43).get("T6-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(44).get("T7-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(45).get("T8-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(46).get("T9-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(47).get("T10-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(48).get("T11-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(49).get("T12-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(50).get("T13-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(51).get("T14-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(52).get("T15-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(53).get("T16-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(54).get("T17-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(55).get("T18-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(56).get("T19-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(57).get("T20-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(58).get("T21-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(59).get("T22-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(60).get("T23-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(61).get("T24-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(62).get("T25-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(63).get("T26-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(64).get("T27-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(65).get("T28-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(66).get("T29-B"),16));
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(67).get("T30-B"),16));
					cmdBuf.writeShort(tilelist.get(1));//互感选边码
					cmdBuf.writeShort(Integer.parseInt(tileInfo.list2.get(68).get("互感次数"),16));
					cmdBuf.writeShort(tilelist.get(3));
					cmdBuf.writeShort(tilelist.get(4));
					cmdBuf.writeShort(tilelist.get(5));
					cmdBuf.writeShort(tilelist.get(6));
					cmdBuf.writeShort(tilelist.get(7));
					cmdBuf.writeShort(tilelist.get(8));
					cmdBuf.writeShort(tilelist.get(9));
					cmdBuf.writeShort(tilelist.get(10));
					cmdBuf.writeShort(tilelist.get(11));
					cmdBuf.writeShort(tilelist.get(12));
					cmdBuf.writeShort(tilelist.get(13));

					//测试结束
					cmdBuf.writeInt(0xFBFEBFEF);

					cf.channel().writeAndFlush(cmdBuf);
				}
			});
			button.setPreferredSize(new Dimension(45,60));
			int name = ClientPlayerPanel.cardPics[i];
			ImageIcon icon = new ImageIcon("./res/type_2/card_type_down/" + name + ".png");
			button.setIcon(icon);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = i % 16;
			gridBagConstraints1.gridy = 2 * (i / 16);
			jConfigcardPanel2.add(button, gridBagConstraints1);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = i % 16;
			gridBagConstraints2.gridy = 1 + 2 * (i / 16);
			jConfigcardPanel2.add(new JLabel(String.valueOf(1 + i % 4)), gridBagConstraints2);
			cardButtons.addElement(button);
		}
		jConfigCardPanel.setLayout(new BorderLayout());
		jConfigCardPanel.add(BorderLayout.NORTH, jConfigSelectJPanel);
//		jConfigCardPanel.add(BorderLayout.CENTER, jConfigCardJScrollPane);
		jConfigCardPanel.add(BorderLayout.CENTER, jConfigcardPanel2);
		
		groupusertableModel = new ClientTableModel(ConstantValue.GROUPUSERCMD2);
		groupuserTable = new ClientTable(groupusertableModel);
		jGroupUserPanel = new JScrollPane(groupuserTable);
		
		jTabbedpane.setTabPlacement(JTabbedPane.LEFT );
		if (ConstantValue.USERTYPE == ConstantValue.ADMIN) {
			int i = 0;
			jTabbedpane.addTab(tabNames[i++], null, jPassPanel, "jPassPanel");// 加入第一个页面
			jTabbedpane.addTab(tabNames[i++], null, jTimePanel, "jTimePanel");
			jTabbedpane.addTab(tabNames[i++], null, jMachinePanel,
					"jMachinePanel");
			jTabbedpane.addTab(tabNames[i++], null, jCoderPanel, "jCoderPanel");
		} else if (ConstantValue.USERTYPE == ConstantValue.MANAGER) {
			int i = 4;
			jTabbedpane.addTab(tabNames[i++], null, jCardPanel, "jCardPanel");
			jTabbedpane.addTab(tabNames[i++], null, jGroupPanel, "jGroupPanel");
		} else if (ConstantValue.USERTYPE == ConstantValue.WORKER) {
			int i = 6;
			jTabbedpane.addTab(tabNames[i++], null, jConfigCardPanel,
					"jConfigCardPanel");
		} else if (ConstantValue.USERTYPE == ConstantValue.GROUPER) {
			int i = 7;
			jTabbedpane.addTab(tabNames[i++], null, jGroupUserPanel,
					"jGroupUserPanel");
		} else {
			int i = 8;
			jTabbedpane.addTab("A", null, jPlayerPanelA,
					"A牌");
			jTabbedpane.addTab("B", null, jPlayerPanelB,
					"B牌");
			
			jTabbedpane.addTab(tabNames[i], null, jPlayerPanel2,
					"上报数据");
//			jTabbedpane.addTab(tabNames[i], null, jPlayerPanel3,
//					"jPlayerPanel3");
		}

		stateLabel = new JLabel("信息");
		stateLabel.setPreferredSize(new Dimension(30, 30));		
		plainLabel = new JLabel("");
		plainLabel.setPreferredSize(new Dimension(30, 30));	
		queryButton = new JButton("查询");
		addButton = new JButton("增加");
		delButton = new JButton("删除");
		modiButton = new JButton("修改");
		stopButton = new JButton("停止");
		exitButton = new JButton("退出");
		configButton=new JButton();
		configButton.setIcon(new javax.swing.ImageIcon("./res/config1.jpg"));
		configButton.setMaximumSize(new java.awt.Dimension(30, 30));
		configButton.setMinimumSize(new java.awt.Dimension(30, 30));
		configButton.setPreferredSize(new java.awt.Dimension(30, 30));
		configButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
						ConfigDialog dialog = new ConfigDialog(ClientFrame.this, true);
                        dialog.setVisible(true);
                    }
                });
            }
        });
//		JButton testButton = new JButton();
//		testButton.setMaximumSize(new java.awt.Dimension(30, 30));
//		testButton.setMinimumSize(new java.awt.Dimension(30, 30));
//		testButton.setPreferredSize(new java.awt.Dimension(30, 30));
//		testButton.addActionListener(new java.awt.event.ActionListener() {
//			public void actionPerformed(java.awt.event.ActionEvent evt) {
//				java.awt.EventQueue.invokeLater(new Runnable() {
//					public void run() {
//						testDialog = new TestDialog(ClientFrame.this);
//						testDialog.setVisible(true);
//					}
//				});
//			}
//		});
		linkLabel = new JLabel();
		linkLabel.setIcon(new ImageIcon("res/off.jpg"));
		linkLabel.setPreferredSize(new Dimension(30,30));
		machineLabel = new JLabel();
		machineLabel.setIcon(new ImageIcon("res/off.jpg"));
		machineLabel.setPreferredSize(new Dimension(30,30));

//		jButtonPane.setLayout(new FlowLayout(FlowLayout.LEFT));
//		jButtonPane.add(linkButton);
//		if(ConstantValue.USERTYPE == ConstantValue.WORKER || ConstantValue.USERTYPE == ConstantValue.PLAYER){
//			jButtonPane.add(machineButton);
//		}
//		jButtonPane.add(stateLabel);
//		jButtonPane.add(queryButton);
//		jButtonPane.add(addButton);
//		jButtonPane.add(delButton);
//		jButtonPane.add(modiButton);
//		jButtonPane.add(stopButton);
//		jButtonPane.add(plainLabel);
//		jButtonPane.add(exitButton);
//		jButtonPane.add(cardLabel);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new java.awt.BorderLayout());
		JPanel buttonPanel1 = new JPanel();
		buttonPanel1.setBackground(new java.awt.Color(102, 102, 102));
		buttonPanel.add(jButtonPane, java.awt.BorderLayout.NORTH);
		buttonPanel.add(buttonPanel1, java.awt.BorderLayout.CENTER);
		
		jButtonPane.setLayout(new java.awt.GridBagLayout());
		jButtonPane.setBackground(new java.awt.Color(102, 102, 102));
		jButtonPane.add(linkLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		if (ConstantValue.USERTYPE == ConstantValue.WORKER || ConstantValue.USERTYPE == ConstantValue.PLAYER) {
			jButtonPane.add(machineLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}
		if(ConstantValue.USERTYPE == ConstantValue.PLAYER){
			jButtonPane.add(configButton, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}		
		if (ConstantValue.USERTYPE != ConstantValue.PLAYER) {
			jButtonPane.add(stateLabel, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			jButtonPane.add(queryButton, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			jButtonPane.add(addButton, new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			jButtonPane.add(delButton, new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			jButtonPane.add(modiButton, new GridBagConstraints(0, 7, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			jButtonPane.add(stopButton, new GridBagConstraints(0, 8, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			jButtonPane.add(plainLabel, new GridBagConstraints(0, 9, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			// jButtonPane.add(exitButton, new GridBagConstraints(0, 11, 1, 1, 0, 0,
			// GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0),
			// 0, 0));
		}
//		if (ConstantValue.USERTYPE == ConstantValue.PLAYER) {
//			jButtonPane.add(testButton, new GridBagConstraints(0, 10, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
//		}
		setLayout(new BorderLayout());
		getContentPane().add(BorderLayout.CENTER, jTabbedpane);
//		getContentPane().add(BorderLayout.CENTER, jPlayerPanel3);
		getContentPane().add(BorderLayout.EAST, buttonPanel);

		// 查询
		queryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					onQueryCmd();
					//测试牌状态的命令，在界面显示 测试
					if(curcmdcode2 == ConstantValue.PLAYERCMD){
						ChannelFuture cf = ClientMain.loginWin.getChannelFuture();
						ByteBuf cmdBuf2 = cf.channel().alloc().buffer();
						cmdBuf2.writeShort(ConstantValue.USERTYPE);
						cmdBuf2.writeShort(ConstantValue.WAKEUPCARD);
						cmdBuf2.writeShort(ConstantValue.NULLREC);
						cmdBuf2.writeBytes(ConstantValue.strUserName.getBytes());// 10 bytes
						cmdBuf2.writeInt(0xFBFEBFEF);

						cf.channel().writeAndFlush(cmdBuf2);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// 增加
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					curRow = getSelectedTableIndex();
					new ClientFrameAdd(curcmdcode, curTableModel
							.getDataVector());

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// 删除
		delButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if ((curRow = getSelectedTableIndex()) == -1) {
						JOptionPane.showMessageDialog(null, "请选择一条记录！", "信息",
								JOptionPane.INFORMATION_MESSAGE);

						return;
					}
					// 不允许删除管理员
					if (curcmdcode == ConstantValue.PASSWORDCMD) {
						String strgrade = (String) curTableModel.getValueAt(
								curRow, 2);
						int grade = Integer.parseInt(strgrade);
				//		int grade = ConstantValue.getUserGrade(Integer.parseInt(strgrade));
						if (grade == ConstantValue.ADMIN
								|| ConstantValue.USERTYPE >= grade) {
							JOptionPane.showMessageDialog(null, "无权删除该记录！",
									"信息", JOptionPane.INFORMATION_MESSAGE);

							return;
						}
					}
					if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(
							null, "确认删除？", "删除", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE)) {
						onDelCmd();

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// 修改
		modiButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if ((curRow = getSelectedTableIndex()) == -1) {
						JOptionPane.showMessageDialog(null, "请选择一条记录！", "信息",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					// 弹出对话框增加记录, cmdcode,所有数据，当前选中数据
					new ClientFrameAdd(curcmdcode, curTableModel
							.getDataVector(), curRow);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					System.exit(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if(curcmdcode2 == ConstantValue.PLAYERCMD){
						ChannelFuture cf = ClientMain.loginWin.getChannelFuture();
						ByteBuf cmdBuf2 = cf.channel().alloc().buffer();
						cmdBuf2.writeShort(ConstantValue.USERTYPE);
						cmdBuf2.writeShort(ConstantValue.SLEEPCARD);
						cmdBuf2.writeShort(ConstantValue.NULLREC);
						cmdBuf2.writeBytes(ConstantValue.strUserName.getBytes());// 10 bytes
						cmdBuf2.writeInt(0xFBFEBFEF);

						cf.channel().writeAndFlush(cmdBuf2);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});


		jTabbedpane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				// JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
				getSelectedTableIndex();
				onQueryCmd();
			}
		});
		
		//配牌界面，选择主机的回调函数
		jMachineBox.addItemListener(new ItemListener() {
			public void itemStateChanged(final ItemEvent e) {
				int index = jMachineBox.getSelectedIndex();
				if(jMachineBox.getSelectedItem() == null){
					return;
				}
				String strCard = jMachineBox.getSelectedItem().toString();
				Vector<?> v = cardtableModel.getDataVector();
				jCardbBox.removeAllItems();
				for(int i = 0; i<v.size();i++){
					Vector<String> tmpVector = (Vector<String>)v.elementAt(i);
					String machineString = tmpVector.elementAt(1);
					if(machineString.equals(strCard)){
						jCardbBox.addItem(tmpVector.elementAt(2));
						jCardbBox.addItem(tmpVector.elementAt(3));
						break;
					}
				}

			}
		});
		
		//配牌器界面，选择AB牌的回调函数
		jCardbBox.addItemListener(new ItemListener() {
			public void itemStateChanged(final ItemEvent e) {
				int index = jCardbBox.getSelectedIndex();
				if(jMachineBox.getSelectedItem() == null){
					return;
				}
				for(int i = 0; i < cardButtons.size();i++){
					int name = ClientPlayerPanel.cardPics[i];
					ImageIcon icon = new ImageIcon("./res/type_2/card_type_down/" + name + ".png");
					((JButton)cardButtons.elementAt(i)).setIcon(icon);
				}
				repaint();
			}
		});
		

	}

	/**
	 * 切换tab事件
	 * 
	 * @return
	 */
	public int getSelectedTableIndex() {
		int index = jTabbedpane.getSelectedIndex();
		stopButton.setVisible(false);
		if (ConstantValue.USERTYPE == ConstantValue.ADMIN) {
			if (index == 0) {
				curTable = passTable;
				curTableModel = passtableModel;
				curcmdcode = ConstantValue.PASSWORDCMD;
				curcmdcode2 = ConstantValue.PASSWORDCMD2;
				setButtonState(false, false, true);
				return passTable.getSelectedRow();
			} else if (index == 1) {
				curTable = timeTable;
				curTableModel = timetableModel;
				curcmdcode = ConstantValue.SETTIMECMD;
				curcmdcode2 = ConstantValue.SETTIMECMD2;
				setButtonState(false, false, true);
				return timeTable.getSelectedRow();
			} else if (index == 2) {
				curTable = machineTable;
				curTableModel = machinetableModel;
				curcmdcode = ConstantValue.MACHINECMD;
				curcmdcode2 = ConstantValue.MACHINECMD2;
				setButtonState(true, true, true);
				return machineTable.getSelectedRow();

			} else {
				curTable = coderTable;
				curTableModel = codertableModel;
				curcmdcode = ConstantValue.CODERCMD;
				curcmdcode2 = ConstantValue.CODERCMD2;
				setButtonState(true, true, false);
				return coderTable.getSelectedRow();

			}
		} else if (ConstantValue.USERTYPE == ConstantValue.MANAGER) {
			if (index == 0) {
				curTable = cardTable;
				curTableModel = cardtableModel;
				curcmdcode = ConstantValue.CARDCMD;
				curcmdcode2 = ConstantValue.CARDCMD2;
				setButtonState(true, true, true);
				return cardTable.getSelectedRow();
			} else {
				curTable = groupTable;
				curTableModel = grouptableModel;
				curcmdcode = ConstantValue.GROUPCMD;
				curcmdcode2 = ConstantValue.GROUPCMD2;
				setButtonState(true, true, true);
				return groupTable.getSelectedRow();
			}
		} else if (ConstantValue.USERTYPE == ConstantValue.WORKER) {
			if (index == 0) {
				curTable = configcardTable;
				curTableModel = configcardtableModel;
				curcmdcode = ConstantValue.CONFIGCODECMD;
				curcmdcode2 = ConstantValue.CONFIGCODECMD2;
				setButtonState(false, false, false);
			}
		} else if (ConstantValue.USERTYPE == ConstantValue.GROUPER) {
			if (index == 0) {
				curTable = groupuserTable;
				curTableModel = groupusertableModel;
				curcmdcode = ConstantValue.GROUPUSERCMD;
				curcmdcode2 = ConstantValue.GROUPUSERCMD2;
				setButtonState(true, true, true);
				return groupuserTable.getSelectedRow();
			}
		} else if (ConstantValue.USERTYPE == ConstantValue.PLAYER) {
			curcmdcode = ConstantValue.PLAYERCMD;
			curcmdcode2 = ConstantValue.PLAYERCMD;
			queryButton.setText("启动");
			stopButton.setVisible(true);
			setButtonState(false, false, false);
		} else {
			return -1;
		}
		return -1;
	}

	public void setButtonState(boolean add, boolean del, boolean mod) {
//		addButton.setEnabled(add);
//		delButton.setEnabled(del);
//		modiButton.setEnabled(mod);
		addButton.setVisible(add);
		delButton.setVisible(del);
		modiButton.setVisible(mod);

		repaint();
	}

	/**
	 * 查询的回调函数，根据当前页面查询
	 */
	public void onQueryCmd() {
		curRow = getSelectedTableIndex();
		ChannelFuture cf = ClientMain.loginWin.getChannelFuture();
		//配置群用户的时候需要查一下群信息（时间约束和群用户对应关系）
		if(curcmdcode2 == ConstantValue.GROUPUSERCMD2){
			ByteBuf cmdBuf2 = cf.channel().alloc().buffer();
			cmdBuf2.writeShort(ConstantValue.USERTYPE);
			cmdBuf2.writeShort(ConstantValue.GROUPCMD2);
			cmdBuf2.writeShort(ConstantValue.NULLREC);
			cmdBuf2.writeBytes(ConstantValue.strUserName.getBytes());// 10 bytes
			cmdBuf2.writeInt(0xFBFEBFEF);

			cf.channel().writeAndFlush(cmdBuf2);
		}
		// 发送查询命令，等待结果并刷新
		if(curcmdcode2 != ConstantValue.CONFIGCODECMD2 && curcmdcode2 != ConstantValue.PLAYERCMD){
			ByteBuf cmdBuf = cf.channel().alloc().buffer();
			cmdBuf.writeShort(ConstantValue.USERTYPE);
			cmdBuf.writeShort(curcmdcode2);
			cmdBuf.writeShort(ConstantValue.NULLREC);
			cmdBuf.writeBytes(ConstantValue.strUserName.getBytes());// 10 bytes
			cmdBuf.writeInt(0xFBFEBFEF);
	
			cf.channel().writeAndFlush(cmdBuf);
		}
		
		// 厂家配置牌的时候需要查一下主机信息供选择,玩家需要查主机信息
		if (curcmdcode2 == ConstantValue.CARDCMD2 || curcmdcode2 == ConstantValue.PLAYERCMD) {
			ByteBuf cmdBuf2 = cf.channel().alloc().buffer();
			cmdBuf2.writeShort(ConstantValue.USERTYPE);
			cmdBuf2.writeShort(ConstantValue.MACHINECMD2);
			cmdBuf2.writeShort(ConstantValue.NULLREC);
			cmdBuf2.writeBytes(ConstantValue.strUserName.getBytes());// 10 bytes
			cmdBuf2.writeInt(0xFBFEBFEF);

			cf.channel().writeAndFlush(cmdBuf2);
		}
		

		//配置牌的时候  需要查一下牌管理表
		if (curcmdcode2 == ConstantValue.CONFIGCODECMD2) {
			ByteBuf cmdBuf2 = cf.channel().alloc().buffer();
			cmdBuf2.writeShort(ConstantValue.USERTYPE);
			cmdBuf2.writeShort(ConstantValue.CARDCMD2);
			cmdBuf2.writeShort(ConstantValue.NULLREC);
			cmdBuf2.writeBytes(ConstantValue.strUserName.getBytes());// 10 bytes
			cmdBuf2.writeInt(0xFBFEBFEF);

			cf.channel().writeAndFlush(cmdBuf2);
			//更新牌信息，便于调试
			tileInfo = new ExcelReader();
		}
		
		//配置群的时候需要查一下管理员时间
		if(curcmdcode2 == ConstantValue.GROUPCMD2){
			ByteBuf cmdBuf2 = cf.channel().alloc().buffer();
			cmdBuf2.writeShort(ConstantValue.USERTYPE);
			cmdBuf2.writeShort(ConstantValue.SETTIMECMD2);
			cmdBuf2.writeShort(ConstantValue.NULLREC);
			cmdBuf2.writeBytes(ConstantValue.strUserName.getBytes());// 10 bytes
			cmdBuf2.writeInt(0xFBFEBFEF);

			cf.channel().writeAndFlush(cmdBuf2);
		}
		

	}

	/**
	 * 状态栏更新
	 * 
	 * @param str
	 */
	public void setStateLabel(String str,int mode,int color) {
		stateLabel.setText(str);

		if(color == 1){
			stateLabel.setForeground(Color.red);
		}
		if(mode == 1){
			linkLabel.setIcon(new ImageIcon("res/on.jpg"));
		}else{
			linkLabel.setIcon(new ImageIcon("res/off.jpg"));

		}
		repaint();
	}
	
	/**
	 * 设置主机状态显示
	 */
	public void refresh(int state) {
//		if(mode == 0){
//			cardLabel.setText(str);
//		}else{
//			str = cardLabel.getText()  + str;
//			cardLabel.setText(str);
//		}
//		if(color == 1){
//			cardLabel.setForeground(Color.red);
//		}
		if(state == 1){
			machineLabel.setIcon(new ImageIcon("res/on.jpg"));
		}else{
			machineLabel.setIcon(new ImageIcon("res/off.jpg"));

		}
		
		repaint();
	}
	

	/**
	 * 增加数据回调
	 */
	public void onAddCmd(Vector<?> data) {
		ChannelFuture cf = ClientMain.loginWin.getChannelFuture();
		ByteBuf cmdBuf = cf.channel().alloc().buffer();
		cmdBuf.writeShort(ConstantValue.USERTYPE);
		cmdBuf.writeShort(curcmdcode); // 当前的命令
		cmdBuf.writeShort(ConstantValue.ADDREC);
		cmdBuf.writeBytes(ConstantValue.strUserName.getBytes());// 10 bytes

		if (curcmdcode == ConstantValue.PASSWORDCMD) {
			String strNameString = (String) data.elementAt(1);
			cmdBuf.writeBytes(strNameString.getBytes());
			String strGrade = (String) data.elementAt(2);
			cmdBuf.writeShort((short) Integer.parseInt(strGrade));
		} else if (curcmdcode == ConstantValue.MACHINECMD) {
			for (int i = 0; i < 9; i++) {
				String strID = (String) data.elementAt(i+1);
				cmdBuf.writeShort((short) Integer.parseInt(strID));
			}
		} else if (curcmdcode == ConstantValue.CODERCMD) {
			String strID = (String) data.elementAt(1);
			cmdBuf.writeShort((short) Integer.parseInt(strID));
		} else if (curcmdcode == ConstantValue.CARDCMD) {
			String strID = (String) data.elementAt(1);
			cmdBuf.writeShort((short) Integer.parseInt(strID));
			for (int i = 1; i < 6; i++) {
				String str = (String) data.elementAt(i+1);
				cmdBuf.writeBytes(str.getBytes());
			}
		} else if (curcmdcode == ConstantValue.GROUPCMD) {
			String strID = (String) data.elementAt(1);
			cmdBuf.writeShort((short) Integer.parseInt(strID));
			cmdBuf.writeBytes(((String) data.elementAt(2)).getBytes());
			cmdBuf.writeShort((short) Integer.parseInt((String) data
					.elementAt(3)));
			cmdBuf.writeShort((short) Integer.parseInt((String) data
					.elementAt(4)));
			cmdBuf.writeBytes(((String) data.elementAt(5)).getBytes());
		} else if (curcmdcode == ConstantValue.GROUPUSERCMD) {
			String strID = (String) data.elementAt(1);
			cmdBuf.writeShort((short) Integer.parseInt(strID));
			cmdBuf.writeBytes(((String) data.elementAt(2)).getBytes());
			cmdBuf.writeShort((short) Integer.parseInt((String) data
					.elementAt(3)));
			cmdBuf.writeShort((short) Integer.parseInt((String) data
					.elementAt(4)));
			cmdBuf.writeShort((short) Integer.parseInt((String) data
					.elementAt(5)));
		}

		cmdBuf.writeInt(0xFBFEBFEF);

		cf.channel().writeAndFlush(cmdBuf);
	}

	/**
	 * 删除数据回调
	 */
	public void onDelCmd() {
		// 删除
		ChannelFuture cf = ClientMain.loginWin.getChannelFuture();
		ByteBuf cmdBuf = cf.channel().alloc().buffer();
		cmdBuf.writeShort(ConstantValue.USERTYPE);
		cmdBuf.writeShort(curcmdcode);
		cmdBuf.writeShort(ConstantValue.DELREC);
		cmdBuf.writeBytes(ConstantValue.strUserName.getBytes());// 10
																// bytes
		if (curcmdcode == ConstantValue.PASSWORDCMD) {
			String strNameString = (String) curTableModel.getValueAt(curRow, 1);
			cmdBuf.writeBytes(strNameString.getBytes());
			cmdBuf.writeShort((short) 0);// 删除直接填0
		} else if (curcmdcode == ConstantValue.MACHINECMD) {
			String strNameString = (String) curTableModel.getValueAt(curRow, 1);
			cmdBuf.writeShort(Integer.parseInt(strNameString));
			cmdBuf.writeShort((short) 0);// 删除直接填0
		} else if (curcmdcode == ConstantValue.CODERCMD) {
			String strNameString = (String) curTableModel.getValueAt(curRow, 1);
			cmdBuf.writeShort(Integer.parseInt(strNameString));
			cmdBuf.writeShort((short) 0);// 删除直接填0
		} else if (curcmdcode == ConstantValue.CARDCMD) {
			String strNameString = (String) curTableModel.getValueAt(curRow, 1);
			cmdBuf.writeShort(Integer.parseInt(strNameString));
			cmdBuf.writeShort((short) 0);// 删除直接填0
		} else if (curcmdcode == ConstantValue.GROUPCMD) {
			String strNameString = (String) curTableModel.getValueAt(curRow, 1);
			cmdBuf.writeShort(Integer.parseInt(strNameString));
			cmdBuf.writeShort((short) 0);// 删除直接填0
		} else if (curcmdcode == ConstantValue.GROUPUSERCMD) {
			String strGroupId = (String) curTableModel.getValueAt(curRow, 1);
			String strUserName = (String) curTableModel.getValueAt(curRow, 2);
			cmdBuf.writeShort(Integer.parseInt(strGroupId));// 群
			cmdBuf.writeBytes(strUserName.getBytes());// 用户密码
			cmdBuf.writeShort((short) 0);// 删除直接填0
		}

		cmdBuf.writeInt(0xFBFEBFEF);
		cf.channel().writeAndFlush(cmdBuf);
	}

	/**
	 * 修改数据的回调
	 */
	public void onModiCmd(Vector<?> data) {
		ChannelFuture cf = ClientMain.loginWin.getChannelFuture();
		ByteBuf cmdBuf = cf.channel().alloc().buffer();
		cmdBuf.writeShort(ConstantValue.USERTYPE);
		cmdBuf.writeShort(curcmdcode); // 当前的命令
		cmdBuf.writeShort(ConstantValue.MODIREC);
		cmdBuf.writeBytes(ConstantValue.strUserName.getBytes());// 10 bytes

		if (curcmdcode == ConstantValue.PASSWORDCMD) {
			String strNameString = (String) data.elementAt(1);
			cmdBuf.writeBytes(strNameString.getBytes());
			String strGrade = (String) data.elementAt(2);
			cmdBuf.writeShort((short) Integer.parseInt(strGrade));
		} else if (curcmdcode == ConstantValue.SETTIMECMD) {
			String strtime = (String) data.elementAt(1);
			cmdBuf.writeInt(Integer.parseInt(strtime));
			String strGrade = (String) data.elementAt(2);
			cmdBuf.writeShort((short) Integer.parseInt(strGrade));
		} else if (curcmdcode == ConstantValue.MACHINECMD) {
			for (int i = 0; i < 9; i++) {
				cmdBuf.writeShort((short) Integer.parseInt((String) data
						.elementAt(i+1)));
			}
		} else if (curcmdcode == ConstantValue.CARDCMD) {
			String strID = (String) data.elementAt(1);
			cmdBuf.writeShort((short) Integer.parseInt(strID));
			for (int i = 1; i < 6; i++) {
				String str = (String) data.elementAt(i+1);
				cmdBuf.writeBytes(str.getBytes());
			}
		} else if (curcmdcode == ConstantValue.GROUPCMD) {
			String strID = (String) data.elementAt(1);
			cmdBuf.writeShort((short) Integer.parseInt(strID));
			cmdBuf.writeBytes(((String) data.elementAt(2)).getBytes());
			cmdBuf.writeShort((short) Integer.parseInt((String) data
					.elementAt(3)));
			cmdBuf.writeShort((short) Integer.parseInt((String) data
					.elementAt(4)));
			cmdBuf.writeBytes(((String) data.elementAt(5)).getBytes());
		} else if (curcmdcode == ConstantValue.GROUPUSERCMD) {
			String strID = (String) data.elementAt(1);
			cmdBuf.writeShort((short) Integer.parseInt(strID));
			cmdBuf.writeBytes(((String) data.elementAt(2)).getBytes());
			cmdBuf.writeShort((short) Integer.parseInt((String) data
					.elementAt(3)));
			cmdBuf.writeShort((short) Integer.parseInt((String) data
					.elementAt(4)));
			cmdBuf.writeShort((short) Integer.parseInt((String) data
					.elementAt(5)));
		}

		cmdBuf.writeInt(0xFBFEBFEF);

		cf.channel().writeAndFlush(cmdBuf);
	}
	

	public void showWindow() {
		setVisible(true);
	}

	/**
	 * 根据查询数据刷新界面
	 * 
	 * @param icmdcode
	 * @param rowData
	 */
	public void refresh(int icmdcode, Vector<?> rowData) {
		if (icmdcode == ConstantValue.PASSWORDCMD2) {
			passtableModel.setRowData(rowData);
			passtableModel.setDataVector();
		} else if (icmdcode == ConstantValue.SETTIMECMD2) {
			timetableModel.setRowData(rowData);
			timetableModel.setDataVector();
		} else if (icmdcode == ConstantValue.MACHINECMD2) {
			machinetableModel.setRowData(rowData);
			machinetableModel.setDataVector();
		} else if (icmdcode == ConstantValue.CODERCMD2) {
			codertableModel.setRowData(rowData);
			codertableModel.setDataVector();
		} else if (icmdcode == ConstantValue.CARDCMD2) {
			cardtableModel.setRowData(rowData);
			cardtableModel.setDataVector();
			//刷新配牌器界面
			refreshMachineAndCards(rowData);
		} else if (icmdcode == ConstantValue.GROUPCMD2) {
			grouptableModel.setRowData(rowData);
			grouptableModel.setDataVector();
		} else if (icmdcode == ConstantValue.CONFIGCODECMD2) {
		//	configcardtableModel.setDataVector();
		} else if (icmdcode == ConstantValue.GROUPUSERCMD2) {
			groupusertableModel.setRowData(rowData);
			groupusertableModel.setDataVector();
		} else if (icmdcode == ConstantValue.PLAYERCMD) {
			
		} else if (icmdcode == ConstantValue.WAKEUPCARD) {
			
		} else if  (icmdcode == ConstantValue.SLEEPCARD) {
			
		} 

		repaint();
	}
	
	/**
	 * 配牌的时候需要刷新主机列表
	 */
	public void refreshMachineAndCards(Vector<?> v){
		int i;
		String acardString,bcardString;
		jMachineBox.removeAllItems();
		for(i = 0; i<v.size();i++){
			Vector<String> tmpVector = (Vector<String>)v.elementAt(i);
			String machineString = tmpVector.elementAt(1);
			acardString = tmpVector.elementAt(2);
			bcardString = tmpVector.elementAt(3);
			jMachineBox.addItem(machineString);
			
		}
		if(i>0){
			jMachineBox.setSelectedIndex(0);
		}
	}
	
	/**
	 * 配置牌成功后刷新牌的状态
	 * @param cardcode
	 */
	public void refreshConfigCard(int cardcode,String strVol){
		if(cardcode != curcardcode){
			return;
		}
		jVoltageField.setText(strVol);
		//找牌码 和 索引的对应关系
		String strName = ClientMain.getTileNameById(cardcode);//饼1
		if(strName != null){
			for(int i = 0; i < ConstantValue.cardPicStrings2.length;i++){
				if(strName.equals(ConstantValue.cardPicStrings2[i])){
					int name = ClientPlayerPanel.cardPics[i];
					ImageIcon icon = new ImageIcon("./res/type_2/card_type_down/" + name + ".jpg");
					((JButton)cardButtons.elementAt(i)).setIcon(icon);
					setEnabled(true);
					timer.stop();
					progressBar.setValue(0);
					currentProgress = 0;
					repaint();
					return;
				}
			}
		}

		
		
	}
	
	/**
	 * 刷新显示牌和打牌界面，正式版本不显示
	 * @param data
	 */
	public void refresh(final ArrayList<I牌堆行数据> data, final int tilesetid) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (tilesetid == Bridge.logicA.getTileSetid()) {
					jPlayerPanelA.showData(data);// 显示牌，测试界面显示
				} else {
					jPlayerPanelB.showData(data);// 显示牌，测试界面显示
				}

				// 打牌显示 洗牌和动牌
				// A是动牌完成，是否显示要看一下B状态，如果B是idle就一直显示到idle
				if (Bridge.logicA.getState() == ConstantValue.IdleState) {
					ABInfo = 2;
				} else if(Bridge.logicB.getState() == ConstantValue.IdleState) {
					ABInfo = 1;
				}
				if(tilesetid == Bridge.logicA.getTileSetid() && ABInfo == 1){
					// jPlayerPanel3.showData(data);
				}
				if(tilesetid == Bridge.logicB.getTileSetid() && ABInfo == 2){
					// jPlayerPanel3.showData(data);
				}
				
			//	repaint();
			}
		});
	}
	
	/**
	 * 刷新抓牌打牌界面
	 * 从抓牌、动牌完成开始显示 到 出牌完成结束。
	 * @param data
	 */
	public void refresh(PlayerTileData data, int tilesetid){
		jPlayerPanel3.showData(data);
		repaint();
	}
		
	
	//测试用，显示牌，邻居，方向，功率
	public void refreshTest(StringBuffer str){
		if(jTextArea.getLineCount() == 5000){
			jTextArea.setText("");
		}
		
		jTextArea.append(str.toString().toUpperCase());
		JScrollBar scrollBar=jPlayerPanel2.getVerticalScrollBar();
		scrollBar.setValue(scrollBar.getMaximum());
//		repaint();
	}
	
	//测试用power
	public void refreshPowerTest(I牌堆数据 data){
		if(jTextArea.getLineCount() == 1000){
			jTextArea.setText("");
		}
//		jTextArea.setText("");
		HashMap<Integer,String> map = new HashMap<Integer,String>();    
		StringBuffer stringBuffer = new StringBuffer();
		int line = data.get牌堆中牌的总行数();
		for(int i = 0; i < line; i++){
			I牌堆行数据  linedata = data.getItem(i);
			int numofline = linedata.get行中总牌数();
			int ipower = 0;
			for(int j = 0; j < numofline;j++){
				I单张牌数据 single = linedata.getItem(j);
				stringBuffer.append(Integer.toHexString(single.getTilecodeid()));
				stringBuffer.append("|");
				stringBuffer.append(single.getzDirectionTime());
				stringBuffer.append(",");
			}
			ipower = Ulti.getTilesPower(linedata,0);
			map.put(ipower, stringBuffer.toString());
			stringBuffer.setLength(0);
			ipower = 0;
		}
		
		stringBuffer.setLength(0);
		for(Map.Entry<Integer, String> entry : map.entrySet()){
			int ipower = entry.getKey();
			String  linedata = entry.getValue();
			stringBuffer.append(ipower+":");
			stringBuffer.append(linedata+"\n");

		}
		stringBuffer.append("\n");
		
		jTextArea.append(stringBuffer.toString());
		jTextArea.repaint();
	}
	

	/**
	 * 获取牌info
	 * @param index 牌索引
	 */
	public ArrayList<Integer> getTileParameter(String strName,int tileset){
		ArrayList<Integer> retArrayList = new ArrayList<Integer>();
		if(tileset == 1){
			for (Map<String, String> map : tileInfo.list1) {
				for (Entry<String, String> entry : map.entrySet()) {
					if(entry.getValue().equals(strName)){//find it!
						retArrayList.add(Integer.parseInt(map.get("编码"),16));
						retArrayList.add(Integer.parseInt(map.get("11位互感码"),16));
						retArrayList.add(Integer.parseInt(map.get("T-Report时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm起始0时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm第一次设置1时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm第二次设置0时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm第三次设置1时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm第四次设置0时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm第五次设置1时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm第六次设置0时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm第七次设置1时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm第八次设置0时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm第九次设置1时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm尾部设置0时长"),16));
						return retArrayList;
					}
				}
			}
		}else{
			for (Map<String, String> map : tileInfo.list) {
				for (Entry<String, String> entry : map.entrySet()) {
					if(entry.getValue().equals(strName)){
						retArrayList.add(Integer.parseInt(map.get("编码"),16));
						retArrayList.add(Integer.parseInt(map.get("11位互感码"),16));
						retArrayList.add(Integer.parseInt(map.get("T-Report时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm起始0时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm第一次设置1时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm第二次设置0时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm第三次设置1时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm第四次设置0时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm第五次设置1时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm第六次设置0时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm第七次设置1时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm第八次设置0时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm第九次设置1时长"),16));
						retArrayList.add(Integer.parseInt(map.get("pwm尾部设置0时长"),16));
						return retArrayList;
					}
				}
			}
		}
		return retArrayList;
	}
	

}
