package algorithmHospitalSide;

import jade.lang.acl.ACLMessage;
import resources.Hospital;
import resources.TimeTable;

public class AppointmentAlgorithm0H {

	public static ACLMessage makeAppointment(String speciality, String timestamp,
			ACLMessage reply,Hospital hospital) {

		long ts = Long.valueOf(timestamp).longValue();
		TimeTable timetable = hospital.getTimetable();
		if (timetable.slotTaken(speciality, ts)) {
			while (timetable.slotTaken(speciality, ts)) {
				ts = ts + 3600;
				if(ts>1420423200)
					break;
			}
			reply.setContent("Remarcacao-" + ts);
			reply.setPerformative(ACLMessage.DISCONFIRM);
			System.out.println("nao marcado");

		} else {

			String[] parts = reply.getReplyWith().split("@");
			timetable.scheduleAppointment(ts, parts[0], speciality);
			reply.setContent("Marcado-" + ts);
			reply.setPerformative(ACLMessage.CONFIRM);
			System.out.println("Marcado-" + timetable.timetable.get(ts));

		}
		return reply;

	}
}
