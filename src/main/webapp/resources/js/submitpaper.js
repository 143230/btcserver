/**
 * Created by yaosheng on 2017/6/1.
 */
function save_writer_paper(uid, type){
    console.dir(uid);

    if(uid==undefined){
        if(confirm("请先登陆")){
            window.location="/login/login.jsp";
        }
        return false;
    }
    var paper_title = document.getElementById("paper_title").value;
    var paper_url = document.getElementById("paper_url").value;

    console.dir(paper_title);
    var success = function(msg) {
        console.dir("success"+" for paper: "+uid);
    };
    var error = function(msg){
        console.dir("error"+" for paper: "+uid);
    };
    $.ajax({
        type : "POST",
        url : "./submitwriterPaper",
        data:{"uid":uid, "paper_title":paper_title,"paper_url":paper_url,"type":type},
        success : success,
        error : error,
        dataType : "text"
    });
    /*for (var k=1;k<=5;k++){
     if(k==score)continue;
     var stara = document.getElementById("stara"+k);
     addClass(stara,"fixed");
     }*/
    return false;
}

function save_interest_paper(uid, type){
    console.dir(uid);

    if(uid==undefined){
        if(confirm("请先登陆")){
            window.location="/login/login.jsp";
        }
        return false;
    }
    var paper_title = document.getElementById("paper_titl1").value;
    var paper_url = document.getElementById("paper_url1").value;

    console.dir(paper_title);
    var success = function(msg) {
        console.dir("success"+" for paper: "+uid);
    };
    var error = function(msg){
        console.dir("error"+" for paper: "+uid);
    };
    $.ajax({
        type : "POST",
        url : "./submitwriterPaper",
        data:{"uid":uid, "paper_title":paper_title,"paper_url":paper_url,"type":type},
        success : success,
        error : error,
        dataType : "text"
    });
    /*for (var k=1;k<=5;k++){
     if(k==score)continue;
     var stara = document.getElementById("stara"+k);
     addClass(stara,"fixed");
     }*/
    return false;
}