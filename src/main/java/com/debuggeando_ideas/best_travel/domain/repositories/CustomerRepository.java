package com.debuggeando_ideas.best_travel.domain.repositories;

import com.debuggeando_ideas.best_travel.domain.entities.CustomerEntity;
import org.springframework.data.repository.CrudRepository;


//Repositories es para nosotros hacer consulta en la base de datos
public interface CustomerRepository extends CrudRepository<CustomerEntity,String> {

}
