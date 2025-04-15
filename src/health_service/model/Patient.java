package health_service.model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Patient implements Serializable {
    private static final AtomicInteger sequence = new AtomicInteger(1);

    private final int id;
    private String name;
    private boolean isPrivate;
    private double balance = 0.0;
    private MedicalFacility currentFacility;

    public Patient(String name, boolean isPrivate) {
        this.id = sequence.getAndIncrement();
        setName(name);
        setIsPrivate(isPrivate);
        this.currentFacility = null;
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

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.balance = balance;
    }

    public MedicalFacility getCurrentFacility() {
        return currentFacility;
    }

    public void setCurrentFacility(MedicalFacility currentFacility) {
        this.currentFacility = currentFacility;
    }

    public void deductBalance(double amount) {
        if (balance < amount) {
            throw new IllegalArgumentException("Insufficient balance to cover the fee");
        }
        this.balance -= amount;
    }

    @Override
    public String toString() {
        return String.format("Patient ID: %d, Name: %s, Private: %b, Balance: %.2f",
                id, name, isPrivate, balance);
    }
}