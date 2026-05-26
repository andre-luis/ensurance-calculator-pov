package com.insurance.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Schema(description = "Customer entity representing an insurance customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the customer", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @Schema(description = "Full name of the customer", example = "John Doe", required = true)
    private String name;
    
    @Schema(description = "Customer's residential address", example = "123 Main St, Springfield, IL 62701", required = true)
    private String address;
    
    @Schema(description = "Customer's contact information (phone/email)", example = "john.doe@email.com or +1-555-123-4567", required = true)
    private String contactInformation;
    
    @Schema(description = "Customer's driver's license number", example = "DL123456789", required = true)
    private String driversLicenseNumber;

    // Lombok will generate getters, setters, toString, equals, and hashCode methods

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }

    public String getDriversLicenseNumber() {
        return driversLicenseNumber;
    }

    public void setDriversLicenseNumber(String driversLicenseNumber) {
        this.driversLicenseNumber = driversLicenseNumber;
    }
}
