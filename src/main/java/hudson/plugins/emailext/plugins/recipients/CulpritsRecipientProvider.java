/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hudson.plugins.emailext.plugins.recipients;

import hudson.plugins.emailext.EmailRecipientUtils;
import hudson.plugins.emailext.plugins.RecipientProviderDescriptor;
import hudson.plugins.emailext.plugins.RecipientProvider;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.User;
import hudson.plugins.emailext.ExtendedEmailPublisherContext;
import hudson.plugins.emailext.ExtendedEmailPublisherDescriptor;
import java.util.Set;
import javax.mail.internet.InternetAddress;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 *
 * @author acearl
 */

public class CulpritsRecipientProvider extends RecipientProvider {
    
    @DataBoundConstructor
    public CulpritsRecipientProvider() {
        
    }
    
    @Override
    public void addRecipients(ExtendedEmailPublisherContext context, EnvVars env, Set<InternetAddress> to, Set<InternetAddress> cc) {
    ExtendedEmailPublisherDescriptor descriptor = Jenkins.getInstance().getDescriptorByType(ExtendedEmailPublisherDescriptor.class);
        Set<User> users = context.getBuild().getCulprits();

        for (User user : users) {
            if (!EmailRecipientUtils.isExcludedRecipient(user, context.getListener())) {
                String userAddress = EmailRecipientUtils.getUserConfiguredEmail(user);
                if (userAddress != null) {
                    descriptor.debug(context.getListener().getLogger(), "Adding user address %s, they were not considered an excluded committer", userAddress);
                    EmailRecipientUtils.addAddressesFromRecipientList(to, cc, userAddress, env, context.getListener());
                } else {
                    context.getListener().getLogger().println("Failed to send e-mail to " + user.getFullName() + " because no e-mail address is known, and no default e-mail domain is configured");
                }
            }
        }
    }
    
    @Extension
    public static final class DescriptorImpl extends RecipientProviderDescriptor {
        
        @Override
        public String getDisplayName() {
            return "Culprits";
        }
        
    }
    
}
