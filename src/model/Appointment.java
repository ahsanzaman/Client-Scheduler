package model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Appointment model class for storing data retrieved from appointments table in db.
 * This also saves a string version of the start, end datetime, a separate StartDate/EndDate, StartTime and EndTime for ease.
 */

public class Appointment {
    private long appointmentId;
    private String title;
    private String description;
    private String location;
    private String type;
    private String startStr;
    private ZonedDateTime start;
    private LocalDate startDate;
    private LocalTime startTime;
    private String endStr;
    private ZonedDateTime end;
    private LocalDate endDate;
    private LocalTime endTime;
    private long customerId;
    private String customerName;
    private long userId;
    private String userName;
    private long contactId;

    public String getStartStr() {
        return startStr;
    }

    public void setStartStr(String startStr) {
        this.startStr = startStr;
    }

    public String getEndStr() {
        return endStr;
    }

    public void setEndStr(String endStr) {
        this.endStr = endStr;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    private String contactName;

    public Appointment(long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Appointment(long appointmentId, String title, String description, String location, String type, ZonedDateTime start, ZonedDateTime end, long customerId, long userId, long contactId) {
        this.appointmentId = appointmentId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.startStr = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"));
        this.startDate = start.toLocalDate();
        this.startTime = start.toLocalTime();
        this.end = end;
        this.endStr = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"));
        this.endDate = end.toLocalDate();
        this.endTime = end.toLocalTime();
        this.customerId = customerId;
        this.userId = userId;
        this.contactId = contactId;
    }

    public long getAppointmentId() {
        return appointmentId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
    public LocalTime getStartTime() { return startTime; }

    public LocalDate getEndDate() {
        return endDate;
    }
    public LocalTime getEndTime() { return endTime; }

    public long getCustomerId() {
        return customerId;
    }

    public long getUserId() {
        return userId;
    }

    public long getContactId() {
        return contactId;
    }

    public void setAppointmentId(long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public ZonedDateTime getStart() { return start; }

    public ZonedDateTime getEnd() { return end; }

    public void setStart(ZonedDateTime start) { this.start = start; }

    public void setEnd(ZonedDateTime end) { this.end = end; }
}

