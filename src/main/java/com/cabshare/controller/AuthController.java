package com.cabshare.controller;

import com.cabshare.dto.RegisterForm;
import com.cabshare.service.UserService;
import com.cabshare.util.BusinessException;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form, BindingResult result,
                           RedirectAttributes redirect) {
        if ("DRIVER".equals(form.getRole())) {
            if (form.getVehicleNumber() == null || form.getVehicleNumber().isBlank()) {
                result.rejectValue("vehicleNumber", "required", "Vehicle number is required");
            }
            if (form.getVehicleModel() == null || form.getVehicleModel().isBlank()) {
                result.rejectValue("vehicleModel", "required", "Vehicle model is required");
            }
            if (form.getSeatCapacity() == null || form.getSeatCapacity() < 2 || form.getSeatCapacity() > 6) {
                result.rejectValue("seatCapacity", "range", "Seats must be between 2 and 6");
            }
            if (form.getLuggageCapacity() == null || form.getLuggageCapacity() < 0 || form.getLuggageCapacity() > 6) {
                result.rejectValue("luggageCapacity", "range", "Luggage must be between 0 and 6");
            }
        }
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.register(form);
        } catch (BusinessException e) {
            result.rejectValue("email", "duplicate", e.getMessage());
            return "register";
        }
        redirect.addFlashAttribute("message", "Account created. Please log in.");
        return "redirect:/login";
    }

    /** After login, send each role to its own dashboard. */
    @GetMapping("/home")
    public String home(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            }
            if (authority.getAuthority().equals("ROLE_DRIVER")) {
                return "redirect:/driver/dashboard";
            }
        }
        return "redirect:/passenger/home";
    }
}
