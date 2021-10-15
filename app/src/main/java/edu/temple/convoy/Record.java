package edu.temple.convoy;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Record implements Serializable {
    String username,URL;
    LocalDateTime localDate;


    public Record(String username, String URL, LocalDateTime localDate){
        this.username = username;
        this.URL = URL;
        this.localDate = localDate;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getLocalDate() {
        return localDate;
    }

    public String getURL() {
        return URL;
    }

    public void setLocalDate(LocalDateTime localDate) {
        this.localDate = localDate;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Record{" +
                "username='" + username + '\'' +
                ", URL='" + URL + '\'' +
                ", localDate=" + localDate +
                '}';
    }
}