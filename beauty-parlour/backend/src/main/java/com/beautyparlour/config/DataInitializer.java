package com.beautyparlour.config;

import com.beautyparlour.model.entity.Appointment;
import com.beautyparlour.model.entity.Service;
import com.beautyparlour.model.entity.User;
import com.beautyparlour.model.enums.AppointmentStatus;
import com.beautyparlour.model.enums.Role;
import com.beautyparlour.model.enums.ServiceCategory;
import com.beautyparlour.repository.AppointmentRepository;
import com.beautyparlour.repository.ServiceRepository;
import com.beautyparlour.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(final String... args) {
        seedUsers();
        seedServices();
        seedAppointments();
    }

    private void seedUsers() {
        if (!userRepository.existsByEmail("admin@beautyparlour.com")) {
            final var admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@beautyparlour.com");
            admin.setPasswordHash(passwordEncoder.encode("Admin@1234"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            log.info("Admin created: admin@beautyparlour.com / Admin@1234");
        }

        if (userRepository.findByRoleAndIsActiveTrue(Role.STAFF).isEmpty()) {
            seedStaff("Priya Sharma", "priya.sharma@beautyparlour.com");
            seedStaff("Anjali Verma", "anjali.verma@beautyparlour.com");
            seedStaff("Meena Patel",  "meena.patel@beautyparlour.com");
            log.info("Staff created (password: Staff@1234)");
        }

        if (userRepository.findByRoleAndIsActiveTrue(Role.CUSTOMER).isEmpty()) {
            seedCustomer("Riya Nair",     "riya.nair@example.com",     "9876543210");
            seedCustomer("Sunita Kapoor", "sunita.kapoor@example.com", "9876543211");
            seedCustomer("Divya Menon",   "divya.menon@example.com",   "9876543212");
            seedCustomer("Pooja Singh",   "pooja.singh@example.com",   "9876543213");
            log.info("Sample customers created (password: Customer@1234)");
        }
    }

    private void seedStaff(final String name, final String email) {
        final var staff = new User();
        staff.setName(name);
        staff.setEmail(email);
        staff.setPasswordHash(passwordEncoder.encode("Staff@1234"));
        staff.setRole(Role.STAFF);
        userRepository.save(staff);
    }

    private void seedCustomer(final String name, final String email, final String phone) {
        final var customer = new User();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setPasswordHash(passwordEncoder.encode("Customer@1234"));
        customer.setRole(Role.CUSTOMER);
        userRepository.save(customer);
    }

    private void seedServices() {
        if (!serviceRepository.findByIsActiveTrue().isEmpty()) return;

        // HAIR
        saveService("Haircut & Styling",   "Precision cut with blow-dry finish",               ServiceCategory.HAIR,    45,  45);
        saveService("Hair Colour (Global)", "Full head colour with premium ammonia-free dye",   ServiceCategory.HAIR,   120,  90);
        saveService("Hair Spa Treatment",  "Deep conditioning spa for dry & damaged hair",      ServiceCategory.HAIR,    75,  60);
        saveService("Keratin Smoothing",   "Long-lasting frizz-free keratin treatment",         ServiceCategory.HAIR,   200, 120);
        saveService("Blow-dry & Styling",  "Professional blowout with heat-protection finish",  ServiceCategory.HAIR,    35,  30);

        // SKIN
        saveService("Classic Facial",      "Deep-cleanse + scrub + massage + mask",             ServiceCategory.SKIN,    65,  60);
        saveService("De-Tan Treatment",    "Removes tan and brightens skin tone",               ServiceCategory.SKIN,    55,  45);
        saveService("Gold Facial",         "Luxurious gold-infused brightening facial",         ServiceCategory.SKIN,   120,  75);
        saveService("Anti-Aging Treatment","Collagen boost + firming massage",                  ServiceCategory.SKIN,   150,  90);

        // NAILS
        saveService("Manicure",            "Classic nail shaping, cuticle care & polish",       ServiceCategory.NAILS,   30,  30);
        saveService("Pedicure",            "Foot soak, scrub, massage & polish",                ServiceCategory.NAILS,   45,  45);
        saveService("Gel Nail Extensions", "Full set of gel nail extensions",                   ServiceCategory.NAILS,   75,  60);
        saveService("Nail Art Design",     "Custom nail art on natural or extended nails",      ServiceCategory.NAILS,   50,  45);

        // MAKEUP
        saveService("Party Makeup",        "Full face glam for parties and events",             ServiceCategory.MAKEUP, 100,  60);
        saveService("Bridal Makeup",       "Complete bridal look with airbrush finish",         ServiceCategory.MAKEUP, 350, 180);
        saveService("Eye Makeup",          "Smoky / cut-crease / natural eye look",             ServiceCategory.MAKEUP,  55,  30);

        log.info("16 services seeded across HAIR, SKIN, NAILS and MAKEUP");
    }

    private void saveService(final String name, final String description,
                              final ServiceCategory category, final int price, final int durationMins) {
        final var s = new Service();
        s.setName(name);
        s.setDescription(description);
        s.setCategory(category);
        s.setPrice(new BigDecimal(price));
        s.setDurationMins(durationMins);
        serviceRepository.save(s);
    }

    private void seedAppointments() {
        if (appointmentRepository.count() > 0) return;

        // SequencedCollection.getFirst() / getLast() — JEP 431
        final var staff     = userRepository.findByRoleAndIsActiveTrue(Role.STAFF);
        final var customers = userRepository.findByRoleAndIsActiveTrue(Role.CUSTOMER);
        final var services  = serviceRepository.findByIsActiveTrue();

        if (staff.isEmpty() || customers.isEmpty() || services.isEmpty()) return;

        final var priya  = staff.getFirst();
        final var anjali = staff.get(1);
        final var meena  = staff.size() > 2 ? staff.getLast() : staff.getFirst();

        final var riya   = customers.getFirst();
        final var sunita = customers.get(1);
        final var divya  = customers.size() > 2 ? customers.get(2) : customers.getFirst();
        final var pooja  = customers.size() > 3 ? customers.getLast() : customers.getFirst();

        final var haircut      = findService(services, "Haircut");
        final var hairSpa      = findService(services, "Hair Spa");
        final var facial       = findService(services, "Classic Facial");
        final var goldFacial   = findService(services, "Gold Facial");
        final var manicure     = findService(services, "Manicure");
        final var pedicure     = findService(services, "Pedicure");
        final var partyMakeup  = findService(services, "Party Makeup");
        final var bridalMakeup = findService(services, "Bridal Makeup");

        final var now = ZonedDateTime.now(ZoneOffset.UTC);

        // Past — COMPLETED
        saveAppointment(riya,   priya,  haircut,      now.minusDays(5).withHour(10).withMinute(0), AppointmentStatus.COMPLETED, "Regular trim");
        saveAppointment(sunita, anjali, facial,       now.minusDays(4).withHour(11).withMinute(0), AppointmentStatus.COMPLETED, "Skin feels great!");
        saveAppointment(divya,  meena,  manicure,     now.minusDays(3).withHour(14).withMinute(0), AppointmentStatus.COMPLETED, null);
        saveAppointment(pooja,  priya,  partyMakeup,  now.minusDays(2).withHour(16).withMinute(0), AppointmentStatus.COMPLETED, "Anniversary dinner");

        // Past — CANCELLED
        saveAppointment(riya,   anjali, hairSpa,      now.minusDays(6).withHour(9).withMinute(0),  AppointmentStatus.CANCELLED, "Rescheduled");

        // Near future — CONFIRMED
        saveAppointment(sunita, priya,  goldFacial,   now.plusDays(1).withHour(10).withMinute(0),  AppointmentStatus.CONFIRMED, null);
        saveAppointment(divya,  anjali, pedicure,     now.plusDays(1).withHour(13).withMinute(0),  AppointmentStatus.CONFIRMED, "Gel top coat please");

        // Upcoming — PENDING
        saveAppointment(pooja,  meena,  bridalMakeup, now.plusDays(3).withHour(8).withMinute(0),   AppointmentStatus.PENDING,   "Wedding on 15th");
        saveAppointment(riya,   priya,  haircut,      now.plusDays(4).withHour(11).withMinute(0),  AppointmentStatus.PENDING,   null);
        saveAppointment(sunita, anjali, manicure,     now.plusDays(5).withHour(15).withMinute(0),  AppointmentStatus.PENDING,   "French tips");

        log.info("10 sample appointments seeded");
    }

    private void saveAppointment(final User customer, final User staff, final Service service,
                                  final ZonedDateTime startTime, final AppointmentStatus status,
                                  final String notes) {
        final var a = new Appointment();
        a.setCustomer(customer);
        a.setStaff(staff);
        a.setService(service);
        a.setStartTime(startTime);
        a.setEndTime(startTime.plusMinutes(service.getDurationMins()));
        a.setStatus(status);
        a.setNotes(notes);
        appointmentRepository.save(a);
    }

    private Service findService(final List<Service> services, final String keyword) {
        return services.stream()
                .filter(s -> s.getName().contains(keyword))
                .findFirst()
                .orElse(services.getFirst());
    }
}
