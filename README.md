Tutorial: JOSS If-Modified-Since
================================

This is a working project to demonstrate JOSS for demonstating natural If-Modified-Since when streaming objects through the application layer.

To see this project in action:
------------------------------

1. Checkout the code
2. Put your account information in src/main/resources/application-context.xml, or activate simulation mode (see below)
3. Run this command (it starts a container in the foreground):
    mvn tomcat7:run
4. Point your browser to [http://localhost:8080/](http://localhost:8080/)

To see how you can do all this yourself, have a look at the class `nl.tweeenveertig.openstack.tutorial.StreamingController`.

No Object Storage account?
--------------------------
Either get one at https://www.cloudvps.com/blog/cloudvps-object-store-beta-test-more-testers-welcome/

Or, alternatively, change the authentication lines in the application-context.xml to make use of the ClientMock. In this case you will not need an account and you can still see the basic concept at work.

```xml
    <bean id="cloudConfig" class="org.javaswift.joss.client.factory.AccountConfig">
        <property name="tenantName" value="tenantName"/> <!-- optional -->
        <property name="tenantId" value="tenantId"/> <!-- optional -->
        <property name="username" value="username"/>
        <property name="password" value="password"/>
        <property name="authUrl" value="http://url.where.you.authenticate"/>
        <property name="authenticationMethod" value="BASIC"/>
        <property name="mock" value="true"/>
    </bean>
```

The "mock" property makes use of the mock Swift server.