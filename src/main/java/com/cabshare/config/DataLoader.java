package com.cabshare.config;

import com.cabshare.model.Driver;
import com.cabshare.model.Role;
import com.cabshare.model.User;
import com.cabshare.repository.DriverRepository;
import com.cabshare.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Creates demo accounts the first time the application starts (only if the database is empty). */
@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, DriverRepository driverRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }
        createUser("Admin", "admin@cabshare.com", "admin123", Role.ADMIN);
        createUser("Rahul Sharma", "rahul@cabshare.com", "passenger123", Role.PASSENGER);
        createUser("Priya Kumar", "priya@cabshare.com", "passenger123", Role.PASSENGER);

        User driverUser = createUser("Suresh Gowda", "suresh@cabshare.com", "driver123", Role.DRIVER);
        Driver driver = new Driver();
        driver.setUser(driverUser);
        driver.setVehicleNumber("KA01AB1234");
        driver.setVehicleModel("Toyota Innova");
        driver.setSeatCapacity(6);
        driver.setLuggageCapacity(4);
        driver.setVerified(true);
        driverRepository.save(driver);

        System.out.println("CabShare demo accounts created (see README.md for the logins).");
    }

    private User createUser(String name, String email, String password, Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        return userRepository.save(user);
    }
}
