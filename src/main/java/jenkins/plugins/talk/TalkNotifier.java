package jenkins.plugins.talk;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import jenkins.model.JenkinsLocationConfiguration;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class TalkNotifier extends Notifier {

    private static final Logger logger = Logger.getLogger(TalkNotifier.class.getName());

    private String webHook;
    private String buildServerUrl;
    private String sendAs;
    private boolean startNotification;
    private boolean notifySuccess;
    private boolean notifyAborted;
    private boolean notifyNotBuilt;
    private boolean notifyUnstable;
    private boolean notifyFailure;
    private boolean notifyBackToNormal;
    private boolean notifyRepeatedFailure;
    private boolean includeTestSummary;
    private boolean showCommitList;

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    public String getWebHook() {
        return webHook;
    }

    public String getBuildServerUrl() {
        if(buildServerUrl == null || buildServerUrl == "") {
            JenkinsLocationConfiguration jenkinsConfig = new JenkinsLocationConfiguration();
            return jenkinsConfig.getUrl();
        }
        else {
            return buildServerUrl;
        }
    }

    public String getSendAs() {
        return sendAs;
    }

    public boolean getStartNotification() {
        return startNotification;
    }

    public boolean getNotifySuccess() {
        return notifySuccess;
    }

    public boolean getShowCommitList() {
        return showCommitList;
    }

    public boolean getNotifyAborted() {
        return notifyAborted;
    }

    public boolean getNotifyFailure() {
        return notifyFailure;
    }

    public boolean getNotifyNotBuilt() {
        return notifyNotBuilt;
    }

    public boolean getNotifyUnstable() {
        return notifyUnstable;
    }

    public boolean getNotifyBackToNormal() {
        return notifyBackToNormal;
    }

    public boolean includeTestSummary() {
        return includeTestSummary;
    }

    public boolean getNotifyRepeatedFailure() {
        return notifyRepeatedFailure;
    }

    @DataBoundConstructor
    public TalkNotifier(final String webHook, final String buildServerUrl,
                        final String sendAs, final boolean startNotification, final boolean notifyAborted, final boolean notifyFailure,
                        final boolean notifyNotBuilt, final boolean notifySuccess, final boolean notifyUnstable, final boolean notifyBackToNormal,
                        final boolean notifyRepeatedFailure, final boolean includeTestSummary, final boolean showCommitList) {
        super();
        this.webHook = webHook;
        this.buildServerUrl = buildServerUrl;
        this.sendAs = sendAs;
        this.startNotification = startNotification;
        this.notifyAborted = notifyAborted;
        this.notifyFailure = notifyFailure;
        this.notifyNotBuilt = notifyNotBuilt;
        this.notifySuccess = notifySuccess;
        this.notifyUnstable = notifyUnstable;
        this.notifyBackToNormal = notifyBackToNormal;
        this.notifyRepeatedFailure = notifyRepeatedFailure;
        this.includeTestSummary = includeTestSummary;
        this.showCommitList = showCommitList;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    public TalkService newTalkService(AbstractBuild r, BuildListener listener) {
        String webHookn = this.webHook;
        if (StringUtils.isEmpty(webHook)) {
            webHook = getDescriptor().getWebHook();
        }

        EnvVars env = null;
        try {
            env = r.getEnvironment(listener);
        } catch (Exception e) {
            listener.getLogger().println("Error retrieving environment vars: " + e.getMessage());
            env = new EnvVars();
        }
        webHook = env.expand(webHook);

        return new StandardTalkService(webHook);
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return true;
    }

    @Override
    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
        if (startNotification) {
            Map<Descriptor<Publisher>, Publisher> map = build.getProject().getPublishersList().toMap();
            for (Publisher publisher : map.values()) {
                if (publisher instanceof TalkNotifier) {
                    logger.info("Invoking Started...");
                    new ActiveNotifier((TalkNotifier) publisher, listener).started(build);
                }
            }
        }
        return super.prebuild(build, listener);
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private String webHook;
        private String buildServerUrl;
        private String sendAs;

        public DescriptorImpl() {
            load();
        }

        public String getWebHook() {
            return webHook;
        }

        public String getBuildServerUrl() {
            if(buildServerUrl == null || buildServerUrl == "") {
                JenkinsLocationConfiguration jenkinsConfig = new JenkinsLocationConfiguration();
                return jenkinsConfig.getUrl();
            }
            else {
                return buildServerUrl;
            }
        }

        public String getSendAs() {
            return sendAs;
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public TalkNotifier newInstance(StaplerRequest sr, JSONObject json) {
            String webHook = sr.getParameter("talkWebHook");
            boolean startNotification = "true".equals(sr.getParameter("talkStartNotification"));
            boolean notifySuccess = "true".equals(sr.getParameter("talkNotifySuccess"));
            boolean notifyAborted = "true".equals(sr.getParameter("talkNotifyAborted"));
            boolean notifyNotBuilt = "true".equals(sr.getParameter("talkNotifyNotBuilt"));
            boolean notifyUnstable = "true".equals(sr.getParameter("talkNotifyUnstable"));
            boolean notifyFailure = "true".equals(sr.getParameter("talkNotifyFailure"));
            boolean notifyBackToNormal = "true".equals(sr.getParameter("talkNotifyBackToNormal"));
            boolean notifyRepeatedFailure = "true".equals(sr.getParameter("talkNotifyRepeatedFailure"));
            boolean includeTestSummary = "true".equals(sr.getParameter("includeTestSummary"));
            boolean showCommitList = "true".equals(sr.getParameter("talkShowCommitList"));
            return new TalkNotifier(webHook, buildServerUrl, sendAs, startNotification, notifyAborted,
                    notifyFailure, notifyNotBuilt, notifySuccess, notifyUnstable, notifyBackToNormal, notifyRepeatedFailure,
                    includeTestSummary, showCommitList);
        }

        @Override
        public boolean configure(StaplerRequest sr, JSONObject formData) throws FormException {
            webHook = sr.getParameter("talkWebHook");
            buildServerUrl = sr.getParameter("talkBuildServerUrl");
            sendAs = sr.getParameter("talkSendAs");
            if(buildServerUrl == null || buildServerUrl == "") {
                JenkinsLocationConfiguration jenkinsConfig = new JenkinsLocationConfiguration();
                buildServerUrl = jenkinsConfig.getUrl();
            }
            if (buildServerUrl != null && !buildServerUrl.endsWith("/")) {
                buildServerUrl = buildServerUrl + "/";
            }
            save();
            return super.configure(sr, formData);
        }

        TalkService getTalkService(final String webHook) {
            return new StandardTalkService(webHook);
        }

        @Override
        public String getDisplayName() {
            return "Jianliao Notifications";
        }

        public FormValidation doTestConnection(@QueryParameter("talkWebHook") final String webHook,
                                               @QueryParameter("talkBuildServerUrl") final String buildServerUrl) throws FormException {
            try {
                String targetUrl = webHook;
                if (StringUtils.isEmpty(targetUrl)) {
                    targetUrl = this.webHook;
                }
                String targetBuildServerUrl = buildServerUrl;
                if (StringUtils.isEmpty(targetBuildServerUrl)) {
                    targetBuildServerUrl = this.buildServerUrl;
                }
                TalkService testTalkService = getTalkService(targetUrl);
                String message = "Jianliao/Jenkins plugin: you're all set on " + targetBuildServerUrl;
                boolean success = testTalkService.publish(message);
                return success ? FormValidation.ok("Success") : FormValidation.error("Failure");
            } catch (Exception e) {
                return FormValidation.error("Client error : " + e.getMessage());
            }
        }
    }
}
