package algorithmPatientSide;

import resources.Patient;
import jade.lang.acl.ACLMessage;

public class ReappointmentAlgorithmP {

	public static ACLMessage reappointment(ACLMessage reply,String[] parts,Patient patient) {
		if (patient
				.freeTime(Long.valueOf(parts[3]).longValue())) {
			reply.setContent("AdiantamentoAceite-" + parts[1]
					+ "-" + parts[2] + "-" + parts[3]);
			patient.setAppointment(Long.valueOf(parts[3])
					.longValue());
			patient.getTimetableTimetable()
					.replace(
							Long.valueOf(parts[2]).longValue(),
							"livre");
		} else {
			reply.setContent("AdiantamentoNegado-" + parts[1]
					+ "-" + parts[3]);
		}
		return reply;
	}

}
