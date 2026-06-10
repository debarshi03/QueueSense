package com.example.QueueSense.QueueSense.service;

import com.example.QueueSense.QueueSense.dto.*;
import com.example.QueueSense.QueueSense.entity.Appointment;
import com.example.QueueSense.QueueSense.entity.QueueEntry;
import com.example.QueueSense.QueueSense.entity.ServiceProvider;
import com.example.QueueSense.QueueSense.entity.User;
import com.example.QueueSense.QueueSense.entity.type.AppointmentStatus;
import com.example.QueueSense.QueueSense.entity.type.QueueStatus;
import com.example.QueueSense.QueueSense.queue.QueueService;
import com.example.QueueSense.QueueSense.repository.AppointmentRepository;
import com.example.QueueSense.QueueSense.repository.QueueRepository;
import com.example.QueueSense.QueueSense.repository.ServiceProviderRepository;
import com.example.QueueSense.QueueSense.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;
    private final ServiceProviderRepository serviceProviderRepository;
    private final QueueService queueService;
    private final QueueRepository queueRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;


//    public @Nullable AppointmentStatusResponseDto updateStatus(AppointmentStatusRequestDto appointmentStatusRequestDto) {
//        Appointment appointment = appointmentRepository.findById(appointmentStatusRequestDto.getAppointmentId())
//                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
//
//        if (appointment.getStatus() == appointmentStatusRequestDto.getStatus()) {
//            throw new IllegalArgumentException("Appointment already has status: " + appointmentStatusRequestDto.getStatus());
//        }
//
//
//        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
//            QueueEntry queueEntry=queueRepository.findByAppointment(appointment)
//                    .orElseThrow(()->new IllegalArgumentException("Cannot change status after completion"));
//            queueEntry.setStatus(QueueStatus.COMPLETED);
//            queueRepository.save(queueEntry);
//
//            queueService.recalculateQueue(appointment.getProvider().getAverageServiceTime());
//        }
//
//        if (appointment.getStatus() == AppointmentStatus.IN_PROGRESS) {
//            QueueEntry queueEntry=queueRepository.findByAppointment(appointment)
//                    .orElseThrow(()->new IllegalArgumentException("Cannot change status after completion"));
//            queueEntry.setStatus(QueueStatus.IN_PROGRESS);
//            queueRepository.save(queueEntry);
//
//            queueService.recalculateQueue(appointment.getProvider().getAverageServiceTime());
//        }
//
//
//        if (appointment.getStatus() == AppointmentStatus.BOOKED &&
//                appointmentStatusRequestDto.getStatus() != AppointmentStatus.COMPLETED) {
//
//            throw new IllegalArgumentException("Invalid status transition");
//        }
//        appointment.setStatus(appointmentStatusRequestDto.getStatus());
//        appointmentRepository.save(appointment);
//
//
//        notificationService.sendNotification(
//                appointment.getUser(),
//                "Your Appointment is Completed"
//        );
//
//        AppointmentStatusResponseDto responseDto=new AppointmentStatusResponseDto();
//        responseDto.setAppointmentId(appointment.getId());
//        responseDto.setStatus(appointment.getStatus());
//        responseDto.setMessage("Status is updated Successfully");
//        responseDto.setUpdatedAt(LocalDateTime.now());
//
//        return responseDto;
//    }

    public AppointmentStatusResponseDto updateStatus(AppointmentStatusRequestDto appointmentStatusRequestDto){
        Appointment appointment=appointmentRepository.findById(appointmentStatusRequestDto.getAppointmentId())
                .orElseThrow(()->new IllegalArgumentException("Appointment not found"));

        AppointmentStatus oldStatus=appointment.getStatus();
        AppointmentStatus newStatus=appointmentStatusRequestDto.getStatus();

        if(oldStatus==newStatus){
            throw new IllegalArgumentException("Appointment already has Status"+ newStatus);
        }

        if(oldStatus==AppointmentStatus.COMPLETED){
            throw new IllegalArgumentException("Appointment status can not be changed after completion");
        }

        Long providerId= appointment.getProvider().getId();
        int avgTime= appointment.getProvider().getAverageServiceTime();

        QueueEntry queueEntry=queueRepository.findByAppointment(appointment)
                .orElseThrow(()-> new IllegalArgumentException("Appointment not found"));

        appointment.setStatus(appointmentStatusRequestDto.getStatus());
        appointmentRepository.save(appointment);

        if(newStatus==AppointmentStatus.COMPLETED) {
            queueEntry.setStatus(QueueStatus.COMPLETED);
            queueEntry.setEndTime(LocalDateTime.now());
            queueRepository.save(queueEntry);

            notificationService.sendNotification(
                    appointment.getUser(),
                    "Your apointment is completed"
            );


            List<QueueEntry> waiting = queueRepository
                    .findByAppointment_Provider_IdAndStatusOrderByPositionAsc(
                            providerId,
                            QueueStatus.WAITING
                    );

            if (!waiting.isEmpty()) {
                QueueEntry next = waiting.get(0);
                next.setStatus(QueueStatus.IN_PROGRESS);
                queueRepository.save(next);

                notificationService.sendNotification(
                        next.getAppointment().getUser(),
                        "Now it's your turn"
                );

                emailService.sendMail(
                        next.getAppointment().getUser().getEmail(),
                        "Appointment Update",
                        "It's your turn now"
                );
            }
            queueService.recalculateQueue(providerId,avgTime);
        }

        else if(newStatus==AppointmentStatus.IN_PROGRESS){
            queueEntry.setStatus(QueueStatus.IN_PROGRESS);
            queueEntry.setStartTime(LocalDateTime.now());
            queueRepository.save(queueEntry);
            notificationService.sendNotification(
                    appointment.getUser(),
                    "Your Appointment has started"
            );

            if (queueEntry.getStartTime()!=null && queueEntry.getEndTime()!=null){
                long actualTime= Duration.between(
                        queueEntry.getStartTime(),
                        queueEntry.getEndTime()
                ).toMinutes();

                ServiceProvider provider=appointment.getProvider();
                int oldAvgTime=provider.getAverageServiceTime();
                int newAvgTime=(oldAvgTime+ (int) actualTime)/2;

                provider.setAverageServiceTime(newAvgTime);
                serviceProviderRepository.save(provider);
            }
        }

        else if (newStatus == AppointmentStatus.NO_SHOW) {

            queueEntry.setStatus(QueueStatus.NO_SHOW);
            queueRepository.save(queueEntry);

            List<QueueEntry> waitList =
                    queueRepository.findByAppointment_Provider_IdAndStatusOrderByPositionAsc(
                            providerId,
                            QueueStatus.WAITING
                    );


            if (!waitList.isEmpty()) {

                QueueEntry next = waitList.get(0);

                next.setStatus(QueueStatus.IN_PROGRESS);
                next.setStartTime(LocalDateTime.now());

                queueRepository.save(next);


                notificationService.sendNotification(
                        next.getAppointment().getUser(),
                        "Previous user missed turn. It's your turn now"
                );
            }


            queueService.recalculateQueue(providerId, avgTime);


            notificationService.sendNotification(
                    appointment.getUser(),
                    "You missed your appointment (NO SHOW)"
            );
            emailService.sendMail(
                    appointment.getUser().getEmail(),
                    "Appointment Update",
                    "You missed your appointment"
            );
        }

        AppointmentStatusResponseDto appointmentStatusResponseDto= new AppointmentStatusResponseDto();
        appointmentStatusResponseDto.setAppointmentId(appointment.getId());
        appointmentStatusResponseDto.setStatus(appointment.getStatus());
        appointmentStatusResponseDto.setMessage("Status updated");
        appointmentStatusResponseDto.setUpdatedAt(LocalDateTime.now());

        return appointmentStatusResponseDto;


    }


    public @Nullable AppointmentResponseDto createNewAppointment(CreateAppointmentRequestDto createAppointmentRequestDto) {
        Long providerId=createAppointmentRequestDto.getProviderId();
        Long userId=createAppointmentRequestDto.getUserId();
        User user= userRepository.findById(userId).orElseThrow();
        ServiceProvider serviceProvider=serviceProviderRepository.findById(providerId).orElseThrow();

        LocalDate date = LocalDateTime.now()
                .toLocalDate();

        LocalDateTime startOfDay=date.atStartOfDay();
        LocalDateTime endOfDay=date.atTime(LocalTime.MAX);

//        long totalAssignAppointment= appointmentRepository
//                .countByProvider_IdAndAppointmentTimeBetween(
//                        providerId,
//                        startOfDay,
//                        endOfDay
//                );

        long totalAssignAppointment =
                appointmentRepository
                        .countByProvider_IdAndAppointmentTimeBetweenAndStatusNot(
                                providerId,
                                startOfDay,
                                endOfDay,
                                AppointmentStatus.CANCELLED
                        );

        if(totalAssignAppointment>= serviceProvider.getMaxAppointment()){
            throw new IllegalArgumentException("Today's appointment slots are full, please choose another date to book your appointment");
        }

        Appointment appointment = Appointment.builder()
                .reason(createAppointmentRequestDto.getReason())
                .appointmentTime(LocalDateTime.now())
                .status(AppointmentStatus.BOOKED)
                .build();

        appointment.setProvider(serviceProvider);
        appointment.setUser(user);
        user.getAppointments().add(appointment);

        appointment = appointmentRepository.save(appointment);

        notificationService.sendNotification(
                user,
                "Appointment is Created"
        );

        queueService.addToQueue(appointment,serviceProvider.getAverageServiceTime());

        queueService.recalculateQueue(providerId, serviceProvider.getAverageServiceTime());

        emailService.sendMail(
                user.getEmail(),
                "Appointment Created",
                "Your appointment is booked successfully"
        );

        return modelMapper.map(appointment, AppointmentResponseDto.class);



    }

    public @Nullable AppointmentStatusResponseDto cancelAppointment(AppointmentStatusRequestDto appointmentStatusRequestDto) {
        Appointment appointment=appointmentRepository.findById(appointmentStatusRequestDto.getAppointmentId())
                .orElseThrow(()-> new IllegalArgumentException("Appointment not found with id"+ appointmentStatusRequestDto.getAppointmentId()));

        AppointmentStatus oldStatus= appointment.getStatus();
        AppointmentStatus newStatus= appointmentStatusRequestDto.getStatus();

        if (newStatus != AppointmentStatus.CANCELLED) {
            throw new IllegalArgumentException("Only CANCELLED is allowed here");
        }

        if ( oldStatus == newStatus ){
            throw new IllegalArgumentException("Appointment already has started " + newStatus);
        }

        if (oldStatus== AppointmentStatus.COMPLETED){
            throw new IllegalArgumentException("You can't cancel your appointment because your appointment is already    completed");

        }

        if (oldStatus == AppointmentStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Cannot cancel once service has started");
        }

        Long providerId= appointment.getProvider().getId();
        int avgTime= appointment.getProvider().getAverageServiceTime();

        appointment.setStatus(appointmentStatusRequestDto.getStatus());
        appointmentRepository.save(appointment);

        QueueEntry queueEntry= queueRepository.findByAppointment(appointment)
                .orElseThrow(()-> new IllegalArgumentException("Appointment not found"));

        if(newStatus==AppointmentStatus.CANCELLED){
            boolean wasInprogress=queueEntry.getStatus()==QueueStatus.IN_PROGRESS;
            queueEntry.setStatus(QueueStatus.CANCELLED);
            queueRepository.save(queueEntry);

            if(wasInprogress){
                List<QueueEntry> waitList= queueRepository.findByAppointment_Provider_IdAndStatusOrderByPositionAsc(
                        providerId,
                        QueueStatus.WAITING

                );

                if (!waitList.isEmpty()){
                    QueueEntry next=waitList.get(0);
                    next.setStatus(QueueStatus.IN_PROGRESS);
                    next.setStartTime(LocalDateTime.now());
                    queueRepository.save(next);

                    notificationService.sendNotification(
                            next.getAppointment().getUser(),
                            "It's your turn now"
                    );

                    emailService.sendMail(
                            next.getAppointment().getUser().getEmail(),
                            "Appointment Update",
                            "It's your turn now"
                    );
                }

            }

            queueService.recalculateQueue(providerId, avgTime);

            notificationService.sendNotification(
                    appointment.getUser(),
                    "Your appointment is cancelled"
            );

            emailService.sendMail(
                    appointment.getUser().getEmail(),
                    "Appointment Update",
                    "Your appointment is cancelled"
            );
        }
        AppointmentStatusResponseDto responseDto=new AppointmentStatusResponseDto();
        responseDto.setAppointmentId(appointment.getId());
        responseDto.setMessage("Status Updated");
        responseDto.setStatus(appointmentStatusRequestDto.getStatus());
        responseDto.setUpdatedAt(LocalDateTime.now());
        return responseDto;
    }

    public @Nullable ProviderAnalyticsDto getAnalytics(Long id) {
        ProviderAnalyticsDto dto= new ProviderAnalyticsDto();

        dto.setTotalAppointments(
                appointmentRepository.countByProvider_Id(id)
        );

        dto.setAvgWaitTime(
                queueRepository.getAverageWaitTime(id).intValue()
        );

        dto.setCompletedAppointments(
                appointmentRepository.countByProvider_IdAndStatus(id,AppointmentStatus.COMPLETED)
        );

        dto.setCancelledAppointments(
                appointmentRepository.countByProvider_IdAndStatus(id, AppointmentStatus.CANCELLED)
        );

        dto.setNoShowAppointments(
                appointmentRepository.countByProvider_IdAndStatus(id, AppointmentStatus.NO_SHOW)
        );

        return dto;
    }
}














