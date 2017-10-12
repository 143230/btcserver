<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html;
charset=UTF-8"
         pageEncoding="UTF-8"%>

<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>
<html>
<head lang="en">
  <meta charset="UTF-8">
  <title>Login Page | Amaze UI Example</title>
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="format-detection" content="telephone=no">
  <meta name="renderer" content="webkit">
  <meta http-equiv="Cache-Control" content="no-siteapp" />
  <link rel="alternate icon" type="image/png" href="<%=basePath%>resources/i/favicon.png">
    <link rel="stylesheet" href="<%=basePath%>resources/css/style.css"/>
    <link rel="stylesheet" href="<%=basePath%>resources/css/font-awesome.min.css">
    <link rel="stylesheet" href="<%=basePath%>resources/css/bootstrap.min.css">

    <script type="text/javascript" src="<%=basePath%>resources/js/jquery.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>resources/js/jquery.json-2.2.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>resources/js/logandreg.js"></script>
</head>
<body>
<div class="header">
  <div class="center-block">
    <h1>AI Research</h1>
    <p>paper recommendation system<br/>聚焦人工智能</p>
  </div>
  <hr />
</div>
<div class="main-content">
  <div class="content-panel  content-small">
    <div class="panel  panel-default">
      <div class="panel-heading">
          注册账号
          <span id="msg-notice" style="margin-left:30px;color:red">${result}</span>
      </div>
      <div class="panel-body">
        <form id="submitform" name="registerform" action="/login/register" onsubmit="return submitForm(this)" method="post">
          <div class="form-group">
            <label>邮箱</label>
            <input id="email" name="email" type="email" placeholder="Enter email" class="form-control">
          </div>
            <div class="form-group">
                <label>用户名</label>
                <input id="name" name="name" type="name" placeholder="Enter User Name" class="form-control">
            </div>
          <div class="form-group">
            <label>密码</label>
            <input id="password" name="password" type="password" placeholder="Password" class="form-control">
          </div>
            <div class="form-group">
                <label>再次输入密码</label>
                <input id="repeatpassword" name="password" type="password" placeholder="Password" class="form-control">
            </div>
            <div class="form-group">
                <label>用户身份</label>
                <select name="utype" class="form-control">
                    <option value="1">本科生</option>
                    <option value="2">研究生</option>
                    <option value="3">博士</option>
                    <option value="4">副教授</option>
                    <option value="5">教授</option>
                </select>
            </div>
          <div class="checkbox">
              <label>
                  <input name="00010" type="checkbox" value="true">
                  <span class="fa fa-check"></span>人工智能
              </label>
              <label>
                  <input name="00020" type="checkbox" value="true">
                  <i class="fa fa-check"></i>计算机图形
              </label>
              <label>
                  <input name="00030" type="checkbox" value="true">
                  <span class="fa fa-check"></span>计算机视觉
              </label>
              <label>
                  <input name="00040"  type="checkbox" value="true">
                  <span class="fa fa-check"></span>数据挖掘
              </label>
              <label>
                  <input name="00050"  type="checkbox" value="true">
                  <span class="fa fa-check"></span>机器学习
              </label>
              <label>
                  <input name="00060"  type="checkbox" value="true">
                  <span class="fa fa-check"></span>自然语言处理
              </label>
              <label>
                  <input name="00070"  type="checkbox" value="true">
                  <span class="fa fa-check"></span>模式识别
              </label>
              <label>
                  <input name="00080"  type="checkbox" value="true">
                  <span class="fa fa-check"></span>万维网
              </label>
              <label>
                  <input name="00090"  type="checkbox" value="true">
                  <span class="fa fa-check"></span>语言识别
              </label>
              <label>
                  <input name="00100"  type="checkbox" value="true">
                  <span class="fa fa-check"></span>语义网
              </label>
              <label>
                  <input name="00110"  type="checkbox" value="true">
                  <span class="fa fa-check"></span>知识图谱
              </label>
              <label>
                  <input name="00120"  type="checkbox" value="true">
                  <span class="fa fa-check"></span>信息检索
              </label>
          </div>
          <button id="registerbutton" type="submit" class="btn btn-primary">注册</button>
        </form>
      </div>
    </div>
      <hr>
    <p align="center">© 2014 AllMobilize, Inc. Licensed under MIT license.</p>
  </div>
</div>
</body>
</html>
