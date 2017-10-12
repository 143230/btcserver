function activeLi(active_tab){
    console.dir(active_tab.id);
    var total_tabs = 6;
    for (var k=1;k<=total_tabs;k++) {
        var tab = document.getElementById("tab" + k);
        var panel = document.getElementById("panel" + k)
        if (tab && panel) {
            removeClass(tab, "active");
            removeClass(panel, "active");
        }
    }
    for (var k=1;k<=total_tabs;k++){
        var panel = document.getElementById("panel"+k);
        var tab = document.getElementById("tab" + k);
        if(active_tab.id == "tab"+k && panel){
            addClass(tab,"active");
            addClass(panel,"active");
        }
    }
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