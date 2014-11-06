package project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;


import java.util.Vector;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TimeTable {
	// timestamp -> Ocupa��o("livre"||"Especialidade|Paciente;Especialidade|Paciente") Hospital
	// timestamp -> Ocupa��o("ocupado"||"livre"||"Especialidade"(||"Marca��o?)")
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

	public HashMap<String,String> interpretConsultations(String slotConsultations){
		HashMap<String,String> intrepertation = new HashMap<String,String>();
		if(slotConsultations.equals("livre")){
			intrepertation.put("livre", "livre");
		}
		else{
			String[] Consultations = slotConsultations.split(";");
			for(int i=0;i<Consultations.length;i++)
			{
				String[] Patient = Consultations[i].split("-");
				intrepertation.put(Patient[0], Patient[1]);
			}

		}
		return intrepertation;
	}

	public Boolean slotTaken(String Speciality, int TimeStamp){

		return  interpretConsultations(timetable.get(TimeStamp)).containsKey(Speciality);

	}

	public void ScheduleAppointment(int TimeStamp, String PatientName, String Speciality){
		if(interpretConsultations(timetable.get(TimeStamp)).get("livre") == "livre")
			timetable.replace(TimeStamp, Speciality+"-"+PatientName);
		else{
			timetable.replace(TimeStamp, timetable.get(TimeStamp)+";"+Speciality+"-"+PatientName);
		}
	}

	//returns timestamp of the first free time since the time given (0 if none)
	public int FirstAvailable(int TimeStamp){

		for (int i=TimeStamp; i>0; i+=3600) {
			if(timetable.containsKey(i)){
				if(timetable.get(i).equals("livre")){
					return i;
				}
			}
			else
				break;
		}

		return 0;
	}
	
	//returns the name of the patients with consultations(null if none)
	public Vector<String> PatientsToNotify(int TimeStamp){
		String content = new String();
		if(timetable.containsKey(TimeStamp))
			content = timetable.get(TimeStamp);
		if(!content.equals("livre"))
		{
			Vector<String> PatientsNames = new Vector<String>();
			
			HashMap<String,String> temp = interpretConsultations(content);
			
			for(String key:temp.keySet()){
				PatientsNames.add(temp.get(key));
			}
			
			return PatientsNames;
		}
		return null;
	}

	/*public static void main(String args[]) throws IOException{
		TimeTable t = new TimeTable("TimeTable.xlsx",0);
		System.out.println(t.timetable.toString());
		t.ScheduleAppointment(1420106400,"Patient1","uranus");
		System.out.println(t.timetable.toString());
		t.ScheduleAppointment(1420106400,"jorge","jorge");
		System.out.println(t.timetable.toString());
		Vector<String> temp = t.PatientsToNotify(1420106400);
		for(int i=0; i<temp.size();i++){
			System.out.println(temp.get(i));
		}


	}*/
}
