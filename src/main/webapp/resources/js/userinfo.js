/**
 * Created by yaosheng on 2017/6/2.
 */
function addTag(uid){
    console.dir(uid);

    if(uid==undefined){
        if(confirm("请先登陆")){
            window.location="/login/login.jsp";
        }
        return false;
    }
    var tags = document.getElementById("tags").value;
    console.dir(tags);
    var success = function(msg) {
        console.dir("success"+" for paper: "+uid);
    };
    var error = function(msg){
        console.dir("error"+" for paper: "+uid);
    };
    $.ajax({
        type : "POST",
        url : "./addTag",
        data:{"uid":uid, "tags":tags},
        success : success,
        error : error,
        dataType : "text"
    });

    return false;
}
