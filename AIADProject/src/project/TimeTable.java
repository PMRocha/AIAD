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
	// timestamp -> Ocupação("livre"||"Especialidade|Paciente;Especialidade|Paciente") Hospital
	// timestamp -> Ocupação("ocupado"||"livre"||"Especialidade"(||"Marcação?)")
	public HashMap<Long, String> timetable;

	TimeTable(String fileName, int i) throws IOException{
		timetable = new HashMap<Long, String>();
		try
		{
			double ttemp=0;
			FileInputStream file = new FileInputStream(new File(fileName));

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
						timetable.put((long)ttemp, cell.getStringCellValue());
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

	public Boolean slotTaken(String speciality, long timeStamp){

		return  (interpretConsultations(timetable.get(timeStamp)).containsKey(speciality) || interpretConsultations(timetable.get(timeStamp)).containsKey("ocupado"));

	}

	public void scheduleAppointment(Long timeStamp, String patientName, String speciality){
		if(interpretConsultations(timetable.get(timeStamp)).get("livre") == "livre")
			timetable.replace(timeStamp, speciality+"-"+patientName);
		else{
			timetable.replace(timeStamp, timetable.get(timeStamp)+";"+speciality+"-"+patientName);
		}
	}

	//returns timestamp of the first free time since the time given (0 if none)
	public long firstAvailable(long timeEpooch){

		for (long i=timeEpooch; i>0; i+=3600) {
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
	public Vector<String> patientsToNotify(int timeStamp){
		String content = new String();
		if(timetable.containsKey(timeStamp))
			content = timetable.get(timeStamp);
		if(!content.equals("livre"))
		{
			Vector<String> patientsNames = new Vector<String>();
			
			HashMap<String,String> temp = interpretConsultations(content);
			
			for(String key:temp.keySet()){
				patientsNames.add(temp.get(key));
			}
			
			return patientsNames;
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
