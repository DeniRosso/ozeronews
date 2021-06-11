package com.example.ozeronews.service;

import com.example.ozeronews.exception.OAuth2AuthenticationProcessingException;
import com.example.ozeronews.models.AuthProvider;
import com.example.ozeronews.models.Role;
import com.example.ozeronews.models.User;
import com.example.ozeronews.repo.UserRepo;
import com.example.ozeronews.repo.UserRepository;
import com.example.ozeronews.security.UserPrincipal;
import com.example.ozeronews.security.oauth2.user.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.UUID;

@Service
public class UserService {

    private UserRepository userRepository;
    private UserRepo userRepo;
    private MailSenderService mailSenderService;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserRepo userRepo,
                       MailSenderService mailSenderService) {
        this.userRepository = userRepository;
        this.userRepo = userRepo;
        this.mailSenderService = mailSenderService;
    }

    @Value("${upload.path}")
    private String uploadPath;

    public void createFirstUser() {

        ZonedDateTime dateStamp = ZonedDateTime.of(LocalDateTime.parse("2019-04-21 09:41:12.789",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
                ZoneId.of("Europe/Moscow"));

        User user = new User();
        user.setId(1L);
        user.setUsername("Admin");
        user.setEmail("ozero.newsmedia@gmail.com");
        user.setPassword(new BCryptPasswordEncoder().encode("12"));
        user.setRole(Collections.singleton(Role.ADMIN));
        user.setAuthProvider(Collections.singleton(AuthProvider.LOCAL));
        user.setActive(true);
        user.setDateStamp(dateStamp);
        userRepo.save(user);
    }

    public void saveNewUser(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setPassword2(null);
        user.setRole(Collections.singleton(Role.USER));
        user.setAuthProvider(Collections.singleton(AuthProvider.LOCAL));
        user.setActive(true);
        user.setDateStamp(ZonedDateTime.now(ZoneId.of("UTC")));
        userRepo.save(user);
    }

    public void editUser(User currentUser, User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setPassword2(null);
        user.setRole(currentUser.getRole());
        user.setAuthProvider(currentUser.getAuthProvider());
        user.setActive(currentUser.isActive());
        user.setDateStamp(ZonedDateTime.now(ZoneId.of("UTC")));
        System.out.println("edit user = " + user);
        userRepo.save(user);
    }

    public OAuth2User processOAuth2User(UserInfo userInfo, String authProvider) {
        return processAuthorizationUser(userInfo, authProvider);
    }

    public OidcUser processOidcUser(UserInfo userInfo, String authProvider) {
        return processAuthorizationUser(userInfo, authProvider);
    }

    public UserPrincipal processAuthorizationUser(UserInfo userInfo, String authProvider) {

        // Проверка существования email у авторизованного пользователя
        if (StringUtils.isEmpty(userInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        // Проверка существования пользователя с email авторизованного пользователя
        User user = userRepo.findByEmail(userInfo.getEmail());

        if(user != null) {
            if(!user.getAuthProvider().stream().findFirst().get().equals(AuthProvider.valueOf(authProvider))) {
                System.err.println("Looks like you're signed up with " + user.getAuthProvider() + " account. " +
                        "Please use your " + user.getAuthProvider() + " account to login.");
                throw new OAuth2AuthenticationProcessingException(
                        "Looks like you're signed up with " + user.getAuthProvider() + " account. " +
                                " Please use your " + user.getAuthProvider() + " account to login.");
            }
            user = updateAuthorizationUser(userInfo, user);
        } else {
            user = createAuthorizationUser(userInfo, authProvider);
        }
        return UserPrincipal.create(user);
    }

    private User createAuthorizationUser(UserInfo userInfo, String authProvider) {
        User user = new User();
        user.setUsername(userInfo.getName());
        user.setEmail(userInfo.getEmail());
//        user.setPassword(passwordEncoder.encode(authProvider));
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setAvatar(userInfo.getPicture());
        user.setRole(Collections.singleton(Role.USER));
        user.setAuthProvider(Collections.singleton(AuthProvider.valueOf(authProvider)));
        user.setActive(true);
        user.setDateStamp(ZonedDateTime.now(ZoneId.of("UTC")));
        return userRepo.save(user);
    }

    private User updateAuthorizationUser(UserInfo userInfo, User user) {
        user.setUsername(userInfo.getName());
        user.setAvatar(userInfo.getPicture());
        user.setActive(true);
        return userRepo.save(user);
    }

    public String addAvatar(MultipartFile file) throws IOException {
        String resultFilename = null;

        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);
//            File uploadDir = new File("/pictures");
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            resultFilename = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFilename));
//            file.transferTo(new File(resultFilename));
        }

//        if (file != null && !file.getOriginalFilename().isEmpty()) {
//            String uuidFile = UUID.randomUUID().toString();
//            resultFilename = uuidFile + "." + file.getOriginalFilename();
//            file.transferTo(new File("src/" + resultFilename));
//        }
        return resultFilename;
    }

    public boolean activateUser(String activateCode) {
        User user = userRepo.findByActivationCode(activateCode);
        if (user == null) {
            return false;
        }
        user.setActivationCode(null);
        userRepo.save(user);
        return true;
    }

    public boolean recoveryUser(User recoveryUser, String recoveryCode) {
        Iterable<User> users = userRepository.findByRecoveryCode(recoveryCode);
        for (User user : users) {
            user.setPassword(new BCryptPasswordEncoder().encode(recoveryUser.getPassword()));
            user.setRecoveryCode(null);
            user.setActive(true);
            user.setDateStamp(ZonedDateTime.now(ZoneId.of("UTC")));
            userRepository.updatePassword(user);
            return true;
        }
        return false;
    }

    public boolean changePassword(User user, String password) {
        // Отправить письмо
        if (!mailSenderService.mailNewPassword(user)) {
            return false;
        }
        // Сохранить новый пароль
//        user.setPassword(passwordEncoder.encode(password));
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setDateStamp(ZonedDateTime.now());
        userRepo.save(user);
        return true;
    }

    public void processOAuthPostLogin(String username) {
        User existUser = userRepo.findByEmail(username);

        if (existUser == null) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setAuthProvider(Collections.singleton((AuthProvider.GOOGLE)));
            newUser.setActive(true);
            System.out.println("newUser = " + newUser);
//            userRepository.save(newUser);
        }

    }
}
