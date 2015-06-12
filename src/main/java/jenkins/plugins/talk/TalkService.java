package jenkins.plugins.talk;

public interface TalkService {
    boolean publish(String message);

    boolean publish(String message, String openLink);
}
