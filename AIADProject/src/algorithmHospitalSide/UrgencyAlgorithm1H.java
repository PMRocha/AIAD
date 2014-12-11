package algorithmHospitalSide;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import resources.Hospital;
import structure.TreeNode;

public class UrgencyAlgorithm1H {

	private static ArrayList<String> patients;
	private static ArrayList<String> patientsNewAppointment;
	private static Hashtable<String, Hashtable<Long, String>> patientSchedules;
	private static Hashtable<Long, String> hospitalSchedule;
	private static String urgentPatient;
	private static String speciality;
	private static long urgentTime;
	private static int scheduleCount;
	private static boolean startAlgorithm;

	public static void startAlgorithm(long urgentTime1, String urgentPatient1,
			Hospital hospital, String speciality1) {
		urgentTime = urgentTime1;
		urgentPatient = urgentPatient1;
		scheduleCount = 0;
		speciality = speciality1;
		startAlgorithm = false;

		patientSchedules = new Hashtable<String, Hashtable<Long, String>>(0);
		hospitalSchedule = new Hashtable<Long, String>(0);
		patientsNewAppointment = new ArrayList<String>(0);
		long finalSearchTime = urgentTime + 12 * 3600;
		patients = new ArrayList<String>(0);
		String patient;

		for (long i = urgentTime; i < finalSearchTime; i += 3600) {
			patient = null;
			if (hospital.getTimetable().slotTaken(speciality, i)) {
				patient = hospital.getTimetable().CheckConsultationsNow(i,
						speciality);
				if (patient != null) {
					patients.add(patient);
				}
			}
		}

		if (patients.size() == 0) {
			hospital.getTimetable().scheduleAppointment(urgentTime1,
					urgentPatient1, speciality);
		}
		System.out.println(patients.toString());
	}

	public static String getPatient(int i) {
		return patients.get(i);
	}

	public static int getPatientsSize() {
		return patients.size();
	}

	public static ACLMessage createMessage(ACLMessage askTimetable,
			Hospital hospital, int i) {

		askTimetable.addReceiver(new AID(patients.get(i), AID.ISLOCALNAME));
		askTimetable.setContent("Horario-" + urgentTime);
		askTimetable.setPerformative(ACLMessage.REQUEST);
		return askTimetable;
	}

	public static void saveSchedule(ACLMessage reply, ACLMessage msg) {

		scheduleCount++;

		// gets Schedule
		String parts[] = msg.getContent().split("-");

		String scheduleParts[] = parts[1].split(",");
		Hashtable<Long, String> schedule = new Hashtable<Long, String>(0);
		Arrays.sort(scheduleParts);

		// patient name
		String[] parts1 = reply.getReplyWith().split("@");
		patientSchedules.put(parts1[0], schedule);

		for (int i = 0; i < scheduleParts.length; i++) {
			parts = scheduleParts[i].split("=");
			schedule.put(Long.valueOf(parts[0]).longValue(), parts[1]);

			if (parts[1].equals("marcado")) {
				hospitalSchedule.put(Long.valueOf(parts[0]).longValue(),
						parts1[0]);
			}
		}

		// System.out.println(hospitalSchedule.toString());

		if (scheduleCount == patients.size())
			startAlgorithm = true;

	}

	public static boolean isStartAlgorithm() {
		return startAlgorithm;
	}

	public static void setStartAlgorithm(boolean startAlgorithm) {
		UrgencyAlgorithm1H.startAlgorithm = startAlgorithm;
	}

	public static void algorithm(Hospital hospital) {

		TreeNode<String> root = new TreeNode<String>("root");
		int value = 0;

		for (long urgentAppointment = urgentTime; urgentAppointment < urgentTime + 3 * 3600; urgentAppointment += 3600) {

			value = calcFunction(
					(int) ((urgentAppointment - urgentTime) / 3600) + 1, 0, 0);
			TreeNode<String> newNode = root.addChild(urgentPatient + "-"
					+ urgentAppointment, value);
			@SuppressWarnings("unchecked")
			// ignore this
			Hashtable<Long, String> schedule = (Hashtable<Long, String>) hospitalSchedule
					.clone();
			developBranch(hospital, newNode, urgentAppointment, urgentPatient,
					schedule, value);

		}

		getSolution(root);
		
	}

	private static void getSolution(TreeNode<String> root) {
		String[] parts;
		root = root.getLeastValue();
		// System.out.println("initially:"+hospitalSchedule.toString());
		while (root.getParent() != null) {
			parts = root.getData().split("-");// root=patient-newTime
			//System.out.println(hospitalSchedule.toString());
			if (urgentPatient.equals(root.getData().split("-")[0])) {
				//System.out.println("urgencia");
				patientsNewAppointment.add(parts[0] + "->MarcadoUrgencia-"
						+ parts[1]);
			} else {
				for (Map.Entry<Long, String> o : hospitalSchedule.entrySet()) {
					Map.Entry<Long, String> entry = o;
					if (entry.getValue().equals(parts[0])) {
						patientsNewAppointment.add(parts[0]
								+ "->RemarcadaConsulta-" + speciality + "-"
								+ entry.getKey() + "-" + parts[1]);
					}
				}
			}
			root = root.getParent();
		}
	}

	private static int calcFunction(int urgentTimePosted,
			int numberPatientsMoved, int numberOfBlocksMoved) {

		return urgentTimePosted * 5 + numberPatientsMoved * 2
				+ numberOfBlocksMoved;
	}

	private static void developBranch(Hospital hospital,
			TreeNode<String> parent, long urgentAppointment, String patient,
			Hashtable<Long, String> schedule, int valueUntilNow) {
		int value = 0;
		String patientCollision = "";

		if (schedule.get(urgentAppointment) != null) {

			patientCollision = schedule.get(urgentAppointment);

			// appoints in the schedule
			// System.out.println(urgentAppointment+":"+patient);
			schedule.put(urgentAppointment, patient);

			for (long time = urgentAppointment + 3600; time < urgentTime + 12 * 3600; time += 3600) {
				// System.out.println(patientCollision + "->" + time);

				value = valueUntilNow
						+ calcFunction(0, 1,
								(int) ((time - urgentAppointment) / 3600));
				// System.out.println(value);
				TreeNode<String> newNode = parent.addChild(patientCollision
						+ "-" + time, value);
				developBranch(hospital, newNode, time, patientCollision,
						schedule, valueUntilNow);
			}
		}

	}

	public static ArrayList<String> getPatientsNewAppointment() {
		return patientsNewAppointment;
	}

	public static void setPatientsNewAppointment(
			ArrayList<String> patientsNewAppointment) {
		UrgencyAlgorithm1H.patientsNewAppointment = patientsNewAppointment;
	}

	public static ACLMessage createMessageToNotifyAlteration(int i,
			ACLMessage msg, Hospital hospital) {
		System.out.println(patientsNewAppointment.get(i));
		String parts[] = patientsNewAppointment.get(i).split("->");
		String parts1[] = parts[1].split("-");
		long time=Long.valueOf(parts1[parts1.length-1]).longValue();

		msg.setContent(parts[1]);
		msg.addReceiver(new AID(parts[0], AID.ISLOCALNAME));
		
		if(parts[0].equals(urgentPatient))
		{
			if(hospital.getTimetable().slotTaken(speciality, time))
			{
				hospital.getTimetable().cancelConsultation(time, speciality);
			}
			hospital.getTimetable().scheduleAppointment(time, parts[0], speciality);
			System.out.println(hospital.getTimetable().CheckConsultationsNow(time, speciality));
		}
		msg.setPerformative(ACLMessage.INFORM);
		return msg;
	}
}
