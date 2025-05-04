package Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import recipeTests.FilterVo;



public class ExcelReader {
	

	public static void read(String path,String sheetName, Integer to_add_col_number,Integer to_avoid_col_number) {

		FileInputStream fis;
		XSSFWorkbook workBook;
		XSSFSheet sheet;

		List<String> lstEliminate=new ArrayList<String>();
		List<String> lstAdd=new ArrayList<String>();
		List<String> recipeToAvoid=new ArrayList<String>();
		List<String> To_Add_If_notFullyVgean=new ArrayList<String>();
		//List<String> optinalRecipes=new ArrayList<String>();

		try {


			fis = new FileInputStream(path);

			workBook = new XSSFWorkbook(fis);

			sheet = workBook.getSheet(sheetName);

			int rowCount=sheet.getPhysicalNumberOfRows();

			for(int rowNum=2;rowNum<rowCount;rowNum++)
			{
				if(sheet.getRow(rowNum).getCell(0)!=null &&  !getCellData(sheet.getRow(rowNum).getCell(0)).toLowerCase().trim().isEmpty())
					lstEliminate.add(getCellData(sheet.getRow(rowNum).getCell(0)).toLowerCase().trim());

				if(sheet.getRow(rowNum).getCell(1)!=null && !getCellData(sheet.getRow(rowNum).getCell(1)).toLowerCase().trim().isEmpty())
					lstAdd.add(getCellData(sheet.getRow(rowNum).getCell(1)).toLowerCase().trim());

				if(to_add_col_number!=null)
				{
					if(sheet.getRow(rowNum).getCell(to_add_col_number.intValue()) !=null
							&& !getCellData(sheet.getRow(rowNum).getCell(to_add_col_number.intValue())).toLowerCase().trim().isEmpty())
						To_Add_If_notFullyVgean.add(getCellData(sheet.getRow(rowNum).getCell(to_add_col_number.intValue())).toLowerCase().trim());
				}

				if(to_avoid_col_number!=null)
				{
					if(sheet.getRow(rowNum).getCell(to_avoid_col_number.intValue()) !=null &&
							!getCellData(sheet.getRow(rowNum).getCell(to_avoid_col_number.intValue())).toLowerCase().trim().isEmpty())
						recipeToAvoid.add(getCellData(sheet.getRow(rowNum).getCell(to_avoid_col_number.intValue())).toLowerCase().trim());
				}

				//	if(!getCellData(sheet.getRow(rowNum).getCell(4)).toLowerCase().trim().isEmpty())
				//		optinalRecipes.add(getCellData(sheet.getRow(rowNum).getCell(4)).toLowerCase().trim());

			}

			try {
				workBook.close();
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	
	}


	public static String getCellData(XSSFCell cell) throws IOException {

		CellType type=cell.getCellType();

		switch(type)
		{
		case FORMULA:
			return cell .getStringCellValue();
		case NUMERIC:
			try {
				return String.valueOf((int)cell .getNumericCellValue());
			} catch (Exception e) {
				return "";
			} 
		case BOOLEAN:
			try {
				return String.valueOf(cell .getBooleanCellValue());
			} catch (Exception e) {
				return "";
			} 
		case STRING:
			return cell .getStringCellValue();
		case BLANK:
			return "";
		}
		return cell .getStringCellValue();

	}



}

