package com.leadlet.service.mapper;

import com.leadlet.domain.Person;
import com.leadlet.service.dto.PersonDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity Person and its DTO PersonDTO.
 */
@Mapper(componentModel = "spring", uses = {ContactPhoneMapper.class, OrganizationMapper.class})
public interface PersonMapper extends EntityMapper<PersonDTO, Person> {

    PersonDTO toDto(Person person);

    @Mapping(target = "appAccount", ignore = true)
    Person toEntity(PersonDTO personDTO);

    default Person fromId(Long id) {
        if (id == null) {
            return null;
        }
        Person person = new Person();
        person.setId(id);
        return person;
    }
}
