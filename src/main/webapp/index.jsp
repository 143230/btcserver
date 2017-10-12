<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: yaosheng
  Date: 2017/5/22
  Time: 15:48
  To change this template use File | Settings | File Templates.
--%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <title>Search Result</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">
    <meta name="format-detection" content="telephone=no">
    <meta name="renderer" content="webkit">
    <meta http-equiv="Cache-Control" content="no-siteapp"/>
    <link rel="alternate icon" type="image/png" href="./resources/i/favicon.png">
    <link rel="stylesheet" href="<%=basePath%>resources/css/style.css"/>
    <link rel="stylesheet" href="<%=basePath%>resources/css/font-awesome.min.css">
    <link rel="stylesheet" href="<%=basePath%>resources/css/bootstrap.min.css">

    <script type="text/javascript" src="<%=basePath%>resources/js/jquery.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>resources/js/jquery.json-2.2.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>resources/js/paneltabchange.js"></script>
</head>
<body>
<header class="am-topbar">
    <nav role="navigation" class="navbar navbar-default">

        <div class="navbar-header">
            <button data-target="#bs-example-navbar-collapse-1" data-toggle="collapse" class="navbar-toggle" type="button">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a href="search" class="navbar-brand">AIOnline</a>
        </div>
        <div id="bs-example-navbar-collapse-1" class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
                <li class="active"><a href="#">AIpapers</a></li>
                <li><a href="#">Arxiv</a></li>
                <li id="dropdown-tab2" class="dropdown">
                    <a data-toggle="dropdown" class="dropdown-toggle" href="#">dblp<b id="dropdown-square2" class="caret"></b></a>
                    <ul id="dropdown-panel2" role="menu" class="dropdown-menu">
                        <li><a href="#">Conferences</a></li>
                        <li><a href="#">Journals</a></li>
                    </ul>
                </li>
            </ul>
            <form role="search" class="navbar-form navbar-left">
                <div class="form-group">
                    <input type="text" placeholder="Search" class="form-control">
                </div>
                <button class="btn btn-primary" type="submit">
                    Submit
                </button>
            </form>
            <ul class="nav navbar-nav navbar-right">
                <c:choose>
                    <c:when test="${! empty LOGIN_USER}">
                        <li id="dropdown-tab1" class="dropdown" onclick="activeDropDown('dropdown-tab1')">
                            <a class="dropdown-toggle" href="#">
                                <i class="icon-user"></i>
                                    ${LOGIN_USER.uname}
                                        <b id="dropdown-square1" class="caret"></b>
                            </a>
                            <ul id="dropdown-panel1" role="menu" class="dropdown-menu">
                                <li><a href="./userinfo">个人中心</a></li>
                                <li class="divider"></li>
                                <li><a href="login/logout">退出登录</a></li>
                            </ul>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="login/login.jsp">登陆</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </nav>
</header>

<div class="main-content">
    <div class="content-panel">
        <span style="font-size:1.2em;">搜索结果</span>
        <hr style="margin:0 0 20px 0;">
        <c:forEach items="${papers}" var="p" varStatus="loop">
            <article class="blog-main">
                <h4 class="am-article-title blog-title">
                    <a href="paperinfo?id=${p.id}">${p.title}</a>
                </h4>
                <c:forEach items="${authorMap.get(p.id)}" var="author" varStatus="a_loop">
                    <c:choose>
                        <c:when test="${! empty author.aid}">
                            <span><a href="author?id=${author.aid}"><u>${author.authorname.split(",")[0]}</u></a></span>
                        </c:when>
                        <c:otherwise>
                            <span><a href="#"><u>${author.authorname.split(",")[0]}</u></a></span>
                        </c:otherwise>
                    </c:choose>

                </c:forEach>
                <span class="paper-time">
                        ${p.time.toLocaleString().split("-")[0]}
                </span>

                <div class="am-g blog-content">
                    <div class="am-u-lg-12">
                        <p>${p.paperAbstract}<a href="paperinfo?id=${p.id}">[more]</a></p>

                    </div>
                </div>

            </article>
            <hr>
        </c:forEach>
        <div class="paging-panel" >
            <ul>
                <li class="btn btn-default li-left"><a href="search?pageNum=${previousPage}">&laquo; 上一页</a></li>
                <li class="btn btn-default li-right"><a href="search?pageNum=${nextPage}">下一页 &raquo;</a></li>
            </ul>
        </div>
    </div>

</div>
<footer class="footer">
    <p>blog template<br/>
        <small>© Copyright XXX. by the AmazeUI Team.</small>
    </p>
</footer>

<!--[if lt IE 9]>
<script src="http://libs.baidu.com/jquery/1.11.1/jquery.min.js"></script>
<script src="http://cdn.staticfile.org/modernizr/2.8.3/modernizr.js"></script>
<script src="<%=basePath%>resources/js/amazeui.ie8polyfill.min.js"></script>
<![endif]-->

<!--[if (gte IE 9)|!(IE)]><!-->
<script src="<%=basePath%>resources/js/jquery.min.js"></script>

</body>
</html>
