package Utils;

import java.io.IOException;
import org.testng.annotations.DataProvider;

public class DataProviders {
	 
	@DataProvider(name = "LCHF_Final_list")
    public static Object[][] loginDataProvider() throws IOException {
        return ExcelReader.readExcelData("Final list for LCHFElimination ");
    }	
	
}
