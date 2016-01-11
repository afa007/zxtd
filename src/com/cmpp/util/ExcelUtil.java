package com.cmpp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;

public class ExcelUtil {

	private static Gson gson = new Gson();

	/**
	 * 通过传入excel文件的完整路径。然后解析excel文件。
	 * 
	 * @param path
	 * @return ArrayList<ArrayList<ArrayList<String>>>
	 */
	private static ArrayList<ArrayList<ArrayList<String>>> parseExcel(
			String path) {
		ArrayList<ArrayList<ArrayList<String>>> sheets = new ArrayList<ArrayList<ArrayList<String>>>();

		Workbook workbook = null;
		try {
			// excel文件有两种格式(.xls(2003) , .xlsx(2007))
			// XSSFWorkbook 用来加载后缀名为 .xlsx 文件的
			workbook = new XSSFWorkbook(new FileInputStream(new File(path)));
		} catch (Exception ex) {
			try {
				// HSSFWorkbook 用来加载后缀名为 .xls 文件的
				workbook = new HSSFWorkbook(new FileInputStream(new File(path)));

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// excel文件里面有很多的sheet, 要遍历每一张sheet
		for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
			Sheet sheet = workbook.getSheetAt(numSheet);
			if (sheet == null) {
				continue;
			}
			// 每张sheet里面有很多的row, 要遍历每行
			ArrayList<ArrayList<String>> Row = new ArrayList<ArrayList<String>>();
			String sql = "";
			String msisdn = "";
			String msg = "";
			String date = "";
			for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
				Row row = sheet.getRow(rowNum);
				if (row == null) {
					continue;
				}
				// 每行都有很多列, 要遍历每列
				ArrayList<String> arrCell = new ArrayList<String>();
				for (int cellNum = 0; cellNum <= row.getLastCellNum(); cellNum++) {
					Cell cell = row.getCell(cellNum);
					if (cell == null) {
						continue;
					}
					/*
					if (1 == cellNum) {
						msisdn = getValue(cell);
					}

					if (2 == cellNum) {
						msg = getValue(cell);
					}

					if (10 == cellNum) {
						date = getValue(cell);
					}
					*/
					if (0 == cellNum) {
						msisdn = getValue(cell);
					}

					if (3 == cellNum) {
						msg = getValue(cell);
					}

					if (4 == cellNum) {
						date = getValue(cell);
					}
					
					arrCell.add(getValue(cell));
				}
				Row.add(arrCell);
				// System.out.println(gson.toJson(arrCell));
				/*sql = "insert into tb_smspocessor(SMSPOCESSOR_ID, MSISDN ,TYPE,CONTENT,CREATETIME,STATUS) values(uuid(), '"
						+ msisdn
						+ "', '接收', '"
						+ msg
						+ "', '"
						+ date
						+ "','1');";*/
				sql = "insert into tb_smspocessor(SMSPOCESSOR_ID, MSISDN ,TYPE,CONTENT,CREATETIME,STATUS) values(uuid(), '"
						+ msisdn
						+ "', '发送', '"
						+ msg
						+ "', '"
						+ date
						+ "','1');";
				System.out.println(sql);
			}

			sheets.add(Row);

		}
		return sheets;
	}

	/*
	 * 得到每个cell里面的值
	 */
	private static String getValue(Cell cell) {
		if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			// System.out.println(String.valueOf(cell.getBooleanCellValue()));
			return String.valueOf(cell.getBooleanCellValue());
		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			// System.out.println(String.valueOf(cell.getNumericCellValue()));
			return String.valueOf(cell.getNumericCellValue());
		} else {
			// System.out.println(String.valueOf(cell.getStringCellValue()));
			return String.valueOf(cell.getStringCellValue());
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<String> flist = new ArrayList<String>();
		flist.add("smsgetlog");
		flist.add("smsget");
		
		try {
			// 文件生成路径
			PrintStream ps = new PrintStream("D:\\其他\\数据移植\\smsdata\\smsdata\\sendlog.txt");
			System.setOut(ps);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String path = "D:\\其他\\数据移植\\smsdata\\smsdata\\sendlog.xls";
		ExcelUtil.parseExcel(path);
		

		flist.add("sendlog");
		flist.add("sendlog2");
		flist.add("send1");
		flist.add("send1a");
		flist.add("send1b");
		
	}

}
