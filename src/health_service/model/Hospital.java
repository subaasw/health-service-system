package health_service.model;

import java.util.ArrayList;
import java.util.List;

public class Hospital extends MedicalFacility {
    private double probAdmit;
    private List<Procedure> procedures;

    public Hospital(String name, double probAdmit) {
        super(name);
        this.probAdmit = probAdmit;
        this.procedures = new ArrayList<>();
    }

    public double getProbAdmit() {
        return probAdmit;
    }

    public void setProbAdmit(double probAdmit) {
        this.probAdmit = probAdmit;
    }

    public List<Procedure> getProcedures() {
        return procedures;
    }

    public void addProcedure(Procedure procedure) {
        procedures.add(procedure);
    }

    @Override
    public boolean visit(Patient patient) {
        double random = Math.random();
        if (random < probAdmit) {
            patient.setCurrentFacility(this);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + ", Admission Probability: " + probAdmit;
    }
}