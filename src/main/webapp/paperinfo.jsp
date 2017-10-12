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
    <title>${paper.title}</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">
    <meta name="format-detection" content="telephone=no">
    <meta name="renderer" content="webkit">
    <meta http-equiv="Cache-Control" content="no-siteapp"/>
    <link rel="alternate icon" type="image/png" href="./resources/i/favicon.png">
    <%--<link rel="stylesheet" href="./resources/css/amazeui.min.css"/>--%>
    <link rel="stylesheet" href="./resources/css/font-awesome.min.css">
    <link rel="stylesheet" href="./resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="./resources/css/star-score.css">
    <link rel="stylesheet" href="./resources/css/style.css"/>

    <script type="text/javascript" src="./resources/js/jquery.min.js"></script>
    <script type="text/javascript" src="./resources/js/jquery.json-2.2.min.js"></script>
    <script type="text/javascript" src="./resources/js/supportpaper.js"></script>
    <script type="text/javascript" src="./resources/js/paperinfo.js"></script>
    <script type="text/javascript" src="./resources/js/paneltabchange.js"></script>
</head>
<body onload="pageLoadAction2('${paper.id}','${LOGIN_USER.id}')">
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
            <article class="blog-main">
                <h3 class="am-article-title blog-title">
                    <a href="paperinfo?id=${paper.id}">${paper.title}</a>
                </h3>
                <div class="paper-author">
                    <c:forEach items="${authors}" var="author" varStatus="loop">
                        <c:if test="${! empty author.aid}">
                            <span><a href="author?id=${author.aid}"><u>${author.authorname.split(",")[0]}</u></a></span>
                        </c:if>
                        <c:if test="${empty author.aid}">
                            <span><a href="#"><u>${author.authorname.split(",")[0]}</u></a></span>
                        </c:if>

                    </c:forEach>
                    <span class="paper-time">
                        ${paper.time.toLocaleString().split("-")[0]}
                    </span>
                    <%--SupportPaper(${paper.id})--%>
                    <div id="starBg" class="star_bg">
                        <input type="radio" id="starScore1" class="score score_1" value="1" name="score">
                        <a href="#starScore1" id="stara1" class="star star_1" onclick="return SupportPaper('${paper.id}',1,'${LOGIN_USER.id}')" title="差"><label for="starScore1">差</label></a>
                        <input type="radio" id="starScore2" class="score score_2" value="2" name="score">
                        <a href="#starScore2" id="stara2" class="star star_2" onclick="return SupportPaper('${paper.id}',2,'${LOGIN_USER.id}')" title="较差"><label for="starScore2">较差</label></a>
                        <input type="radio" id="starScore3" class="score score_3" value="3" name="score">
                        <a href="#starScore3" id="stara3" class="star star_3" onclick="return SupportPaper('${paper.id}',3,'${LOGIN_USER.id}')" title="普通"><label for="starScore3">普通</label></a>
                        <input type="radio" id="starScore4" class="score score_4" value="4" name="score">
                        <a href="#starScore4" id="stara4" class="star star_4" onclick="return SupportPaper('${paper.id}',4,'${LOGIN_USER.id}')" title="较好"><label for="starScore4">较好</label></a>
                        <input type="radio" id="starScore5" class="score score_5" value="5" name="score">
                        <a href="#starScore5" id="stara5" class="star star_5" onclick="return SupportPaper('${paper.id}',5,'${LOGIN_USER.id}')" title="好"><label for="starScore5">好</label></a>
                    </div>
                </div>
                <div class="am-g blog-content">
                    <div class="am-u-lg-12">
                        <p>${paper.paperAbstract}</p>

                    </div>
                </div>

            </article>
        <hr>
        <h4><i class="icon-tags"></i>相关标签</h4>
        <div class="panel-body">
            <c:forEach items="${tags}" var="tag" varStatus="loop">
                <span class="label label-default pull-left paper-tag">${tag}</span>
            </c:forEach>
        </div>
        <hr>

        <div id="nq-tabs" class="note-tab">
            <ul class="nav nav-tabs">
                <li id="tab1" class="active" onclick="activeLi(this)">
                    <a href="#refpanel">
                        <i class="icon-lightbulb"></i>
                        <c:if test="${paper.publisher == 'arxiv' }">
                            相似论文
                        </c:if>
                        <c:if test="${paper.publisher != 'arxiv' }">
                            参考文献
                        </c:if>
                    </a>
                </li>
                <li id="tab2" onclick="activeLi(this)">
                    <a href="#questionspanel">
                        <i class="icon-question-sign"></i>
                        问题
                    </a>
                </li>
                <li id="tab3" onclick="activeLi(this)">
                    <a href="#notespanel">
                        <i class="icon-book"></i>
                        笔记
                    </a>
                </li>
                <li id="tab4" onclick="activeLi(this)">
                    <a href="#notespanel">
                        <i class="icon-pencil"></i>
                        写笔记
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
                <%--<div class="paging-panel" >
                    <ul>
                        <li class="btn btn-default li-left"><a href="#">&laquo; 上一页</a></li>
                        <li class="btn btn-default li-right"><a href="#">下一页 &raquo;</a></li>
                    </ul>
                </div>--%>
            </div>
            <div id="panel2" class="nq-panel">
                <c:forEach items="${notes}" var="n" varStatus="loop">
                    <article class="blog-main">
                    <span >
                        <h4>
                                ${n.contexturi}
                        </h4>
                    </span>
                        <div class="am-g blog-content">
                            <div class="am-u-lg-12">
                                <p>${n.context}</p>
                            </div>
                        </div>
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
               暂无相关笔记
            </div>
            <div  id="panel4" class="nq-panel">
                <form  class="form-horizontal" method="post">
                    <input name="paper_id" style="display: none" value="${paper.id}"/>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">作者</label>
                        <div class="col-lg-10">
                            <input id="author" type="text" placeholder="作者" class="form-control">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">作者单位</label>
                        <div class="col-lg-10">
                            <input id="author_place" type="text" placeholder="作者单位" class="form-control">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">文章来源</label>
                        <div class="col-lg-10">
                            <input id="paper_source" type="text" placeholder="文章来源" class="form-control">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">关键词</label>
                        <div class="col-lg-10">
                            <input id="paper_keywords" type="text" placeholder="关键词" class="form-control">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">论文问题</label>
                        <div class="col-lg-10">
                            <textarea id="problem" type="text" placeholder="论文问题" class="form-control"></textarea>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">论文采用技术</label>
                        <div class="col-lg-10">
                            <textarea id="paper_tech" type="text" placeholder="论文采用技术" class="form-control"></textarea>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">论文研究方向</label>
                        <div class="col-lg-10">
                            <textarea id="paper_area" type="text" placeholder="论文研究方向" class="form-control"></textarea>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">论文模型</label>
                        <div class="col-lg-10">
                            <textarea rows="5" id="paper_model" type="url" placeholder="论文模型" class="form-control"></textarea>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">相关工作</label>
                        <div class="col-lg-10">
                            <textarea rows="5" id="related_word" type="url" placeholder="相关工作" class="form-control"></textarea>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">论文简评</label>
                        <div class="col-lg-10">
                            <textarea rows="5" id="paper_brief_comment" type="url" placeholder="论文简评" class="form-control"></textarea>
                        </div>
                    </div>
                    <div class="form-group">
                        <button type="submit" class="btn-save btn btn-sm btn-primary" ONCLICK="savenotes('${LOGIN_USER.id}','${paper.id}')">提交报告</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

</div>
<div id="papernotes" class="note-question-panel">
    <div id="takenotesbutton">
        <%--<a class="btn btn-default" onclick="takeactions('takenotesform')">
            <i class="icon-question-sign"></i>
            做笔记
        </a>--%>
        <a class="btn btn-default" onclick="takeactions('askquestionsform')">
            <i class="icon-pencil"></i>
            提问题
        </a>
    </div>
    <div id="takenotesform" class="panel panel-default" style="display: none;margin: 5px 5px 0 5px;">
        <div class="panel-nq panel-heading">
            <label>标题：</label>
            <input id="notetitle" type="text">
        </div>
        <div class="panel-nq panel-content">
            <textarea id="notecontent"></textarea>
        </div>
        <div class="panel-nq">
            <a class="btn-save btn btn-primary" onclick="savenotes('${LOGIN_USER.id}','${paper.id}')">保存笔记</a>
            <a class="btn-cancel btn btn-default" onclick="cancelactions('takenotesform')">取消</a>
        </div>
    </div>
    <div id="askquestionsform"  class="panel panel-default" style="display: none;margin: 5px 5px 0 5px;">
        <div class="panel-nq panel-heading">
            <label>标题：</label>
            <input id="questiontitle" type="text">
        </div>
        <div class="panel-nq panel-content">
            <textarea id="questioncontent"></textarea>
        </div>
        <div class="panel-nq">
            <a class="btn-save btn btn-primary" onclick="savequestions('${paper.id}')">保存问题</a>
            <a class="btn-cancel btn btn-default" onclick="cancelactions('askquestionsform')">取消</a>
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
<script>
    scroll({'id':'papernotes'})
</script>
</body>
</html>
