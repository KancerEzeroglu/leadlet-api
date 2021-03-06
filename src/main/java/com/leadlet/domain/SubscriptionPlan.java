package com.leadlet.domain;

import com.leadlet.domain.enumeration.PlanName;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A SubscriptionPlan.
 */
@Entity
@Table(name = "subscription_plan")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SubscriptionPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_name")
    private PlanName planName;

    @Column(name = "allowed_features")
    private String allowedFeatures;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanName getPlanName() {
        return planName;
    }

    public SubscriptionPlan planName(PlanName planName) {
        this.planName = planName;
        return this;
    }

    public void setPlanName(PlanName planName) {
        this.planName = planName;
    }

    public String getAllowedFeatures() {
        return allowedFeatures;
    }

    public SubscriptionPlan allowedFeatures(String allowedFeatures) {
        this.allowedFeatures = allowedFeatures;
        return this;
    }

    public void setAllowedFeatures(String allowedFeatures) {
        this.allowedFeatures = allowedFeatures;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubscriptionPlan subscriptionPlan = (SubscriptionPlan) o;
        if (subscriptionPlan.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), subscriptionPlan.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SubscriptionPlan{" +
            "id=" + getId() +
            ", planName='" + getPlanName() + "'" +
            ", allowedFeatures='" + getAllowedFeatures() + "'" +
            "}";
    }
}
