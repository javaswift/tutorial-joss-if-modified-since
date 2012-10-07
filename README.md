Tutorial: JOSS If-Modified-Since
================================

This is a working project to demonstrate JOSS for demonstating natural If-Modified-Since when streaming objects through the application layer.

To see this project in action:
------------------------------

1. Checkout the code
2. Put your account information in src/main/resources/credentials.properties, or activate simulation mode (see below)
3. Run this command (it starts a container in the foreground):
    mvn jetty:run
4. Point your browser to [http://localhost:8081/](http://localhost:8081/)

To see how you can do all this yourself, have a look at the class `nl.tweeenveertig.openstack.tutorial.StreamingController`.

No Object Storage account?
--------------------------
Either get one at https://www.cloudvps.com/blog/cloudvps-object-store-beta-test-more-testers-welcome/

Or, alternatively, change the authentication lines in StorageProvider to make use of the ClientMock. In this case you will not need an account and you can still see the basic concept at work.

```java
            account = new ClientImpl().authenticate(tenant, username, password, auth_url);
//            account = new ClientMock().allowEveryone().authenticate(tenant, username, password, auth_url);
```