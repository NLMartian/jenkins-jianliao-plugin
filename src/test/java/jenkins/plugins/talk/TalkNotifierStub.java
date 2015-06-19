package jenkins.plugins.talk;

public class TalkNotifierStub extends TalkNotifier {

    public TalkNotifierStub(String teamDomain, String authToken, String room, String buildServerUrl,
                            String sendAs, boolean startNotification, boolean notifyAborted, boolean notifyFailure,
                            boolean notifyNotBuilt, boolean notifySuccess, boolean notifyUnstable, boolean notifyBackToNormal,
                            boolean notifyRepeatedFailure, boolean includeTestSummary, boolean showCommitList,
                            boolean includeCustomMessage, String customMessage) {
        super(authToken, buildServerUrl, sendAs, startNotification, notifyAborted, notifyFailure,
                notifyNotBuilt, notifySuccess, notifyUnstable, notifyBackToNormal, notifyRepeatedFailure,
                includeTestSummary, showCommitList);
    }

    public static class DescriptorImplStub extends TalkNotifier.DescriptorImpl {

        private TalkService talkService;

        @Override
        public synchronized void load() {
        }

        @Override
        TalkService getTalkService(final String authToken) {
            return talkService;
        }

        public void setTalkService(TalkService talkService) {
            this.talkService = talkService;
        }
    }
}
