function submitForm(form){
    if(!submitLoginForm(form)){
        return false;
    }
    if($("#name").val()==""){
        $("#msg-notice").html("用户名称不能为空!");
        return false;
    }
    if($("#repeatpassword").val()==""){
        $("#msg-notice").html("请再次输入密码！");
        return false;
    }
    if($("#password").val()!=$("#repeatpassword").val()){
        $("#msg-notice").html("两次输入密码不一致!");
        return false;
    }
    return true;
}

function submitLoginForm(form){
    if($("#email").val()==""){
        $("#msg-notice").html("邮箱地址不能为空!");
        return false;
    }
    if($("#password").val()==""){
        $("#msg-notice").html("密码不能为空!");
        return false;
    }
    return true;
}