package Utils;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;

public class ExcelReader {
	
	public static Object[][] readExcelData(String sheetName) {
        try {
        	FileInputStream fis = new FileInputStream("src/test/resources/data/IngredientsAndComorbidities-ScrapperHackathon.xlsx");
        	Workbook workbook = new XSSFWorkbook(fis);
        	Sheet sheet = workbook.getSheet(sheetName);
        
            int rowCount = sheet.getLastRowNum();
            int colCount = sheet.getRow(0).getLastCellNum();
            Object[][] data = new Object[rowCount][colCount];
            
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);                
                if (row != null)
                {
	                for (int j = 0; j < colCount; j++) {
	                    Cell cell = row.getCell(j);
	                    if(cell != null)
	                    {
	                    	data[i - 1][j] = cell.toString();
	                    }
	                    else
	                    {
	                    	data[i - 1][j] = "";
	                    }	
	                }
                }
            }
            workbook.close();
    		fis.close();
            return data;
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            return null;
        }
	}
 
}