package project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TimeTable {
	// timestamp -> Ocupação("livre"||"Especialidade/Paciente") Hospital
	// timestamp -> Ocupação("ocupado"||"livre"||"Especialidade")
	public HashMap<Integer, String> timetable;

	TimeTable(String FileName, int i) throws IOException{
		timetable = new HashMap<Integer, String>();
		try
		{
			double ttemp=0;
			FileInputStream file = new FileInputStream(new File(FileName));

			//Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(i);

			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

			//Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) 
			{
				Row row = rowIterator.next();
				//For each row, iterate through all the columns
				Iterator<Cell> cellIterator = row.cellIterator();

				while (cellIterator.hasNext()) 
				{
					Cell cell = cellIterator.next();
					//Check the cell type and format accordingly
					switch (evaluator.evaluateInCell(cell).getCellType()) 
					{
					case Cell.CELL_TYPE_NUMERIC:
						ttemp= cell.getNumericCellValue();
						break;
					case Cell.CELL_TYPE_STRING:
						timetable.put((int)ttemp, cell.getStringCellValue());
						break;
					case Cell.CELL_TYPE_FORMULA:
						//Not again
						break;
					}
				}

			}
			file.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws IOException{
		new TimeTable("TimeTable.xlsx",0);
		
	}

}
