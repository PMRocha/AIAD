package algorithmHospitalSide;

import java.util.ArrayList;

import resources.Hospital;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class ReappointmentAlgorithmH {

	static private ArrayList<String> patientsUsed;
	static private long startTime;
	static private long endTime;

	public static ACLMessage reappointment(String nextPatient,
			String speciality, long time, long nextPatientAppointmentTime,
			ACLMessage reply) {
		reply.addReceiver(new AID(nextPatient, AID.ISLOCALNAME));
		reply.setContent("AdiantamentoConsulta-" + speciality + "-"
				+ nextPatientAppointmentTime + "-" + time);
		return reply;
	}

	public static ACLMessage forwardAppointment(String nextPatient,
			String speciality, long time, long nextPatientAppointmentTime,
			ACLMessage forwardMessage) {
		forwardMessage.addReceiver(new AID(nextPatient, AID.ISLOCALNAME));
		forwardMessage.setPerformative(ACLMessage.INFORM);
		forwardMessage.setContent("AdiantamentoConsulta-" + speciality + "-"
				+ nextPatientAppointmentTime + "-" + time);
		return forwardMessage;
	}

	public static void setTime(long startTime1) {
		startTime = startTime1;
		endTime = startTime1 + 12 * 3600;
	}

	public static String getNextPatient(String speciality, Hospital hospital) {

		String patient = hospital
				.getNextPatient(speciality, startTime, endTime);

		if (!patient.equals("")) {
			String nextPatientparts[] = patient.split("-");
			long nextPatientAppointmentTime = Long.valueOf(nextPatientparts[1])
					.longValue();
			if (startTime < endTime) {
				startTime = nextPatientAppointmentTime + 3600;
				if (!patientsUsed.contains(nextPatientparts[0])) {
					patientsUsed.add(nextPatientparts[0]);
					return patient;
				} else {
					return getNextPatient(speciality, hospital);
				}
			} else {
				return "";
			}
		} else {
			return "";
		}

	}

	public static void resetPatients() {
		patientsUsed = new ArrayList<String>();
	}

	public static void addPatients(String patient) {
		patientsUsed.add(patient);

	}
}
