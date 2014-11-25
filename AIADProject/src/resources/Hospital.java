package resources;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import agents.HospitalAgent;

public class Hospital {

	private TimeTable timetable;
	private long timeEpooch = 1420066800;

	public Hospital(int i, HospitalAgent hospitalAgent) {
		try {
			timetable = new TimeTable("TimeTable.xlsx", i);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		runTime(this, hospitalAgent);
	}

	public TimeTable getTimetable() {
		return timetable;
	}

	public void setTimetable(TimeTable timetable) {
		this.timetable = timetable;
	}

	public void clearAppointment(String speciality, long time) {
		timetable.cancelConsultation(time, speciality);
	}

	public void getNextPatient(String speciality) {
		System.out.println("teste:"
				+ timetable.nextPatient(timeEpooch, timeEpooch + 12 * 3600,
						speciality));

	}

	public long mergingAppointment(String speciality, String patientSchedule) {
		patientSchedule = patientSchedule.substring(1,
				patientSchedule.length() - 1);

		String[] blocks = patientSchedule.split(",");
		String[] blockParts;
		long time;

		for (int i = 0; i < blocks.length; i++) {
			blockParts = blocks[i].split("=");
			time = Long.valueOf(blockParts[0].substring(1));

			if (time > timeEpooch) {
				// se o paciente estiver livre
				if (blockParts[1].equals("livre")) {
					if (!timetable.slotTaken(speciality, time)) {
						return time;
					}
				}
			}
		}
		return 0;
	}

	// starts clock
	private void runTime(Hospital hospital, HospitalAgent hospitalAgent) {

		ScheduledExecutorService exec = Executors
				.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				String format = "dd/MM/yyyy HH:mm:ss";
				timeEpooch += 3600;
				System.out.print(/*
								 * System.currentTimeMillis() + " " +
								 * TimeClock.timeEpooch 1000 + " " + new
								 * java.text.SimpleDateFormat(format)
								 * .format(new java.util.Date(System
								 * .currentTimeMillis())) + " " +
								 */new java.text.SimpleDateFormat(format)
						.format(new java.util.Date(timeEpooch * 1000)));

				if (timetable.timetable.get(timeEpooch).equals("fechado"))
					System.out.println("-fechado");

				else
					System.out.println("-aberto");

				if (timeEpooch > 1420423200) {
					System.out.println("fim");
					timetable.exportTimeTable("result");
					exec.shutdown();
				}

				hospital.getTimetable().patientsToNotify(timeEpooch,
						hospitalAgent);
				hospital.getTimetable().patientsHavingAppointment(timeEpooch);

			}
		}, 0, 1, TimeUnit.SECONDS);

	}

}
