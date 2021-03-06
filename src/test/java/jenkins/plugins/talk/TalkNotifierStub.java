package jenkins.plugins.talk;

public class TalkNotifierStub extends TalkNotifier {

    public TalkNotifierStub(String teamDomain, String authToken, String room, String buildServerUrl,
                            String sendAs, boolean startNotification, boolean notifyAborted, boolean notifyFailure,
                            boolean notifyNotBuilt, boolean notifySuccess, boolean notifyUnstable, boolean notifyBackToNormal,
                            boolean notifyRepeatedFailure, boolean includeTestSummary, boolean showCommitList,
                            boolean includeCustomMessage, String customMessage) {
        super(teamDomain, authToken, room, buildServerUrl, sendAs, startNotification, notifyAborted, notifyFailure,
                notifyNotBuilt, notifySuccess, notifyUnstable, notifyBackToNormal, notifyRepeatedFailure,
                includeTestSummary, showCommitList, includeCustomMessage, customMessage);
    }

    public static class DescriptorImplStub extends TalkNotifier.DescriptorImpl {

        private TalkService slackService;

        @Override
        public synchronized void load() {
        }

        @Override
        TalkService getSlackService(final String teamDomain, final String authToken, final String room) {
            return slackService;
        }

        public void setSlackService(TalkService slackService) {
            this.slackService = slackService;
        }
    }
}
