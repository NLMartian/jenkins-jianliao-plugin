package jenkins.plugins.talk;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

import jenkins.model.Jenkins;
import hudson.ProxyConfiguration;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

public class StandardTalkService implements TalkService {

    private static final Logger logger = Logger.getLogger(StandardTalkService.class.getName());

    private String webHook;

    public StandardTalkService(String hookUrl) {
        super();
        this.webHook = hookUrl;
    }

    public boolean publish(String message) {
        return publish(message, null);
    }

    public boolean publish(String message, String openLink) {
        boolean result = true;
        String url = webHook;
        logger.info("Posting: to talk.ai using " + url +": " + message + " link:" + openLink);
        HttpClient client = getHttpClient();
        PostMethod post = new PostMethod(url);
        JSONObject json = new JSONObject();

        try {
            json.put("text", message);
            json.put("redirectUrl", openLink);
            post.setRequestBody(json.toString());
            post.setRequestHeader("Content-Type", "application/json");
            int responseCode = client.executeMethod(post);
            String response = post.getResponseBodyAsString();
            if(responseCode != HttpStatus.SC_OK) {
                logger.log(Level.WARNING, "Talk post may have failed. Response: " + response);
                result = false;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error posting to Talk", e);
            result = false;
        } finally {
            logger.info("Posting succeeded");
            post.releaseConnection();
        }
        return result;
    }

    protected HttpClient getHttpClient() {
        HttpClient client = new HttpClient();
        if (Jenkins.getInstance() != null) {
            ProxyConfiguration proxy = Jenkins.getInstance().proxy;
            if (proxy != null) {
                client.getHostConfiguration().setProxy(proxy.name, proxy.port);
                String username = proxy.getUserName();
                String password = proxy.getPassword();
                // Consider it to be passed if username specified. Sufficient?
                if (username != null && !"".equals(username.trim())) {
                    logger.info("Using proxy authentication (user=" + username + ")");
                    // http://hc.apache.org/httpclient-3.x/authentication.html#Proxy_Authentication
                    // and
                    // http://svn.apache.org/viewvc/httpcomponents/oac.hc3x/trunk/src/examples/BasicAuthenticationExample.java?view=markup
                    client.getState().setProxyCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials(username, password));
                }
            }
        }
        return client;
    }

}
