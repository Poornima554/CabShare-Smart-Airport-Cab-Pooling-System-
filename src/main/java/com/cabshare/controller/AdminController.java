package com.cabshare.controller;

import com.cabshare.model.Driver;
import com.cabshare.model.Role;
import com.cabshare.model.RideStatus;
import com.cabshare.repository.DriverRepository;
import com.cabshare.repository.RideRepository;
import com.cabshare.repository.UserRepository;
import com.cabshare.service.AdminService;
import com.cabshare.service.AuditService;
import com.cabshare.util.BusinessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final RideRepository rideRepository;
    private final AdminService adminService;
    private final AuditService auditService;

    public AdminController(UserRepository userRepository, DriverRepository driverRepository,
                           RideRepository rideRepository, AdminService adminService, AuditService auditService) {
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.rideRepository = rideRepository;
        this.adminService = adminService;
        this.auditService = auditService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long pendingDrivers = 0;
        for (Driver d : driverRepository.findAll()) {
            if (!d.isVerified()) {
                pendingDrivers++;
            }
        }
        model.addAttribute("passengers", userRepository.countByRole(Role.PASSENGER));
        model.addAttribute("drivers", userRepository.countByRole(Role.DRIVER));
        model.addAttribute("pendingDrivers", pendingDrivers);
        model.addAttribute("waitingRides", rideRepository.countByStatus(RideStatus.AWAITING_DRIVER));
        model.addAttribute("activeRides", rideRepository.countByStatusIn(List.of(
                RideStatus.DRIVER_ASSIGNED, RideStatus.DRIVER_ARRIVING, RideStatus.AT_PICKUP, RideStatus.IN_PROGRESS)));
        model.addAttribute("completedRides", rideRepository.countByStatus(RideStatus.COMPLETED));
        return "admin/dashboard";
    }

    @GetMapping("/drivers")
    public String drivers(Model model) {
        model.addAttribute("drivers", driverRepository.findAll());
        return "admin/drivers";
    }

    @PostMapping("/drivers/{id}/verify")
    public String verify(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        return changeVerification(id, true, principal, redirect);
    }

    @PostMapping("/drivers/{id}/suspend")
    public String suspend(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        return changeVerification(id, false, principal, redirect);
    }

    private String changeVerification(Long id, boolean verified, Principal principal, RedirectAttributes redirect) {
        try {
            adminService.setDriverVerified(principal.getName(), id, verified);
            redirect.addFlashAttribute("message", verified ? "Driver verified." : "Driver suspended.");
        } catch (BusinessException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/drivers";
    }

    @GetMapping("/rides")
    public String rides(Model model) {
        model.addAttribute("rows", adminService.rideRows());
        return "admin/rides";
    }

    @PostMapping("/rides/{id}/dispatch")
    public String redispatch(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        try {
            int sent = adminService.redispatch(principal.getName(), id);
            redirect.addFlashAttribute("message", sent + " new offer(s) sent to drivers.");
        } catch (BusinessException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/rides";
    }

    @GetMapping("/audit")
    public String audit(Model model) {
        model.addAttribute("logs", auditService.latest());
        return "admin/audit";
    }
}
