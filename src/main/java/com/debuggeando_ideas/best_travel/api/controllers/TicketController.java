package com.debuggeando_ideas.best_travel.api.controllers;

import com.debuggeando_ideas.best_travel.api.models.request.TicketRequest;
import com.debuggeando_ideas.best_travel.api.models.responses.ErrorsResponse;
import com.debuggeando_ideas.best_travel.api.models.responses.TicketResponse;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.ITicketService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;


// es el que nos permite utilizar las peticiones HTTP y los paths
@RestController
@RequestMapping(path = "ticket")
@AllArgsConstructor
@Tag(name = "Ticket")
public class TicketController {
    private final ITicketService ticketService;

    @ApiResponse(responseCode = "400",
            description = "When the request have a field  invalid we response this ",
            content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorsResponse.class))
            }
    )
    @PostMapping
    public ResponseEntity <TicketResponse> post(@RequestBody TicketRequest request){
        return ResponseEntity.ok(ticketService.create(request));
    }

    @GetMapping(path = "{id}")
    public ResponseEntity <TicketResponse> get(@PathVariable UUID id){
        return ResponseEntity.ok(ticketService.read(id));
    }


    @PutMapping(path = "{id}")
    public   ResponseEntity <TicketResponse> put(@PathVariable UUID id,@RequestBody TicketRequest request){

        return ResponseEntity.ok(this.ticketService.update(request,id));
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity <Void> delete(@PathVariable UUID id){
        this.ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity <Map<String, BigDecimal>> getFlyPrice(@RequestParam Long flyId){
        return ResponseEntity.ok(Collections.singletonMap("flyPrice",this.ticketService.findPrice(flyId)));
    }

}
