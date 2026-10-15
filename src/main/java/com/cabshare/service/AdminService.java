package com.cabshare.service;

import com.cabshare.dto.RideRow;
import com.cabshare.model.*;
import com.cabshare.repository.DriverRepository;
import com.cabshare.repository.RideRepository;
import com.cabshare.repository.RideStatusHistoryRepository;
import com.cabshare.util.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    private final DriverRepository driverRepository;
    private final RideRepository rideRepository;
    private final RideStatusHistoryRepository historyRepository;
    private final DispatchService dispatchService;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public AdminService(DriverRepository driverRepository, RideRepository rideRepository,
                        RideStatusHistoryRepository historyRepository, DispatchService dispatchService,
                        NotificationService notificationService, AuditService auditService) {
        this.driverRepository = driverRepository;
        this.rideRepository = rideRepository;
        this.historyRepository = historyRepository;
        this.dispatchService = dispatchService;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    /** Admin approves or suspends a driver. The action is written to the audit log. */
    @Transactional
    public void setDriverVerified(String adminName, Long driverId, boolean verified) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new BusinessException("Driver not found"));
        driver.setVerified(verified);
        if (!verified) {
            driver.setOnline(false);
        }
        driverRepository.save(driver);

        String driverName = driver.getUser().getName();
        auditService.log(adminName, verified ? "DRIVER_VERIFIED" : "DRIVER_SUSPENDED",
                "Driver " + driverName + " (" + driver.getVehicleNumber() + ")");
        notificationService.notify(driver.getUser(), verified
                ? "Your profile is verified. You can now go online."
                : "Your profile was suspended by the admin.");
    }

    /** Admin sends offers again for a ride that is still waiting for a driver. */
    @Transactional
    public int redispatch(String adminName, Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new BusinessException("Ride not found"));
        int sent = dispatchService.dispatch(ride);
        auditService.log(adminName, "RIDE_REDISPATCH", "Ride #" + rideId + ", new offers sent: " + sent);
        return sent;
    }

    /** All rides with their status history, newest first. */
    public List<RideRow> rideRows() {
        List<RideRow> rows = new ArrayList<>();
        for (Ride ride : rideRepository.findAllByOrderByIdDesc()) {
            RideRow row = new RideRow();
            row.setRide(ride);
            row.setHistory(historyRepository.findByRideOrderByIdAsc(ride));
            rows.add(row);
        }
        return rows;
    }
}
