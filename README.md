Tutorial: JOSS If-Modified-Since
================================

_UNDER CONSTRUCTION_

This is a working project to demonstrate JOSS for demonstating natural If-Modified-Since when streaming objects through the application layer.


To see this project in action:
------------------------------

1. Checkout the code
2. Put your account information in src/main/resources/credentials.properties
3. Run this command (it starts a container in the foreground):
    mvn jetty:run
4. Point your browser to [http://localhost:8081/](http://localhost:8081/)

To see how you can do all this yourself, have a look at the class `nl.tweeenveertig.openstack.tutorial.StreamingController`.
