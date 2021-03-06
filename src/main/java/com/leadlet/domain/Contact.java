package com.leadlet.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leadlet.domain.enumeration.Gender;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Contact.
 */
@Entity
@Table(name = "contact")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Contact extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "login")
    private String login;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ContactPhone> phones = new HashSet<>();

    @Column(name = "email")
    @Email
    private String email;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<Activity> activities;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<Deal> deals;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<Note> notes;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<Timeline> timelines;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Set<ContactPhone> getPhones() {
        return phones;
    }

    public void setPhones(Set<ContactPhone> phones) {
        this.phones = phones;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Activity> getActivities() {
        return activities;
    }

    public void setActivities(Set<Activity> activities) {
        this.activities = activities;
    }

    public Set<Deal> getDeals() {
        return deals;
    }

    public void setDeals(Set<Deal> deals) {
        this.deals = deals;
    }

    public Set<Note> getNotes() {
        return notes;
    }

    public void setNotes(Set<Note> notes) {
        this.notes = notes;
    }

    public Set<Timeline> getTimelines() {
        return timelines;
    }

    public void setTimelines(Set<Timeline> timelines) {
        this.timelines = timelines;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact)) return false;
        Contact contact = (Contact) o;
        return Objects.equals(id, contact.id);
    }

    @Override
    public int hashCode() {

        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Contact{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", address='" + address + '\'' +
            ", login='" + login + '\'' +
            ", phones=" + phones +
            ", email='" + email + '\'' +
            ", gender='" + gender + '\'' +
            '}';
    }
}
