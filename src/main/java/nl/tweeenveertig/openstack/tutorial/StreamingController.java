package nl.tweeenveertig.openstack.tutorial;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.tweeenveertig.openstack.client.Account;
import nl.tweeenveertig.openstack.client.Container;
import nl.tweeenveertig.openstack.client.StoredObject;
import nl.tweeenveertig.openstack.exception.NotModifiedException;
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
    * The storage provider we'll use. This logs in and provides us with the logged in account.
    */
    private StorageProvider storageProvider;

    /**
    * Create a streaming controller.
    * @param storageProvider the storage provider to use
    */
    @Autowired
    public StreamingController(StorageProvider storageProvider) {

        this.storageProvider = storageProvider;
    }

    /**
    * Show the index page.
    * @return a view for the index page
    */
    @RequestMapping("/")
    public ModelAndView showIndexPage() {

        Container container = getTutorialContainer();
        container.makePublic();
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
    * Use case: show case the usage of If-Modified-Since
    *
    * @param objectName the name of the object to download
    * @param response   the response to send the data to
    * @throws IOException when streaming the content fails
    */
    @RequestMapping("/download/{objectName:.+}")
    public void downloadContent(@PathVariable String objectName,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, DateParseException {

        // Read the If-Modified-Since value from the HTTP request
        // and make sure the ObjectStore gets this
        Long ifModifiedSince = request.getDateHeader("If-Modified-Since");

        // Fetch the container and object so that metadata and
        // object are accessible
        Container container = getTutorialContainer();
        StoredObject storedObject = container.getObject(objectName);

        // This is a crucial statement that triggers the browser to
        // send an If-Modified-Since with the next request
        response.setDateHeader("Last-Modified", storedObject.getLastModifiedAsDate().getTime());
        response.setContentType(storedObject.getContentType());

        // Get the object and send the If-Modified-Since object so
        // it can be passed to the Object Store.
        try {
            // Set up the download instructions with the
            // If-Modified-Since header so that the object
            // store can make a judgement call on whether
            // to serve the content.
            DownloadInstructions downloadInstructions =
                new DownloadInstructions()
                    .setSinceConditional(
                        new IfModifiedSince(ifModifiedSince));
            InputStream dataStream =
                    storedObject.downloadObjectAsInputStream(
                        downloadInstructions);

            // The image is changed here, so you can see the last
            // modification date/time over the image. YOU PROBABLY
            // DON'T NEED THIS
            BufferedImage originalImage = ImageIO.read(dataStream);
            BufferedImage watermarkedImage =
                    ImageUtils.placeText(originalImage,
                            storedObject.getLastModified());
            dataStream.close();
            dataStream = createInputStream(watermarkedImage);

            OutputStream responseStream = null;
            try {
                // Copy the stream to the HTTP response
                FileCopyUtils.copy(dataStream,
                        response.getOutputStream());
            } finally {
                if (responseStream != null) {
                    responseStream.close();
                }
            }
        } catch (NotModifiedException err) {
            response.setStatus(HttpStatus.SC_NOT_MODIFIED);
        }
    }

    private InputStream createInputStream(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

}
