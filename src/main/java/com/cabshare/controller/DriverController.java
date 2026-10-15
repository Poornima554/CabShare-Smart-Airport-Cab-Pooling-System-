package com.cabshare.controller;

import com.cabshare.model.Driver;
import com.cabshare.model.Ride;
import com.cabshare.model.RideStatus;
import com.cabshare.model.User;
import com.cabshare.service.*;
import com.cabshare.util.BusinessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/driver")
public class DriverController {

    private final UserService userService;
    private final DriverService driverService;
    private final DispatchService dispatchService;
    private final RideService rideService;
    private final NotificationService notificationService;

    public DriverController(UserService userService, DriverService driverService, DispatchService dispatchService,
                            RideService rideService, NotificationService notificationService) {
        this.userService = userService;
        this.driverService = driverService;
        this.dispatchService = dispatchService;
        this.rideService = rideService;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        User user = userService.getByEmail(principal.getName());
        Driver driver = driverService.getDriver(user);
        model.addAttribute("user", user);
        model.addAttribute("driver", driver);
        model.addAttribute("offers", driverService.activeOffers(driver));
        model.addAttribute("ride", driverService.currentRide(driver));
        model.addAttribute("notifications", notificationService.latestFor(user));
        return "driver/dashboard";
    }

    @PostMapping("/availability")
    public String availability(@RequestParam("online") boolean online, Principal principal, RedirectAttributes redirect) {
        try {
            driverService.setOnline(userService.getByEmail(principal.getName()), online);
            redirect.addFlashAttribute("message", online ? "You are online." : "You are offline.");
        } catch (BusinessException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/driver/dashboard";
    }

    @PostMapping("/offers/{id}/accept")
    public String acceptOffer(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        try {
            dispatchService.acceptOffer(userService.getByEmail(principal.getName()), id);
            redirect.addFlashAttribute("message", "Ride accepted. The passengers have been notified.");
        } catch (BusinessException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/driver/dashboard";
    }

    @PostMapping("/offers/{id}/reject")
    public String rejectOffer(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        try {
            dispatchService.rejectOffer(userService.getByEmail(principal.getName()), id);
            redirect.addFlashAttribute("message", "Offer rejected.");
        } catch (BusinessException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/driver/dashboard";
    }

    @PostMapping("/rides/{id}/status")
    public String updateRide(@PathVariable("id") Long id, @RequestParam("target") RideStatus target,
                             Principal principal, RedirectAttributes redirect) {
        try {
            rideService.advance(userService.getByEmail(principal.getName()), id, target);
            redirect.addFlashAttribute("message", "Ride status updated to " + target + ".");
        } catch (BusinessException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/driver/dashboard";
    }

    @GetMapping("/earnings")
    public String earnings(Principal principal, Model model) {
        Driver driver = driverService.getDriver(userService.getByEmail(principal.getName()));
        List<Ride> rides = driverService.completedRides(driver);
        model.addAttribute("rides", rides);
        model.addAttribute("total", driverService.totalEarnings(rides));
        return "driver/earnings";
    }
}
