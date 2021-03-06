package com.leadlet.service.dto;


import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the Activity entity.
 */
public class ActivityDTO implements Serializable {

    private Long id;

    private String title;

    private String memo;

    private Instant start;

    private Instant end;

    private ActivityTypeDTO type;

    private DetailedDealDTO deal;

    private ContactDTO contact;

    private UserDTO agent;

    private LocationDTO location;

    private boolean done;

    private Instant closedDate;

    public Long getId() {
        return id;
    }

    public ActivityDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ActivityDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getMemo() {
        return memo;
    }

    public ActivityDTO setMemo(String memo) {
        this.memo = memo;
        return this;
    }

    public Instant getStart() {
        return start;
    }

    public ActivityDTO setStart(Instant start) {
        this.start = start;
        return this;
    }

    public Instant getEnd() {
        return end;
    }

    public ActivityDTO setEnd(Instant end) {
        this.end = end;
        return this;
    }

    public DetailedDealDTO getDeal() {
        return deal;
    }

    public ActivityDTO setDeal(DetailedDealDTO deal) {
        this.deal = deal;
        return this;
    }

    public ContactDTO getContact() {
        return contact;
    }

    public ActivityDTO setContact(ContactDTO contact) {
        this.contact = contact;
        return this;
    }

    public UserDTO getAgent() {
        return agent;
    }

    public ActivityDTO setAgent(UserDTO agent) {
        this.agent = agent;
        return this;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public ActivityDTO setLocation(LocationDTO location) {
        this.location = location;
        return this;
    }

    public boolean isDone() {
        return done;
    }

    public ActivityDTO setDone(boolean done) {
        this.done = done;
        return this;
    }

    public Instant getClosedDate() {
        return closedDate;
    }

    public ActivityDTO setClosedDate(Instant closedDate) {
        this.closedDate = closedDate;
        return this;
    }

    public ActivityTypeDTO getType() {
        return type;
    }

    public void setType(ActivityTypeDTO type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActivityDTO)) return false;
        ActivityDTO that = (ActivityDTO) o;
        return done == that.done &&
            Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ActivityDTO{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", memo='" + memo + '\'' +
            ", start=" + start +
            ", end=" + end +
            ", type=" + type +
            ", location=" + location +
            ", done=" + done +
            ", closedDate=" + closedDate +
            '}';
    }
}
