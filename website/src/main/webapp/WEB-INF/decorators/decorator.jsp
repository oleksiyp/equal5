<!DOCTYPE html>
<%@page language="java" contentType="text/html; encoding=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>

<html lang="en">
<head>
    <title>equal5 - <decorator:title></decorator:title></title>
    <link rel="stylesheet"
          type="text/css"
          media="all"
          href="<c:url value='/css/bootstrap.css'/>" />
    <link rel="stylesheet"
          type="text/css"
          media="all"
          href="<c:url value='/css/global.css'/>"
          />
    <script type="text/javascript"
            src="<c:url value='/js/bootstrap.js'/>"></script>
    <decorator:head />
</head>
<body>
<div class="container">
    <div class="navbar">
        <div class="navbar-inner">
            <div class="container">
                <a class="brand" href="#">equal<sup>5</sup></a>
                <ul class="nav">
                    <li class="active">
                        <a href="#">
                            <i class="icon-th icon-white"></i>
                            App
                        </a>
                    </li>
                    <li>
                        <a href="#">
                            <i class="icon-list-alt icon-white"></i>
                            Graphics
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </div>
    <div class="row-fluid">
        <div class="span12">
            <ul class="nav nav-tabs">
                <li class="active">
                    <a href="#">
                        <i class="icon-check"></i>
                        Run
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="icon-hand-right"></i>
                        Standalone
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="icon-wrench"></i>
                        Develop
                    </a>
                </li>
            </ul>
        </div>
    </div>
    <div style="min-height: 200px">
        <decorator:body />
    </div>

    <footer class="footer">
        <p class="pull-right"><a href="#">Back to top</a></p>
        <p>equal<sup>5</sup> - graphics builder</p>
        <p>equal5.org 2013 &copy;</p>
    </footer>

</div>

</body>
</html>
