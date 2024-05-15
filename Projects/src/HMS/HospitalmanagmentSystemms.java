package HMS;

import javax.print.Doc;
import java.sql.*;
import java.util.Scanner;

public class HospitalmanagmentSystemms {
    private static final  String url ="jdbc:mysql://localhost:3306/hospital";
    private static final String username="root";
    private static final String password ="aman123";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            Patient patient = new Patient(connection,scanner);
            Doctor doctor = new Doctor(connection);
            while (true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add patient");
                System.out.println("2. view patients ");
                System.out.println("3. view Doctors");
                System.out.println("4.Book Appointment");
                System.out.println("5.Exit");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();
                switch (choice){
                    case 1 :
                        // Add patient
                        patient.addPatient();
                        System.out.println();
                    case 2 :
                        patient.viewPatients();
                        System.out.println();

                    case 3:
                        doctor.viewDoctors();
                        System.out.println();

                    case 4:
                        // Book appointment
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();

                    case 5:
                        return;

                    default:
                        System.out.println("Enter valid choice!!!");

                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

    }
    public static void bookAppointment(Patient patient,Doctor doctor,Connection connection,Scanner scanner){
        System.out.println("Enter Patient id: ");
        int patientId = scanner.nextInt();
        System.out.println("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.println("Enter appointment date(yyyy-MM-DD)");
        String appointmentdate = scanner.next();
        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)){
            if(checkDoctorAvailability(doctorId,appointmentdate,connection)){
                String appointmentQuery = "INSERT INTO appointments(patient_id,doctor_id,appointment_date) VALUES(?,?,?)";
                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1,patientId);
                    preparedStatement.setInt(2,doctorId);
                    preparedStatement.setString(3,appointmentdate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected>0){
                        System.out.println("Appointment booked!");
                    }else {
                        System.out.println("failed to book appointment");
                    }

                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else {
                System.out.println("Doctor not available on this date!!");
            }
        }else {
            System.out.println("Either doctor or patient doesn't exist!!!");
        }

    }

    private static boolean checkDoctorAvailability(int doctorId, String appointmentdate,Connection connection) {
        String query = "SELECT COUNT(*) FROM appointment WHERE doctor_id = ? AND appointment_date=?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appointmentdate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count==0){
                    return true;
                }else {
                    return false;
                }
            }


        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;

    }

}
