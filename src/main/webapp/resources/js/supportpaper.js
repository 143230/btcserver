function SupportPaper(paper_id,score,author_id){
    console.dir(score);
    if(author_id=="" || author_id==undefined){
        if(confirm("请先登陆")){
            window.location="/login/login.jsp";
        }
        $("#starScore"+score).attr("checked",false);
        return false;
    }
    var success = function(msg) {
        console.dir("success"+" for paper: "+paper_id);
    };
    var error = function(msg){
        console.dir("error"+" for paper: "+paper_id);
    };
    $.ajax({
        method : "GET",
        url : "./supportpaper",
        data:{"pid":paper_id,"score":score,"aid":author_id},
        success : success,
        error : error,
        dataType : "text"
    });
    /*for (var k=1;k<=5;k++){
        if(k==score)continue;
        var stara = document.getElementById("stara"+k);
        addClass(stara,"fixed");
    }*/
    return true;
}

function hasClass(obj, cls) {
    return obj.className.match(new RegExp('(\\s|^)' + cls + '(\\s|$)'));
}
function removeClass(obj, cls) {
    if (hasClass(obj, cls)) {
        var reg = new RegExp('(\\s|^)' + cls + '(\\s|$)');
        obj.className = obj.className.replace(reg, ' ');
    }
}
function addClass(obj, cls) {
    if (!this.hasClass(obj, cls)) obj.className += " " + cls;
}
function fouseOnAuthor(author_id,user_id){
    if(user_id=="" || user_id==undefined){
        if(confirm("请先登陆")){
            window.location="/login/login.jsp";
        }
        return;
    }
    var success = function(msg) {
        console.dir("success fouse on author: "+author_id);
        if($("#focus-author").text()=="关注该用户"){
            $("#focus-author").text("取消关注");
        }else{
            $("#focus-author").text("关注该用户");
        }
    };
    var error = function(msg){
        console.dir("error fouse on author: "+author_id);
    };
    $.ajax({
        method : "GET",
        url : "./fouseonauthor",
        data:{"aid":author_id,"uid":user_id},
        success : success,
        error : error,
        dataType : "text"
    });
}
function pageLoadAction1(author_id,user_id){
    if(user_id=="" || user_id==undefined){
        return;
    }
    var success = function(msg) {
        console.dir(msg);
        if(msg=="true"){
            $("#focus-author").text("取消关注");
        }else{
            $("#focus-author").text("关注该用户");
        }
    };
    var error = function(msg){
        console.dir("error fouse on author: "+user_id);
    };
    $.ajax({
        method : "GET",
        url : "./testinterest",
        data:{"uid":user_id,"aid":author_id},
        success : success,
        error : error,
        dataType : "text"
    });
}