var colors = ["#000000", "#FFFF00", "#1CE6FF", "#FF34FF", "#FF4A46", "#008941", "#006FA6", "#A30059",
        "#FFDBE5", "#7A4900", "#0000A6", "#63FFAC", "#B79762", "#004D43", "#8FB0FF", "#997D87"];

$(document).ready(function(){

        $("#errors").hide();
        setEditor("editor1","scala"); 
        $("#fileList").children("li").each(function(){
            installTabClickedAction($(this));
        })


        $("#compileBtn").click(function(){
            updateCode(0);
        });

        $("#populateBtn").click(function(){
            updateCode(1);
        });

        $("#runBtn").click(function() {
            updateCode(2);
        });


        $("#plusFile").click(function(){
            newFile(); 
        });
})

var setEditor = function(editorId,mode){
    var editor = ace.edit(editorId);
    editor.setTheme("ace/theme/monokai");
    changeEditorMode(editor, mode);
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
var newFile = function(){
    var li = $("<li class='active'><a href='#'></a></li>");
    var idx = $("#fileList").children("li").size() + 1;
    li.children().each(function(){
        $(this).text('test'+idx+'.scala');
    });
    $("#fileList").children(".active").removeClass("active"); 
    li.attr('id','fileName' + idx);
    $("#fileList").append(li);
    installTabClickedAction(li);
    var code = $("<textarea class='code active' rows='18'></textarea>");
    code.attr('id','code' + idx);
    $("#workspace").children(".active").each(function(){
        $(this).removeClass("active");
        $(this).hide();
    })
    $("#workspace").append(code);
    var editor = $("<div class='editor active' id='editor"+idx+"'></div>");
    $("#workspace").append(editor);
    setEditor("editor"+idx,"scala");
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
    input.blur(function() {
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
     });
}
var installTabClickedAction = function(tab){
    tab.click(function(){

        if($(this).hasClass("active")){
        
            //edit the file name
            enableEditingTabName($(this));
        }
        else{
            
            //switch to other tabs
            $("#fileList").children(".active").each(function(){
                $(this).removeClass("active");
            })

            tab.addClass("active");

            var idx = tab.attr('id').slice(-1);
            $("#workspace").children(".active").each(function(){
                $(this).removeClass("active");
                $(this).hide(); 
            })
            $("#code"+idx).addClass("active");
            $("#editor"+idx).show();
            $("#editor"+idx).addClass("active");
            ace.edit("editor"+idx).focus();
        }
    })
}

var getAllFiles = function(){
    var files = {}
    $("#fileList").children("li").each(function(index){
        var idx = $(this).attr('id').slice(-1);
        var codeId = "editor" + idx;
        var codeName = $(this).text();
        files[codeName] = ace.edit(codeId).getValue();
    })

    return JSON.stringify(files);
    
}

var updateCode = function(event){

    //jsRoutes.controllers.Application.updateCode($("#code1").text()).ajax(callback);
    var rURL;
    var onSuccess;
    if (event == 0) {
        rURL = '/compileCode';
        onSuccess = onCompileSuccess;
    } else if (event == 1) {
        rURL = '/populate'
        onSuccess = onPopulateSuccess;
    } else {
        rURL = '/runCode';
        onSuccess = onRunSuccess;
    }

    var callback = {
        success : onSuccess,
        error : onError
    }

    $.ajax({
        type : 'POST',
        url : rURL,
        headers: { 
                'Accept': 'application/json',
                'Content-Type': 'application/json' 
                },
        data : getAllFiles(),
        success : onSuccess,
        error: onError
        });
};

var generatePopulatedGraphFromJson = function(data) {

    $('#populatedGraphContainer').remove();
    $('#graphParent2').html('<div id="populatedGraphContainer"></div>');
    var s = new sigma({renderer: {
        container: document.getElementById('populatedGraphContainer'),
        type: 'canvas'
    },
        settings: {
        edgeLabelSize: 'proportional',
        labelThreshold: 0
    }});
    var nodeId = 0;
    var nodeDict = {};
    var nodePropertyCount = {};
    var totalNumNodes = 0;
    for(var nodeGroup in data['nodes']) {
        totalNumNodes += data['nodes'][nodeGroup].length;
    }
<<<<<<< HEAD
    var getNodeByLabel = function(label){
        var id = nodeDict[label];
        return s.graph.nodes(id);
    }
    var nodeGroupCount = 0;
    for(var nodeGroup in data['nodes']) {

        nodeGroupCount++;
        for(var node in data['nodes'][nodeGroup]) {
            nodePropertyCount[data['nodes'][nodeGroup][node]] = 0;
            nodeDict[data['nodes'][nodeGroup][node]] = 'n' + ++nodeId;
            s.graph.addNode({
                id: 'n' + nodeId,
                label: data['nodes'][nodeGroup][node],
                size: 3,
                x: Math.cos(2 * nodeId * Math.PI / totalNumNodes),
                y: Math.sin(2 * nodeId * Math.PI / totalNumNodes),
                color: colors[nodeGroupCount % colors.length]
            });
            
        };
=======

    var colorGroup = ["#ffff66", "#ff99bb"];
    var nodeGroupCount = 0;
    for(var nodeGroup in data['nodes']) {
    nodeGroupCount++;
    for(var node in data['nodes'][nodeGroup]) {
                alert(data['nodes'][nodeGroup][node])
                nodeDict[data['nodes'][nodeGroup][node]] = 'n' + ++nodeId;
                s.graph.addNode({
                    id: 'n' + nodeId,
                    label: data['nodes'][nodeGroup][node],
                    size: 3,
                    x: Math.cos(2 * nodeId * Math.PI / totalNumNodes),
                    y: Math.sin(2 * nodeId * Math.PI / totalNumNodes),
                    color: colorGroup[nodeGroupCount % colorGroup.length]
                });
            };

>>>>>>> Implement running app front end
    };

    var edgeId = 0;
    for(var source in data['edges']) {
        for(var targetNode in data['edges'][source]) {

            s.graph.addEdge({
                id: 'e' + edgeId++,
                source: nodeDict[source],
                target: nodeDict[data['edges'][source][targetNode]],
                type: 'curve'
            });
        }
    };
<<<<<<< HEAD

    var propertyCount = 0
    for(var node in data['properties']){
        for(var propertyIndex in data['properties'][node]){
            propertyCount ++;
            ++nodePropertyCount[node];
            var parentNode = getNodeByLabel(node);
            s.graph.addNode({
                id: 'p' + propertyCount,
                label: data['properties'][node][propertyIndex],
                size: 1,
                x: parentNode.x + 0.5 * Math.cos(2 * nodePropertyCount[node] * Math.PI / 6),
                y: parentNode.y + 0.5 * Math.sin(2 * nodePropertyCount[node] * Math.PI / 6),
                color: colors[propertyCount % colors.length]
            });
            s.graph.addEdge({
                id: 'e'+ edgeId++,
                source: nodeDict[node],
                target: 'p' + propertyCount,
                type: 'curve'
            });
        }
    }

=======
>>>>>>> Implement running app front end
    s.refresh();
    $("#populatedGraphContainer").css("position","absolute");
}

var generateSchemaGraphFromJson = function(data){
    $('#schemaGraphContainer').remove();
    $('#graphParent1').html('<div id="schemaGraphContainer"></div>');
    var s = new sigma({renderer: {
        container: document.getElementById('schemaGraphContainer'),
        type: 'canvas'
    },
        settings: {
        edgeLabelSize: 'proportional',
        labelThreshold: 0
    }});
    var nodeId = 0;
    var nodeDict = {};
    var nodePropertyCount = {};

    for(var node in data['nodes']){
        nodePropertyCount[data['nodes'][node]] = 0;
        nodeDict[data['nodes'][node]] = 'n'+ ++nodeId;
        s.graph.addNode({
            id: 'n'+ nodeId,
            label: data['nodes'][node],
            size: 3,
            x: Math.cos(2 * nodeId * Math.PI / data['nodes'].length),
            y: Math.sin(2 * nodeId * Math.PI / data['nodes'].length),
            color: "#ec5148"
        });
    };

    var edgeId = 0;
    for(var edge in data['edges']){
        s.graph.addEdge({
            id: 'e'+ edgeId++,
            // Reference extremities:
            source: nodeDict[data['edges'][edge][0]],
            target: nodeDict[data['edges'][edge][1]],
            type: 'curve'
        });
    };

    var getNodeByLabel = function(label){
        var id = nodeDict[label];
        return s.graph.nodes(id);
    }

    //generate properties nodes and edges
    for(var property in data['properties']){
        ++nodePropertyCount[data['properties'][property]];
        var parentNode = getNodeByLabel(data['properties'][property]);
        s.graph.addNode({
            id: 'p' + property + nodePropertyCount[data['properties'][property]],
            label: property,
            size: 1,
            x: parentNode.x + 0.5 * Math.cos(2 * nodePropertyCount[data['properties'][property]] * Math.PI / 6),
            y: parentNode.y + 0.5 * Math.sin(2 * nodePropertyCount[data['properties'][property]] * Math.PI / 6),
            color: "#0000ff"
        });
        s.graph.addEdge({
            id: 'e'+ edgeId++,
            source: nodeDict[data['properties'][property]],
            target: 'p' + property + nodePropertyCount[data['properties'][property]],
            type: 'curve'
        });
    }

    s.refresh();
    $("#schemaGraphContainer").css("position","absolute");
}

var displayOutput = function(data) {
    document.getElementById('tab3').innerHTML = '';
    var para = document.createElement("P");
    var text = document.createTextNode(data["stdout"]);
    para.appendChild(text)
    document.getElementById('tab3').appendChild(para)
}

jQuery(document).ready(function() {
    $('.tabs .tab-links a').on('click', function(e)  {
        var currentAttrValue = jQuery(this).attr('href');
        changeTab(currentAttrValue)
        e.preventDefault();
    });
});

var changeTab = function(currentAttrValue) {

    // Show/Hide Tabs
    $('.tabs #' + currentAttrValue).show().siblings().hide();
    $("." + currentAttrValue).addClass('active');
    $("." + currentAttrValue).siblings().removeClass('active');


}

var alertError = function(data) {
    alert(JSON.stringify(data));
    if(data['error']){
        var message = "";
        for(var index in data['error']){
            for(var index2 in data['error'][index]){
                message += data['error'][index][index2] + "<br>";
            }
        }
        $("#errors").html(message);
        $("#errors").show();
    }else{
        $("#errors").hide();
    }
}

var onCompileSuccess = function(data){
    alertError(data);
<<<<<<< HEAD
    changeTab("tab1")
=======
    //changeTab("#tab1")
>>>>>>> Implement running app front end
    generateSchemaGraphFromJson(data);
}

var onPopulateSuccess = function(data) {
    alertError(data);
<<<<<<< HEAD
    changeTab("tab2")
=======
    //changeTab("#tab2")
>>>>>>> Implement running app front end
    generatePopulatedGraphFromJson(data);
}

var onRunSuccess = function(data) {
    alertError(data);
    //changeTab("#tab3")
    displayOutput(data)
}

var onError = function(data){
    alert("error"+data);
}
