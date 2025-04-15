package health_service.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HealthService implements Serializable {
    private String name;
    private List<Patient> patients;
    private List<MedicalFacility> facilities;
    private List<String[]> recentActivities;

    public HealthService(String name) {
        this.name = name;
        this.patients = new ArrayList<>();
        this.facilities = new ArrayList<>();
        this.recentActivities = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<String[]> getRecentActivities() {
        return recentActivities;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public List<MedicalFacility> getFacilities() {
        return facilities;
    }

    public void logActivity(String type, String description) {
        String[] activity = new String[] {
                LocalDateTime.now().toString(),
                type,
                description
        };
        recentActivities.add(0, activity);

        if (recentActivities.size() > 100) {
            recentActivities.remove(recentActivities.size() - 1);
        }
    }

    public void addPatient(Patient patient) {
        patients.add(patient);
        logActivity("Patient", "Added new patient: " + patient.getName());

    }

    public void addFacility(MedicalFacility facility) {
        facilities.add(facility);
        logActivity("Facility", "Added new " +
                (facility instanceof Hospital ? "hospital: " : "clinic: ") +
                facility.getName());

    }

    public void removePatient(Patient patient) {
        patients.remove(patient);
        logActivity("Patient", "Removed patient: " + patient.getName());
    }

    public void removeFacility(MedicalFacility facility) {
        facilities.remove(facility);
        logActivity("Facility", "Removed " +
                (facility instanceof Hospital ? "hospital: " : "clinic: ") +
                facility.getName());
    }

    public void clearAll() {
        patients.clear();
        facilities.clear();
    }

    @Override
    public String toString() {
        return "Health Service: " + name;
    }
}