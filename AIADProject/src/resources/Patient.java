package resources;

import gui.PatientGUI;

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
	private int typeAppointment;// if 0 the appointment will be normal, if 1
									// the appointment will be a urgence
	private PatientAgent patientAgent;
	private long timeEpooch = 1420066800;
	private int algorithmAppointment;
	private boolean done;
	
	//helps with appointment
	private long timeCommunication;
	private boolean useReserved;

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public Patient(String speciality, int page, long startCommunication,
			int typeCommunication, int algorithmAppointment,
			PatientAgent patientAgent) {
		
		setTimeCommunication(startCommunication);
		setUseReserved(false);
		this.setSpeciality(speciality);
		this.patientAgent = patientAgent;
		this.algorithmAppointment = algorithmAppointment;
		done=false;
		if (startCommunication <= 1420070400) {
			this.startCommunication = 1420070400;
		} else
			this.startCommunication = startCommunication;

		this.typeAppointment = typeCommunication;
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

				timeEpooch += 3600;

				if (startCommunication == timeEpooch) {

					if (algorithmAppointment == 0) {
						if (typeAppointment == 0) {
							long halfDayDif = timeEpooch + 12 * 3600;
							patientAgent.appointment0(halfDayDif);
						}

						else if (typeAppointment == 1) {
							long nextHour = timeEpooch + 3600;
							patientAgent.appointmentUrg(nextHour);
						}
					}
					
					else if (algorithmAppointment == 1) { 
						if (typeAppointment == 0) {
							patientAgent.appointment1(timetable.timetable.toString());
						}

						else if (typeAppointment == 1) {
							long nextHour = timeEpooch + 3600;
							patientAgent.appointmentUrg(nextHour);
						}
					}
					else
					System.out.println("Algoritmo de marcacao tem de ser assinalado com 0 ou 1 (5º parametro)");
				}
				
				//printDate();
				if (timeEpooch > 1420426800) {
					System.out.println("fim");
					done=true;
					System.exit(0);
				}
				
			}
			
			private void printDate() {
				String format = "dd/MM/yyyy HH:mm:ss";
				System.out.print(/*
								 * System.currentTimeMillis() + " " +
								 * TimeClock.timeEpooch 1000 + " " + new
								 * java.text.SimpleDateFormat(format)
								 * .format(new java.util.Date(System
								 * .currentTimeMillis())) + " " +
								 */new java.text.SimpleDateFormat(format)
						.format(new java.util.Date(timeEpooch * 1000)));

				if (timetable.timetable.get(timeEpooch).equals("fechado"))
					System.out.print("-fechado");

				else
					System.out.print("-aberto");

				System.out.println("-----------------------------------------------------------------------------------");
			}
		}, 0, 1, TimeUnit.SECONDS);

	}

	public void setAppointment(long longValue) {
		timetable.timetable.replace(longValue, "marcado");
	}

	public boolean appointment(long time) {
		return (timetable.timetable.get(time).equals("marcado")||timetable.timetable.get(time).equals("marcadoUrgencia"));
	}

	public boolean freeTime(long time) {
		return timetable.timetable.get(time).equals("livre");
	}

	public void setUrgentAppointment(long longValue) {
		timetable.timetable.replace(longValue, "marcadoUrgencia");
	}

	public long getTimeCommunication() {
		return timeCommunication;
	}

	public void setTimeCommunication(long timeCommunication) {
		this.timeCommunication = timeCommunication;
	}

	public boolean isUseReserved() {
		return useReserved;
	}

	public void setUseReserved(boolean useReserved) {
		this.useReserved = useReserved;
	}

	public long getTimeEpooch() {
		return timeEpooch;
	}
	
}
