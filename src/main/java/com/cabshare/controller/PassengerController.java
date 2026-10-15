package com.cabshare.controller;

import com.cabshare.dto.TripRequestForm;
import com.cabshare.model.TripRequest;
import com.cabshare.model.TripStatus;
import com.cabshare.model.User;
import com.cabshare.service.*;
import com.cabshare.util.BusinessException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/passenger")
public class PassengerController {

    private final UserService userService;
    private final PassengerService passengerService;
    private final MatchingService matchingService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    public PassengerController(UserService userService, PassengerService passengerService,
                               MatchingService matchingService, PaymentService paymentService,
                               NotificationService notificationService) {
        this.userService = userService;
        this.passengerService = passengerService;
        this.matchingService = matchingService;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }

    @GetMapping("/home")
    public String home(Principal principal, Model model) {
        User user = userService.getByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("notifications", notificationService.latestFor(user));
        return "passenger/home";
    }

    @GetMapping("/find-ride")
    public String findRidePage(Model model) {
        model.addAttribute("form", new TripRequestForm());
        return "passenger/find-ride";
    }

    @PostMapping("/find-ride")
    public String findRide(@Valid @ModelAttribute("form") TripRequestForm form, BindingResult result,
                           Principal principal, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "passenger/find-ride";
        }
        try {
            User user = userService.getByEmail(principal.getName());
            TripRequest request = passengerService.createRequest(user, form);
            if (request.getStatus() == TripStatus.MATCHED) {
                redirect.addFlashAttribute("message", "Good news! We found a passenger to share with. Please review the proposal below.");
            } else {
                redirect.addFlashAttribute("message", "Your request is saved. We will propose a shared ride as soon as a compatible passenger appears, or you can book solo from the card below.");
            }
            return "redirect:/passenger/my-rides";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "passenger/find-ride";
        }
    }

    @GetMapping("/my-rides")
    public String myRides(Principal principal, Model model) {
        User user = userService.getByEmail(principal.getName());
        model.addAttribute("items", passengerService.myRides(user));
        return "passenger/my-rides";
    }

    @PostMapping("/members/{id}/accept")
    public String accept(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        try {
            matchingService.respondToProposal(userService.getByEmail(principal.getName()), id, true);
            redirect.addFlashAttribute("message", "You accepted the shared ride.");
        } catch (BusinessException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/passenger/my-rides";
    }

    @PostMapping("/members/{id}/decline")
    public String decline(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        try {
            matchingService.respondToProposal(userService.getByEmail(principal.getName()), id, false);
            redirect.addFlashAttribute("message", "You declined the shared ride.");
        } catch (BusinessException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/passenger/my-rides";
    }

    @PostMapping("/requests/{id}/solo")
    public String bookSolo(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        try {
            matchingService.bookSolo(userService.getByEmail(principal.getName()), id);
            redirect.addFlashAttribute("message", "Solo ride booked. We are now looking for a driver.");
        } catch (BusinessException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/passenger/my-rides";
    }

    @PostMapping("/requests/{id}/cancel")
    public String cancel(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        try {
            passengerService.cancelRequest(userService.getByEmail(principal.getName()), id);
            redirect.addFlashAttribute("message", "Your request was cancelled.");
        } catch (BusinessException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/passenger/my-rides";
    }

    @PostMapping("/payments/{id}/pay")
    public String pay(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        try {
            paymentService.pay(userService.getByEmail(principal.getName()), id);
            redirect.addFlashAttribute("message", "Payment successful (simulated).");
        } catch (BusinessException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/passenger/my-rides";
    }
}
