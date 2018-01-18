package com.leadlet.service.impl;

import com.leadlet.domain.ContactPhone;
import com.leadlet.domain.Person;
import com.leadlet.repository.PersonRepository;
import com.leadlet.repository.util.SearchCriteria;
import com.leadlet.repository.util.SpecificationsBuilder;
import com.leadlet.security.SecurityUtils;
import com.leadlet.service.PersonService;
import com.leadlet.service.dto.PersonDTO;
import com.leadlet.service.mapper.PersonMapper;
import com.leadlet.web.rest.util.ParameterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.List;


/**
 * Service Implementation for managing Contact.
 */
@Service
@Transactional
public class PersonServiceImpl implements PersonService {

    private final Logger log = LoggerFactory.getLogger(PersonServiceImpl.class);

    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    public PersonServiceImpl(PersonRepository personRepository, PersonMapper personMapper) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
    }

    /**
     * Save a person.
     *
     * @param personDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public PersonDTO save(PersonDTO personDTO) {
        log.debug("Request to save Person : {}", personDTO);
        Person person = personMapper.toEntity(personDTO);
        person.setAppAccount(SecurityUtils.getCurrentUserAppAccountReference());

        person = personRepository.save(person);
        return personMapper.toDto(person);
    }

    /**
     * Update a person.
     *
     * @param personDTO the entity to update
     * @return the persisted entity
     */
    @Override
    public PersonDTO update(PersonDTO personDTO) {
        log.debug("Request to update Person : {}", personDTO);

        Person person = personMapper.toEntity(personDTO);
        Person personFromDb = personRepository.findOneByIdAndAppAccount_Id(person.getId(), SecurityUtils.getCurrentUserAppAccountId());
        if (personFromDb != null) {
            person.setAppAccount(SecurityUtils.getCurrentUserAppAccountReference());
            person = personRepository.save(person);
            return personMapper.toDto(person);
        } else {
            throw new EntityNotFoundException();
        }
    }

    /**
     * Get all the persons.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PersonDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Persons");
        return personRepository.findByAppAccount_Id(SecurityUtils.getCurrentUserAppAccountId(), pageable)
            .map(personMapper::toDto);
    }

    @Override
    public Page<PersonDTO> search(String filter, Pageable pageable) {
        log.debug("Request to get all Persons");
        SpecificationsBuilder builder = new SpecificationsBuilder();

        if (!StringUtils.isEmpty(filter)) {
            List<SearchCriteria> criteriaList = ParameterUtil.createCriterias(filter);

            for (SearchCriteria criteria : criteriaList) {
                builder.with(criteria);
            }

        }

        // TODO add account criteria
        // builder.with("appAccount",":", SecurityUtils.getCurrentUserAppAccountReference());

        Specification<Person> spec = builder.build();

        return personRepository.findAll(spec, pageable)
            .map(personMapper::toDto);

    }

    /**
     * Get one person by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public PersonDTO findOne(Long id) {
        log.debug("Request to get Person : {}", id);
        Person contact = personRepository.findOneByIdAndAppAccount_Id(id, SecurityUtils.getCurrentUserAppAccountId());
        return personMapper.toDto(contact);
    }

    /**
     * Delete the person by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Person : {}", id);

        Person person = personRepository.findOneByIdAndAppAccount_Id(id, SecurityUtils.getCurrentUserAppAccountId());
        if (person != null) {
            personRepository.delete(id);
        } else {
            throw new EntityNotFoundException();
        }
    }
}