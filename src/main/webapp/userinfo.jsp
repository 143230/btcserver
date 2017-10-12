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
    <title>${LOGIN_USER.uname}</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">
    <meta name="format-detection" content="telephone=no">
    <meta name="renderer" content="webkit">
    <meta http-equiv="Cache-Control" content="no-siteapp"/>
    <link rel="alternate icon" type="image/png" href="./resources/i/favicon.png">
    <link rel="stylesheet" href="./resources/css/font-awesome.min.css">
    <link rel="stylesheet" href="./resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="./resources/css/style.css"/>

    <script type="text/javascript" src="./resources/js/jquery.min.js"></script>
    <script type="text/javascript" src="./resources/js/jquery.json-2.2.min.js"></script>
    <script type="text/javascript" src="./resources/js/supportpaper.js"></script>
    <script type="text/javascript" src="./resources/js/paperinfo.js"></script>
    <script type="text/javascript" src="./resources/js/paneltabchange.js"></script>
    <script type="text/javascript" src="./resources/js/submitpaper.js"></script>
    <script type="text/javascript" src="./resources/js/userinfo.js"></script>
</head>
<body >
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
    <div class="user-panel">
        <div class="user-nav">
            <div id="user-nav-panel" class="user-sidebar">
                <ul class="nav nav-tabs">
                    <li id="tab1" class="active" onclick="activeLi(this)">
                        <a href="#"><i class="icon-user"></i> 个人信息</a>
                    </li>
                    <li id="tab2" onclick="activeLi(this)">
                        <a href="#"><i class="icon-cloud-upload"></i>上传论文</a>
                    </li>
                    <li id="tab3" onclick="activeLi(this)">
                        <a href="#"><i class="icon-tag"></i>添加标签</a>
                    </li>
                    <li id="tab4" onclick="activeLi(this)">
                        <a href="#"><i class="icon-heart"></i>感兴趣论文</a>
                    </li>
                    <li id="tab5" onclick="activeLi(this)">
                        <a href="#"><i class="icon-book"></i>用户读过的论文</a>
                    </li>
                    <li id="tab6" onclick="activeLi(this)">
                        <a href="#"><i class="icon-lightbulb"></i>用户发表的论文</a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="user-content">
            <div id="panel1" class="user-panel active">
                <div style="float: left;"><i class="icon-user" style="font-size:5em;"></i></div>
                <span style="margin: 5px 0 5px 5px;font-size:2em;">${LOGIN_USER.uname}</span>

                <br>
                <hr style="margin-top: 40px;">
                <form class="form-horizontal" method="post" action="/updateUser">
                    <input name="uid" type="text" value="${LOGIN_USER.id}" style="display: none;">
                    <div class="form-group">
                        <label class="col-sm-2 control-label">用户名</label>
                        <div class="col-lg-10">
                            <input name="username" type="text" value="${LOGIN_USER.uname}" class="form-control">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">用户身份</label>
                        <div class="col-lg-10">
                            <select name="usertype" class="form-control">
                                <option value="1" ${LOGIN_USER.utype==1?"selected='selected'":""}>本科生</option>
                                <option value="2" ${LOGIN_USER.utype==2?"selected='selected'":""}>研究生</option>
                                <option value="3" ${LOGIN_USER.utype==3?"selected='selected'":""}>博士</option>
                                <option value="4" ${LOGIN_USER.utype==4?"selected='selected'":""}>副教授</option>
                                <option value="5" ${LOGIN_USER.utype==5?"selected='selected'":""}>教授</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">用户主页</label>
                        <div class="col-lg-10">
                            <input name="userurl" type="text" value="${LOGIN_USER.url}" class="form-control">
                        </div>
                    </div>
                    <div class="form-group">
                        <button type="submit" class="btn-save btn btn-sm btn-primary">修改个人信息</button>
                    </div>
                </form>
            </div>
            <div id="panel2" class="user-panel">
                <form class="form-horizontal" >
                    <div class="form-group">
                        <label class="col-sm-2 control-label">论文名称</label>
                        <div class="col-lg-10">
                            <input id="paper_title" type="text" placeholder="论文名称" class="form-control" value="">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">论文地址</label>
                        <div class="col-lg-10">
                            <input id="paper_url" type="url" placeholder="论文地址" class="form-control" value="">
                        </div>
                    </div>
                    <div class="form-group">
                        <button type="submit" class="btn-save btn btn-sm btn-primary" onclick="return save_writer_paper('${LOGIN_USER.id}','${0}')">提交论文</button>
                        <label  >暂不支持PDF上传</label>
                    </div>
                </form>
            </div>
            <div id="panel3" class="user-panel">
                <form class="form-horizontal">
                    <div class="form-group">
                        <label class="col-sm-2 control-label">标签名称</label>
                        <div class="col-lg-10">
                            <input id="tags" type="text" placeholder="添加标签（多标签以逗号分隔）" class="form-control">
                        </div>
                    </div>
                    <div class="form-group">
                        <button type="submit" class="btn-save btn btn-sm btn-primary" onclick="return addTag('${LOGIN_USER.id}')">提交标签</button>
                    </div>

                </form>
            </div>
            <div id="panel4" class="user-panel">
                <form class="form-horizontal">
                    <div class="form-group">
                        <label class="col-sm-2 control-label">论文名称</label>
                        <div class="col-lg-10">
                            <input id="paper_title1" type="text" placeholder="论文名称" class="form-control" value="">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">论文地址</label>
                        <div class="col-lg-10">
                            <input id="paper_url2" type="url" placeholder="论文地址" class="form-control" value="">
                        </div>
                    </div>
                    <div class="form-group">
                        <button type="submit" class="btn-save btn btn-sm btn-primary" ONCLICK="return save_interest_paper('${LOGIN_USER.id}','${1}')">提交论文</button>
                        <label  >暂不支持PDF上传</label>
                    </div>
                </form>
                <hr>

            </div>
            <div id="panel5" class="user-panel">
                <c:forEach items="${hisPapers}" var="p" varStatus="loop">
                    <article class="blog-main">
                    <span >
                        <h5>
                        <a href="paperinfo?id=${p.id}">${p.title}</a></h5>
                    </span>
                        <c:forEach items="${hisAuthorMap.get(p.id)}" var="author" varStatus="a_loop">
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
            <div id="panel6" class="user-panel">
                <c:forEach items="${writerPapers}" var="p" varStatus="loop">
                    <article class="blog-main">
                    <span >
                        <h4>
                        <a href="paperinfo?id=${p.id}">${p.title}</a></h4>
                    </span>
                        <c:forEach items="${wriAuthorMap.get(p.id)}" var="author" varStatus="a_loop">
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

<!--[if lt IE 9]>
<script src="http://libs.baidu.com/jquery/1.11.1/jquery.min.js"></script>
<script src="http://cdn.staticfile.org/modernizr/2.8.3/modernizr.js"></script>
<script src="./resources/js/amazeui.ie8polyfill.min.js"></script>
<![endif]-->

<!--[if (gte IE 9)|!(IE)]><!-->
<script src="./resources/js/jquery.min.js"></script>

</body>
</html>
