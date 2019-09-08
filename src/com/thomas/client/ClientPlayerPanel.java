package com.thomas.client;

import com.thomas.data.ConstantValue;
import com.thomas.data.I单张牌数据;
import com.thomas.data.I牌堆行数据;
import com.thomas.logic.PlayerTileData;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * @author LongJing
 */
public class ClientPlayerPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanel20;

	public static int[] cardPics = { 10, 10, 10, 10, 
			11, 11, 11, 11, 12, 12, 12, 12, 
			13, 13, 13, 13, 14, 14, 14, 14, 
			15, 15, 15, 15, 16, 16, 16, 16, 
			17, 17, 17, 17, 18, 18, 18, 18, 
			1, 1, 1, 1, 2, 2, 2, 2, 
			3, 3, 3, 3, 4, 4, 4, 4,
			5, 5, 5, 5, 6, 6, 6, 6, 
			7, 7, 7, 7, 8, 8, 8, 8, 
			9, 9, 9, 9, 19, 19, 19, 19, 
			20, 20, 20, 20, 21, 21, 21, 21, 
			22, 22, 22, 22, 23, 23, 23, 23,
			24, 24, 24, 24, 25, 25, 25, 25,
			26, 26, 26, 26, 27, 27, 27, 27, 
			28, 28, 28, 28, 29, 29, 29, 29, 
			30, 30, 30, 30, 31, 31, 31, 31, 
			32, 32, 32, 32, 33, 33, 33, 33, 
			34, 34, 34, 34, 
			35, 36, 37, 38, 39, 40, 42, 41 };

	public Color color1 = new Color(102, 102, 102);
	public Color color2 = new Color(255, 255, 255);
	public ArrayList<I单张牌数据> huiziTilelist = null;// 混子牌
	public int nextMax = 10;// 预测牌张数

	public ClientPlayerPanel() {
		initComponents();

		try {
			String 预测牌 = ClientMain.configMap.get("预测牌");
			nextMax = Integer.parseInt(预测牌);
		} catch (NumberFormatException e) {
			nextMax = 10;
			e.printStackTrace();
		}
	}
    
    private void initComponents() {
    	java.awt.GridBagConstraints gridBagConstraints;
    	
    	jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        
        this.setBackground(new java.awt.Color(102, 102, 102));
        jPanel1.setBackground(color1);
        jPanel2.setBackground(color1);
        jPanel3.setBackground(color1);
        jPanel4.setBackground(color1);
        jPanel5.setBackground(color1);
        jPanel6.setBackground(color1);
        jPanel7.setBackground(color1);
        jPanel8.setBackground(color1);
        jPanel9.setBackground(color1);
        jPanel10.setBackground(color1);
        jPanel11.setBackground(color1);
        jPanel12.setBackground(color1);
        jPanel13.setBackground(color1);
        jPanel14.setBackground(color1);
        jPanel15.setBackground(color1);
        jPanel16.setBackground(color1);
        jPanel17.setBackground(color1);
        jPanel18.setBackground(color1);
        jPanel19.setBackground(color1);
        jPanel20.setBackground(color1);

        this.setMinimumSize(new java.awt.Dimension(1200, 700));

        this.setMaximumSize(new java.awt.Dimension(1200, 700));
        this.setMinimumSize(new java.awt.Dimension(1200, 700));
        this.setPreferredSize(new java.awt.Dimension(1200, 700));
        this.setLayout(new java.awt.GridBagLayout());

        jPanel1.setMinimumSize(new java.awt.Dimension(50, 700));
        jPanel1.setPreferredSize(new java.awt.Dimension(50, 700));
        jPanel1.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 14;
        this.add(jPanel1, gridBagConstraints);

        jPanel2.setMinimumSize(new java.awt.Dimension(100, 700));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 700));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel20.setPreferredSize(new java.awt.Dimension(100, 100));
        jPanel20.setLayout(new java.awt.GridBagLayout());
        jPanel2.add(jPanel20, java.awt.BorderLayout.SOUTH);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 14;
        this.add(jPanel2, gridBagConstraints);

        jPanel3.setMinimumSize(new java.awt.Dimension(50, 700));
        jPanel3.setPreferredSize(new java.awt.Dimension(50, 700));
        jPanel3.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 23;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 14;
        this.add(jPanel3, gridBagConstraints);

        jPanel4.setMinimumSize(new java.awt.Dimension(50, 700));
        jPanel4.setPreferredSize(new java.awt.Dimension(50, 700));
        jPanel4.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 14;
        this.add(jPanel4, gridBagConstraints);

        jPanel5.setMinimumSize(new java.awt.Dimension(50, 700));
        jPanel5.setPreferredSize(new java.awt.Dimension(50, 700));
        jPanel5.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 22;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 14;
        this.add(jPanel5, gridBagConstraints);

        jPanel6.setMinimumSize(new java.awt.Dimension(700, 50));
        jPanel6.setPreferredSize(new java.awt.Dimension(700, 50));
        jPanel6.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 14;
        this.add(jPanel6, gridBagConstraints);

        jPanel7.setMinimumSize(new java.awt.Dimension(700, 50));
        jPanel7.setPreferredSize(new java.awt.Dimension(700, 50));
        jPanel7.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 14;
        this.add(jPanel7, gridBagConstraints);

        jPanel8.setMinimumSize(new java.awt.Dimension(100, 700));
        jPanel8.setPreferredSize(new java.awt.Dimension(100, 700));
        jPanel8.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 14;
        this.add(jPanel8, gridBagConstraints);

        jPanel9.setMinimumSize(new java.awt.Dimension(100, 700));
        jPanel9.setPreferredSize(new java.awt.Dimension(100, 700));
        jPanel9.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 20;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 14;
        this.add(jPanel9, gridBagConstraints);

        jPanel10.setMinimumSize(new java.awt.Dimension(700, 100));
        jPanel10.setPreferredSize(new java.awt.Dimension(700, 100));
        jPanel10.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 14;
        gridBagConstraints.gridheight = 2;
        this.add(jPanel10, gridBagConstraints);

        jPanel11.setMinimumSize(new java.awt.Dimension(700, 100));
        jPanel11.setPreferredSize(new java.awt.Dimension(700, 100));
        jPanel11.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 14;
        gridBagConstraints.gridheight = 2;
        this.add(jPanel11, gridBagConstraints);

        jPanel12.setMinimumSize(new java.awt.Dimension(100, 400));
        jPanel12.setPreferredSize(new java.awt.Dimension(100, 400));
        jPanel12.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 8;
        this.add(jPanel12, gridBagConstraints);

        jPanel13.setMinimumSize(new java.awt.Dimension(100, 400));
        jPanel13.setPreferredSize(new java.awt.Dimension(100, 400));
        jPanel13.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 18;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 8;
        this.add(jPanel13, gridBagConstraints);

        jPanel14.setMinimumSize(new java.awt.Dimension(500, 100));
        jPanel14.setPreferredSize(new java.awt.Dimension(500, 100));
        jPanel14.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.gridheight = 2;
        this.add(jPanel14, gridBagConstraints);

        jPanel15.setMinimumSize(new java.awt.Dimension(500, 100));
        jPanel15.setPreferredSize(new java.awt.Dimension(500, 100));
        jPanel15.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.gridheight = 2;
        this.add(jPanel15, gridBagConstraints);

        jPanel16.setMinimumSize(new java.awt.Dimension(100, 200));
        jPanel16.setPreferredSize(new java.awt.Dimension(100, 200));
        jPanel16.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 4;
        this.add(jPanel16, gridBagConstraints);

        jPanel17.setMinimumSize(new java.awt.Dimension(100, 200));
        jPanel17.setPreferredSize(new java.awt.Dimension(100, 200));
        jPanel17.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 16;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 4;
        this.add(jPanel17, gridBagConstraints);

        jPanel18.setMinimumSize(new java.awt.Dimension(300, 100));
        jPanel18.setPreferredSize(new java.awt.Dimension(300, 100));
        jPanel18.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.gridheight = 2;
        this.add(jPanel18, gridBagConstraints);

        jPanel19.setMinimumSize(new java.awt.Dimension(300, 100));
        jPanel19.setPreferredSize(new java.awt.Dimension(300, 100));
        jPanel19.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.gridheight = 2;
        this.add(jPanel19, gridBagConstraints);
    }
    
	private void removeAllTiles() {
		jPanel1.removeAll();
		jPanel20.removeAll();
		jPanel3.removeAll();
		jPanel4.removeAll();
		jPanel5.removeAll();
		jPanel6.removeAll();
		jPanel7.removeAll();
		jPanel8.removeAll();
		jPanel9.removeAll();
		jPanel10.removeAll();
		jPanel11.removeAll();
		jPanel12.removeAll();
		jPanel13.removeAll();
		jPanel14.removeAll();
		jPanel15.removeAll();
		jPanel16.removeAll();
		jPanel17.removeAll();
		jPanel18.removeAll();
		jPanel19.removeAll();
	}

	private String getIcon(I单张牌数据 single, String type, String direct) {
		String s = ".png";
		if (type.equals("type_3") && (single.getBadType() == 5 || single.getBadType() == 6)) {
			// 牌堆的牌5:杠牌后抓走的牌;6:混子牌 不显示
			return "";
		}
		if ((single.getBadType() > 0 && single.getBadType() < 5) || ( this.huiziTilelist != null && this.huiziTilelist.contains(single))) {// 坏牌 混子牌反色
			s = ".jpg";
		}
		return "./res/" + type + "/" + direct + "/" + getName(single.getTilecodeid()) + s;
	}

	private int getName(int tilecodeid) {
		// 根据牌码获取牌的显示
		String strName = ClientMain.getTileNameById(tilecodeid);
		int k = 0;
		if (strName != null) {
			for (k = 0; k < ConstantValue.cardPicStrings2.length; k++) {
				if (strName.equals(ConstantValue.cardPicStrings2[k])) {
					break;
				}
			}
		}
		return ClientPlayerPanel.cardPics[k];
	}

	public void showData(final ArrayList<I牌堆行数据> resultTiles) {
		this.removeAllTiles();
		if (null == resultTiles || resultTiles.size() < 8) {
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					I牌堆行数据 south1 = resultTiles.get(0);
					I牌堆行数据 south2 = resultTiles.get(1);
					I牌堆行数据 east1 = resultTiles.get(2);
					I牌堆行数据 east2 = resultTiles.get(3);
					I牌堆行数据 north1 = resultTiles.get(4);
					I牌堆行数据 north2 = resultTiles.get(5);
					I牌堆行数据 west1 = resultTiles.get(6);
					I牌堆行数据 west2 = resultTiles.get(7);
					initResultTilesSouth(south1, south2);
					initResultTilesEast(east1, east2);
					initResultTilesNorth(north1, north2);
					initResultTilesWest(west1, west2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initResultTilesSouth(I牌堆行数据 south1, I牌堆行数据 south2) {
		String type = "type_3";// 图片大小
		String direct = "card_type_down";// 图片方向
		if (null == south1 || null == south1.get牌数据() || null == south2 || null == south2.get牌数据() || south1.get牌数据().length != south2.get牌数据().length) {
			return;
		}
		I单张牌数据[] private牌数据1 = south1.get牌数据();
		I单张牌数据[] private牌数据2 = south2.get牌数据();
		for (int i = 0; i < private牌数据1.length; i++) {
			GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
			gridBagConstraints0.gridx = i;
			gridBagConstraints0.gridy = 0;
			JLabel label = new JLabel(String.valueOf(i + 1));
			label.setForeground(color2);
			jPanel15.add(label, gridBagConstraints0);

			I单张牌数据 single1 = private牌数据1[i];
			if (null == single1) {
				continue;
			}
			JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
			if (single1.getBadType() > 0 && single1.getBadType() < 5) {
				label1.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
			}
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = i;
			gridBagConstraints1.gridy = 1;
			jPanel15.add(label1, gridBagConstraints1);
			I单张牌数据 single2 = private牌数据2[i];
			if (null == single2) {
				continue;
			}
			JLabel label2 = new JLabel(new ImageIcon(this.getIcon(single2, type, direct)));
			if (single2.getBadType() > 0 && single2.getBadType() < 5) {
				label2.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
			}
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = i;
			gridBagConstraints2.gridy = 2;
			jPanel15.add(label2, gridBagConstraints2);
		}
	}

	private void initResultTilesEast(I牌堆行数据 east1, I牌堆行数据 east2) {
		String type = "type_3";// 图片大小
		String direct = "card_type_right";// 图片方向
		if (null == east1 || null == east1.get牌数据() || null == east2 || null == east2.get牌数据() || east1.get牌数据().length != east2.get牌数据().length) {
			return;
		}
		I单张牌数据[] private牌数据1 = east1.get牌数据();
		I单张牌数据[] private牌数据2 = east2.get牌数据();
		int index = private牌数据1.length - 1;
		for (int i = 0; i < private牌数据1.length; i++) {
			GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
			gridBagConstraints0.gridx = 0;
			gridBagConstraints0.gridy = index;
			JLabel label = new JLabel(String.valueOf(i + 1));
			label.setForeground(color2);
			jPanel13.add(label, gridBagConstraints0);

			I单张牌数据 single1 = private牌数据1[i];
			if (null == single1) {
				continue;
			}
			JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
			if (single1.getBadType() > 0 && single1.getBadType() < 5) {
				label1.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
			}
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = index;
			jPanel13.add(label1, gridBagConstraints1);
			I单张牌数据 single2 = private牌数据2[i];
			if (null == single2) {
				continue;
			}
			JLabel label2 = new JLabel(new ImageIcon(this.getIcon(single2, type, direct)));
			if (single2.getBadType() > 0 && single2.getBadType() < 5) {
				label2.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
			}
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 2;
			gridBagConstraints2.gridy = index;
			jPanel13.add(label2, gridBagConstraints2);
			index--;
		}
	}

	private void initResultTilesNorth(I牌堆行数据 north1, I牌堆行数据 north2) {
		String type = "type_3";// 图片大小
		String direct = "card_type_up";// 图片方向
		if (null == north1 || null == north1.get牌数据() || null == north2 || null == north2.get牌数据() || north1.get牌数据().length != north2.get牌数据().length) {
			return;
		}
		I单张牌数据[] private牌数据1 = north1.get牌数据();
		I单张牌数据[] private牌数据2 = north2.get牌数据();
		int index = private牌数据1.length - 1;
		for (int i = 0; i < private牌数据1.length; i++) {
			GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
			gridBagConstraints0.gridx = index;
			gridBagConstraints0.gridy = 2;
			JLabel label = new JLabel(String.valueOf(i + 1));
			label.setForeground(color2);
			jPanel14.add(label, gridBagConstraints0);

			I单张牌数据 single1 = private牌数据1[i];
			if (null == single1) {
				continue;
			}
			JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
			if (single1.getBadType() > 0 && single1.getBadType() < 5) {
				label1.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
			}
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = index;
			gridBagConstraints1.gridy = 1;
			jPanel14.add(label1, gridBagConstraints1);
			I单张牌数据 single2 = private牌数据2[i];
			if (null == single2) {
				continue;
			}
			JLabel label2 = new JLabel(new ImageIcon(this.getIcon(single2, type, direct)));
			if (single2.getBadType() > 0 && single2.getBadType() < 5) {
				label2.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
			}
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = index;
			gridBagConstraints2.gridy = 0;
			jPanel14.add(label2, gridBagConstraints2);
			index--;
		}
	}

	private void initResultTilesWest(I牌堆行数据 west1, I牌堆行数据 west2) {
		String type = "type_3";// 图片大小
		String direct = "card_type_left";// 图片方向
		if (null == west1 || null == west1.get牌数据() || null == west2 || null == west2.get牌数据() || west1.get牌数据().length != west2.get牌数据().length) {
			return;
		}
		I单张牌数据[] private牌数据1 = west1.get牌数据();
		I单张牌数据[] private牌数据2 = west2.get牌数据();
		for (int i = 0; i < private牌数据1.length; i++) {
			GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
			gridBagConstraints0.gridx = 2;
			gridBagConstraints0.gridy = i;
			JLabel label = new JLabel(String.valueOf(i + 1));
			label.setForeground(color2);
			jPanel12.add(label, gridBagConstraints0);

			I单张牌数据 single1 = private牌数据1[i];
			if (null == single1) {
				continue;
			}
			JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
			if (single1.getBadType() > 0 && single1.getBadType() < 5) {
				label1.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
			}
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = i;
			jPanel12.add(label1, gridBagConstraints1);
			I单张牌数据 single2 = private牌数据2[i];
			if (null == single2) {
				continue;
			}
			JLabel label2 = new JLabel(new ImageIcon(this.getIcon(single2, type, direct)));
			if (single2.getBadType() > 0 && single2.getBadType() < 5) {
				label2.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.red));
			}
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = i;
			jPanel12.add(label2, gridBagConstraints2);
		}
	}

	public void showData(final PlayerTileData resultData) {
		this.removeAllTiles();
		if (null == resultData) {
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					initHuizi(resultData.getHuiziTilelist());
					initNextTiles(resultData.getNextTiles(), resultData.getPlayerindex());
					initCurrentTiles(resultData.getCurrentTiles(), resultData.getCurrentCpgTiles(), resultData.getPlayerindex());
					initResultTiles(resultData.getResultTilelist(), resultData.getZhuapaiorder());
					initCurrentPlayerTiles(resultData.getCurrentPlayerTiles(), resultData.getPlayerindex());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initHuizi(ArrayList<I单张牌数据> huiziTilelist) {
		if (null == huiziTilelist) {
			return;
		}
		this.huiziTilelist = huiziTilelist;
		String type = "type_1";// 图片大小
		String direct = "card_type_down";// 图片方向
		for (int i = 0; i < huiziTilelist.size(); i++) {
			I单张牌数据 single = huiziTilelist.get(i);
			if (null == single || single.getBadType() != 6) {
				continue;
			}
			JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			jPanel20.add(label, gridBagConstraints);
			break;
		}
	}

	private void initNextTiles(ArrayList<LinkedList<I单张牌数据>> nextTiles, int playerindex) {
		if (null == nextTiles || nextTiles.size() < 4) {
			return;
		}
		LinkedList<I单张牌数据> south = nextTiles.get(playerindex);
		LinkedList<I单张牌数据> east = nextTiles.get((playerindex + 1) % 4);
		LinkedList<I单张牌数据> north = nextTiles.get((playerindex + 2) % 4);
		LinkedList<I单张牌数据> west = nextTiles.get((playerindex + 3) % 4);
		
		initNextTilesSouth(south);
		initNextTilesEast(east);
		initNextTilesNorth(north);
		initNextTilesWest(west);
	}

	private void initNextTilesSouth(LinkedList<I单张牌数据> list) {
		if (null == list || list.isEmpty()) {
			return;
		}
		String type = "type_2";// 图片大小
		String direct = "card_type_down";// 图片方向
		int index = list.size();
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = index--;
		gridBagConstraints1.gridy = 0;
		jPanel7.add(new JLabel(new ImageIcon("./res/2.png")), gridBagConstraints1);
		for (int i = 0; i < list.size(); i++) {
			if (i >= nextMax) {
				break;
			}
			I单张牌数据 single = list.get(i);
			if (null == single) {
				continue;
			}
			JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = index--;
			gridBagConstraints2.gridy = 0;
			jPanel7.add(label, gridBagConstraints2);
		}
	}

	private void initNextTilesEast(LinkedList<I单张牌数据> list) {
		if (null == list || list.isEmpty()) {
			return;
		}
		String type = "type_2";// 图片大小
		String direct = "card_type_right";// 图片方向
		int index = 0;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = index++;
		jPanel5.add(new JLabel(new ImageIcon("./res/3.png")), gridBagConstraints1);
		for (int i = 0; i < list.size(); i++) {
			if (i >= nextMax) {
				break;
			}
			I单张牌数据 single = list.get(i);
			if (null == single) {
				continue;
			}
			JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = index++;
			jPanel5.add(label, gridBagConstraints2);
		}
	}

	private void initNextTilesNorth(LinkedList<I单张牌数据> list) {
		if (null == list || list.isEmpty()) {
			return;
		}
		String type = "type_2";// 图片大小
		String direct = "card_type_up";// 图片方向
		int index = 0;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = index++;
		gridBagConstraints1.gridy = 0;
		jPanel6.add(new JLabel(new ImageIcon("./res/4.png")), gridBagConstraints1);
		for (int i = 0; i < list.size(); i++) {
			if (i >= nextMax) {
				break;
			}
			I单张牌数据 single = list.get(i);
			if (null == single) {
				continue;
			}
			JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = index++;
			gridBagConstraints2.gridy = 0;
			jPanel6.add(label, gridBagConstraints2);
		}
	}

	private void initNextTilesWest(LinkedList<I单张牌数据> list) {
		if (null == list || list.isEmpty()) {
			return;
		}
		String type = "type_2";// 图片大小
		String direct = "card_type_left";// 图片方向
		int index = list.size();
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = index--;
		jPanel4.add(new JLabel(new ImageIcon("./res/1.png")), gridBagConstraints1);
		for (int i = 0; i < list.size(); i++) {
			if (i >= nextMax) {
				break;
			}
			I单张牌数据 single = list.get(i);
			if (null == single) {
				continue;
			}
			JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = index--;
			jPanel4.add(label, gridBagConstraints2);
		}
	}

	private void initCurrentTiles(ArrayList<LinkedList<I单张牌数据>> currentTiles, ArrayList<LinkedList<I单张牌数据>> currentCpgTiles, int playerindex) {
		if (null == currentTiles || currentTiles.size() < 4 || null == currentCpgTiles || currentCpgTiles.size() < 4 || playerindex > 3) {
			return;
		}
		LinkedList<I单张牌数据> southCurrentTile = currentTiles.get(playerindex);
		LinkedList<I单张牌数据> eastCurrentTile = currentTiles.get((playerindex + 1) % 4);
		LinkedList<I单张牌数据> northCurrentTile = currentTiles.get((playerindex + 2) % 4);
		LinkedList<I单张牌数据> westCurrentTile = currentTiles.get((playerindex + 3) % 4);

		LinkedList<I单张牌数据> southCurrentCpgTile = currentCpgTiles.get(playerindex);
		LinkedList<I单张牌数据> eastCurrentCpgTile = currentCpgTiles.get((playerindex + 1) % 4);
		LinkedList<I单张牌数据> northCurrentCpgTile = currentCpgTiles.get((playerindex + 2) % 4);
		LinkedList<I单张牌数据> westCurrentCpgTile = currentCpgTiles.get((playerindex + 3) % 4);

		initCurrentTilesSouth(southCurrentTile, southCurrentCpgTile);
		initCurrentTilesEast(eastCurrentTile, eastCurrentCpgTile);
		initCurrentTilesNorth(northCurrentTile, northCurrentCpgTile);
		initCurrentTilesWest(westCurrentTile, westCurrentCpgTile);
	}

	private void initCurrentTilesSouth(LinkedList<I单张牌数据> southCurrentTile, LinkedList<I单张牌数据> southCurrentCpgTile) {
		if (null == southCurrentTile) {
			return;
		}
		ArrayList<ArrayList<I单张牌数据>> newTile = this.sortTiles(southCurrentTile);
		String type = "type_1";// 图片大小
		String direct = "card_type_down";// 图片方向
		int index = 0;
		if (null != southCurrentCpgTile) {
			for (int i = 0; i < southCurrentCpgTile.size(); i++) {
				I单张牌数据 single = southCurrentCpgTile.get(i);
				if (null == single) {
					continue;
				}
				JLabel label = new JLabel(new ImageIcon(this.getIcon(single, "type_4", direct)));
				GridBagConstraints gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = index++;
				gridBagConstraints.gridy = 0;
				gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
				jPanel11.add(label, gridBagConstraints);
			}
		}
		for (int i = 0; i < newTile.size(); i++) {
			ArrayList<I单张牌数据> list = newTile.get(i);
			if (null == list) {
				continue;
			}
			for (int j = 0; j < list.size(); j++) {
				I单张牌数据 single = list.get(j);
				JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
				GridBagConstraints gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = index++;
				gridBagConstraints.gridy = 0;
				jPanel11.add(label, gridBagConstraints);
			}
		}
	}

	private void initCurrentTilesEast(LinkedList<I单张牌数据> eastCurrentTile, LinkedList<I单张牌数据> eastCurrentCpgTile) {
		if (null == eastCurrentTile) {
			return;
		}
		ArrayList<ArrayList<I单张牌数据>> newTile = this.sortTiles(eastCurrentTile);
		String type = "type_1";// 图片大小
		String direct = "card_type_right";// 图片方向
		int index = eastCurrentTile.size() - 1;
		if (null != eastCurrentCpgTile) {
			index += eastCurrentCpgTile.size();
			for (int i = 0; i < eastCurrentCpgTile.size(); i++) {
				I单张牌数据 single = eastCurrentCpgTile.get(i);
				if (null == single) {
					continue;
				}
				JLabel label = new JLabel(new ImageIcon(this.getIcon(single, "type_4", direct)));
				GridBagConstraints gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = index--;
				gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
				jPanel9.add(label, gridBagConstraints);
			}
		}
		for (int i = 0; i < newTile.size(); i++) {
			ArrayList<I单张牌数据> list = newTile.get(i);
			if (null == list) {
				continue;
			}
			for (int j = 0; j < list.size(); j++) {
				I单张牌数据 single = list.get(j);
				JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
				GridBagConstraints gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = index--;
				jPanel9.add(label, gridBagConstraints);
			}
		}
	}

	private void initCurrentTilesNorth(LinkedList<I单张牌数据> northCurrentTile, LinkedList<I单张牌数据> northCurrentCpgTile) {
		if (null == northCurrentTile) {
			return;
		}
		ArrayList<ArrayList<I单张牌数据>> newTile = this.sortTiles(northCurrentTile);
		String type = "type_1";// 图片大小
		String direct = "card_type_up";// 图片方向
		int index = northCurrentTile.size() - 1;
		if (null != northCurrentCpgTile) {
			index += northCurrentCpgTile.size();
			for (int i = 0; i < northCurrentCpgTile.size(); i++) {
				I单张牌数据 single = northCurrentCpgTile.get(i);
				if (null == single) {
					continue;
				}
				JLabel label = new JLabel(new ImageIcon(this.getIcon(single, "type_4", direct)));
				GridBagConstraints gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = index--;
				gridBagConstraints.gridy = 0;
				gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
				jPanel10.add(label, gridBagConstraints);
			}
		}
		for (int i = 0; i < newTile.size(); i++) {
			ArrayList<I单张牌数据> list = newTile.get(i);
			if (null == list) {
				continue;
			}
			for (int j = 0; j < list.size(); j++) {
				I单张牌数据 single = list.get(j);
				JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
				GridBagConstraints gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = index--;
				gridBagConstraints.gridy = 0;
				jPanel10.add(label, gridBagConstraints);
			}
		}
	}

	private void initCurrentTilesWest(LinkedList<I单张牌数据> westCurrentTile, LinkedList<I单张牌数据> westCurrentCpgTile) {
		if (null == westCurrentTile) {
			return;
		}
		ArrayList<ArrayList<I单张牌数据>> newTile = this.sortTiles(westCurrentTile);
		String type = "type_1";// 图片大小
		String direct = "card_type_left";// 图片方向
		int index = 0;
		if (null != westCurrentCpgTile) {
			for (int i = 0; i < westCurrentCpgTile.size(); i++) {
				I单张牌数据 single = westCurrentCpgTile.get(i);
				if (null == single) {
					continue;
				}
				JLabel label = new JLabel(new ImageIcon(this.getIcon(single, "type_4", direct)));
				GridBagConstraints gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = index++;
				gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
				jPanel8.add(label, gridBagConstraints);
			}
		}
		for (int i = 0; i < newTile.size(); i++) {
			ArrayList<I单张牌数据> list = newTile.get(i);
			if (null == list) {
				continue;
			}
			for (int j = 0; j < list.size(); j++) {
				I单张牌数据 single = list.get(j);
				JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
				GridBagConstraints gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = index++;
				jPanel8.add(label, gridBagConstraints);
			}
		}
	}

	private ArrayList<ArrayList<I单张牌数据>> sortTiles(LinkedList<I单张牌数据> tiles) {
		ArrayList<ArrayList<I单张牌数据>> newTiles = new ArrayList<ArrayList<I单张牌数据>>();
		if (null == tiles || tiles.isEmpty()) {
			return newTiles;
		}
		for (int i = 0; i < 42; i++) {
			newTiles.add(null);
		}
		for (int i = 0; i < tiles.size(); i++) {
			I单张牌数据 single = tiles.get(i);
			if (null == single) {
				continue;
			}
			int name = getName(single.getTilecodeid());
			if (name <= 42 && name > 0) {
				ArrayList<I单张牌数据> temp = newTiles.get(name);
				if (null == temp) {
					temp = new ArrayList<I单张牌数据>();
					newTiles.set(name, temp);
				}
				temp.add(single);
			}
		}
		return newTiles;
	}

	private void initResultTiles(ArrayList<I单张牌数据> resultTilelist, boolean zhuapaiorder) {
		if (null == resultTilelist) {
			return;
		}
		ArrayList<I单张牌数据> temp1 = new ArrayList<I单张牌数据>();
		ArrayList<I单张牌数据> temp2 = new ArrayList<I单张牌数据>();
		ArrayList<I单张牌数据> temp3 = new ArrayList<I单张牌数据>();
		ArrayList<I单张牌数据> temp4 = new ArrayList<I单张牌数据>();
		List<ArrayList<I单张牌数据>> list = new ArrayList<ArrayList<I单张牌数据>>();
		int m = 1;
		for (int i = 0; i < resultTilelist.size(); i++) {
			I单张牌数据 single = resultTilelist.get(i);
			if (null == single) {
				m++;
				continue;
			}
			if (m == 1) {
				temp1.add(single);
			} else if (m == 2) {
				temp2.add(single);
			} else if (m == 3) {
				temp3.add(single);
			} else if (m == 4) {
				temp4.add(single);
			}
		}
		list.add(temp1);
		if (m > 1) {
			list.add(temp2);
		}
		if (m > 2) {
			list.add(temp3);
		}
		if (m > 3) {
			list.add(temp4);
		}
		if (zhuapaiorder) {// 顺时针
			initResultTiles1(list);
		} else {// 逆时针
			initResultTiles2(list);
		}
	}

	private void initResultTiles1(List<ArrayList<I单张牌数据>> list) {
		ArrayList<I单张牌数据> south = null;
		ArrayList<I单张牌数据> east = null;
		ArrayList<I单张牌数据> north = null;
		ArrayList<I单张牌数据> west = null;
		if (list.size() > 0) {
			south = list.get(0);
		}
		if (list.size() > 1) {
			west = list.get(1);
		}
		if (list.size() > 2) {
			north = list.get(2);
		}
		if (list.size() > 3) {
			east = list.get(3);
		}
		if (null != south && !south.isEmpty() && check(south)) {
			initResultSouth1(south);
		}
		if (null != west && !west.isEmpty() && check(west)) {
			initResultWest1(west);
		}
		if (null != north && !north.isEmpty() && check(north)) {
			initResultNorth1(north);
		}
		if (null != east && !east.isEmpty() && check(east)) {
			initResultEast1(east);
		}
	}

	private boolean check(ArrayList<I单张牌数据> list) {
		if (list == null || list.isEmpty()) {
			return false;
		}
		for (I单张牌数据 single : list) {
			if (single.getBadType() != 5) {
				return true;
			}
		}
		return false;
	}

	private void initResultSouth1(ArrayList<I单张牌数据> south) {
		String type = "type_3";// 图片大小
		String direct = "card_type_down";// 图片方向
		int size = south.size();
		if (size % 2 == 1) {
			int index = size / 2;
			I单张牌数据 single = south.get(0);
			JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = index--;
			gridBagConstraints.gridy = 2;
			jPanel15.add(label, gridBagConstraints);

			for (int i = 1; i < south.size(); i++) {
				I单张牌数据 single1 = south.get(i);
				if (null == single) {
					continue;
				}
				JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
				if (i % 2 == 1) {
					GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
					gridBagConstraints0.gridx = index;
					gridBagConstraints0.gridy = 0;
					gridBagConstraints0.insets = new java.awt.Insets(0, 2, 0, 2);
					JLabel label2 = new JLabel(String.valueOf(index + 1));
					label2.setForeground(color2);
					jPanel15.add(label2, gridBagConstraints0);

					GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
					gridBagConstraints1.gridx = index;
					gridBagConstraints1.gridy = 1;
					jPanel15.add(label1, gridBagConstraints1);
				} else {
					GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
					gridBagConstraints2.gridx = index;
					gridBagConstraints2.gridy = 2;
					jPanel15.add(label1, gridBagConstraints2);
					index--;
				}
			}
		} else {
			int index = size / 2 - 1;
			for (int i = 0; i < south.size(); i++) {
				I单张牌数据 single1 = south.get(i);
				if (null == single1) {
					continue;
				}
				JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
				if (i % 2 == 0) {
					GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
					gridBagConstraints0.gridx = index;
					gridBagConstraints0.gridy = 0;
					gridBagConstraints0.insets = new java.awt.Insets(0, 2, 0, 2);
					JLabel label2 = new JLabel(String.valueOf(index + 1));
					label2.setForeground(color2);
					jPanel15.add(label2, gridBagConstraints0);

					GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
					gridBagConstraints1.gridx = index;
					gridBagConstraints1.gridy = 1;
					jPanel15.add(label1, gridBagConstraints1);
				} else {
					GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
					gridBagConstraints2.gridx = index;
					gridBagConstraints2.gridy = 2;
					jPanel15.add(label1, gridBagConstraints2);
					index--;
				}
			}
		}
	}

	private void initResultWest1(ArrayList<I单张牌数据> west) {
		String type = "type_3";// 图片大小
		String direct = "card_type_left";// 图片方向
		int size = west.size();
		if (size % 2 == 1) {
			int index = size / 2;
			I单张牌数据 single = west.get(0);
			JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = index--;
			jPanel12.add(label, gridBagConstraints);

			for (int i = 1; i < west.size(); i++) {
				I单张牌数据 single1 = west.get(i);
				if (null == single1) {
					continue;
				}
				JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
				if (i % 2 == 1) {
					GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
					gridBagConstraints0.gridx = 2;
					gridBagConstraints0.gridy = index;
					gridBagConstraints0.insets = new java.awt.Insets(2, 0, 2, 0);
					JLabel label2 = new JLabel(String.valueOf(index + 1));
					label2.setForeground(color2);
					jPanel12.add(label2, gridBagConstraints0);

					GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
					gridBagConstraints1.gridx = 1;
					gridBagConstraints1.gridy = index;
					jPanel12.add(label1, gridBagConstraints1);
				} else {
					GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
					gridBagConstraints2.gridx = 0;
					gridBagConstraints2.gridy = index;
					jPanel12.add(label1, gridBagConstraints2);
					index--;
				}
			}
		} else {
			int index = size / 2 - 1;
			for (int i = 0; i < west.size(); i++) {
				I单张牌数据 single1 = west.get(i);
				if (null == single1) {
					continue;
				}
				JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
				if (i % 2 == 0) {
					GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
					gridBagConstraints0.gridx = 2;
					gridBagConstraints0.gridy = index;
					gridBagConstraints0.insets = new java.awt.Insets(2, 0, 2, 0);
					JLabel label2 = new JLabel(String.valueOf(index + 1));
					label2.setForeground(color2);
					jPanel12.add(label2, gridBagConstraints0);

					GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
					gridBagConstraints1.gridx = 1;
					gridBagConstraints1.gridy = index;
					jPanel12.add(label1, gridBagConstraints1);
				} else {
					GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
					gridBagConstraints2.gridx = 0;
					gridBagConstraints2.gridy = index;
					jPanel12.add(label1, gridBagConstraints2);
					index--;
				}
			}
		}
	}

	private void initResultNorth1(ArrayList<I单张牌数据> north) {
		String type = "type_3";// 图片大小
		String direct = "card_type_up";// 图片方向
		int size = north.size();
		int length = size / 2;
		int index = 0;
		if (size % 2 == 1) {
			I单张牌数据 single = north.get(0);
			JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = index++;
			gridBagConstraints.gridy = 0;
			jPanel14.add(label, gridBagConstraints);

			for (int i = 1; i < north.size(); i++) {
				I单张牌数据 single1 = north.get(i);
				if (null == single1) {
					continue;
				}
				JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
				if (i % 2 == 1) {
					GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
					gridBagConstraints0.gridx = index;
					gridBagConstraints0.gridy = 2;
					gridBagConstraints0.insets = new java.awt.Insets(0, 2, 0, 2);
					JLabel label2 = new JLabel(String.valueOf(length--));
					label2.setForeground(color2);
					jPanel14.add(label2, gridBagConstraints0);

					GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
					gridBagConstraints1.gridx = index;
					gridBagConstraints1.gridy = 1;
					jPanel14.add(label1, gridBagConstraints1);
				} else {
					GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
					gridBagConstraints2.gridx = index;
					gridBagConstraints2.gridy = 0;
					jPanel14.add(label1, gridBagConstraints2);
					index++;
				}
			}
		} else {
			for (int i = 0; i < north.size(); i++) {
				I单张牌数据 single1 = north.get(i);
				if (null == single1) {
					continue;
				}
				JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
				if (i % 2 == 0) {
					GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
					gridBagConstraints0.gridx = index;
					gridBagConstraints0.gridy = 2;
					gridBagConstraints0.insets = new java.awt.Insets(0, 2, 0, 2);
					JLabel label2 = new JLabel(String.valueOf(length--));
					label2.setForeground(color2);
					jPanel14.add(label2, gridBagConstraints0);

					GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
					gridBagConstraints1.gridx = index;
					gridBagConstraints1.gridy = 1;
					jPanel14.add(label1, gridBagConstraints1);
				} else {
					GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
					gridBagConstraints2.gridx = index;
					gridBagConstraints2.gridy = 0;
					jPanel14.add(label1, gridBagConstraints2);
					index++;
				}
			}
		}
	}

	private void initResultEast1(ArrayList<I单张牌数据> east) {
		String type = "type_3";// 图片大小
		String direct = "card_type_right";// 图片方向
		int size = east.size();
		int length = size / 2;
		int index = 0;
		if (size % 2 == 1) {
			I单张牌数据 single = east.get(0);
			JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = index++;
			jPanel13.add(label, gridBagConstraints);

			for (int i = 1; i < east.size(); i++) {
				I单张牌数据 single1 = east.get(i);
				if (null == single1) {
					continue;
				}
				JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
				if (i % 2 == 1) {
					GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
					gridBagConstraints0.gridx = 0;
					gridBagConstraints0.gridy = index;
					gridBagConstraints0.insets = new java.awt.Insets(2, 0, 2, 0);
					JLabel label2 = new JLabel(String.valueOf(length--));
					label2.setForeground(color2);
					jPanel13.add(label2, gridBagConstraints0);

					GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
					gridBagConstraints1.gridx = 1;
					gridBagConstraints1.gridy = index;
					jPanel13.add(label1, gridBagConstraints1);
				} else {
					GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
					gridBagConstraints2.gridx = 2;
					gridBagConstraints2.gridy = index;
					jPanel13.add(label1, gridBagConstraints2);
					index++;
				}
			}
		} else {
			for (int i = 0; i < east.size(); i++) {
				I单张牌数据 single1 = east.get(i);
				if (null == single1) {
					continue;
				}
				JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
				if (i % 2 == 0) {
					GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
					gridBagConstraints0.gridx = 0;
					gridBagConstraints0.gridy = index;
					gridBagConstraints0.insets = new java.awt.Insets(2, 0, 2, 0);
					JLabel label2 = new JLabel(String.valueOf(length--));
					label2.setForeground(color2);
					jPanel13.add(label2, gridBagConstraints0);

					GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
					gridBagConstraints1.gridx = 1;
					gridBagConstraints1.gridy = index;
					jPanel13.add(label1, gridBagConstraints1);
				} else {
					GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
					gridBagConstraints2.gridx = 2;
					gridBagConstraints2.gridy = index;
					jPanel13.add(label1, gridBagConstraints2);
					index++;
				}
			}
		}
	}

	private void initResultTiles2(List<ArrayList<I单张牌数据>> list) {
		ArrayList<I单张牌数据> south = null;
		ArrayList<I单张牌数据> east = null;
		ArrayList<I单张牌数据> north = null;
		ArrayList<I单张牌数据> west = null;
		if (list.size() > 0) {
			south = list.get(0);
		}
		if (list.size() > 1) {
			east = list.get(1);
		}
		if (list.size() > 2) {
			north = list.get(2);
		}
		if (list.size() > 3) {
			west = list.get(3);
		}
		if (null != south && !south.isEmpty()) {
			initResultSouth2(south);
		}
		if (null != east && !east.isEmpty()) {
			initResultEast2(east);
		}
		if (null != north && !north.isEmpty()) {
			initResultNorth2(north);
		}
		if (null != west && !west.isEmpty()) {
			initResultWest2(west);
		}
	}

	private void initResultSouth2(ArrayList<I单张牌数据> south) {
		String type = "type_3";// 图片大小
		String direct = "card_type_down";// 图片方向
		int size = south.size();
		int length = size / 2;
		int index = 0;
		if (size % 2 == 1) {
			I单张牌数据 single = south.get(0);
			JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = index++;
			gridBagConstraints.gridy = 2;
			jPanel15.add(label, gridBagConstraints);

			for (int i = 1; i < south.size(); i++) {
				I单张牌数据 single1 = south.get(i);
				if (null == single1) {
					continue;
				}
				JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
				if (i % 2 == 1) {
					GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
					gridBagConstraints0.gridx = index;
					gridBagConstraints0.gridy = 0;
					gridBagConstraints0.insets = new java.awt.Insets(0, 2, 0, 2);
					JLabel label2 = new JLabel(String.valueOf(length--));
					label2.setForeground(color2);
					jPanel15.add(label2, gridBagConstraints0);

					GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
					gridBagConstraints1.gridx = index;
					gridBagConstraints1.gridy = 1;
					jPanel15.add(label1, gridBagConstraints1);
				} else {
					GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
					gridBagConstraints2.gridx = index;
					gridBagConstraints2.gridy = 2;
					jPanel15.add(label1, gridBagConstraints2);
					index++;
				}
			}
		} else {
			for (int i = 0; i < south.size(); i++) {
				I单张牌数据 single1 = south.get(i);
				if (null == single1) {
					continue;
				}
				JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
				if (i % 2 == 0) {
					GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
					gridBagConstraints0.gridx = index;
					gridBagConstraints0.gridy = 0;
					gridBagConstraints0.insets = new java.awt.Insets(0, 2, 0, 2);
					JLabel label2 = new JLabel(String.valueOf(length--));
					label2.setForeground(color2);
					jPanel15.add(label2, gridBagConstraints0);

					GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
					gridBagConstraints1.gridx = index;
					gridBagConstraints1.gridy = 1;
					jPanel15.add(label1, gridBagConstraints1);
				} else {
					GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
					gridBagConstraints2.gridx = index;
					gridBagConstraints2.gridy = 2;
					jPanel15.add(label1, gridBagConstraints2);
					index++;
				}
			}
		}
	}

	private void initResultEast2(ArrayList<I单张牌数据> east) {
		String type = "type_3";// 图片大小
		String direct = "card_type_right";// 图片方向
		int size = east.size();
		if (size % 2 == 1) {
			int index = size / 2;
			I单张牌数据 single = east.get(0);
			JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = index--;
			jPanel13.add(label, gridBagConstraints);

			for (int i = 1; i < east.size(); i++) {
				I单张牌数据 single1 = east.get(i);
				if (null == single1) {
					continue;
				}
				JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
				if (i % 2 == 1) {
					GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
					gridBagConstraints0.gridx = 0;
					gridBagConstraints0.gridy = index;
					gridBagConstraints0.insets = new java.awt.Insets(2, 0, 2, 0);
					JLabel label2 = new JLabel(String.valueOf(index + 1));
					label2.setForeground(color2);
					jPanel13.add(label2, gridBagConstraints0);

					GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
					gridBagConstraints1.gridx = 1;
					gridBagConstraints1.gridy = index;
					jPanel13.add(label1, gridBagConstraints1);
				} else {
					GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
					gridBagConstraints2.gridx = 2;
					gridBagConstraints2.gridy = index;
					jPanel13.add(label1, gridBagConstraints2);
					index--;
				}
			}
		} else {
			int index = size / 2 - 1;
			for (int i = 0; i < east.size(); i++) {
				I单张牌数据 single1 = east.get(i);
				if (null == single1) {
					continue;
				}
				JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
				if (i % 2 == 0) {
					GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
					gridBagConstraints0.gridx = 0;
					gridBagConstraints0.gridy = index;
					gridBagConstraints0.insets = new java.awt.Insets(2, 0, 2, 0);
					JLabel label2 = new JLabel(String.valueOf(index + 1));
					label2.setForeground(color2);
					jPanel13.add(label2, gridBagConstraints0);

					GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
					gridBagConstraints1.gridx = 1;
					gridBagConstraints1.gridy = index;
					jPanel13.add(label1, gridBagConstraints1);
				} else {
					GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
					gridBagConstraints2.gridx = 2;
					gridBagConstraints2.gridy = index;
					jPanel13.add(label1, gridBagConstraints2);
					index--;
				}
			}
		}
	}

	private void initResultNorth2(ArrayList<I单张牌数据> north) {
		String type = "type_3";// 图片大小
		String direct = "card_type_up";// 图片方向
		int size = north.size();
		if (size % 2 == 1) {
			int index = size / 2;
			I单张牌数据 single = north.get(0);
			JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = index--;
			gridBagConstraints.gridy = 0;
			jPanel14.add(label, gridBagConstraints);

			for (int i = 1; i < north.size(); i++) {
				I单张牌数据 single1 = north.get(i);
				if (null == single) {
					continue;
				}
				JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
				if (i % 2 == 1) {
					GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
					gridBagConstraints0.gridx = index;
					gridBagConstraints0.gridy = 2;
					gridBagConstraints0.insets = new java.awt.Insets(0, 2, 0, 2);
					JLabel label2 = new JLabel(String.valueOf(index + 1));
					label2.setForeground(color2);
					jPanel14.add(label2, gridBagConstraints0);

					GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
					gridBagConstraints1.gridx = index;
					gridBagConstraints1.gridy = 1;
					jPanel14.add(label1, gridBagConstraints1);
				} else {
					GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
					gridBagConstraints2.gridx = index;
					gridBagConstraints2.gridy = 0;
					jPanel14.add(label1, gridBagConstraints2);
					index--;
				}
			}
		} else {
			int index = size / 2 - 1;
			for (int i = 0; i < north.size(); i++) {
				I单张牌数据 single1 = north.get(i);
				if (null == single1) {
					continue;
				}
				JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
				if (i % 2 == 0) {
					GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
					gridBagConstraints0.gridx = index;
					gridBagConstraints0.gridy = 2;
					gridBagConstraints0.insets = new java.awt.Insets(0, 2, 0, 2);
					JLabel label2 = new JLabel(String.valueOf(index + 1));
					label2.setForeground(color2);
					jPanel14.add(label2, gridBagConstraints0);

					GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
					gridBagConstraints1.gridx = index;
					gridBagConstraints1.gridy = 1;
					jPanel14.add(label1, gridBagConstraints1);
				} else {
					GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
					gridBagConstraints2.gridx = index;
					gridBagConstraints2.gridy = 0;
					jPanel14.add(label1, gridBagConstraints2);
					index--;
				}
			}
		}
	}

	private void initResultWest2(ArrayList<I单张牌数据> west) {
		String type = "type_3";// 图片大小
		String direct = "card_type_left";// 图片方向
		int size = west.size();
		int length = size / 2;
		int index = 0;
		if (size % 2 == 1) {
			I单张牌数据 single = west.get(0);
			JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = index++;
			jPanel12.add(label, gridBagConstraints);

			for (int i = 1; i < west.size(); i++) {
				I单张牌数据 single1 = west.get(i);
				if (null == single1) {
					continue;
				}
				JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
				if (i % 2 == 1) {
					GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
					gridBagConstraints0.gridx = 2;
					gridBagConstraints0.gridy = index;
					gridBagConstraints0.insets = new java.awt.Insets(2, 0, 2, 0);
					JLabel label2 = new JLabel(String.valueOf(length--));
					label2.setForeground(color2);
					jPanel12.add(label2, gridBagConstraints0);

					GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
					gridBagConstraints1.gridx = 1;
					gridBagConstraints1.gridy = index;
					jPanel12.add(label1, gridBagConstraints1);
				} else {
					GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
					gridBagConstraints2.gridx = 0;
					gridBagConstraints2.gridy = index;
					jPanel12.add(label1, gridBagConstraints2);
					index++;
				}
			}
		} else {
			for (int i = 0; i < west.size(); i++) {
				I单张牌数据 single1 = west.get(i);
				if (null == single1) {
					continue;
				}
				JLabel label1 = new JLabel(new ImageIcon(this.getIcon(single1, type, direct)));
				if (i % 2 == 0) {
					GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
					gridBagConstraints0.gridx = 2;
					gridBagConstraints0.gridy = index;
					gridBagConstraints0.insets = new java.awt.Insets(2, 0, 2, 0);
					JLabel label2 = new JLabel(String.valueOf(length--));
					label2.setForeground(color2);
					jPanel12.add(label2, gridBagConstraints0);

					GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
					gridBagConstraints1.gridx = 1;
					gridBagConstraints1.gridy = index;
					jPanel12.add(label1, gridBagConstraints1);
				} else {
					GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
					gridBagConstraints2.gridx = 0;
					gridBagConstraints2.gridy = index;
					jPanel12.add(label1, gridBagConstraints2);
					index++;
				}
			}
		}
	}

	private void initCurrentPlayerTiles(ArrayList<I单张牌数据> currentPlayerTiles, int playerindex) {
		if (null == currentPlayerTiles || currentPlayerTiles.size() < 4 || playerindex > 3) {
			return;
		}
		I单张牌数据 south = currentPlayerTiles.get(playerindex);
		I单张牌数据 east = currentPlayerTiles.get((playerindex + 1) % 4);
		I单张牌数据 north = currentPlayerTiles.get((playerindex + 2) % 4);
		I单张牌数据 west = currentPlayerTiles.get((playerindex + 3) % 4);
		if (null != south) {
			initCurrentPlayerTilesSouth(south);
		}
		if (null != east) {
			initCurrentPlayerTilesEast(east);
		}
		if (null != north) {
			initCurrentPlayerTilesNorth(north);
		}
		if (null != west) {
			initCurrentPlayerTilesWest(west);
		}
	}

	private void initCurrentPlayerTilesSouth(I单张牌数据 single) {
		String type = "type_2";// 图片大小
		String direct = "card_type_down";// 图片方向
		JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(50, 0, 0, 0);
		jPanel19.add(label, gridBagConstraints);
	}

	private void initCurrentPlayerTilesEast(I单张牌数据 single) {
		String type = "type_2";// 图片大小
		String direct = "card_type_right";// 图片方向
		JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		jPanel17.add(label, gridBagConstraints);
	}

	private void initCurrentPlayerTilesNorth(I单张牌数据 single) {
		String type = "type_2";// 图片大小
		String direct = "card_type_up";// 图片方向
		JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 50, 0);
		jPanel18.add(label, gridBagConstraints);
	}

	private void initCurrentPlayerTilesWest(I单张牌数据 single) {
		String type = "type_2";// 图片大小
		String direct = "card_type_left";// 图片方向
		JLabel label = new JLabel(new ImageIcon(this.getIcon(single, type, direct)));
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		jPanel16.add(label, gridBagConstraints);
	}
}
