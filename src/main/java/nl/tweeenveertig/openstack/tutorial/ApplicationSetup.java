package nl.tweeenveertig.openstack.tutorial;

import java.io.IOException;

import javax.annotation.PostConstruct;

import nl.tweeenveertig.openstack.client.Account;
import nl.tweeenveertig.openstack.client.Container;
import nl.tweeenveertig.openstack.client.StoredObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Singleton component that sets up the data store upon startup.
 *
 * @author <a href="mailto:oscar.westra@42.nl">Oscar Westra van Holthe - Kind</a>
 */
@Component
public class ApplicationSetup {

    /**
     * Our storage provider.
     */
    @Autowired
    private StorageProvider storageProvider;

    @PostConstruct
    public void initializeStorage() throws IOException {

        Account account = storageProvider.getAccount();
        Container container = account.getContainer(StorageProvider.TUTORIAL_CONTAINER);
        if (container.exists()) {
            emptyContainer(container);
        } else {
            container.create();
        }
        if (container.isPublic()) {
            container.makePrivate();
        }

        addInitialContent(container);
    }

    private void emptyContainer(Container container) {

        for (StoredObject storedObject : container.listObjects()) {
            storedObject.delete();
        }
    }

    private void addInitialContent(Container container) throws IOException {

        StoredObject object = container.getObject(StorageProvider.USE_CASE_1_OBJECT);
        object.uploadObject(getClass().getResourceAsStream(StorageProvider.USE_CASE_1_RESOURCE));
    }
}
