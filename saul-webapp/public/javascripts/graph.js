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
});
var enableColoringNeighbors = function(s){
    s.graph.nodes().forEach(function(n) {
        n.originalColor = n.color;
    });
    s.graph.edges().forEach(function(e) {
        e.originalColor = e.color;
    });

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
        type: 'canvas'
    },
        settings: {
        edgeLabelSize: 'proportional',
        labelThreshold: 10
    }});

    $('#graphParent2').parent().find(".recenter").click(function(){
        generatePopulatedGraphFromJson(jsonData);
    });
    s.bind('overNode',function(e){
        $("#nodetext2").text(e.data.node.label);
    });
    var dragListener = sigma.plugins.dragNodes(s, s.renderers[0]);
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
        totalNumNodes += data['nodes'][nodeGroup].length;
    }

    var getNodeByLabel = function(label){
        var id = nodeDict[label];
        return s.graph.nodes(id);
    }
    var selectedNodes = {}
    var selectedProps = {}
    var nodeGroupCount = 0;
    for(var nodeGroup in data['nodes']) {
        nodeGroupCount++;
        for(var node in data['nodes'][nodeGroup]) {
            var nodeValue = data['nodes'][nodeGroup][node]
            nodePropertyCount[nodeValue] = 0;
            var curId = ++nodeId
            nodeDict[nodeValue] = 'n' + curId;
            var nodeColor = colors[nodeGroupCount % colors.length]
            if(selectedData != null) {
                if(!(nodeGroup in selectedData['nodes'])) {
                    nodeColor = '#eee'
                } else {
                    if($.inArray(nodeValue, selectedData['nodes'][nodeGroup]) == -1) {
                        nodeColor = '#eee'
                    } else {
                        selectedNodes['n' + curId] = true
                        selectedProps[nodeValue] = selectedData['properties'][nodeValue]
                    }
                }
            }
            s.graph.addNode({
                id: 'n' + nodeId,
                label: data['nodes'][nodeGroup][node],
                size: 3,
                x: (totalNumNodes > 30 ? 3*(3+(nodeId / 30)) : 1) * Math.cos(2 * nodeId * Math.PI / (totalNumNodes > 30 ? 30 : totalNumNodes)),
                y: (totalNumNodes > 30 ? 3*(3+(nodeId / 30)) : 1) * Math.sin(2 * nodeId * Math.PI / (totalNumNodes > 30 ? 30 : totalNumNodes)),
                color: nodeColor,
                assignedColor: colors[nodeGroupCount % colors.length]
            });
            
        };
    };

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
                x: parentNode.x + 1 * Math.cos(2 * nodePropertyCount[node] * Math.PI / 6),
                y: parentNode.y + 1 * Math.sin(2 * nodePropertyCount[node] * Math.PI / 6),
                color: nodeColor,
                assignedColor: colors[propertyCount % colors.length]
            });
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

    enableColoringNeighbors(s);
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

    $('#graphParent1').parent().find(".recenter").click(function(){
        generateSchemaGraphFromJson(data);
    });
    s.bind('overNode',function(e){
        $("#nodetext1").text(e.data.node.label);
    });
    var dragListener = sigma.plugins.dragNodes(s, s.renderers[0]);
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