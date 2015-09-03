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
        case "form":
            renderForm(item);
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

function callFunctionAfterAjax(url, method, requestData, context, nextFunction) {
    $.ajax({
        url: url,
        data: requestData,
        method: method,
        context: {context: context, nextFunction: nextFunction},
    }).success(function(data, status, jqXHR){
        this.nextFunction(this.context, data, "success");
    }).error(function(jqXHR, textStatus, errorThrown){
        var message = $("#message");
        message.empty();

        this.nextFunction(this.context, jqXHR.responseJSON, "error");

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
function renderForm(item){
    prepareContent();
    renderFormContent(item)
}

function renderFormContent(data) {
    var content = $("#table");
    content.empty();

    if(typeof data == "object" && data.content != null && data.content.form != null){
        stopLoading();
        if(data.content.form.length > 0){

             var $form = $("<form onsubmit='formExperimentSubmit(this, event)'></form>");
            $form.appendTo(content);

            _renderTableFormContent($form, data.content.form);

            $form.data("data", data.content);
            $form.append("<button type='reset' class='btn btn-default'>Cancel</button><button type='submit' class='btn btn-primary'>Run</button>");
        }
    }
}

function formExperimentSubmit(form, event){
    var $form = $(form);
    var data = $form.data("data");

    var formData = _collectFormData($form);

    callFunctionAfterAjax(
        data.action.url,
        data.action.method,
        formData,
        null,
        showExperimentSubmitDetails
    );

    console.log(formData);

    event.preventDefault();
    return false;
}

function showExperimentSubmitDetails(context, data, type){
    var $detail = $("#detail");
    $detail.empty();

    var tree = $("<div id='tree'></div>");
    tree.appendTo($detail);

    tree.jstree({
        'core' : {
            'data' :  parseDetailsDataToJsTree(data, "default")
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

function _collectFormData(container, prefix) {
    var formData = {};

    var formGroup = container.children(".form-group");

    for(var i = 0; i < formGroup.length; i++){
        var group = formGroup.eq(i);

        var formControl = group.children(".form-control");
        if(formControl.length > 0) {
            var name = formControl.attr("name");
            var value = ""

            if(formControl.attr("type") == "checkbox" ) {
                value = formControl.is(":checked") ? "true" : "false";
            } else {
                value = formControl.val();
            }

            var configContainer = group.find(".configurations-container");
            if(configContainer.length > 0){
                var config = _collectFormData(group.find(".configurations-container"), value + ".");
                for(var c in config) {
                    formData[c] = config[c];
                }
            }


            formData[prefix != null ? prefix + name : name] = value;
        } else {

            var arrayItems = group.children(".array-draft, .array-item");

            var name = arrayItems.attr("name");
            var value = "";

            for(var j= 0; j < arrayItems.length; j++) {
                var item = arrayItems.eq(j).children(".array-content");

                var idName = item.children(".form-group").children(".col-xs-6").children(".form-control");
                if(idName.length == 2) {
                    var combination =  idName.eq(1).val() + "@" + idName.eq(0).val();

                    if(value.length > 0) {
                        value += ",";
                    }

                    value += combination;

                    var config = _collectFormData(item.find(".configurations-container"), idName.eq(0).val() + ".");
                    for(var c in config) {
                        formData[c] = config[c];
                    }
                }
            }

            formData[name] = value;
        }
    }

    return formData;
}

function _renderTableFormContent(container, form, skipLabel){
    var skip = skipLabel != null ? skipLabel : false;

    for(var i in form){
        var item = form[i];
        var formElement = $("<div class='form-group'></div>");

        if(!skip)    formElement.append("<label class='control-label' for='" + item.id + "'>" +  item.id + "</label>");

        switch(item.type) {
            case "enum":

                var $select = $("<select class='form-control' name='" + item.id + "'>");

                if(item.enum instanceof Array) {

                    for(var op in item.enum ){
                        $select.append("<option>" + item.enum[op] +"</option>")
                    }

                } else if(typeof item.enum == "object" && item.enum.action != null) {
                    callFunctionAfterAjax(
                        item.enum.action,
                        "GET",
                        null,
                        {item: item, select: $select},
                        function(context, data, type){
                             if(type == "success") {
                                for(var d in data){
                                     context.select.append("<option>" + (data[d].id != null ? data[d].id : data[d].key) +"</option>")
                                }
                             }
                        }
                    );
                }

                formElement.append($select);
            break;
            case "array":

                var draft = $("<div class='array-draft col-xs-12'><div class='array-content col-xs-10'></div><div class='array-controls  col-xs-2' ></div></div>");
                draft.appendTo(formElement);
                draft.attr("name", item.id);

                _renderTableFormContent(draft.children(".array-content"), [item.content], true);
                var removeButton = $("<a href='javascript:void(0)' onclick='removeArrayElement(this, event)' class='btn btn-danger btn-xs'><span class='glyphicon glyphicon-remove' aria-hidden='true'></span></a>");
                removeButton.appendTo(draft.children(".array-controls"));

                var addButton = $("<div class='add-button col-xs-12'><a href='javascript:void(0)' onclick='addArrayElement(this, event)' class='btn btn-success btn-xs'><span class='glyphicon glyphicon-plus' aria-hidden='true'></span></a></div>");

                addButton.appendTo(formElement);
            break;
            case "idkeyconfiguration":

                formElement.append("<div class='col-xs-6'><input type='text' class='form-control array-item prefix' value='' placeholder='Access name'/></div>");

                var $select = $("<div class='col-xs-6'><select class='form-control array-item col-xs-6' onchange='idChange(this);'></div>");
                $select.children("select").data("item", item);

                if(typeof item.enum == "object" && item.enum.action != null) {
                    callFunctionAfterAjax(
                        item.enum.action,
                        "GET",
                        null,
                        {item: item, select: $select.children("select")},
                        function(context, data){
                            for(var d in data){
                                context.select.append("<option value='"  + (data[d].key != null ? data[d].key : data[d].id) + "' >" + (data[d].id != null ? data[d].id : data[d].key) +"</option>")
                            }

                            context.select.trigger("onchange");
                        }
                    );
                }

                var $div = $("<div class='panel panel-default col-xs-12''><div class='panel-body configurations-container'></div></div>")
                formElement.append($select);

                formElement.append($div);
            break;
            case "idconfiguration":
                var $select = $("<select class='form-control array-item col-xs-6'  name='" + item.id + "' onchange='idChange(this);' >");
                $select.data("item", item);

                if(typeof item.enum == "object" && item.enum.action != null) {
                    callFunctionAfterAjax(
                        item.enum.action,
                        "GET",
                        null,
                        {item: item, select: $select},
                        function(context, data){
                            for(var d in data){
                                context.select.append("<option value='"  + (data[d].key != null ? data[d].key : data[d].id) + "' >" + (data[d].id != null ? data[d].id : data[d].key) +"</option>")
                            }

                            context.select.trigger("onchange");
                        }
                    );
                }

                var $div = $("<div class='panel panel-default col-xs-12''><div class='panel-body configurations-container'></div></div>")
                formElement.append($select);

                formElement.append($div);
            break;
            case "string":
                formElement.append("<input type='text' class='form-control' name='" + item.id + "' value='" + (item.value == null ? "" : item.value) + "' />");
            break;
        }

        formElement.appendTo(container);
    }
}

function idChange(element) {
    var $element = $(element);
    var item = $element.data("item");
    if(item  != null && item.action != null) {
            var container = $element.closest(".form-group");
            var configContainer = container.find(".configurations-container").empty();
            callFunctionAfterAjax(
                item.action.replace("{0}", $element.children("option:selected").text()),
                "GET",
                null,
                {item: item, container: configContainer},
                function(context, data){
                    var config = [];
                    for(var i in data.configuration) {
                    var c = data.configuration[i] ;
                        if(c.key != "id" && c.key != "source-id") {
                            config.push(c);
                        }
                    }
                     _renderFormContent(context.container, config, "");
                }
            );
    }
}

function addArrayElement(element, event) {
    var $element = $(element);
    var container = $element.closest(".form-group");
    var draft = container.children(".array-draft");
    var addButtons = container.children(".add-button");
    var clone = draft.clone().removeClass("array-draft").addClass("array-item");
    clone.find("input").val("");
    clone.find(".configurations-container").empty();
    var draftSelect = draft.find("select.array-item ");
    var cloneSelect = clone.find("select.array-item ");
    cloneSelect.data("item", draftSelect.data("item"));

    clone.insertBefore(addButtons);

    cloneSelect.trigger("onchange");
}

function removeArrayElement(element, event) {
    var $element = $(element);
    var container = $element.closest(".array-item");

    if(container.length > 0) {
        container.remove();
    } else {
        container = $element.closest(".array-draft");
        var formGroup = $element.closest(".form-group");
        var items = formGroup.children(".array-item");

        if(container.length > 0 && items.length == 0) {
            container.find("input").val("");
            container.find(".configurations-container").empty();
        } else if(container.length > 0) {
            container.remove();
            items.eq(0).removeClass("array-item").addClass("array-draft");
        }
    }
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

function _renderForm(form, config){
    var $detail = $("#detail");
    $detail.empty();

    var $form = $("<form onsubmit='formSubmit(event)' onreset='formReset(event)'></form>");
    $form.appendTo($detail);

    _renderFormContent($form, config, "");

    $form.data("config", config);
    $form.data("data", form);

    $form.append("<button type='reset' class='btn btn-default'>Cancel</button><button type='submit' class='btn btn-primary'>Submit</button>");
}

function _renderFormContent(container, config, prefix){
    for(var i in config){
        var formElement = $("<div class='form-group'></div>");

          if(config[i].requirement.toLowerCase() == "hidden") {
                formElement.append("<input type='hidden' class='form-control' name='" + prefix + config[i].key + "' value='" + config[i].value + "' />");
            } else  {
               formElement.append("<label class='control-label' for='" + prefix + config[i].key + "'>" +  config[i].key +  "(" + config[i].requirement + ") </label>");

               switch(config[i].type.toLowerCase()){
                        case "options":
                        var $select = $("<select class='form-control' name='" + prefix + config[i].key + "'>");

                        for(var op in config[i].options){
                            $select.append("<option>" +config[i].options[op] +"</option>")
                        }

                        formElement.append($select);

                        break;
                        case "boolean":
                            formElement.append("<input type='checkbox' class='form-control' name='" + prefix + config[i].key + "' value='" + config[i].value + "' />");
                        break;
                        case "integer":
                        case "double":
                        case "string":
                            formElement.append("<input type='text' class='form-control' name='" + prefix + config[i].key + "' value='" + config[i].value + "' />");

                        break;
                    }
                }
        formElement.appendTo(container);
    }
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
            _renderForm(data.form, config);
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




