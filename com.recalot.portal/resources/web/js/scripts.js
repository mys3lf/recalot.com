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

function renderButtons(container, id, buttons, configuration){
    for(var i in buttons){
        var button = $("<a href='javascript:void(0)' onclick='buttonClick(event)' class='btn btn-info btn-xs'></a>");
        button.data("data", buttons[i]);
        button.data("id", id);
        if(configuration != null){
            button.data("config", configuration);
        }
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
                tr.data("data", data[i]);
                tr.appendTo(table.children("tbody"));
            }
            table.data("data", item);
        } else {
            content.append("<span>No data available</span>");
        }
    }
}

function renderList(item){
    prepareContent();
    renderContent(item, renderListBody)
}

function renderDetail(data, item){
    var detail = $("#detail");
    detail.empty();

    var detailButtons = $("#detail-buttons");
    detailButtons.empty();

    detail.css("background-image", "url('/portal/imgs/loading.gif')");

    $.ajax({
        url: data.content.detail.action.replace("{0}", item.id != null ? item.id : item.key),
        method: data.content.detail.method != null ? data.content.detail.method : "GET",
        context: {data: data, item: item, nextFunction: _renderDetailWindow}
    }).success(function(data, status, jqXHR){
        this.nextFunction(this.data, this.item, data);
    }).error(function(jqXHR, textStatus, errorThrown){
        var message = $("#message");
        message.empty();

        $("<div class='alert alert-dismissible alert-danger'><button type='button' class='close' data-dismiss='alert'>×</button><strong>The current request could not be processed</strong>" + errorThrown + "</div>").appendTo(message);
    })
}

function _renderDetailWindow(data, item, details) {
    stopLoading();
    var $detail = $("#detail");

    $detail.data("data", data);
    $detail.data("item", item);
    $detail.data("details", details);

    if(data.content.detail.buttons != null){
        var buttons = $("#detail-buttons");

        renderButtons(buttons, "", data.content.detail.buttons, details.configuration);
    }

    var tree = $("<div id='tree'></div>");
    tree.appendTo($detail);

    tree.jstree({
        'core' : {
            'data' :  parseDetailsDataToJsTree(details, "default")
          },
          "plugins" : [ "types" ],
          "types" : {
            "default" : {
              "icon" : "glyphicon glyphicon-flash"
            },
            "configuration" : {
              "icon" : "glyphicon glyphicon-wrench"
            }
          }
        });
}

function parseDetailsDataToJsTree(detail, type){
    var array = [];

    for(var i in detail){
        var obj = {text: i };
        obj.type = type;
        if(typeof detail[i] == "object"){
            obj.children = parseDetailsDataToJsTree(detail[i], i == "configuration" ? "configuration" : type);
            obj.type = "configuration";
        } else {
            obj.text = obj.text + ":" + detail[i];
        }

        array.push(obj);
    }

    return array;
}

function tableClick(e){
    var target = $(e.target);
    var tr = target.closest("tr");
    if(tr.length > 0 && tr.parent().get(0).localName != "thead"){
        var table = tr.closest("table");
        var data= table.data("data");
        var item = tr.data("data");

        if(data != null && item != null){
            renderDetail(data, item);
        }
    }
}

function renderForm(form, config){
    var $detail = $("#detail");
    $detail.empty();

    var $form = $("<form onsubmit='formSubmit(event)' onreset='formReset(event)'></form>");
    $form.appendTo($detail);

    for(var i in config){

        var formElement = $("<div class='form-group'></div>");


          if(config[i].requirement.toLowerCase() == "hidden") {
                formElement.append("<input type='hidden' class='form-control' name='" + config[i].key + "' value='" + config[i].value + "' />");
            } else  {
               formElement.append("<label class='control-label' for='" + config[i].key + "'>" +  config[i].key +  "(" + config[i].requirement + ") </label>");

               switch(config[i].type.toLowerCase()){
                        case "options":
                        var $select = $("<select class='form-control' name='" + config[i].key + "'>");

                        for(var op in config[i].options){
                            $select.append("<option>" +config[i].options[op] +"</option>")
                        }

                        formElement.append($select);

                        break;
                        case "boolean":
                            formElement.append("<input type='checkbox' class='form-control' name='" + config[i].key + "' value='" + config[i].value + "' />");
                        break;
                        case "integer":
                        case "double":
                        case "string":
                            formElement.append("<input type='text' class='form-control' name='" + config[i].key + "' value='" + config[i].value + "' />");

                        break;
                    }
                }
        formElement.appendTo($form);
    }
    $form.data("config", config);
    $form.data("data", form);

    $form.append("<button type='reset' class='btn btn-default'>Cancel</button><button type='submit' class='btn btn-primary'>Submit</button>");
}

function formSubmit(e){
    var data = {};

    var target = $(e.target).closest("form");

    var inputs = target.find(".form-control");
    for(var i = 0; i < inputs.length; i++){
        var input = inputs.eq(i);
         if(input.attr("type") == "checkbox"){
            data[input.attr("name")] = input.is(":checked");
        } else {
            data[input.attr("name")] = input.val();
        }
    }

    var form = target.data("data");
    var config = target.data("config");

    $.ajax({
        url: form.action,
        method: form.method,
        data: data
    }).success(function(data, status, jqXHR){
       var message = $("#message");
        message.empty();

        $("<div class='alert alert-dismissible alert-success'><button type='button' class='close' data-dismiss='alert'>×</button><strong>Successful executed!</strong>Please reload the site.</div>").appendTo(message);
    }).error(function(jqXHR, textStatus, errorThrown){
        var message = $("#message");
        message.empty();

        $("<div class='alert alert-dismissible alert-danger'><button type='button' class='close' data-dismiss='alert'>×</button><strong>The current request could not be processed</strong>" + errorThrown + "</div>").appendTo(message);
    });

    e.preventDefault();

    resetDetail();

    return false;
}

function formReset(e){
    resetDetail();
}

function resetDetail(e){

    var detail = $("#detail");
    var detailButtons = $("#detail-buttons");
    var data  = detail.data("data");
    var item  = detail.data("item");
    var details  = detail.data("details");

    if(data != null && item != null && details != null){
        detail.empty();
        detailButtons.empty();

        _renderDetailWindow(data, item, details);
    }
}

function buttonClick(e){

    var target = $(e.target).closest("a");
    var data = target.data("data");
    var config = target.data("config");
    var id = target.data("id");

   if(data != null){
        if(data.form != null && config != null){
            renderForm(data.form, config);
        } else if(data.confirm != null){
            var r = confirm(data.confirm.text);
            if (r == true) {
                $.ajax({
                    url: data.confirm.action.replace("{0}", id),
                    method: data.confirm.method,
                }).success(function(data, status, jqXHR){
                   var message = $("#message");
                    message.empty();

                    $("<div class='alert alert-dismissible alert-success'><button type='button' class='close' data-dismiss='alert'>×</button><strong>Successful executed!</strong>Please reload the site.</div>").appendTo(message);
                }).error(function(jqXHR, textStatus, errorThrown){
                    var message = $("#message");
                    message.empty();

                    $("<div class='alert alert-dismissible alert-danger'><button type='button' class='close' data-dismiss='alert'>×</button><strong>The current request could not be processed</strong>" + errorThrown + "</div>").appendTo(message);
                });
            }
         }
    }

    e.stopPropagation();
    e.preventDefault();
    return false;
}


$(window).load(function(){
    var currentId = getParameterByName("id");

    if(currentId == ""){
        currentId = structure[0].id;
    }

    buildNavigation(structure, currentId);

    var currentItem = getCurrentItem(structure, currentId);

    render(currentItem);

    $("#table").click(tableClick);
});




