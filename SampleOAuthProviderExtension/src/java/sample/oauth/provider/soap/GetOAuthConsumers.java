package sample.oauth.provider.soap;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.Metadata;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;
import net.oauth.OAuthConsumer;
import org.dom4j.QName;
import sample.oauth.provider.core.SampleZmOAuthProvider;

import java.util.Map;
import java.util.Set;

/**
 */
public class GetOAuthConsumers extends DocumentHandler {

    public static final QName GET_OAUTH_CONSUMERS_REQUEST =
            QName.get("GetOAuthConsumersRequest", MailConstants.NAMESPACE);
    public static final QName GET_OAUTH_CONSUMERS_RESPONSE =
            QName.get("GetOAuthConsumersResponse", MailConstants.NAMESPACE);

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Mailbox mailbox = getRequestedMailbox(zsc);
        Set<String> authzedConsumerKeys = null;
        Metadata oAuthConfig = mailbox.getConfig(null, "zwc:oauth");
        if (oAuthConfig != null) {
            Metadata metadata = oAuthConfig.getMap("authorized_consumers", true);
            if (metadata != null) {
                authzedConsumerKeys = metadata.asMap().keySet();
            }
        }
        Element response = zsc.createElement(GET_OAUTH_CONSUMERS_RESPONSE);
        if (authzedConsumerKeys != null) {
            for (String key : authzedConsumerKeys) {
                OAuthConsumer consumer;
                try {
                    consumer = SampleZmOAuthProvider.getConsumer(key);
                } catch (Exception e) {
                    throw ServiceException.FAILURE("Unable to find registered OAuth Consumer with key " + key, null);
                }
                response.addElement("oauthConsumer").
                         addAttribute("key", key).
                         addAttribute("desc", (String) consumer.getProperty("description"));
            }
        }
        return response;
    }
}
