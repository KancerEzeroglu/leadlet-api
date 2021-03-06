package com.leadlet.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.leadlet.config.Constants;
import com.leadlet.domain.User;
import com.leadlet.repository.UserRepository;
import com.leadlet.security.AuthoritiesConstants;
import com.leadlet.service.MailService;
import com.leadlet.service.UserService;
import com.leadlet.service.dto.UserDTO;
import com.leadlet.service.dto.UserUpdateDTO;
import com.leadlet.service.mapper.UserMapper;
import com.leadlet.web.rest.util.HeaderUtil;
import com.leadlet.web.rest.util.PaginationUtil;
import com.leadlet.web.rest.vm.ManagedUserVM;
import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing users.
 * <p>
 * This class accesses the User entity, and needs to fetch its collection of authorities.
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>
 * Another option would be to have a specific JPA entity graph to handle this case.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    private static final String ENTITY_NAME = "userManagement";

    private final UserRepository userRepository;

    private final MailService mailService;

    private final UserService userService;

    private final UserMapper userMapper;

    public UserResource(UserRepository userRepository, MailService mailService,
                        UserService userService, UserMapper userMapper) {

        this.userRepository = userRepository;
        this.mailService = mailService;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * POST  /users  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     *
     * @param managedUserVM the user to create
     * @return the ResponseEntity with status 201 (Created) and with body the new user, or with status 400 (Bad Request) if the login or email is already in use
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/users")
    @Timed
    //@Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity createUser(@Valid @RequestBody ManagedUserVM managedUserVM) throws URISyntaxException {
        log.debug("REST request to save User : {}", managedUserVM);

        if (managedUserVM.getId() != null) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new user cannot already have an ID"))
                .body(null);
            // Lowercase the user login before comparing with database
        } else if (userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase()).isPresent()) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "userexists", "Login already in use"))
                .body(null);
        } else {
            User newUser = userService.createUser(managedUserVM);
//            mailService.sendCreationEmail(newUser);
            return ResponseEntity.created(new URI("/api/users/" + newUser.getLogin()))
                .headers(HeaderUtil.createAlert("userManagement.created", newUser.getLogin()))
                .body(newUser);
        }
    }

    /**
     * PUT  /users : Updates an existing User.
     *
     * @param userUpdateDTO the user to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated user,
     * or with status 400 (Bad Request) if the login or email is already in use,
     * or with status 500 (Internal Server Error) if the user couldn't be updated
     */
    @PutMapping("/users")
    @Timed
    public ResponseEntity<UserUpdateDTO> updateUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        log.debug("REST request to update User : {}", userUpdateDTO);
        /*
        User existingUser = userRepository.findOne(userUpdateDTO.getId());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserVM.getId()))) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "userexists", "Login already in use")).body(null);
        }
        */
        Optional<UserUpdateDTO> updatedUser = userService.updateUser(userUpdateDTO);

        return ResponseUtil.wrapOrNotFound(updatedUser);
    }

    /**
     * GET  /users : get all the users.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of users in body
     */
    @GetMapping("/users")
    @Timed
    public ResponseEntity<List<UserDTO>> getUsers(@ApiParam String q, @ApiParam Pageable pageable) throws IOException {
        log.debug("REST request to get a page of Users");

        // TODO fix
        Page<UserDTO> page = userService.search(q, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * @return a string list of the all of the roles
     */
    @GetMapping("/users/authorities")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public List<String> getAuthorities() {
        return userService.getAuthorities();
    }

    /**
     * GET  /users/:login : get the "login" user.
     *
     * @param login the login of the user to find
     * @return the ResponseEntity with status 200 (OK) and with body the "login" user, or with status 404 (Not Found)
     */
    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    @Timed
    public ResponseEntity<UserDTO> getUser(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        return ResponseUtil.wrapOrNotFound(
            userService.getUserWithAuthoritiesByLoginAndAppAccount(login)
                .map(userMapper::toDto));
    }

    /**
     * GET  /users/:login : get the "login" user.
     *
     * @return the ResponseEntity with status 200 (OK) and with body the "login" user, or with status 404 (Not Found)
     */
    @GetMapping("/users/current")
    @Timed
    public ResponseEntity<UserDTO> getCurrentUser() {
        log.debug("REST request to get current user");
        return ResponseUtil.wrapOrNotFound(
            userService.getCurrentUser()
                .map(userMapper::toDto));
    }

    /**
     * GET  /users/:id : get the "id" user.
     *
     * @param id the id of the userDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the userDTO, or with status 404 (Not Found)
     */
    @GetMapping("/users/{id}")
    @Timed
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        log.debug("REST request to get User : {}", id);
        UserDTO userDTO = userService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(userDTO));
    }

    /**
     * DELETE /users/:login : delete the "login" User.
     *
     * @param id the login of the user to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/users/{id:" + Constants.LOGIN_REGEX + "}")
    @Timed
    //@Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<UserDTO> deleteUser(@PathVariable Long id) {
        log.debug("REST request to delete User: {}", id);
        userService.deleteUser(id);

        UserDTO result = new UserDTO();
        result.setId(id);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString()))
            .body(result);
    }
}
