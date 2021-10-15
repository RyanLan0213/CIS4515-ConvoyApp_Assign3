package edu.temple.convoy;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Vehicle implements Serializable {

    String username,firstname,lastname;
    double latitude, longitude;

    public Vehicle(String username, String firstname, String lastname,double latitude,double longitude){
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLng getLocation() {
        return new LatLng(this.latitude, this.longitude);
    }
    public String getUsername(){
        return username;
    }
    public String getFirstname(){
        return firstname;
    }
    public String getLastname(){
        return lastname;
    }
    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
    
    public void setUsername(String username){
        this.username = username;
    }
    public void setFirstname(String firstname){
        this.firstname = firstname;
    }
    public void setLastname(String lastname){
        this.lastname = lastname;
    }
    public void setLatitude(double latitude){
        this.latitude = latitude;
    }
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }


    public String toString() {
        return ("Username: " + username
                + "\t First Name: " + firstname
                + "\t Last Name: " + lastname
                + "\t Latitude: " + latitude
                + "\t Longitude: " + longitude);
    }

}
