package com.example.dentalmobile.model;

public class booking_model {
    String gender, 
            address, 
            changed, 
            contact_num, 
            email, 
            fullName, 
            imageUrl, 
            sched;


    public booking_model() {
    }

    public booking_model(String gender, String address, String changed, String contact_num, String email, String fullName, String imageUrl, String sched ) {
        this.gender = gender;
        this.address = address;
        this.changed = changed;
        this.contact_num = contact_num;
        this.email = email;
        this.fullName = fullName;
        this.imageUrl = imageUrl;
        this.sched = sched;

    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getChanged() {
        return changed;
    }

    public void setChanged(String changed) {
        this.changed = changed;
    }

    public String getContact_num() {
        return contact_num;
    }

    public void setContact_num(String contact_num) {
        this.contact_num = contact_num;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSched() {
        return sched;
    }

    public void setSched(String sched) {
        this.sched = sched;
    }



}