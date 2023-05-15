package com.debuggeando_ideas.best_travel.infraestructure.services;

import com.debuggeando_ideas.best_travel.api.models.request.ReservationRequest;
import com.debuggeando_ideas.best_travel.api.models.responses.HotelResponse;
import com.debuggeando_ideas.best_travel.api.models.responses.ReservationResponse;
import com.debuggeando_ideas.best_travel.domain.entities.ReservationEntity;
import com.debuggeando_ideas.best_travel.domain.repositories.CustomerRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.HotelRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.ReservationRepository;
import com.debuggeando_ideas.best_travel.domain.util.enums.Tables;
import com.debuggeando_ideas.best_travel.domain.util.exceptions.IdNotFoundException;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.IReservationService;
import com.debuggeando_ideas.best_travel.infraestructure.helpers.BlackListHelper;
import com.debuggeando_ideas.best_travel.infraestructure.helpers.CustomerHelper;
import com.debuggeando_ideas.best_travel.infraestructure.helpers.EmailHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Transactional
@Service
@Slf4j
@AllArgsConstructor
public class ReservationService  implements IReservationService {

    private final HotelRepository hotelRepository;
    private final CustomerRepository customerRepository;
    private final ReservationRepository reservationRepository;
    private final CustomerHelper customerHelper;
    private BlackListHelper blackListHelper;
    private final EmailHelper emailHelper;

    @Override
    public ReservationResponse create(ReservationRequest request) {
        blackListHelper.isInBlackListCustomer(request.getIdClient());
        var hotel = hotelRepository.findById(request.getIdHotel()).orElseThrow(()-> new IdNotFoundException("hotel"));
        var customer = customerRepository.findById(request.getIdClient()).orElseThrow(()-> new IdNotFoundException("customer"));

        var reservationToPersist= ReservationEntity.builder()
                .id(UUID.randomUUID())
                .hotel(hotel)
                .customer(customer)
                .totalDays(request.getTotalDays())
                .dateTimeReservation(LocalDateTime.now())
                .dateStart(LocalDate.now())
                .dateEnd(LocalDate.now().plusDays(request.getTotalDays()))
                .price(hotel.getPrice().add(hotel.getPrice().multiply(charges_price_percentage)))
                .build();

        var reservationPersisted= reservationRepository.save(reservationToPersist);
        if(Objects.nonNull(request.getEmail()))this.emailHelper.sendMail(request.getEmail(),customer.getFullName(),Tables.reservation.name());

        customerHelper.incrase(customer.getDni(),ReservationService.class);
        return this.entityToResponse(reservationPersisted);
    }

    @Override
    public ReservationResponse read(UUID id) {

        var reservationFromDB =this.reservationRepository.findById(id).orElseThrow(()-> new IdNotFoundException(Tables.reservation.name()));

        return this.entityToResponse(reservationFromDB);
    }

    @Override
    public ReservationResponse update(ReservationRequest request, UUID id) {
        var hotel = hotelRepository.findById(request.getIdHotel()).orElseThrow(()-> new IdNotFoundException("hotel"));
        var reservationToUpdate=this.reservationRepository.findById(id).orElseThrow(()-> new IdNotFoundException("reservation"));
        reservationToUpdate.setHotel(hotel);
        reservationToUpdate.setTotalDays(request.getTotalDays());
        reservationToUpdate.setDateTimeReservation(LocalDateTime.now());
        reservationToUpdate.setDateStart(LocalDate.now());
        reservationToUpdate.setDateEnd(LocalDate.now().plusDays(request.getTotalDays()));
        reservationToUpdate.setPrice(hotel.getPrice().add(hotel.getPrice().multiply(charges_price_percentage)));

        var reservationUpdated=this.reservationRepository.save(reservationToUpdate);
        log.info("Reservation updated whith id{}",reservationUpdated.getId());
        return this.entityToResponse(reservationUpdated);
    }

    @Override
    public void delete(UUID id) {
        var reservationToDelete= reservationRepository.findById(id).orElseThrow(()-> new IdNotFoundException("reservation"));
        this.reservationRepository.delete(reservationToDelete);
    }



    private ReservationResponse entityToResponse(ReservationEntity entity){
        var response = new ReservationResponse();
        //Entity son los datos y copia exactamente en el response
        BeanUtils.copyProperties(entity,response);
        var hotelResponse= new HotelResponse();
        BeanUtils.copyProperties(entity.getHotel(),hotelResponse);
        response.setHotel(hotelResponse);
        return response;

    }
    @Override
    public BigDecimal findPrice(Long hotelId) {
        var hotel=hotelRepository.findById(hotelId).orElseThrow(()-> new IdNotFoundException("hotel"));
        return hotel.getPrice().add(hotel.getPrice().multiply(charges_price_percentage));
    }
    public static final BigDecimal charges_price_percentage=BigDecimal.valueOf(0.20);
}
