package com.cabshare.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ride_groups")
public class RideGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 10, scale = 2)
    private BigDecimal estimatedTotalFare;

    @Enumerated(EnumType.STRING)
    private GroupStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "rideGroup", fetch = FetchType.EAGER)
    private List<RideMember> members = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getEstimatedTotalFare() { return estimatedTotalFare; }
    public void setEstimatedTotalFare(BigDecimal estimatedTotalFare) { this.estimatedTotalFare = estimatedTotalFare; }

    public GroupStatus getStatus() { return status; }
    public void setStatus(GroupStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<RideMember> getMembers() { return members; }
    public void setMembers(List<RideMember> members) { this.members = members; }

    /** Total number of passengers travelling in this group. */
    public int totalPassengers() {
        int total = 0;
        for (RideMember m : members) {
            total += m.getTripRequest().getPassengerCount();
        }
        return total;
    }

    /** Total number of luggage items in this group. */
    public int totalLuggage() {
        int total = 0;
        for (RideMember m : members) {
            total += m.getTripRequest().getLuggageCount();
        }
        return total;
    }

}
