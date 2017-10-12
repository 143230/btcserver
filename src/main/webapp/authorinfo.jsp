<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: yaosheng
  Date: 2017/5/22
  Time: 15:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <title>${author.authorname.split(",")[0]}</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">
    <meta name="format-detection" content="telephone=no">
    <meta name="renderer" content="webkit">
    <meta http-equiv="Cache-Control" content="no-siteapp"/>
    <link rel="alternate icon" type="image/png" href="./resources/i/favicon.png">
    <link rel="stylesheet" href="./resources/css/style.css"/>
    <link rel="stylesheet" href="./resources/css/font-awesome.min.css">
    <link rel="stylesheet" href="./resources/css/bootstrap.min.css">

    <script type="text/javascript" src="./resources/js/jquery.min.js"></script>
    <script type="text/javascript" src="./resources/js/jquery.json-2.2.min.js"></script>
    <script type="text/javascript" src="./resources/js/supportpaper.js"></script>
    <script type="text/javascript" src="./resources/js/paperinfo.js"></script>
    <script type="text/javascript" src="./resources/js/paneltabchange.js"></script>
</head>
<body onload="pageLoadAction1('${author.aid}','${LOGIN_USER.id}')">
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
                <button class="btn btn-primary" type="submit">Submit</button>
            </form>
            <ul class="nav navbar-nav navbar-right">
                <c:choose>
                    <c:when test="${! empty LOGIN_USER}">
                        <li id="dropdown-tab1" class="dropdown">
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
        <div style="float: left;"><i class="icon-user" style="font-size:5em;"></i></div>
        <span style="margin: 5px 0 5px 5px;font-size:2em;">${author.authorname.split(",")[0]}</span>
        <div  style="margin: 5px 0 50px 60px;">
            <button class="btn btn-radius btn-success" style="border-radius: 500px;" onclick="fouseOnAuthor('${author.aid}','${LOGIN_USER.id}')">
                <i class="icon-eye-open"></i>
                <span id="focus-author">关注该用户</span>
            </button>
        </div>
        <div id="nq-tabs" class="note-tab">
            <ul class="nav nav-tabs">
                <li id="tab1" class="active" onclick="activeLi(this)">
                    <a href="#refpanel">
                        <i class="icon-lightbulb"></i>
                        发表论文
                    </a>
                </li>
                <li id="tab2" onclick="activeLi(this)">
                    <a href="#questionspanel">
                        <i class="icon-book"></i>
                        读过的论文
                    </a>
                </li>
                <li id="tab3" onclick="activeLi(this)">
                    <a href="#notespanel">
                        <i class="icon-leaf"></i>
                        相关领域论文
                    </a>
                </li>
            </ul><br/>
            <div id="panel1" class="nq-panel active">
                <c:forEach items="${refPapers}" var="p" varStatus="loop">
                    <article class="blog-main">
                    <span >
                        <h4>
                        <a href="paperinfo?id=${p.id}">${p.title}</a></h4>
                    </span>
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
                    </article>
                    <hr>
                </c:forEach>
                <div class="paging-panel" >
                    <ul>
                        <li class="btn btn-default li-left"><a href="#">&laquo; 上一页</a></li>
                        <li class="btn btn-default li-right"><a href="#">下一页 &raquo;</a></li>
                    </ul>
                </div>
            </div>
            <div id="panel2" class="nq-panel">
                <c:forEach items="${readedpapers}" var="p" varStatus="loop">
                    <article class="blog-main">
                    <span >
                        <h4>
                        <a href="paperinfo?id=${p.id}">${p.title}</a></h4>
                    </span>
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
                    </article>
                    <hr>
                </c:forEach>
                <div class="paging-panel" >
                    <ul>
                        <li class="btn btn-default li-left"><a href="#">&laquo; 上一页</a></li>
                        <li class="btn btn-default li-right"><a href="#">下一页 &raquo;</a></li>
                    </ul>
                </div>
            </div>
            <div  id="panel3" class="nq-panel">
                <c:forEach items="${relatedpapers}" var="p" varStatus="loop">
                    <article class="blog-main">
                    <span >
                        <h4>
                        <a href="paperinfo?id=${p.id}">${p.title}</a></h4>
                    </span>
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
                    </article>
                    <hr>
                </c:forEach>
                <div class="paging-panel" >
                    <ul>
                        <li class="btn btn-default li-left"><a href="#">&laquo; 上一页</a></li>
                        <li class="btn btn-default li-right"><a href="#">下一页 &raquo;</a></li>
                    </ul>
                </div>
            </div>
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
<script src="./resources/js/amazeui.ie8polyfill.min.js"></script>
<![endif]-->

<!--[if (gte IE 9)|!(IE)]><!-->
<script src="./resources/js/jquery.min.js"></script>

</body>
</html>
