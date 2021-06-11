package com.example.ozeronews.service;

import com.example.ozeronews.models.Contact;
import com.example.ozeronews.models.User;
import com.example.ozeronews.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepo userRepo;

    @Value("${website.baseURL}")
    private String websiteBaseURL;

    @Value("${website.name}")
    private String websiteName;

    @Value("${spring.mail.username}")
    private String username;

    public boolean sendMail(String emailTo, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(username);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);

        return true;
    }

    public boolean mailActivateUser(User user) {
        user.setActivationCode(UUID.randomUUID().toString());

        String subject = "Активация пользователя";
        String message = String.format(
            "Здравствуйте Пользователь %s, \n\n" +
            "Вы получили это письмо, т.к была поизведена регистрация на сайте " + websiteName + ".\n" +
            "Проигнорируйте это письмо, если вы этого не делали. \n\n" +
            "Для завершения регистрации пройдите по ссылке: " + websiteBaseURL + "activate/%s " + "\n\n" +
            "С уважением, команда сайта " + websiteName + ".",
            user.getUsername(),
            user.getActivationCode()
        );
        return sendMail(user.getEmail(), subject, message);
    }

    public boolean mailNewPassword(User user) {
        String subject = "Изменение пароля";
        String message = String.format(
                "Здравствуйте Пользователь %s, \n\n" +
                "Вы получили это письмо, т.к на сайте " + websiteName + " был измеен пароль учетной записи.\n" +
                "Если Вы этого не делали, перейдите на сайт: " + websiteBaseURL + "recovery/ " +
                "измените текущий пароль пользователя.\n\n" +
                "С уважением, команда сайта " + websiteName + ".",
                user.getUsername()
        );
        return sendMail(user.getEmail(), subject, message);
    }

    public boolean mailRecovery(User user) {
        String subject = "Восстановление пользователя";
        String message = String.format(
            "Здравствуйте Пользователь %s, \n\n" +
            "Вы получили это письмо, т.к на сайте " + websiteName + " был выполнен запрос на восстановления пароля.\n" +
            "Если Вы не запрашивали восстановление пароля, проигнорируйте это письмо.\n\n" +
            "Для восстановления пароля перейдите по ссылке: " + websiteBaseURL + "recovery/%s.\n\n" +
            "С уважением, команда сайта " + websiteName + ".",
            user.getUsername(),
            user.getRecoveryCode()
        );
        return sendMail(user.getEmail(), subject, message);
    }

    public boolean mailContact(Contact contact) {

        String message = String.format(
            "Сообщение с сайта " + websiteName + " :\n\n" +
                "%s\n\n" +
                "From: %s\n" +
                "Email: %s\n",
            contact.getText(),
            contact.getName(),
            contact.getEmail()
        );
        boolean status = sendMail(username, contact.getSubject(), message);
        return status;
    }


}
