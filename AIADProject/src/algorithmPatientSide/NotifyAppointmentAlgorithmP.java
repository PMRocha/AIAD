package algorithmPatientSide;

import resources.Patient;
import jade.lang.acl.ACLMessage;

public class NotifyAppointmentAlgorithmP {

	public static ACLMessage doNotification(ACLMessage reply, Patient patient,
			String timeString) {

		long time = Long.valueOf(timeString).longValue();
		if (patient.appointment(time)) {
			reply.setContent("ConfirmadoAproximacao-" + patient.getSpeciality()
					+ "-" + timeString);
			return reply;
		} else {
			if (patient.freeTime(time)) {
				reply.setContent("AproximacaoMarcada-"
						+ patient.getSpeciality() + "-" + timeString);
				return reply;
			} else {
				reply.setContent("RemarcacaoAproximacao-"
						+ patient.getSpeciality() + "-" + timeString + "-"
						+ patient.getTimetable().firstAvailable(time + 3600));
				return reply;
			}

		}
	}

}
