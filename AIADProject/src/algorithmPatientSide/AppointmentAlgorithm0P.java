package algorithmPatientSide;

import resources.Patient;
import jade.lang.acl.ACLMessage;

public class AppointmentAlgorithm0P {

	public static ACLMessage reappointment(long time, ACLMessage msg,
			ACLMessage reply, Patient patient) {
		return appointment(msg, reply, time, patient);

	}

	// does initial appointment when the program is started (the appointment
	// can be made with a 12 hours difference)
	public static ACLMessage appointment(ACLMessage msg, ACLMessage reply,
			long appTime, Patient patient) {
		long time;
		
		if(!patient.isUseReserved())
		time=patient.getTimetable().firstAvailable(appTime);
		else
		time=patient.getTimetable().firstAvailableReschedulable(appTime);
		
		if (time != 0) {

			if (time > patient
					.getTimeCommunication() * 6 * 3600
					&& !patient.isUseReserved()) {
				patient.setUseReserved(true);
				time= patient
						.getTimeCommunication();
			}

			reply.setPerformative(ACLMessage.REQUEST);
			reply.setContent("Marcacao0-" + patient.getSpeciality() + "-"
					+ patient.getTimetable().firstAvailable(appTime));

			return reply;
		} else {
			System.out
					.println("impossivel marcar consulta(não existe nenhum horário disponivel)");
			return null;
		}
	}

}
