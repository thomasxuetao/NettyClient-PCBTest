package com.thomas.client;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class ClientTable extends JTable{

	private static final long serialVersionUID = 1L;

	public ClientTable(){
		
	}
	
	public ClientTable(ClientTableModel model){
		super(model);
		setRowHeight(30);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getTableHeader().setReorderingAllowed(false);   
		getTableHeader().setResizingAllowed(false);  
		
	}
	
	@Override
	public boolean isCellEditable(int row, int column){
          return false;
    }
	
	
}
