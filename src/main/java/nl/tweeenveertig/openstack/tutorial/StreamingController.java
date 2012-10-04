package nl.tweeenveertig.openstack.tutorial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import nl.tweeenveertig.openstack.client.Account;
import nl.tweeenveertig.openstack.client.Container;
import nl.tweeenveertig.openstack.client.StoredObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller to demonstrate If-Modified-Since in combination with JOSS.
 */
@Controller
public class StreamingController {

    /**
     * Content type to use when none is given.
     */
    public static final String DEFAULT_BINARY_CONTENT_TYPE = "application/octet-stream";
    /**
     * The storage provider we'll use. This logs in and provides us with the logged in account.
     */
    private StorageProvider storageProvider;

    /**
     * Create a streaming controller.
     *
     * @param storageProvider the storage provider to use
     */
    @Autowired
    public StreamingController(StorageProvider storageProvider) {

        this.storageProvider = storageProvider;
    }

    /**
     * Show the index page.
     *
     * @return a view for the index page
     */
    @RequestMapping("/")
    public ModelAndView showIndexPage() {

        Container container = getTutorialContainer();
        StoredObject useCaseObject = container.getObject(StorageProvider.USE_CASE_1_OBJECT);

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("public_url", useCaseObject.getPublicURL());
        model.put("resource_name", useCaseObject.getName());

        return new ModelAndView("index", model);
    }

    private Container getTutorialContainer() {

        Account account = storageProvider.getAccount();
        return account.getContainer(StorageProvider.TUTORIAL_CONTAINER);
    }

    /**
     * Use case 1: stream content from storage to the browser. This method also helps for the other use cases.
     *
     * @param objectName the name of the object to download
     * @param response   the response to send the data to
     * @throws IOException when streaming the content fails
     */
    @RequestMapping("/download/{objectName:.+}")
    public void downloadContent(@PathVariable String objectName, HttpServletResponse response) throws IOException {

        // Get the object to download.

        Container container = getTutorialContainer();
        StoredObject storedObject = container.getObject(objectName);

        if (storedObject.exists()) {

            streamObject(storedObject, response);
        } else {

            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void streamObject(StoredObject storedObject, HttpServletResponse response) throws IOException {

        // Get the content type and the data stream.

        String contentType = storedObject.getContentType();
        if (contentType == null) {
            contentType = DEFAULT_BINARY_CONTENT_TYPE;
        }
        InputStream dataStream = storedObject.downloadObjectAsInputStream();

        // Stream the data.

        OutputStream responseStream = null;
        try {

            response.setContentType(contentType);
            responseStream = response.getOutputStream();
            FileCopyUtils.copy(dataStream, responseStream);
        } finally {

            if (responseStream != null) {

                responseStream.close();
            }
        }
    }

}
