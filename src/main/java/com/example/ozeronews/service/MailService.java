package com.example.ozeronews.service;

import com.example.ozeronews.config.AppConfig;
import com.example.ozeronews.models.Contact;
import com.example.ozeronews.models.Head;
import com.example.ozeronews.models.User;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.UUID;

@Service
public class MailService {

    private static final String EMAIL_CONFIRM = "mail/confirm";

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private TemplateEngine htmlTemplateEngine;

    @Autowired
    private AppConfig appConfig;

    // Send activation user mail
    public boolean activateUser(User user, final Locale locale) throws MessagingException {

        Head head = appConfig.getHead();
        user.setActivationCode(UUID.randomUUID().toString());

        // Prepare the evaluation context
        final Context ctx = new Context(locale);

        ctx.setVariable("websiteURL", head.getWebsiteURL());
        ctx.setVariable("logo", "image");
        ctx.setVariable("username", user.getUsername());
        ctx.setVariable("title", "Активация пользователя");
        ctx.setVariable("text",
                "Вы создали учетную запись на сайте " + head.getWebsiteName() +"." +
                        " Для завершения регистрации Вам осталось активировать свою учетную запись.");
        ctx.setVariable("button", "Активировать мою учетную запись");
        ctx.setVariable("link", head.getWebsiteURL() + "activate/" + user.getActivationCode());

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        message.setSubject("Активация пользователя");
        message.setFrom(head.getEmailHelp());
        message.setTo(user.getEmail());

        // Create the HTML body using Thymeleaf
        final String htmlContent = this.htmlTemplateEngine.process(EMAIL_CONFIRM, ctx);
        message.setText(htmlContent, true /* isHtml */);

        // Add the inline image, referenced from the HTML code as "cid:${imageResourceName}"
        try {
            InputStream logoFileStream = resourceLoader.getResource(head.getWebsiteLogo()).getInputStream();
            final InputStreamSource imageSource = new ByteArrayResource(IOUtils.toByteArray(logoFileStream));
            message.addInline("image", imageSource, "image/png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.javaMailSender.send(mimeMessage);
        return true;
    }

    // Send confirm new password mail
    public boolean changePassword(User user, final Locale locale) throws MessagingException {

        Head head = appConfig.getHead();

        // Prepare the evaluation context
        final Context ctx = new Context(locale);

        ctx.setVariable("websiteURL", head.getWebsiteURL());
        ctx.setVariable("logo", "image");
        ctx.setVariable("username", user.getUsername());
        ctx.setVariable("title", "Изменение пароля");
        ctx.setVariable("text",
                "Вы получили это письмо, т.к. на сайте " + head.getWebsiteName() +
                        " был изменен пароль для учетной записи.");
        ctx.setVariable("button", "Перейтина сайт");
        ctx.setVariable("link", head.getWebsiteURL());

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        message.setSubject("Изменение пароля");
        message.setFrom(head.getEmailHelp());
        message.setTo(user.getEmail());

        // Create the HTML body using Thymeleaf
        final String htmlContent = this.htmlTemplateEngine.process(EMAIL_CONFIRM, ctx);
        message.setText(htmlContent, true /* isHtml */);

        // Add the inline image, referenced from the HTML code as "cid:${imageResourceName}"
        try {
            InputStream logoFileStream = resourceLoader.getResource(head.getWebsiteLogo()).getInputStream();
            final InputStreamSource imageSource = new ByteArrayResource(IOUtils.toByteArray(logoFileStream));
            message.addInline("image", imageSource, "image/png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.javaMailSender.send(mimeMessage);
        return true;
    }

    // Send recovery user mail
    public boolean recoveryUser(User user, final Locale locale) throws MessagingException {

        Head head = appConfig.getHead();

        // Prepare the evaluation context
        final Context ctx = new Context(locale);

        ctx.setVariable("websiteURL", head.getWebsiteURL());
        ctx.setVariable("logo", "image");
        ctx.setVariable("username", user.getUsername());
        ctx.setVariable("title", "Восстановление пользователя");
        ctx.setVariable("text",
                "Вы выполнили запрос восстановления пароля на сайте " + head.getWebsiteName() + "." +
                        " Для восстановления пароля перейдите на сайт:");
        ctx.setVariable("button", "Восстановить учетную запись");
        ctx.setVariable("link", head.getWebsiteURL() + "recovery/" + user.getRecoveryCode());

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        message.setSubject("Восстановление пользователя");
        message.setFrom(head.getEmailHelp());
        message.setTo(user.getEmail());

        // Create the HTML body using Thymeleaf
        final String htmlContent = this.htmlTemplateEngine.process(EMAIL_CONFIRM, ctx);
        message.setText(htmlContent, true /* isHtml */);

        // Add the inline image, referenced from the HTML code as "cid:${imageResourceName}"
        try {
            InputStream logoFileStream = resourceLoader.getResource(head.getWebsiteLogo()).getInputStream();
            final InputStreamSource imageSource = new ByteArrayResource(IOUtils.toByteArray(logoFileStream));
            message.addInline("image", imageSource, "image/png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.javaMailSender.send(mimeMessage);
        return true;
    }

    // Send contact mail from user
    public boolean contact(Contact contact, final Locale locale) throws MessagingException {

        Head head = appConfig.getHead();

        // Prepare the evaluation context
        final Context ctx = new Context(locale);

        ctx.setVariable("websiteURL", head.getWebsiteURL());
        ctx.setVariable("logo", "image");
        ctx.setVariable("username", "");
        ctx.setVariable("title", "Сообщение от " + contact.getName());
        ctx.setVariable("text",
                "Тема сообщения: " + contact.getSubject() + "\n\n" +
                contact.getText() + "\n\n" +
                "От: " + contact.getName() + "\n" +
                "Email: " + contact.getEmail());
        ctx.setVariable("button", "Перейти на сайт");
        ctx.setVariable("link", head.getWebsiteURL());

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        message.setSubject("Сообщение от " + contact.getEmail());
        message.setFrom(head.getEmailHelp());
        message.setTo(head.getEmailHelp());

        // Create the HTML body using Thymeleaf
        final String htmlContent = this.htmlTemplateEngine.process(EMAIL_CONFIRM, ctx);
        message.setText(htmlContent, true /* isHtml */);

        // Add the inline image, referenced from the HTML code as "cid:${imageResourceName}"
        try {
            InputStream logoFileStream = resourceLoader.getResource(head.getWebsiteLogo()).getInputStream();
            final InputStreamSource imageSource = new ByteArrayResource(IOUtils.toByteArray(logoFileStream));
            message.addInline("image", imageSource, "image/png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.javaMailSender.send(mimeMessage);
        return true;
    }


//    private byte [] getImageToByByte() throws IOException {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
//
//        File file = new File("classpath:static/images/logo.png");
//        System.out.println("file = " + file);
//
//
//
//        InputStream logoFileStream = resourceLoader.getResource("classpath:static/images/logo.png").getInputStream();
//        InputStreamSource byteArrayResource = new ByteArrayResource(org.apache.commons.io.IOUtils.toByteArray(logoFileStream));
//        System.out.println("byteArrayResource = " + byteArrayResource);
//
//
//
//        BufferedImage image = null; //ImageIO.read(new File("classpath:static/images/logo.png"));
//
//        // Явно указываем расширение файла для простоты реализации
//        ImageIO.write(image, "png", baos);
//        baos.flush();
//
//        String base64String = Base64.encode(baos.toByteArray());
//        baos.close();
//
//        // декодируем полученную строку в массив байт
//        byte[] resByteArray = Base64.decode(base64String);
//
//        return resByteArray;
//    }
}
