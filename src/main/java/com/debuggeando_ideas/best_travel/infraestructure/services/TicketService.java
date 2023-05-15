package com.debuggeando_ideas.best_travel.infraestructure.services;

import com.debuggeando_ideas.best_travel.api.models.request.TicketRequest;
import com.debuggeando_ideas.best_travel.api.models.responses.FlyResponse;
import com.debuggeando_ideas.best_travel.api.models.responses.TicketResponse;
import com.debuggeando_ideas.best_travel.domain.entities.TicketEntity;
import com.debuggeando_ideas.best_travel.domain.repositories.CustomerRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.FlyRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.TicketRepository;
import com.debuggeando_ideas.best_travel.domain.util.enums.BestTravelUtil;
import com.debuggeando_ideas.best_travel.domain.util.enums.Tables;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.ITicketService;
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
import java.util.Objects;
import java.util.UUID;


//Se implementa el ITICKET y se usan aqui los reques y responses
@Transactional
@Service
@Slf4j
@AllArgsConstructor

public class TicketService implements ITicketService {


    private final FlyRepository flyRepository;
    private final CustomerRepository customerRepository;
    private final TicketRepository ticketRepository;
    private final CustomerHelper customerHelper;
    private BlackListHelper blackListHelper;
    private EmailHelper emailHelper;
    @Override
    public TicketResponse create(TicketRequest request) {
        blackListHelper.isInBlackListCustomer(request.getIdClient());
        var fly = flyRepository.findById(request.getIdFly()).orElseThrow();
        var customer = customerRepository.findById(request.getIdClient()).orElseThrow();

        var ticketToPersist=TicketEntity.builder()
                .id(UUID.randomUUID())
                .fly(fly)
                .customer(customer)
                .price(fly.getPrice().add(fly.getPrice().multiply(charger_price_percentage)))
                .purchaseDate(LocalDate.now())
                .departureDate(BestTravelUtil.getRandomSoon())
                .arrivalDate(BestTravelUtil.getRandomLatter())
                .build();

        var ticketPersisted=this.ticketRepository.save(ticketToPersist);

        if(Objects.nonNull(request.getEmail()))this.emailHelper.sendMail(request.getEmail(),customer.getFullName(), Tables.ticket.name());
        customerHelper.incrase(customer.getDni(),TicketService.class);
        log.info("Ticket saved with id: {}",ticketPersisted.getId());


        return this.entityToResponse(ticketPersisted);
    }

    @Override
    public TicketResponse read(UUID id) {
        var ticketFromDB =this.ticketRepository.findById(id).orElseThrow();

        return this.entityToResponse(ticketFromDB);
    }

    @Override
    public TicketResponse update(TicketRequest request, UUID id) {

        var fly = flyRepository.findById(request.getIdFly()).orElseThrow();
        var ticketToUpdate= ticketRepository.findById(id).orElseThrow();
        ticketToUpdate.setFly(fly);
        ticketToUpdate.setPrice(fly.getPrice().add(fly.getPrice().multiply(charger_price_percentage)));
        ticketToUpdate.setDepartureDate(BestTravelUtil.getRandomSoon());
        ticketToUpdate.setArrivalDate(BestTravelUtil.getRandomLatter());


        var ticketUpdated = this.ticketRepository.save(ticketToUpdate);

        log.info("Ticket updated whith id{}",ticketUpdated.getId());
        return this.entityToResponse(ticketToUpdate);
    }

    @Override
    public void delete(UUID id) {
        var ticketToDelete= ticketRepository.findById(id).orElseThrow();
        this.ticketRepository.delete(ticketToDelete);
    }


    @Override
    public BigDecimal findPrice(Long flyId) {
        var fly=this.flyRepository.findById(flyId).orElseThrow();
        return fly.getPrice().add(fly.getPrice().multiply(charger_price_percentage));
    }
    //Entidades a respuestas 
    private TicketResponse entityToResponse(TicketEntity entity){
        var response = new TicketResponse();
        //Entity son los datos y copia exactamente en el response
        BeanUtils.copyProperties(entity,response);
        var flyResponse= new FlyResponse();
        BeanUtils.copyProperties(entity.getFly(),flyResponse);
        response.setFly(flyResponse);
        return response;

    }

    public static final BigDecimal charger_price_percentage=BigDecimal.valueOf(0.25);


}
