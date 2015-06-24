package jenkins.plugins.talk;

public class StandardTalkServiceStub extends StandardTalkService {

    private HttpClientStub httpClientStub;

    public StandardTalkServiceStub(String token) {
        super(token);
    }

    @Override
    public HttpClientStub getHttpClient() {
        return httpClientStub;
    }

    public void setHttpClient(HttpClientStub httpClientStub) {
        this.httpClientStub = httpClientStub;
    }
}
