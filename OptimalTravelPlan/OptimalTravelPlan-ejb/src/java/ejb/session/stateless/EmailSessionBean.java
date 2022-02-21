package ejb.session.stateless;

import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import util.email.EmailManager;

@Stateless

public class EmailSessionBean implements EmailSessionBeanLocal {

    private final String FROM_EMAIL_ADDRESS = "xxx <xxx@gmail.com>";
    private final String GMAIL_USERNAME = "xxx@gmail.com";
    private final String GMAIL_PASSWORD = "xxx";

    @Override
    public Boolean emailCheckoutNotificationSync(String content, String toEmailAddress) {
        EmailManager emailManager = new EmailManager(GMAIL_USERNAME, GMAIL_PASSWORD);
        Boolean result = emailManager.emailCheckoutNotification(content, FROM_EMAIL_ADDRESS, toEmailAddress);

        return result;
    }

    @Asynchronous
    @Override
    public Future<Boolean> emailCheckoutNotificationAsync(String content, String toEmailAddress) throws InterruptedException {
        EmailManager emailManager = new EmailManager(GMAIL_USERNAME, GMAIL_PASSWORD);
        Boolean result = emailManager.emailCheckoutNotification(content, FROM_EMAIL_ADDRESS, toEmailAddress);

        return new AsyncResult<>(result);
    }
}
