package health_service.model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Procedure implements Serializable {
    private static final AtomicInteger sequence = new AtomicInteger(1);

    private final int id;
    private String name;
    private String description;
    private boolean isElective;
    private double cost;

    public Procedure(String name, String description, boolean isElective, double cost) {
        this.id = sequence.getAndIncrement();
        setName(name);
        setDescription(description);
        this.isElective = isElective;
        setCost(cost);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        this.description = description;
    }

    public boolean isElective() {
        return isElective;
    }

    public void setElective(boolean elective) {
        isElective = elective;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative");
        }
        this.cost = cost;
    }

    public double calculateCost(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }

        if (!(patient.getCurrentFacility() instanceof Hospital)) {
            throw new IllegalStateException("Procedure can only be performed on patients in a hospital");
        }

        if (!patient.isPrivate()) {
            return isElective ? cost : 0.0;
        } else {
            return isElective ? 2000.0 : 1000.0;
        }
    }

    @Override
    public String toString() {
        return String.format("Procedure ID: %d, Name: %s, Elective: %b, Cost: %.2f",
                id, name, isElective, cost);
    }
}