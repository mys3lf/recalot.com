$(window).ready(function(){
    $("#run").click(function(){
        runTests();
    })

})

var headDefinition = ["name", "url", "method", "params", "result", "actual"];


function runTests(){
     var resultBody = $("#resultBody");
     resultBody.empty();

    if(tests != null){
        runTestSeq(0);
    }
}

function runTestSeq(index){
    if(index < tests.length){
         runTest(tests[index], index);
    }
}

function reRunTest(element){
    var e = $(element);
    runTest(e.data("result"));
}

function runTest(test, index){
console.log(test);
     $.ajax({url: test.url,
     method : test.method,
     data: test.params,
     context: {test: test, index: index} ,
     complete: function(data, status){
        var contentType = data.getResponseHeader("content-type") || "";
        if(contentType != null && contentType.indexOf(";") != -1) {
            contentType = contentType.substring(0, contentType.indexOf(";"));
        }

        this.test.actual = {status: data.status, length: data.responseText.length, contentType: contentType, text: data.responseText};
        addResultRow(this.test);

        if(this.index != null){
            runTestSeq(++this.index);
        }

    }});
}

function addResultRow(result){
    var resultBody = $("#resultBody");

    var tr = $("<tr></tr>");
    var td = $("<td></td>");


    for(var i in headDefinition){
        var d = headDefinition[i];
        var clone = td.clone();

        var r = result[d];

        if(r != null){
            if(typeof r == "string"){
               clone.html(r)
            } else {
               clone.html(getText(r));
            }
        }

        clone.appendTo(tr);
    }
   var resultTd = td.clone();
   resultTd.appendTo(tr);
   var span = $("<span class='glyphicon'> </span>");
   span.appendTo(resultTd);

    if(result["result"] != null
    && result["result"].status == result["actual"].status
    && ((result["result"].contentType != null && result["result"].contentType == result["actual"].contentType) || (result["result"].contentType == null)) &&
    ((result["result"].text != null && result["result"].text == result["actual"].text)
    || (result["result"].length != null && result["result"].length == result["actual"].length)
    || (result["result"].minLength != null && result["result"].minLength < result["actual"].text.length)
    || (result["result"].minLength == null && result["result"].text == null && result["result"].length == null))){
       span.addClass("glyphicon-ok");
       tr.addClass("success");
    } else {
       span.addClass("glyphicon-remove");
       tr.addClass("danger");
    }


    var reRun = $("<td><a class='btn btn-default btn-xs' onclick='reRunTest(this);' >Run</a></td>");
    reRun.appendTo(tr);
    reRun.children("a").data("result", result);

    tr.prependTo(resultBody);

}

function getText(obj) {
    var a = [];

    for(var i in obj){
        a.push(i + ":" + obj[i]);
    }
    return a.join("<br />");
}