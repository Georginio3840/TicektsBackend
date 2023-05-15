package com.debuggeando_ideas.best_travel.infraestructure.abstract_services;

import com.debuggeando_ideas.best_travel.api.models.request.TicketRequest;
import com.debuggeando_ideas.best_travel.api.models.responses.TicketResponse;

import java.math.BigDecimal;
import java.util.UUID;


//Se implementa en una interface la clase generica que es CRUDSERVICE y los request y responses
//ESTO ES PROPIO , ES DECIR COMO UNAS REGLAS DEL NEGOCIO
public interface ITicketService extends  CrudService <TicketRequest, TicketResponse, UUID>{

    BigDecimal findPrice (Long flyId);


}
