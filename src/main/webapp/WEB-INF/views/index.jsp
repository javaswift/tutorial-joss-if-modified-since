<%@ page import="nl.tweeenveertig.openstack.tutorial.StorageProvider" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <c:set var="path" value="${pageContext.request.contextPath}"/>
    <style type="text/css">
        body {
            font-size: .9em;
            margin: 0 auto;
            max-width: 60em;
        }

        section {
            border: 2px groove gray;
            margin-bottom: 2em;
            padding: 1em;
        }

        section h1 {
            margin-top: 0;
            padding-top: 0;
            font-size: 1.5em;
        }

        section p.footnote {
            border-top: thin dashed gray;
            margin-top: 2em;
            padding-top: 1em;
            font-size: 80%;
        }
    </style>
    <link rel="icon" href="${path}/images/favicon.ico" type="image/x-icon"/>
    <title>JOSS Tutorial - If-Modified-Since</title>
</head>
<body>

<h1>JOSS Tutorial - If-Modified-Since</h1>

<section id="usecase1">
    <h1>Retrieve an image directly through the Public URL</h1>
    <p>
        ...
    </p>
    <p>
        <img src="${public_url}">
    </p>
</section>

<section id="usecase2">
    <h1>Retrieve an image directly through the application layer</h1>
    <p>
        ...
    </p>
    <p>
        <img src="${path}/download/${resource_name}">
    </p>
</section>

</body>
</html>
