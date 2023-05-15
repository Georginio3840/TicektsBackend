package com.debuggeando_ideas.best_travel.infraestructure.abstract_services;

//Datos genericos que se usara en generar en el CRUD CLASE GENERICA DONDE SE IMPLEMENTARAN EN LOS OTROS SERVICES
public interface CrudService <RQ,RS,ID>{

    RS create(RQ request);

    RS read(ID id);

    RS update(RQ request, ID id);

    void delete(ID id);


}
