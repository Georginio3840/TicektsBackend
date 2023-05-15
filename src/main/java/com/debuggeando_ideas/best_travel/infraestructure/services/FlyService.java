package com.debuggeando_ideas.best_travel.infraestructure.services;

import com.debuggeando_ideas.best_travel.api.models.responses.FlyResponse;
import com.debuggeando_ideas.best_travel.domain.entities.FlyEntity;
import com.debuggeando_ideas.best_travel.domain.repositories.FlyRepository;
import com.debuggeando_ideas.best_travel.domain.util.enums.SortType;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.IFlyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@Slf4j
@AllArgsConstructor
public class FlyService implements IFlyService {

    private final FlyRepository flyRepository;



    @Override
    public Page<FlyResponse> realAll(Integer page, Integer size, SortType sortType) {
        PageRequest pageRequest=null;
        switch(sortType){
            case NONE -> pageRequest=PageRequest.of(page,size);
            case LOWER -> pageRequest=PageRequest.of(page,size, Sort.by(FIELD_VY_SORT).ascending());
            case UPPER -> pageRequest=PageRequest.of(page,size,Sort.by(FIELD_VY_SORT).descending());
        }
        return this.flyRepository.findAll(pageRequest).map(this::entityToResponse);
    }

    @Override
    public Set<FlyResponse> readLessPrice(BigDecimal price) {

        return  this.flyRepository.selectLessPrice(price)
                .stream()
                .map(this::entityToResponse)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FlyResponse> readBetweenPrices(BigDecimal min, BigDecimal max) {
        return  this.flyRepository.selectBetweenPrice(min,max)
                .stream()
                .map(this::entityToResponse)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FlyResponse> readByOriginDestiny(String origen, String destiny) {
        return  this.flyRepository.selectOriginDestiny(origen,destiny)
                .stream()
                .map(this::entityToResponse)
                .collect(Collectors.toSet());
    }

    private FlyResponse entityToResponse(FlyEntity entity){
        var response = new FlyResponse();
        //Entity son los datos y copia exactamente en el response
        BeanUtils.copyProperties(entity,response);

        return response;

    }

}
