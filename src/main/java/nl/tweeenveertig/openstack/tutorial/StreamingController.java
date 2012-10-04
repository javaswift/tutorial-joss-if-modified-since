package nl.tweeenveertig.openstack.tutorial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.tweeenveertig.openstack.client.Account;
import nl.tweeenveertig.openstack.client.Container;
import nl.tweeenveertig.openstack.client.StoredObject;
import nl.tweeenveertig.openstack.command.core.CommandException;
import nl.tweeenveertig.openstack.headers.object.conditional.IfModifiedSince;
import nl.tweeenveertig.openstack.model.DownloadInstructions;
import org.apache.http.HttpStatus;
import org.apache.http.impl.cookie.DateParseException;
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
    public void downloadContent(@PathVariable String objectName, HttpServletRequest request, HttpServletResponse response) throws IOException, DateParseException {

        String sinceDate = request.getHeader("If-Modified-Since");

        Container container = getTutorialContainer();
        StoredObject storedObject = container.getObject(objectName);

        response.addHeader("Last-Modified", storedObject.getLastModified());
        response.setContentType(storedObject.getContentType());

        if (storedObject.exists()) {
            streamObject(storedObject, response, sinceDate);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void streamObject(StoredObject storedObject, HttpServletResponse response, String sinceDate) throws IOException, DateParseException {

        try {

            DownloadInstructions downloadInstructions =
                new DownloadInstructions().setSinceConditional(new IfModifiedSince(sinceDate));

            InputStream dataStream = storedObject.downloadObjectAsInputStream(downloadInstructions);

            OutputStream responseStream = null;
            try {
                FileCopyUtils.copy(dataStream, response.getOutputStream());
            } finally {
                if (responseStream != null) { responseStream.close(); }
            }

        } catch (CommandException err) {
            if (HttpStatus.SC_NOT_MODIFIED != err.getHttpStatusCode()) {
                throw err;
            }
            response.setStatus(HttpStatus.SC_NOT_MODIFIED);
        }

    }

}
