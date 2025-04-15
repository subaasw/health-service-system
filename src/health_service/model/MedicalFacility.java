package health_service.model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MedicalFacility implements Serializable {
    private static final AtomicInteger sequence = new AtomicInteger(1);

    private final int id;
    private String name;

    public MedicalFacility(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Facility name cannot be empty");
        }
        this.id = sequence.getAndIncrement();
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Facility name cannot be empty");
        }
        this.name = name;
    }

    @Override
    public String toString() {
        return "Facility Details [ID: " + id + ", Name: " + name + "]";
    }

    public abstract boolean visit(Patient patient);
}