package nl.tweeenveertig.openstack.tutorial;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ResourceBundle;

import eu.medsea.mimeutil.MimeUtil;
import nl.tweeenveertig.openstack.client.Account;
import nl.tweeenveertig.openstack.client.Client;
import nl.tweeenveertig.openstack.client.StoredObject;
import nl.tweeenveertig.openstack.client.impl.ClientImpl;
import nl.tweeenveertig.openstack.client.mock.ClientMock;
import nl.tweeenveertig.openstack.client.mock.MockUserStore;
import org.springframework.stereotype.Service;

/**
 * Service to login to our cloud storage and provide the logged in account.
 *
 * @author <a href="mailto:oscar.westra@42.nl">Oscar Westra van Holthe - Kind</a>
 */
@Service
public class StorageProvider {

    /**
     * The name of the container we'll use for this tutorial.
     */
    public static final String TUTORIAL_CONTAINER = "tutorial-joss-streaming";
    /**
     * The name of the resource to upload for use case 1.
     */
    public static final String USE_CASE_1_RESOURCE = "/Cloud-Computing.jpg";
    /**
     * The name of the stored object for use case 1.
     */
    public static final String USE_CASE_1_OBJECT = "test-object.png";
    /**
     * Buffer size for &quot;MIME magic&quot;, i.e. determining the actual MIME type of the content.
     */
    public static final int MIME_MAGIC_BUFFER_SIZE = 32;
    /**
     * The account (logged in), lazily initialized.
     */
    private Account account;

    /**
     * Create a {@code StorageProvider}.
     */
    public StorageProvider() {

        MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
    }

    /**
     * Get our cloud storage account.
     *
     * @return our account
     */
    public Account getAccount() {

        if (account == null) {

            ResourceBundle credentials = ResourceBundle.getBundle("credentials");
            String tenant = credentials.getString("tenant");
            String username = credentials.getString("username");
            String password = credentials.getString("password");
            String auth_url = credentials.getString("auth_url");
//            account = new ClientImpl().authenticate(tenant, username, password, auth_url);
            account = new ClientMock().allowEveryone().authenticate(tenant, username, password, auth_url);
        }
        return account;
    }
}
