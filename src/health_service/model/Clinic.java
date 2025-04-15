package health_service.model;

import java.util.HashSet;
import java.util.Set;

public class Clinic extends MedicalFacility {
    private double fee;
    private double gapPercent;
    private Set<Patient> registeredPatients; // To track registered patients

    public Clinic(String name, double fee, double gapPercent) {
        super(name);
        if (fee < 0 || gapPercent < 0 || gapPercent > 100) {
            throw new IllegalArgumentException("Fee must be non-negative and gap percentage between 0 and 100.");
        }
        this.fee = fee;
        this.gapPercent = gapPercent;
        this.registeredPatients = new HashSet<>(); // Initialize the set
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        if (fee < 0) {
            throw new IllegalArgumentException("Fee must be non-negative.");
        }
        this.fee = fee;
    }

    public double getGapPercent() {
        return gapPercent;
    }

    public void setGapPercent(double gapPercent) {
        if (gapPercent < 0 || gapPercent > 100) {
            throw new IllegalArgumentException("Gap percentage must be between 0 and 100.");
        }
        this.gapPercent = gapPercent;
    }

    // Method to get consultation fee (renamed for clarity)
    public double getConsultationFee() {
        return fee;
    }

    public double calculateConsultationFee(Patient patient) {
        return patient.isPrivate() ? fee : (fee * gapPercent / 100);
    }

    @Override
    public boolean visit(Patient patient) {
        if (patient.getCurrentFacility() != this) {
            // Register the patient
            patient.setCurrentFacility(this);
            registeredPatients.add(patient);
            return false;
        } else {
            // Perform consultation
            double cost = calculateConsultationFee(patient);
            patient.setBalance(patient.getBalance() + cost);
            System.out.println(patient.getName() + " received a consultation at " + getName() + " for $" + cost);
            return true;
        }
    }

    // Get current patient count
    public int getPatientCount() {
        return registeredPatients.size();
    }

    // Remove patient from registered patients when they change facilities
    public void removePatient(Patient patient) {
        registeredPatients.remove(patient);
    }

    @Override
    public String toString() {
        return String.format("Clinic: %s (Fee: $%.2f, Gap: %.1f%%, Patients: %d)",
                getName(), fee, gapPercent, getPatientCount());
    }
}