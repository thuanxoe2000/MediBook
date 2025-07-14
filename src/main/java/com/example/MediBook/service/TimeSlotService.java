package com.example.MediBook.service;

import com.example.MediBook.dto.request.BookingRequest;
import com.example.MediBook.dto.response.BookingResponse;
import com.example.MediBook.dto.response.TimeSlotResponse;
import com.example.MediBook.entity.*;
import com.example.MediBook.enums.BookingStatus;
import com.example.MediBook.mapper.Mapper;
import com.example.MediBook.repository.*;
import com.example.MediBook.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TimeSlotService {
    TimeSlotRepository timeSlotRepository;
    MailService mailService;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    DepartmentRepository departmentRepository;
    Mapper mapper;
    UserUtil userUtil;

    public List<TimeSlotResponse> getAvailableSlots(LocalDate date, Long dpmId) {
        return timeSlotRepository.findByDateAndAvailableIsTrueAndDepartmentId(date, dpmId)
                .stream().map(mapper::toTimeSlotResponse)
                .collect(Collectors.toList());
    }

    public void createPending(BookingRequest request) {
        TimeSlot timeSlot = timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new RuntimeException("スロットが見つかりません"));
        Department department = departmentRepository.findById(request.getDpmId())
                .orElseThrow(() -> new RuntimeException("診療科が見つかりません"));
        timeSlot.setAvailable(false);
        Booking booking = Booking.builder()
                .createdAt(LocalDateTime.now())
                .note(request.getNote())
                .status(BookingStatus.PENDING)
                .timeSlot(timeSlot)
                .user(userUtil.getCurrentUser())
                .department(department)
                .build();
        bookingRepository.save(booking);
        timeSlotRepository.save(timeSlot);
        sendPendingEmail(department, timeSlot.getDate(), timeSlot.getTime(), userUtil.getCurrentUser());
    }

    private void sendPendingEmail(Department department, LocalDate date, LocalTime time, User user) {
        Hospital hospital = departmentRepository.findHospitalByDepartmentId(department.getId())
                .orElseThrow(() -> new RuntimeException("病院が見つかりません"));

        // send to director
        String textDirector = String.format(
                "%s（診療科：%s）にて、%sの%s時に新しい予約が入りました（予約者：%s）。",
                hospital.getName(), department.getName(), date, time, user.getName()
        );
        sendMail(hospital.getDirector().getUser().getEmail(), "新しい予約", textDirector);

        // send to head
        String textHead = String.format(
                "診療科「%s」に対して、%sの%s時に患者「%s」から新しい予約が入りました。",
                department.getName(), date, time, user.getName()
        );
        sendMail(department.getHead().getEmail(), "新しい予約", textHead);

        // send to user
        String subject = "診療予約確認";
        String text = String.format(
                "%s 様\n\n%sの%s時に診療予約を受け付けました。現在、病院の確認を待っています。",
                user.getName(), date, time
        );
        sendMail(user.getEmail(), subject, text);
    }

    private void sendConfirmEmail(LocalDate date, LocalTime time, Long userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
        String to = user.getEmail();
        String subject = "診療予約確認";
        String text;
        if (message == null) {
            text = String.format(
                    "%s 様\n\n%sの%s時の診療予約が確定しました。",
                    user.getName(), date, time
            );
        } else {
            text = String.format(
                    "%s 様\n\nご予約は次の理由でキャンセルされました: %s",
                    user.getName(), message
            );
        }
        sendMail(to, subject, text);
    }

    private void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailService.sendMail(message);
    }

    public void confirmBooking(Long id, boolean isConfirm, String message) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("予約が見つかりません"));
        if (isConfirm) {
            booking.setStatus(BookingStatus.CONFIRMED);
        } else {
            booking.setStatus(BookingStatus.CANCELED);
        }
        sendConfirmEmail(booking.getTimeSlot().getDate(), booking.getTimeSlot().getTime(), booking.getUser().getId(), message);
        bookingRepository.save(booking);
    }

    public List<BookingResponse> getUserBooking() {
        return bookingRepository
                .findAllByUserId(userUtil.getCurrentUserId())
                .stream()
                .map(mapper::toBookingResponse)
                .collect(Collectors.toList());
    }

    public void cancel(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("予約が見つかりません"));
        TimeSlot timeSlot = booking.getTimeSlot();
        timeSlot.setAvailable(true);
        timeSlotRepository.save(timeSlot);
        booking.setStatus(BookingStatus.CANCELED);
        bookingRepository.save(booking);
    }

    public void finish(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("予約が見つかりません"));
        if (booking.getStatus() != BookingStatus.CANCELED) {
            booking.setStatus(BookingStatus.FINISHED);
        }
        bookingRepository.save(booking);
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    @Transactional
    public void autoFinishConfirmedBookings() {
        LocalDateTime now = LocalDateTime.now().minusHours(1);
        LocalDate limitDate = now.toLocalDate();
        LocalTime limitTime = now.toLocalTime();

        List<Booking> bookings = bookingRepository.findBookingsToFinish(limitDate, limitTime);

        for (Booking booking : bookings) {
            booking.setStatus(BookingStatus.FINISHED);
        }
    }

    public void generateInitialSlotsForDepartment(Department department) {
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 30; i++) {
            LocalDate date = today.plusDays(i);
            boolean exists = timeSlotRepository.existsByDateAndDepartmentId(date, department.getId());
            if (!exists) {
                generateSlotsForDateAndDepartment(date, department);
            }
        }
    }

    private void generateSlotsForDateAndDepartment(LocalDate date, Department department) {
        List<TimeSlot> slots = new ArrayList<>();
        for (int hour = 9; hour <= 16; hour++) {
            TimeSlot slot = TimeSlot.builder()
                    .date(date)
                    .time(LocalTime.of(hour, 0))
                    .available(true)
                    .department(department)
                    .build();
            slots.add(slot);
        }
        timeSlotRepository.saveAll(slots);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void autoCreateFutureSlots() {
        LocalDate targetDate = LocalDate.now().plusDays(30);
        List<Department> departments = departmentRepository.findAll();

        for (Department department : departments) {
            if (!timeSlotRepository.existsByDateAndDepartmentId(targetDate, department.getId())) {
                generateSlotsForDateAndDepartment(targetDate, department);
            }
        }
    }
}
