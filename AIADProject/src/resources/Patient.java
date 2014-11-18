package resources;

import java.io.IOException;
import java.util.HashMap;

public class Patient {

	private String speciality;
	private TimeTable timetable;
	private long startComunication;
	private int typeCommunication;

	public Patient(String speciality, int page) {
		this.setSpeciality(speciality);
		try {
			timetable = new TimeTable("TimeTable.xlsx", page);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public HashMap<Long, String> getTimetableTimetable() {
		return timetable.timetable;
	}
	
	public TimeTable getTimetable() {
		return timetable;
	}

	public void setTimetable(TimeTable timetable) {
		this.timetable = timetable;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}
}
