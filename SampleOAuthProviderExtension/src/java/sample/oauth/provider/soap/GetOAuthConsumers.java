package sample.oauth.provider.soap;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.Metadata;
import com.zimbra.cs.mailbox.MetadataList;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;
import net.oauth.OAuthConsumer;
import org.dom4j.QName;
import sample.oauth.provider.core.SampleZmOAuthProvider;

import java.util.List;
import java.util.Map;

/**
 */
public class GetOAuthConsumers extends DocumentHandler {

    public static final QName GET_OAUTH_CONSUMERS_REQUEST =
            QName.get("GetOAuthConsumersRequest", AccountConstants.NAMESPACE);
    public static final QName GET_OAUTH_CONSUMERS_RESPONSE =
            QName.get("GetOAuthConsumersResponse", AccountConstants.NAMESPACE);

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Mailbox mailbox = getRequestedMailbox(zsc);
        List<String> authzedConsumerKeys = null;
        Metadata oAuthConfig = mailbox.getConfig(null, "zwc:oauth");
        if (oAuthConfig != null) {
            MetadataList metadataList = oAuthConfig.getList("authorized_consumers", true);
            if (metadataList != null) {
                authzedConsumerKeys = metadataList.asList();
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
