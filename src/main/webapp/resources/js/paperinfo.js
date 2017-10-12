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
function takeactions(action_id) {
    var button = document.getElementById("takenotesbutton");
    var action = document.getElementById("papernotes");
    var qorn = document.getElementById(action_id)
    addClass(action,"active");
    button.style.display="none";
    qorn.style.display="block";
    windowsresize({"id":"papernotes"});
}
function cancelactions(action_id) {
    var button = document.getElementById("takenotesbutton");
    var qorn = document.getElementById(action_id);
    var action = document.getElementById("papernotes");
    removeClass(action,"active");
    button.style.display="block";
    qorn.style.display="none";
    windowsresize({"id":"papernotes"});
}
function savenotes(user_id,paper_id){
    console.dir(user_id);
    if(user_id==""){
        if(confirm("请先登陆")){
            window.location="/login/login.jsp";
        }
        return;
    }
    var author = document.getElementById("author").value;
    var author_place = document.getElementById("author_place").value;
    var paper_source = document.getElementById("paper_source").value;
    var paper_keywords = document.getElementById("paper_keywords").value;
    var problems = document.getElementById("problem").value;
    var paper_tech = document.getElementById("paper_tech").value;
    var related_word = document.getElementById("related_word").value;
    var paper_brief_comment = document.getElementById("paper_brief_comment").value;
    var paper_model = document.getElementById("paper_model").value;
    var success = function(msg) {
        console.dir("success"+" take notes for paper: "+paper_id);
        $("#author").val("");
        $("#author_place").val("");
        cancelactions('askquestionsform');
    };
    var error = function(msg){
        console.dir("error"+" take notes for paper: "+paper_id);
    };
    $.ajax({
        type : "POST",
        url : "./takenotes",
        success : success,
        error : error,
        data:{"pid":paper_id,"uid":user_id,"author":author,"author_place":author_place,"paper_source":paper_source,
            "paper_keywords":paper_keywords,"problems":problems,"paper_tech":paper_tech,"paper_model":paper_model
            ,"related_word":related_word,"paper_brief_comment":paper_brief_comment},
        dataType : "text"
    });
}
function savequestions(paper_id){
    var title = document.getElementById("questiontitle").value;
    var content = document.getElementById("questioncontent").value;
    var success = function(msg) {
        console.dir("success"+" take question for paper: "+paper_id);
        $("#questiontitle").val("");
        $("#questioncontent").val("");
        cancelactions('askquestionsform');
    };
    var error = function(msg){
        console.dir("error"+" take question for paper: "+paper_id);
    };
    $.ajax({
        type : "POST",
        url : "./takequestions",
        success : success,
        error : error,
        data:{"questiontitle":title,"questioncontent":content,"paperid":paper_id},
        dataType : "text"
    });
}
function windowsresize(p){
    var d = document,dd = d.documentElement,db = d.body,w = window,o = d.getElementById(p.id),ie = /msie/i.test(navigator.userAgent),style,timer;
    var st = db.scrollTop,c;
    var interval = setInterval(function(){
        var st = db.scrollTop,c;
        c = st  - o.offsetTop + (p.t!=undefined?p.t:(w.innerHeight||db.clientHeight)-o.offsetHeight);
        if(c!=0){
            o.style.top = o.offsetTop + Math.ceil(Math.abs(c)/10)*(c<0?-1:1) + 'px';
        }else{
            clearInterval(interval);
        }
    },20)
}
function parseContent(notepanel, notes) {
    var htmltext = "";
    var tc1 = "<article class=\"blog-main\"> <span > <h4>"
    var tc2 = "</h4> </span> <div class=\"am-g blog-content\"> <div class=\"am-u-lg-12\"> <p>"
    var tc3 = "</div> </div> </article> <hr>";
    var lasttc = "<div class=\"paging-panel\" > <ul> <li class=\"btn btn-default li-left\"><a href=\"" +
        "#\">&laquo; 上一页</a></li> <li class=\"btn btn-default li-right\"><a href=\"" +
        "#\">下一页 &raquo;</a></li> </ul> </div>"
    $.each(notes, function (index, item) {
        console.dir(item);
        var title = item['title'];
        var content = item['content'];
        console.dir(title);
        console.dir(content);
        htmltext+=tc1+title+tc2+content+tc3;
    })
    htmltext+=lasttc;
    notepanel.html(htmltext);
}
function getRelatedPapers(paper_id) {
    var notepanel = $("#panel1");
    var success = function(msg) {
        console.dir(msg);
        parseContent(notepanel,msg);
        console.dir("success"+" take question for paper: "+paper_id);
    };
    var error = function(msg){
        console.dir("error"+" take question for paper: "+paper_id);
    };
    $.ajax({
        type : "GET",
        url : "/searchnotes",
        success : success,
        error : error,
        data:{"uid":user_id,"pid":paper_id,"page":page_num},
        dataType : "json"
    });
}
function getPaperNotes(user_id,paper_id,page_num){
    var notepanel = $("#panel3");
    var success = function(msg) {
        console.dir(msg);
        parseContent(notepanel,msg);
        console.dir("success"+" take question for paper: "+paper_id);
    };
    var error = function(msg){
        console.dir("error"+" take question for paper: "+paper_id);
    };
    $.ajax({
        type : "GET",
        url : "/searchnotes",
        success : success,
        error : error,
        data:{"uid":user_id,"pid":paper_id,"page":page_num},
        dataType : "json"
    });

}
function scroll(p){
    var d = document,dd = d.documentElement,db = d.body,w = window,o = d.getElementById(p.id),ie = /msie/i.test(navigator.userAgent),style,timer;
    if(o){
        //ie8下position:fixed下top left失效
        o.style.cssText +=";position:"+(p.f&&!ie?'fixed':'absolute')+";"+(p.l==undefined?'right:0;':'left:'+p.l+'px;')+(p.t!=undefined?'top:'+p.t+'px':'bottom:0');
        if(p.f&&ie){
            o.style.cssText +=';left:expression(body.scrollLeft + '+(p.l==undefined?db.clientWidth-o.offsetWidth:p.l)+' + "px");top:expression(body.scrollTop +'+(p.t==undefined?db.clientHeight-o.offsetHeight:p.t)+'+ "px" );'
            db.style.cssText +=";background-image:url(about:blank);background-attachment:fixed;"
        }else{
            if(!p.f){
                w.onresize = w.onscroll = function(){
                    clearInterval(timer);
                    timer = setInterval(function(){
                        var st = db.scrollTop,c;
                        c = st  - o.offsetTop + (p.t!=undefined?p.t:(w.innerHeight||db.clientHeight)-o.offsetHeight);
                        if(c!=0){
                            o.style.top = o.offsetTop + Math.ceil(Math.abs(c)/10)*(c<0?-1:1) + 'px';
                        }else{
                            clearInterval(timer);
                        }
                    },20)
                }
            }
        }
    }
}

function pageLoadAction2(paper_id,user_id){
    if(user_id=="" || user_id==undefined){
        return;
    }
    var success = function(msg) {
        console.dir(msg);
        if(msg!="nothing"){
            $("#starScore"+msg).attr("checked",true);
        }
    };
    var error = function(msg){
        console.dir("error score on author: "+user_id);
    };
    $.ajax({
        method : "GET",
        url : "./testscoreonpaper",
        data:{"uid":user_id,"pid":paper_id},
        success : success,
        error : error,
        dataType : "text"
    });
    getPaperNotes(user_id,paper_id,0);
}