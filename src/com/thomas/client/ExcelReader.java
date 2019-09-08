package com.thomas.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {
	public List<Map<String, String>> list;//A 
	public List<Map<String, String>> list1;//B
	public List<Map<String, String>> list2;//common

	public ExcelReader() {
		Workbook wb = null;
		Sheet sheet = null;
		Row row = null;
		String cellData = null;
		String filePath = "麻将编码及参数.xlsx";
		String columns[] = new String[15];//AB
		wb = readExcel(filePath);
		if (wb != null) {
			// 用来存放表中数据
			list = new ArrayList<Map<String, String>>();
			list1 = new ArrayList<Map<String, String>>();
			list2 = new ArrayList<Map<String, String>>();
			// 获取第一个sheet
			sheet = wb.getSheetAt(0);
			// 获取最大行数
			int rownum = 145;// sheet.getPhysicalNumberOfRows();
			// 获取第一行
			row = sheet.getRow(0);
			// 获取最大列数
			int colnum = 15;// row.getPhysicalNumberOfCells();
			if (row != null) {
				for (int j = 0; j < colnum; j++) {
					cellData = (String) getCellFormatValue(row.getCell(j));
					columns[j] = cellData;
				}
			}
			for (int i = 1; i < rownum; i++) {
				Map<String, String> map = new LinkedHashMap<String, String>();
				row = sheet.getRow(i);
				if (row != null) {
					for (int j = 0; j < colnum; j++) {
						cellData = (String) getCellFormatValue(row.getCell(j));
				//		Integer.parseInt(cellData, 16);
						map.put(columns[j], cellData);
					}
				} else {
					break;
				}
				list.add(map);
			}
			//------B----------
			sheet = wb.getSheetAt(1);
			for (int i = 1; i < rownum; i++) {
				Map<String, String> map = new LinkedHashMap<String, String>();
				row = sheet.getRow(i);
				if (row != null) {
					for (int j = 0; j < colnum; j++) {
						cellData = (String) getCellFormatValue(row.getCell(j));
						map.put(columns[j], cellData);
					}
				} else {
					break;
				}
				list1.add(map);
			}
			
			//--------common--------
			rownum = 69;
			colnum = 2;
		
			sheet = wb.getSheetAt(2);
			for (int i = 0; i < rownum; i++) {
				Map<String, String> map = new LinkedHashMap<String, String>();
				row = sheet.getRow(i);
				if (row != null) {
//					for (int j = 0; j < colnum; j++) {
//						cellData = (String) getCellFormatValue(row.getCell(j));
						map.put((String) getCellFormatValue(row.getCell(0)), (String) getCellFormatValue(row.getCell(1)));
//					}
				} else {
					break;
				}
				list2.add(map);
			}
		}
		
		
		
		// 遍历解析出来的list
//		for (Map<String, String> map : list2) {
//			for (Entry<String, String> entry : map.entrySet()) {
//				System.out.print(entry.getKey() + ":" + entry.getValue() + ",");
//			}
//			System.out.println();
//		}

	}

	// 读取excel
	public static Workbook readExcel(String filePath) {
		Workbook wb = null;
		if (filePath == null) {
			return null;
		}
		String extString = filePath.substring(filePath.lastIndexOf("."));
		InputStream is = null;
		try {
			is = new FileInputStream(filePath);
			if (".xls".equals(extString)) {
				wb = new HSSFWorkbook(is);
			} else if (".xlsx".equals(extString)) {
				wb = new XSSFWorkbook(is);
			} else {
				wb = null;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return wb;
	}

	public static Object getCellFormatValue(Cell cell) {
		Object cellValue = null;
		if (cell != null) {
			// 判断cell类型
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC: {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String temp = cell.getStringCellValue();
				// 判断是否包含小数点，如果不含小数点，则以字符串读取，如果含小数点，则转换为Double类型的字符串
				if (temp.indexOf(".") > -1) {
					cellValue = String.valueOf(new Double(temp)).trim();
				} else {
					cellValue = temp.trim();
				}

				// cellValue = String.valueOf(cell.getNumericCellValue());
				break;
			}
			case Cell.CELL_TYPE_STRING: {
				cellValue = cell.getRichStringCellValue().getString();
				break;
			}
			default:
				cellValue = "";
			}
		} else {
			cellValue = "";
		}
		return cellValue;
	}

}