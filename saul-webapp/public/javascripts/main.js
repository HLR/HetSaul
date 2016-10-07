$(document).ready(function(){

    installGraphsTabClick();
    installLeftPanelClick();

    $("#hoverBar").click(function() {
        $('.page-header').toggle();
    });
    $("#errors").hide();

    setupExamples();

})
var setupExamples = function (){
    $("#ToyExample").click(function(){
        deleteAllFiles();
        var content = ["package test","","import edu.illinois.cs.cogcomp.saul.datamodel.DataModel","import logging.Logger.{ error, info }", "","object $$$$$$ extends DataModel {","","    val firstNames = node[String]","    val lastNames = node[String]","    val name = edge(firstNames,lastNames)","    val prefix = property(firstNames,\"prefix\")((s: String) => s.charAt(1).toString)","    val prefix2 = property(firstNames,\"prefix\")((s: String) => s.charAt(0).toString)","","    def main(args : Array[String]): Unit ={","        firstNames.populate(Seq(\"Dave\",\"John\",\"Mark\",\"Michael\"))","        lastNames.populate(Seq(\"Dell\",\"Jacobs\",\"Maron\",\"Mario\"))","        name.populateWith(_.charAt(0) == _.charAt(0))","    }","}"];
        newFile(content);
    });

    var addButtons = function(data){

        for(var pro in data){
            $("#collapseExamples").append('<button type="button" class="btn btn-info well2" id="' + data[pro] +'">' +data[pro]+'</button>');
            
            $("#"+data[pro]).click(function(event){

                $.ajax({
                    type : 'POST',
                    url : "/getExampleFile",
                    headers: { 
                            'Accept': 'application/json',
                            'Content-Type': 'application/json' 
                            },
                    data: JSON.stringify({
                        "projectName": event.target.id
                    }),
                    success : function(data){
                        deleteAllFiles();
                        for(var idx in data){
                            console.log(idx);
                            console.log(data[idx]);
                            newFileWithFilename(idx,data[idx]);
                        }
                    },
                    error: onError
                });
            });
        }
    }
    $.ajax({
        type : 'POST',
        url : "/getExamples",
        headers: { 
                'Accept': 'application/json',
                'Content-Type': 'application/json' 
                },
        data: JSON.stringify({
            "data": ""
        }),
        success : addButtons,
        error: onError
    });


}

var installLeftPanelClick = function(){
    setEditor("editor1","scala"); 
    $("#fileList").children("li").each(function(){
        installTabClickedAction($(this));
    });

    $("#selectedFile").change(function() {
        if (!window.FileReader) {
            alert('Your browser is not supported');
            return false;
        }
        var input = $("#selectedFile").get(0);

        var reader = new FileReader();
        if (input.files.length) {
            var textFile = input.files[0];

            reader.readAsText(textFile);
            $(reader).on('load', processFile);
        }else {
            alert('Please upload a file before continuing')
        } 
    });

    $("#compileBtn").click(function(){
        updateCode(0);
    });

    $("#populateBtn").click(function(){
        updateCode(1);
    });

    $("#runBtn").click(function() {
        updateCode(2);
    });

    $("#queryBtn").click(function() {
        updateCode(3);
    });

    $("#visualizeBtn").click(function() {
        updateCode(-1);
    });

    $("#newmodel").click(function(){
        var content = ["package test","","import edu.illinois.cs.cogcomp.saul.datamodel.DataModel","","object $$$$$$ extends DataModel {","","}"];
        newFile(content);
    });
    $("#newcla").click(function(){
        var content = ["package test","","import edu.illinois.cs.cogcomp.saul.classifier.Learnable","","object $$$$$$ {","","    object $$$$$$Classifier extends Learnable[???](yourDataModel) {","    ","    }","}"];
        newFile(content);
    });
    $("#newapp").click(function(){
        var content = ["package test","","object $$$$$$ {","","    def main(args: Array[String]) {","    ","    }","}"];
        newFile(content);
    });

    $("#deleteFile").click(function(){
        deleteFile();
    });
}
function installGraphsTabClick(){
    $("#gtabs").children("li").each(function(){
        $(this).click(function(){
            $("#gtabs").children(".active").each(function(){
                $(this).removeClass("active");
            });
            $(this).addClass("active");
            $(".tab-content").children(".active").each(function(){
                $(this).removeClass("active");
                $(this).hide();
            });
            $("#"+ $(this).attr("value")).addClass("active").show();
        });
    });
}
function processFile(e) {
    var file = e.target.result,
        results;
    if (file && file.length) {
        results = file.split("\n");
        newFile(results);
    }
}


var deleteAllFiles = function(){
    while($("#fileList").children(".active").length != 0){
        deleteFile();
    }
}
var deleteFile = function(){
    $("#fileList").children(".active").each(function(){
        $(this).remove();
    });
    $("#workspace").children(".active").each(function(){
        $(this).remove();
    });
    $("#fileList").children("li").first().addClass("active");
    $("#workspace").children().first().addClass("active");
    $("#workspace").children().first().show();

}
var setEditor = function(editorId,mode, content){
    var editor = ace.edit(editorId);
    editor.setTheme("ace/theme/monokai");
    changeEditorMode(editor, mode);
    if(content) editor.getSession().getDocument().insertLines(0,content);
    editor.resize(true);
    editor.focus();
}

var changeEditorMode = function(editor, mode){
    var mode;
    if(mode == "scala"){
        mode = require("ace/mode/scala").Mode;
    }
    if(mode == "java"){
    
        mode = require("ace/mode/java").Mode;
    }
    editor.getSession().setMode(new mode());
}

var newFileWithFilename = function(filename,content){
    newFile(content);
    if(endsWith(filename,".java") || endsWith(filename,".scala")){
        $("#fileList").find(".active a").text(filename);
    }
    else{
        $("#fileList").find(".active a").text(filename+".scala");
    }
}
var newFile = function(content){
    content = content || [];
    var li = $("<li class='active'><a href='#'></a></li>");
    var idx = $("#fileList").children("li").size() + 1;
    li.children().each(function(){
        $(this).text('test'+idx+'.scala');
    });
    $("#fileList").children(".active").removeClass("active"); 
    li.attr('id','fileName' + idx);
    $("#fileList").append(li);
    installTabClickedAction(li);

    $("#workspace").children(".active").each(function(){
        $(this).removeClass("active");
        $(this).hide();
    })
    var editor = $("<div class='editor active' id='editor"+idx+"'></div>");
    $("#workspace").append(editor);
    setEditor("editor"+idx,"scala",content);
}

//check string end with suffix
function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

var enableEditingTabName = function(tab){

    var text = tab.text();
    var input = $("<input id='tempName' type='text' value='" + text + "' />");
    tab.find("a").hide();
    tab.append(input);

    input.select();
    input.bind('blur keyup',function(e) {  
        if (e.type == 'blur' || e.keyCode == '13'){
            var text = $('#tempName').val();
            var label = $('#tempName').parent().find("a");
            var idx = tab.attr('id').slice(-1);
            if(endsWith(text,".java")) {
                label.text(text);
                changeEditorMode(ace.edit("editor"+idx),"java");
            }
            if(endsWith(text,".scala")){
                label.text(text);
                changeEditorMode(ace.edit("editor"+idx),"scala");
            }
            label.show();
            $('#tempName').remove()
            $("#fileList .active").removeData("executing");

        }
     });
}
var installTabClickedAction = function(tab){
    tab.click(function(){

        if($(this).hasClass("active")){        
            var $this = $(this);
            if ($this.data("executing")) return;
            $this.data("executing", true);
            //edit the file name
            enableEditingTabName($(this));
        }
        else{        
            //switch to other tabs
            $("#fileList").children(".active").each(function(){
                $(this).removeClass("active");
            })
            tab.addClass("active");
            var idx = tab.attr('id').match(/\d+$/)[0];
            $("#workspace").children(".active").each(function(){
                $(this).removeClass("active");
                $(this).hide(); 
            })
            $("#editor"+idx).show();
            $("#editor"+idx).addClass("active");
            ace.edit("editor"+idx).focus();
        }
    })
}

var getAllFiles = function(){
    var files = {}
    $("#fileList").children("li").each(function(index){
        var idx = $(this).attr('id').match(/\d+$/)[0];
        var codeId = "editor" + idx;
        var codeName = $(this).text();
        files[codeName] = ace.edit(codeId).getValue();
    })
    return JSON.stringify(files);
}

var updateCode = function(event){
    var rURL;
    var onSuccess;
    if (event == 0) {
        rURL = '/compileCode';
        onSuccess = onCompileSuccess;
    } else if (event == 1) {
        rURL = '/populate'
        onSuccess = onPopulateSuccess;
    } else if (event == 2) {
        rURL = '/runCode';
        onSuccess = onRunSuccess;
    } else if (event == 3) {
        rURL = '/query';
        onSuccess = onQuerySuccess;
    } else if (event == -1) {
        rURL = '/visualize';
        onSuccess = onVisualizeSuccess;
    }

    var callback = {
        success : onSuccess,
        error : onError
    }
    var query = $("#query")[0].value
    var files = getAllFiles()
    var dataJson = files
    if(event == 3) {
        dataJson = JSON.stringify({
            "files": files,
            "query": query
        })
    }
    $("#pbar").show();
    $.ajax({
        type : 'POST',
        url : rURL,
        headers: { 
                'Accept': 'application/json',
                'Content-Type': 'application/json' 
                },
        data : dataJson,
        success : onSuccess,
        error: onError
        });
};

var displayOutput = function(data) {
    $("#tab3").addClass("active");
    $("#tab3").html('')
    var info = $("<p></p>").text("Info: \n" + data["stdout"]);
    var error = $("<p style='color:red;'></p>").text("Error: \n" + data["stderr"]);
    $("#tab3").append(info);
    $("#tab3").append(error);
}


var alertError = function(data) {
    console.log(JSON.stringify(data));
    if(data['error']){
        var message = "";
        if(data['error'].constructor === Array){
            for(var index in data['error']){
                for(var index2 in data['error'][index]){
                    message += data['error'][index][index2] + "<br>";
                }
            }
        }else{
                message += data['error'] + "<br>";
        }
        $("#errors").html(message);
        $("#errors").show();
    }else{
        $("#errors").hide();
    }
}

var onCompileSuccess = function(data){
    $("#pbar").hide();
    alertError(data);
    $("#gtab1").click();
    generateSchemaGraphFromJson(data);
    
}

var onPopulateSuccess = function(data) {
    $("#pbar").hide();
    alertError(data);
    $("#gtab2").click();
    generatePopulatedGraphFromJson(data);    
}

var onRunSuccess = function(data) {
    $("#pbar").hide();
    alertError(data);
    $("#gtab3").click();
    displayOutput(data);
}

var onQuerySuccess = function(data) {
    onPopulateSuccess(data);
}

var onVisualizeSuccess = function(data) {
    $("#pbar").hide();
    alertError(data);
    $("#gtab1").click();
    generateSchemaGraphFromJson(data['dataModelSchema']);
    if(data['populatedModel'] != null){
        $("#gtab2").click();
        generatePopulatedGraphFromJson(data['populatedModel']);
    }  
    if(data['log'] != null){ 
        ("#gtab3").click();
        displayOutput(data['log']);
    }
}

var onError = function(data){
    $("#pbar").hide();
    alert("error"+data);
}
