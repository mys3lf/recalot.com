function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function buildNavigation(structure, currentId){
    var nav = $("#recalot-nav");
    nav.empty();

   var subNav = $("#recalot-subnav");
    subNav.empty();

   var breadcrumb = getBreadcrumb(structure, currentId).reverse();

   buildNavigationItems(nav, structure, breadcrumb[0]);

   var subNavStructure = [];

   for(var i in structure){
        if(structure[i].id == breadcrumb[0] && structure[i].children != null) {
            subNavStructure = structure[i].children;
        }
   }

   buildNavigationItems(subNav, subNavStructure, breadcrumb[1]);
}


function getBreadcrumb(structure, currentId){
     if(structure != null)
     {
         for(var i in structure){
             var item = structure[i];

             if(item.id == currentId) {
                 return [currentId];
             }

             var result = getBreadcrumb(item.children, currentId);
             if(result.length > 0){
                 result.push(item.id);
                 return result;
             }
         }
     }

     return [];
 }


 function getCurrentItem(structure, currentId){
    var item = findItem(structure, currentId);
    if(item != null && item.contentRef != null)
    {
    item = findItem(structure, item.contentRef);
    }

    return item;
 }

 function findItem(structure, currentId){
        for(var i in structure){
            var item = structure[i];
            if(item.children != null)
            {
                var result = findItem(item.children, currentId);
                if(result != null) return result;
            }

            if(item.id == currentId) {
                return item;
            }
        }

    return null;
 }


function buildNavigationItems(nav, structure, currentId){
    for(var i in structure){
        var item = structure[i];
        var $item = $("<li><a href='/portal/?id=" + item.id + "'>" + item.name + "</a></li>");
        if(item.id == currentId) $item.addClass("active")
        $item.appendTo(nav);
    }
}



function render(item){
    switch(item.content.type) {
        case "list":
            renderList(item);
        break;
    }
}


function prepareContent(){
    var content = $("#table");
    content.css("background-image", "url('/portal/imgs/loading.gif')");

    var detail = $("#detail");
    detail.css("background-image", "url('/portal/imgs/loading.gif')");

    var message = $("#message");
    message.empty();
}

function stopLoading(){
    var content = $("#table");
    content.css("background-image", "");

    var detail = $("#detail");
    detail.css("background-image", "");
}


function renderContent(item, nextFunction) {
    $.ajax({
        url: item.content.action,
        method: item.content.method,
        context: {item: item, nextFunction: nextFunction}
    }).success(function(data, status, jqXHR){
        this.nextFunction(this.item, data);
    }).error(function(jqXHR, textStatus, errorThrown){
        var message = $("#message");
        message.empty();

        $("<div class='alert alert-dismissible alert-danger'><button type='button' class='close' data-dismiss='alert'>×</button><strong>The current request could not be processed</strong>" + errorThrown + "</div>").appendTo(message);
    })
}

function renderButtons(container, id, buttons){
    for(var i in buttons){
        var button = $("<a href='javascript:void(0)' onclick='buttonClick(this)' class='btn btn-info btn-xs'></a>");
        button.data("data", buttons[i]);
        if(buttons[i].glyphicon != null){
            button.append("<span class='glyphicon " + buttons[i].glyphicon + "' aria-hidden='true'></span>");
        } else {
            button.append(buttons[i].name);
        }

        button.appendTo(container);
    }
}

function renderListBody(item, data){
    var content = $("#table");
    content.empty();
    if(typeof item == "object" && typeof data == "object"){
        stopLoading();
        if(data.length > 0){

            var table = $("<table class='table table-striped table-hover'><thead><tr></tr></thead><tbody></tbody></table>");

            table.children("thead").children("tr").append("<th>#</th>")
            for(var i in data[0]){
                table.children("thead").children("tr").append("<th>" + i + "</th>")
            }
            table.children("thead").children("tr").append("<th></th>")
            table.appendTo(content);

            var count = 1;
            for(var i in data){
                var tr = $("<tr><td>" + (count++) +  "</td></tr>")
                for(var j in data[i]) {
                    tr.append("<td>" + data[i][j] + "</td>");
                }

                if(data[i].id != null && item.content.buttons != null){
                    var td = $("<td class='buttons'></td>");
                    renderButtons(td, data[i].id, item.content.buttons);
                    td.appendTo(tr);
                }

                tr.appendTo(table.children("tbody"));
            }
        } else {
            content.append("<span>No data available</span>");
        }
    }
}

function renderList(item){
    prepareContent();
    renderContent(item, renderListBody)
}

$(window).load(function(){
    var currentId = getParameterByName("id");

    if(currentId == ""){
        currentId = structure[0].id;
    }

    buildNavigation(structure, currentId);

    var currentItem = getCurrentItem(structure, currentId);

    render(currentItem);
});


