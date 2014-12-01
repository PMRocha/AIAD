package algorithmHospitalSide;

import java.util.Arrays;

import jade.lang.acl.ACLMessage;
import resources.Hospital;
import resources.TimeTable;

public class AppointmentAlgorithm1H {

	public static void makeMergingAppointment(String speciality,
			String timestamp, ACLMessage reply,Hospital hospital) {
		TimeTable timetable = hospital.getTimetable();
		long time = mergingAppointment(speciality, timestamp,hospital.getTimeEpooch(),hospital.getTimetable());
		String[] parts = reply.getReplyWith().split("@");
		timetable.scheduleAppointment(time, parts[0], speciality);
		reply.setContent("Marcado-" + time);
		System.out.println("Marcado-" + timetable.timetable.get(time));
	}
	
	//algoritmo 1 de marcação vê um sitio onde ambos os agentes tenham o horário livre
		public static long mergingAppointment(String speciality, String patientSchedule,long timeEpooch,TimeTable timetable) {
			patientSchedule = patientSchedule.substring(1,patientSchedule.length() - 1);

			String[] blocks = patientSchedule.split(",");
			String[] blockParts;
			long time;
			Arrays.sort(blocks);

			for (int i = 0; i < blocks.length; i++) {
				blockParts = blocks[i].split("=");	
				time = Long.valueOf(blockParts[0].replaceAll("\\s+",""));
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
}
