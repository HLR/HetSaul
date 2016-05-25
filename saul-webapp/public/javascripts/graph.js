var colors = ["#000000", "#FFFF00", "#1CE6FF", "#FF34FF", "#FF4A46", "#008941", "#006FA6", "#A30059",
        "#FFDBE5", "#7A4900", "#0000A6", "#63FFAC", "#B79762", "#004D43", "#8FB0FF", "#997D87"];
$(document).ready(function(){
    sigma.classes.graph.addMethod('neighbors', function(nodeId) {
        var k,
        neighbors = {},
        index = this.allNeighborsIndex[nodeId] || {};

        for (k in index)
        neighbors[k] = this.nodesIndex[k];

        return neighbors;
    });

    //enable div for showing statistics
    new Tether({
        element: '#statisticsWrapper',
        target: '.tabs',
        attachment: 'bottom right',
        targetAttachment: 'middle left',
    });
    $('#statisticsWrapper').hide();

});
var enableColoringNeighbors = function(s){
    s.graph.nodes().forEach(function(n) {
        n.originalColor = n.color;
    });
    s.graph.edges().forEach(function(e) {
        e.originalColor = e.color;
    });

    //click to show connecting nodes
    s.bind('clickNode', function(e) {

        var nodeId = e.data.node.id,
            toKeep = s.graph.neighbors(nodeId);
        toKeep[nodeId] = e.data.node;

        s.graph.nodes().forEach(function(n) {
          if (toKeep[n.id])
            n.color = n.assignedColor;
          else
            n.color = '#eee';
        });

        s.graph.edges().forEach(function(e) {
          if (toKeep[e.source] && toKeep[e.target])
            e.color = e.assignedColor;
          else
            e.color = '#eee';
        });
        s.refresh();
    });

    //click to color the neighboring nodes
    s.bind('clickStage', function(e) {
        s.graph.nodes().forEach(function(n) {
          n.color = n.originalColor;
        });

        s.graph.edges().forEach(function(e) {
          e.color = e.originalColor;
        });

        // Same as in the previous event:
        s.refresh();
    });
}


var generatePopulatedGraphFromJson = function(jsonData) {

    $('#populatedGraphContainer').remove();
    $('#graphParent2').html('<div id="populatedGraphContainer"></div>');

    var s = new sigma({renderer: {
        container: document.getElementById('populatedGraphContainer'),
        type: 'canvas',
    },
        settings: {
        edgeLabelSize: 'proportional',
        labelThreshold: 100,
        borderSize:1,
        labelHoverBGColor:"node",
        zoomingRatio: 1.3,
        zoomMax: 1.5
    }});

    $('#graphParent2').parent().find(".recenter").click(function(){
        generatePopulatedGraphFromJson(jsonData);
    });
    s.bind('overNode',function(e){
        $("#nodetext1").text(e.data.node.label);
    });
    s.bind('clickNode', function(e) {
        $('#statisticsWrapper').show();
        var index = e.data.node.id;
        $('#statisticsWrapper').empty();
        $('#statisticsWrapper').append('<span id="statCloseBtn" class="glyphicon glyphicon-remove" aria-hidden="true"></span>');
        $("#statCloseBtn").click(function(){
            $('#statisticsWrapper').hide();
        });
        for(var key in s.graph.nodes(index).stat){
            $('#statisticsWrapper').append("<h4>·"+key+": "+s.graph.nodes(index).stat[key]+"</h4>");
        };
    });

    //disable dragging behavior for now
    //var dragListener = sigma.plugins.dragNodes(s, s.renderers[0]);
    /*dragListener.bind('drag', function(event) {
        s.graph.neighbors(event.node.id).forEach(function(e) {
            e.x = event.node.x;
            e.y = event.node.y;
        })
    });
*/

    var data = jsonData['full']
    var selectedData = jsonData['selected']
    var nodeId = 0;
    var nodeDict = {};
    var nodePropertyCount = {};
    var totalNumNodes = 0;
    for(var nodeGroup in data['nodes']) {
        totalNumNodes += Object.keys(data['nodes'][nodeGroup]).length;
    }
    var getNodeByLabel = function(label){
        var id = nodeDict[label];
        return s.graph.nodes(id);
    }
    var selectedNodes = {}
    var selectedProps = {}
    var nodeGroupCount = 0;

    //parse json
    for(var nodeGroup in data['nodes']) {
        nodeGroupCount++;
        for(var node in data['nodes'][nodeGroup]) {
            nodePropertyCount[node] = 0;
            var curId = ++nodeId;
            nodeDict[node] = 'n' + curId;
            var nodeColor = colors[nodeGroupCount % colors.length];
            if(selectedData != null) {
                if(!(nodeGroup in selectedData['nodes'])) {
                    nodeColor = '#eee';
                } else {
                    if(!(node in selectedData['nodes'][nodeGroup])) {
                        nodeColor = '#eee';
                    } else {
                        selectedNodes['n' + curId] = true;
                        selectedProps[node] = selectedData['properties'][node];
                    }
                }
            }

            //add main nodes
            s.graph.addNode({
                id: 'n' + nodeId,
                label: node+": "+data['nodes'][nodeGroup][node],
                size: 3,
                x: 30 * Math.cos(2 * nodeId * Math.PI / totalNumNodes),
                y: 30 * Math.sin(2 * nodeId * Math.PI / totalNumNodes),
                color: nodeColor,
                assignedColor: colors[nodeGroupCount % colors.length]
            });
            var nodesArray = s.graph.nodes();
            nodesArray[nodesArray.length-1].stat = new Array();
            for(var key in jsonData['Statistics']['nodes'][nodeGroup]){
                if(key == "Frequency") continue;
                nodesArray[nodesArray.length-1].stat[key] = JSON.stringify(jsonData['Statistics']['nodes'][nodeGroup][key]);
            }
        };
    };

    //add edges
    var edgeId = 0;
    for(var source in data['edges']) {
        for(var targetNode in data['edges'][source]) {
            var edgeColor = s.graph.nodes(nodeDict[source]).color
            if(s.graph.nodes(nodeDict[data['edges'][source][targetNode]]).color == '#eee') {
                edgeColor = '#eee'
            }
            s.graph.addEdge({
                id: 'e' + edgeId++,
                source: nodeDict[source],
                target: nodeDict[data['edges'][source][targetNode]],
                type: 'curve',
                color: edgeColor
            });
        }
    };

    //add properties
    var propertyCount = 0
    for(var node in data['properties']){
        for(var propertyIndex in data['properties'][node]){
            for(var pI in data['properties'][node][propertyIndex]){
            propertyCount ++;
            ++nodePropertyCount[node];
            var nodeColor = colors[nodePropertyCount[node] % colors.length]
            //If this property is not associated with a node that's queried
            if(selectedData != null) {
                if(!(nodeDict[node] in selectedNodes)) {
                    nodeColor = '#eee'
                } else {
                    //If this property is not the queried property
                    if(!(node in selectedProps)) {
                        nodeColor = '#eee'
                    } else {
                        var find = false
                        for(var i in selectedProps[node]) {
                            if(pI in selectedProps[node][i]) {
                                find = true
                                break
                            }
                        }
                        if(!find) {
                            nodeColor = '#eee'
                        }
                    }
                }
            }
            var parentNode = getNodeByLabel(node);
            s.graph.addNode({
                id: 'p' + propertyCount,
                label: pI+": "+data['properties'][node][propertyIndex][pI],
                size: 1,
                x: parentNode.x + 10 * Math.cos(2 * nodePropertyCount[node] * Math.PI / data['properties'][node].length),
                y: parentNode.y + 10 * Math.sin(2 * nodePropertyCount[node] * Math.PI / data['properties'][node].length),
                color: nodeColor,
                assignedColor: colors[propertyCount % colors.length]
            });
            var nodesArray = s.graph.nodes();
            nodesArray[nodesArray.length-1].stat = new Array();
            for(var key in jsonData['Statistics']['properties'][pI]){
                if(key == "Frequency") continue;
                nodesArray[nodesArray.length-1].stat[key] = JSON.stringify(jsonData['Statistics']['properties'][pI][key]);
            }
            var edgeColor = s.graph.nodes(nodeDict[node]).color
            if(nodeColor == '#eee') {
                edgeColor = '#eee'
            }
            s.graph.addEdge({
                id: 'e'+ edgeId++,
                source: nodeDict[node],
                target: 'p' + propertyCount,
                type: 'curve',
                color: edgeColor,
                assignedColor: s.graph.nodes(nodeDict[node]).assignedColor
            });
            }
        }
    }

    var resize = function(){
        s.camera.goTo({
            x: 0,
            y: 0,
            angle: 0,
            ratio: 1
        });
    }

    document.getElementById('rescale-graph').removeEventListener("click", resize);
    document.getElementById('rescale-graph').addEventListener('click',resize,true);
    enableColoringNeighbors(s);
    s.refresh();

    //use forceatlas2 algorithm to layout if there are too many nodes
    if(totalNumNodes > 20){
        s.startForceAtlas2();
        setTimeout(
        function() {
        s.stopForceAtlas2();
        }, 2000);
    }
    $("#populatedGraphContainer").css("position","absolute");

    //maximize graph in a new window
    var renderNewWindow = function(){
        var newWindow = window.open('/graph','gg','toolbar=0, location=0, directories=0, status=0, scrollbars=0, resizable=1, copyhistory=0, menuBar=0', true);
        newWindow.onload = function(){ 
            newWindow.dataFromParent = jsonData;
            newWindow.init();
        };   
    }
    document.getElementById('maximize').removeEventListener("click", renderNewWindow);
    document.getElementById('maximize').addEventListener('click',renderNewWindow,true);

    var newWindow = window.open('/plot','pp','toolbar=0, location=0, directories=0, status=0, scrollbars=0, resizable=1, copyhistory=0, menuBar=0', true);
    newWindow.onload = function(){ 
        newWindow.dataFromParent = jsonData['Statistics'];
        newWindow.init();
    };

    return s;
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

    s.bind('overNode',function(e){
        $("#nodetext1").text(e.data.node.label);
    });
    //disable dragging behavior for now
    //var dragListener = sigma.plugins.dragNodes(s, s.renderers[0]);
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

    enableColoringNeighbors(s);
    s.refresh();
    $("#schemaGraphContainer").css("position","absolute");
}