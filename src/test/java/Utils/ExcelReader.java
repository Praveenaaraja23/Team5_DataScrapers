package Utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import recipeData.Filterdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {

    public static Filterdata read(String excelFilePath, String sheetName) {
        Filterdata filterData = new Filterdata();
        try (FileInputStream fis = new FileInputStream(new File(excelFilePath))) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(sheetName);

            List<String> eliminate = new ArrayList<>();
            List<String> add = new ArrayList<>();
            List<String> recipesToAvoid = new ArrayList<>();
            List<String> foodProcessing = new ArrayList<>();

            int rows = sheet.getPhysicalNumberOfRows();
            for (int rowIdx = 1; rowIdx < rows; rowIdx++) {
                Row row = sheet.getRow(rowIdx);

               if (row != null) {
                    Cell eliminateCell = row.getCell(0);
                    if (eliminateCell != null) eliminate.add(eliminateCell.getStringCellValue());

                    Cell addCell = row.getCell(1);
                    if (addCell != null) add.add(addCell.getStringCellValue());

                    Cell recipesToAvoidCell = row.getCell(2);
                    if (recipesToAvoidCell != null) recipesToAvoid.add(recipesToAvoidCell.getStringCellValue());

                    Cell foodProcessingCell = row.getCell(3);
                    if (foodProcessingCell != null) foodProcessing.add(foodProcessingCell.getStringCellValue());
                }
            }

            filterData.setEliminate(eliminate);
            filterData.setAdd(add);
            filterData.setRecipesToAvoid(recipesToAvoid);
            filterData.setFoodProcessing(foodProcessing);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return filterData;
    }
}
