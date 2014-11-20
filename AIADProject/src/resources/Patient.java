package resources;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import agents.PatientAgent;

public class Patient {

	private String speciality;
	private TimeTable timetable;
	private long startCommunication;
	private int typeCommunication;// if 0 the appointment will be normal, if 1
									// the appointment will be a urgence
	private PatientAgent patientAgent;

	public Patient(String speciality, int page, long startCommunication,
			int typeCommunication, PatientAgent patientAgent) {
		
		this.setSpeciality(speciality);
		this.patientAgent=patientAgent;
		
		if (startCommunication <= 1420070400) {
			this.startCommunication = 1420070400;
		} else
			this.startCommunication = startCommunication;

		this.typeCommunication = typeCommunication;
		try {
			timetable = new TimeTable("TimeTable.xlsx", page);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("patient type:" + typeCommunication + " "
				+ startCommunication);
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

	// starts clock
	public void runTime() {

		ScheduledExecutorService exec = Executors
				.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (startCommunication == TimeClock.timeEpooch) {
					System.out.println("mandei mensagem");
					
					long halfDayDif = TimeClock.timeEpooch + 12 * 3600;
					patientAgent.appointment(halfDayDif);
				}
			}
		}, 0, 1, TimeUnit.SECONDS);

	}

}
