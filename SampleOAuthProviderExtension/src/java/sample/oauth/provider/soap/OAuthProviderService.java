package sample.oauth.provider.soap;

import com.zimbra.soap.DocumentDispatcher;
import com.zimbra.soap.DocumentService;

/**
 */
public class OAuthProviderService implements DocumentService {

    @Override
    public void registerHandlers(DocumentDispatcher dispatcher) {
        dispatcher.registerHandler(GetOAuthConsumers.GET_OAUTH_CONSUMERS_REQUEST, new GetOAuthConsumers());
    }
}
