# CabShare - Smart Airport Cab Pooling System

Passengers travelling from **Bengaluru Airport to Majestic** share a cab and split the fare.
The system matches compatible passengers, sends the confirmed group to an eligible driver,
and tracks the ride until completion.

**Stack:** Java 17, Spring Boot 3, Spring MVC + Thymeleaf, Spring Data JPA (Hibernate), Spring Security (session login), MySQL, Maven, JUnit 5.
No Node.js, no JWT, no Lombok, no Docker - nothing extra to install.

---

## 1. Run it in Eclipse

**You need:** JDK 17 or newer, Eclipse IDE for Enterprise Java (any recent version), MySQL Server running on port 3306.

1. Unzip `cabshare.zip`.
2. Open `src/main/resources/application.properties` and set your MySQL password:
   `spring.datasource.password=YOUR_PASSWORD` (default user is `root`).
   You do **not** need to create the database - it is created automatically.
3. In Eclipse: **File > Import > Maven > Existing Maven Projects**, choose the `cabshare` folder, **Finish**.
   Wait for the dependencies to download (first time needs internet).
4. Right-click `src/main/java/com/cabshare/CabshareApplication.java` > **Run As > Java Application**
   (or **Spring Boot App** if you have Spring Tools installed).
5. Open **http://localhost:8080**

To run the tests: right-click the project > **Run As > JUnit Test**.

## 2. Demo accounts (created automatically on first start)

| Role      | Email                  | Password       |
|-----------|------------------------|----------------|
| Admin     | admin@cabshare.com     | admin123       |
| Passenger | rahul@cabshare.com     | passenger123   |
| Passenger | priya@cabshare.com     | passenger123   |
| Driver    | suresh@cabshare.com    | driver123      |

Suresh is already verified (Toyota Innova, 6 seats, 4 bags). New drivers who register must be verified by the admin first.

## 3. Five-minute demo (the "first milestone" from the project plan)

Use two different browsers (or one normal and one private window) so two people can be logged in at once.

1. **Driver** (Suresh) > Dashboard > **Go Online**.
2. **Passenger A** (Rahul) > Find Ride > pick a departure time in the future, 1 passenger, 1 bag > Request Ride.
3. **Passenger B** (Priya) > Find Ride > similar time > Request Ride. A shared ride is proposed:
   estimated total Rs. 1300, share Rs. 650 each.
4. Both passengers open **My Rides** and click **Accept shared ride**.
5. **Driver** refreshes the dashboard > an offer appears > **Accept**.
6. Passengers refresh **My Rides** and see the driver and vehicle details.
7. **Driver** presses the status button four times:
   DRIVER_ARRIVING > AT_PICKUP > IN_PROGRESS > COMPLETED.
8. Passengers click **Pay now (simulated)**. Driver > Earnings shows the completed ride.
9. **Admin** > Rides shows the full status history. Admin > Audit Log shows driver verification actions.

Tip: if no driver is online when the group confirms, the ride waits. It is offered automatically when a driver goes online,
or the admin can press **Send offers again**.

## 4. How the code is organised (for explaining it)

```
controller/   Receive the web request, call a service, pick the page to show
service/      All business rules (matching, dispatch, ride life cycle, payments)
repository/   Spring Data JPA interfaces - database access, no SQL written by hand
model/        Entities (database tables) and enums (statuses)
dto/          Forms with validation, and small view objects for the pages
config/       Spring Security setup, login lookup, demo data
util/         FareCalculator and BusinessException
resources/templates/   Thymeleaf pages (passenger / driver / admin / shared layout)
```

It is a **modular monolith**: one Spring Boot application, layered Controller > Service > Repository > MySQL.

### Rules and where they live

| Rule                                                        | Where                                   |
|-------------------------------------------------------------|-----------------------------------------|
| Each role only sees its own pages                           | `SecurityConfig`                        |
| A user can only touch their own requests, offers and rides  | ownership checks in the services        |
| Matching: capacity, luggage, overlapping departure windows  | `MatchingService.isCompatible`          |
| Fare split with `BigDecimal` (never double)                 | `FareCalculator`                        |
| A group is confirmed only when **every** passenger accepts  | `MatchingService.respondToProposal`     |
| Only verified, online, free drivers whose vehicle fits get offers | `DispatchService.dispatch`        |
| Offers expire after 10 minutes                              | `DriverService.activeOffers`            |
| A ride is never given to two drivers                        | `DispatchService.acceptOffer` (row lock)|
| Ride can only move one step forward                         | `RideStatus.canMoveTo`                  |
| Every status change is stored with who/when/old/new         | `RideService.recordHistory`             |
| Admin actions are audited                                   | `AdminService` + `AuditService`         |

### The one tricky part: "only one driver wins"
`DispatchService.acceptOffer` first **locks the ride row** (`PESSIMISTIC_WRITE`). If two drivers press Accept at the same
moment, the second waits for the first to finish, then sees the ride is already assigned and gets a clear message.

## 5. What is simplified (on purpose) compared with the full specification

- One route only (Airport to Majestic), as the spec recommends for the first version.
- Pairs of passenger requests are matched (two requests per shared ride).
- Offers go to **all** eligible drivers at once; the first to accept wins (instead of one-by-one ranking by distance).
- No maps, GPS or live location; drivers are "nearby" if they are online and free.
- Payments are simulated (a button). No card details are stored.
- Pages refresh manually (no WebSocket). Notifications are stored and shown on the dashboards.
- Session login instead of JWT (simpler for a server-rendered app, and it is allowed by the spec).
- Vehicle details are part of the driver record (one vehicle per driver).
- To keep the code short, Maven wrapper, Docker, Flyway and CI are not included.

## 6. Troubleshooting

- **"Access denied for user 'root'"** - wrong password in `application.properties`.
- **"Communications link failure"** - MySQL is not running or not on port 3306.
- **Port 8080 already in use** - change `server.port` in `application.properties`.
- **Red errors in Eclipse right after import** - wait for the Maven download to finish, then right-click the project >
  **Maven > Update Project**.
- To start again with clean data, drop the `cabshare` database in MySQL and restart the app.
