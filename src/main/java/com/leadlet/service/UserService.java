package com.leadlet.service;

import com.leadlet.config.Constants;
import com.leadlet.domain.AppAccount;
import com.leadlet.domain.Authority;
import com.leadlet.domain.User;
import com.leadlet.repository.AppAccountRepository;
import com.leadlet.repository.AuthorityRepository;
import com.leadlet.repository.UserRepository;
import com.leadlet.security.AuthoritiesConstants;
import com.leadlet.security.SecurityUtils;
import com.leadlet.service.dto.UserDTO;
import com.leadlet.service.dto.UserUpdateDTO;
import com.leadlet.service.mapper.UserMapper;
import com.leadlet.service.util.RandomUtil;
import com.leadlet.web.rest.vm.ManagedUserVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    private final AppAccountRepository appAccountRepository;

    private final UserMapper userMapper;

    private final ElasticsearchService elasticsearchService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository,
                       AppAccountRepository appAccountRepository, UserMapper userMapper,
                       ElasticsearchService elasticsearchService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.appAccountRepository = appAccountRepository;
        this.userMapper = userMapper;
        this.elasticsearchService = elasticsearchService;
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                log.debug("Activated user: {}", user);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
       log.debug("Reset user password for reset key {}", key);

       return userRepository.findOneByResetKey(key)
           .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400)))
           .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                return user;
           });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findOneByLogin(mail)
            .filter(User::getActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                return user;
            });
    }

    public User createAccountWithUser(String login, String password ) {

        // Create AppAccount
        AppAccount newAppAccount = new AppAccount();
        newAppAccount = appAccountRepository.save(newAppAccount);

        User newUser = new User();
        Authority authority = authorityRepository.findOne(AuthoritiesConstants.MANAGER);
        Set<Authority> authorities = new HashSet<>();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(login);
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        authorities.add(authority);
        newUser.setAuthorities(authorities);
        newUser.setAppAccount(newAppAccount);
        newUser = userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);

        // TODO not set activated true
        newUser.setActivated(true);

        newUser = userRepository.save(newUser);

        return newUser;
    }

    public User createUser(ManagedUserVM userDTO) {

        User user = new User();
        user.setLogin(userDTO.getLogin());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey("en"); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = new HashSet<>();
            userDTO.getAuthorities().forEach(
                authority -> authorities.add(authorityRepository.findOne(authority.getName()))
            );
            user.setAuthorities(authorities);
        }
        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        user.setAppAccount(SecurityUtils.getCurrentUserAppAccountReference());
        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user
     * @param lastName last name of user
     * @param langKey language key
     * @param imageUrl image URL of user
     */
    public void updateUser(String firstName, String lastName, String langKey, String imageUrl) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(user -> {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setLangKey(langKey);
            user.setImageUrl(imageUrl);
            log.debug("Changed Information for User: {}", user);
        });
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update
     * @return updated user
     */
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return Optional.of(userRepository
            .findOneByIdAndAppAccount_Id(userDTO.getId(), SecurityUtils.getCurrentUserAppAccountId()))
            .map(user -> {
                user.setLogin(userDTO.getLogin());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                user.setImageUrl(userDTO.getImageUrl());
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());
                Set<Authority> managedAuthorities = user.getAuthorities();
                managedAuthorities.clear();
                userDTO.getAuthorities().forEach(
                    authority -> managedAuthorities.add(authorityRepository.findOne(authority.getName()))
                );
                log.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(userMapper::toDto);
    }

    public Optional<UserUpdateDTO> updateUser(UserUpdateDTO userUpdateDTO) {
        return Optional.of(userRepository
            .findOneByIdAndAppAccount_Id(userUpdateDTO.getId(), SecurityUtils.getCurrentUserAppAccountId()))
            .map(user -> {
                user.setFirstName(userUpdateDTO.getFirstName());
                user.setLastName(userUpdateDTO.getLastName());
                user.setPhone(userUpdateDTO.getPhone());
                if(userUpdateDTO.getPassword() != null){
                    user.setPassword( passwordEncoder.encode(userUpdateDTO.getPassword()));
                }else{
                    user.setPassword(user.getPassword());
                }

                return user;
            })
            .map(UserUpdateDTO::new);
    }

    public void deleteUser(Long id) {
        User user =
        userRepository.findOneByIdAndAppAccount_Id(id,SecurityUtils.getCurrentUserAppAccountId());

        if(user != null){
            userRepository.delete(id);
            log.debug("Deleted User: {}", user);
        }
    }



    public void changePassword(String password) {
        userRepository.findOneByLoginAndAppAccount_Id(SecurityUtils.getCurrentUserLogin(),SecurityUtils.getCurrentUserAppAccountId()).ifPresent(user -> {
            String encryptedPassword = passwordEncoder.encode(password);
            user.setPassword(encryptedPassword);
            log.debug("Changed password for User: {}", user);
        });
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAllByLoginNotAndAppAccount_Id(pageable, Constants.ANONYMOUS_USER,
            SecurityUtils.getCurrentUserAppAccountId()).map(userMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLoginAndAppAccount(String login) {
        return userRepository.findOneWithAuthoritiesByLoginAndAppAccount_Id(login, SecurityUtils.getCurrentUserAppAccountId());
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities(Long id) {
        return userRepository.findOneWithAuthoritiesById(id);
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        User user = userRepository.findOneWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin()).orElse(null);
        if( user!=null )
            user.getAppAccount().getId();
        return user;
    }


    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS));
        for (User user : users) {
            log.debug("Deleting not activated user {}", user.getLogin());
            userRepository.delete(user);
        }
    }

    /**
     * @return a list of all the authorities
     */
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }


    public Optional<User> getCurrentUser() {

        String login = SecurityUtils.getCurrentUserLogin();

        Optional<User> user = userRepository.findOneByLogin(login);

        user.get().getAuthorities().size();
        user.get().getAppAccount().getId();

        return user;
    }

    /**
     * Get one user by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public UserDTO findOne(Long id) {
        log.debug("Request to get User : {}", id);
        User user = userRepository.findOneByIdAndAppAccount_Id(id, SecurityUtils.getCurrentUserAppAccountId());
        return userMapper.toDto(user);
    }

    public Page<UserDTO> search(String searchQuery, Pageable pageable) throws IOException {
        String appAccountFilter = "app_account_id:" + SecurityUtils.getCurrentUserAppAccountId();
        if(StringUtils.isEmpty(searchQuery)){
            searchQuery = appAccountFilter;
        }else {
            searchQuery += " AND " + appAccountFilter;
        }

        Pair<List<Long>, Long> response = elasticsearchService.getEntityIds("leadlet-user", searchQuery, pageable);

        Page<UserDTO> deals = new PageImpl<UserDTO>(userRepository.findAllByIdIn(response.getFirst()).stream()
            .map(userMapper::toDto).collect(Collectors.toList()),
            pageable,
            response.getSecond());


        List<UserDTO> unsorted = userRepository.findAllByIdIn(response.getFirst()).stream()
            .map(userMapper::toDto).collect(Collectors.toList());
        List<Long> sortedIds = response.getFirst();

        // we are getting ids from ES sorted but JPA returns result not sorted
        // below code-piece sorts the returned DTOs to have same sort with ids.
        Collections.sort(unsorted,  new Comparator<UserDTO>() {
            public int compare(UserDTO left, UserDTO right) {
                return Integer.compare(sortedIds.indexOf(left.getId()), sortedIds.indexOf(right.getId()));
            }
        } );

        Page<UserDTO> dealDTOS = new PageImpl<UserDTO>(unsorted,
            pageable,
            response.getSecond());

        return dealDTOS;

    }
}
