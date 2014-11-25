package resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import agents.HospitalAgent;

public class TimeTable { 
	// timestamp ->
	// Ocupação("livre"||"Especialidade|Paciente;Especialidade|Paciente")
	// Hospital
	// timestamp -> Ocupação("ocupado"||"livre"||"Especialidade"(||"Marcação?)")
	public HashMap<Long, String> timetable;

	public TimeTable(String fileName, int i) throws IOException {
		timetable = new HashMap<Long, String>();
		try {
			double ttemp = 0;
			FileInputStream file = new FileInputStream(new File(fileName));

			// Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			// Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(i);

			FormulaEvaluator evaluator = workbook.getCreationHelper()
					.createFormulaEvaluator();

			// Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				// For each row, iterate through all the columns
				Iterator<Cell> cellIterator = row.cellIterator();

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					// Check the cell type and format accordingly
					switch (evaluator.evaluateInCell(cell).getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						ttemp = cell.getNumericCellValue();
						break;
					case Cell.CELL_TYPE_STRING:
						timetable.put((long) ttemp, cell.getStringCellValue());
						break;
					case Cell.CELL_TYPE_FORMULA:
						// Not again
						break;
					}
				}

			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void exportTimeTable(String fileName){
	        //Blank workbook
	        XSSFWorkbook workbook = new XSSFWorkbook();
	         
	        //Create a blank sheet
	        XSSFSheet sheet = workbook.createSheet("Employee Data");
	          
	        //Iterate over data and write to sheet
	        int rownum = 0;
	        for (long key : timetable.keySet())
	        {
	            Row row = sheet.createRow(rownum++);
	            Cell timeStamp = row.createCell(0);
	            Cell Content = row.createCell(1);
	            timeStamp.setCellValue(key);
	            Content.setCellValue(timetable.get(key));
	        }
	        try
	        {
	            //Write the workbook in file system
	            FileOutputStream out = new FileOutputStream(new File(fileName+".xlsx"));
	            workbook.write(out);
	            out.close();
	            System.out.println("howtodoinjava_demo.xlsx written successfully on disk.");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	
	public HashMap<String, String> interpretConsultations(
			String slotConsultations) {
		HashMap<String, String> intrepertation = new HashMap<String, String>();
		if (slotConsultations.equals("livre")) {
			intrepertation.put("livre", "livre");
		}
		else if (slotConsultations.equals("fechado")) {
				intrepertation.put("fechado", "fechado");
				
		} else {
			String[] Consultations = slotConsultations.split(";");
			for (int i = 0; i < Consultations.length; i++) {
				String[] Patient = Consultations[i].split("-");
				intrepertation.put(Patient[0], Patient[1]);
			}

		}
		return intrepertation;
	}

	public String NextPatient (long timeEpooch,long maxTimeSearch, String Speciality) {
		String PatientName = null;
		String content = new String();
		for(long i=timeEpooch;i<maxTimeSearch; i+=3600){
			if (!content.equals("livre")||!content.equals("fechado")) {

				HashMap<String, String> temp = interpretConsultations(content);
				for (String key : temp.keySet()) {
					if(key.equals(Speciality))
						return temp.get(key);
				}
			}
		}

		return PatientName;
	}
	
	public Boolean slotTaken(String speciality, long timeStamp) {

		return (interpretConsultations(timetable.get(timeStamp)).containsKey(
				speciality) || interpretConsultations(timetable.get(timeStamp))
				.containsKey("ocupado"));

	}

	public void scheduleAppointment(Long timeStamp, String patientName,
			String speciality) {
		if (interpretConsultations(timetable.get(timeStamp)).get("livre") == "livre")
			timetable.replace(timeStamp, speciality + "-" + patientName);
		else {
			timetable.replace(timeStamp, timetable.get(timeStamp) + ";"
					+ speciality + "-" + patientName);
		}
	}

	// returns timestamp of the first free time since the time given (0 if none)
	public long firstAvailable(long timeEpooch) {

		for (long i = timeEpooch; i > 0; i += 3600) {
			if (timetable.containsKey(i)) {
				if (timetable.get(i).equals("livre")) {
					return i;
				}
			} else
				break;
		}

		return 0;
	}
	
	public long firstAvailableReschedulable(long timeEpooch) {

		for (long i = timeEpooch; i > 0; i += 3600) {
			if (timetable.containsKey(i)) {
				if (timetable.get(i).equals("livre")||timetable.get(i).equals("ocupador")) {
					return i;
				}
			} else
				break;
		}

		return 0;
	}

	// sends notifications to patients
	public void patientsToNotify(long timeEpooch,
			HospitalAgent hospitalPlanner) {
		
		long help=timeEpooch+3600*24;
				
		String content = new String();
		if (timetable.containsKey(help))
			content = timetable.get(help);
		if (!content.equals("livre")) {

			HashMap<String, String> temp = interpretConsultations(content);

			for (String key : temp.keySet()) {
				hospitalPlanner.sendNotification(temp.get(key),key,help);
			}
		}
	}

	public void patientsHavingAppointment(long timeEpooch) {
		String content = new String();
		if (timetable.containsKey(timeEpooch))
			content = timetable.get(timeEpooch);
		if (!content.equals("livre")||!content.equals("fechado")) {

			HashMap<String, String> temp = interpretConsultations(content);

			for (String key : temp.keySet()) {
				System.out.println("Consulta-"+temp.get(key)+"-"+key);
			}
		}
		
	}
	
	public void cancelConsultation(long timeEpooch, String speciality) {
		String content = new String();
		String newContent = new String();
		if (timetable.containsKey(timeEpooch)){
			content = timetable.get(timeEpooch);
			HashMap<String, String> temp = interpretConsultations(content);

			for (String key : temp.keySet()) {
				if(!key.equals(speciality)){
					if(newContent.length()>1){
						newContent +=";" + key + "-" + timetable.get(key); 
					}
					else{
						newContent += key + "-" + timetable.get(key); 
					}
				}
			}
			timetable.put(timeEpooch, newContent);
		}
	}
}
