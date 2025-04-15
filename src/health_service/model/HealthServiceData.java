package health_service.model;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class HealthServiceData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String serviceName;
    private List<Patient> patients;
    private List<MedicalFacility> facilities;

    public HealthServiceData(String serviceName, List<Patient> patients, List<MedicalFacility> facilities) {
        this.serviceName = serviceName;
        this.patients = new ArrayList<>(patients);
        this.facilities = new ArrayList<>(facilities);
    }

    public String getServiceName() {
        return serviceName;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public List<MedicalFacility> getFacilities() {
        return facilities;
    }
}