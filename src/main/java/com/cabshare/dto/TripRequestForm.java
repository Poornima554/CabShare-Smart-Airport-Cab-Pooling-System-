package com.cabshare.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class TripRequestForm {

    @NotNull(message = "Enter number of passengers")
    @Min(value = 1, message = "At least 1 passenger")
    @Max(value = 4, message = "At most 4 passengers")
    private Integer passengerCount = 1;

    @NotNull(message = "Enter number of bags")
    @Min(value = 0, message = "Cannot be negative")
    @Max(value = 4, message = "At most 4 bags")
    private Integer luggageCount = 0;

    @NotNull(message = "Choose a departure time")
    @Future(message = "Departure time must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime departureTime;

    @NotNull(message = "Enter your maximum wait")
    @Min(value = 5, message = "At least 5 minutes")
    @Max(value = 120, message = "At most 120 minutes")
    private Integer maxWaitMinutes = 30;

    public Integer getPassengerCount() { return passengerCount; }
    public void setPassengerCount(Integer passengerCount) { this.passengerCount = passengerCount; }

    public Integer getLuggageCount() { return luggageCount; }
    public void setLuggageCount(Integer luggageCount) { this.luggageCount = luggageCount; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public Integer getMaxWaitMinutes() { return maxWaitMinutes; }
    public void setMaxWaitMinutes(Integer maxWaitMinutes) { this.maxWaitMinutes = maxWaitMinutes; }

}
