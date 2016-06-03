$(document).ready(function(){
	
});

var extractInformationFromJson = function(data){
	var plots = {};
	for(var k in data){
		plots[k] = {};
		plots[k]['x'] = [];
		plots[k]['y'] = [];

		var idx = 0;
		var nicknameDict = {};
		for(var xx in data[k]['Frequency']){
			if(xx.length > 7){ 
				nicknameDict[xx] = "entity" + idx;
			}
			plots[k]['x'].push("entity" + idx);
			plots[k]['y'].push(data[k]['Frequency'][xx]);
			idx++;
		}
		plots[k]['nickname'] = nicknameDict;
	}
	return plots;
}

var generatePlotsForType = function(data,typ){

	for(var plot in data){
		var xlen = data[plot]['x'].length,
		xScale = d3.scale.ordinal(),
		yScale = d3.scale.linear(),
		range = [];

		var singleLen = 70;
		if(xlen < 3) singleLen = 140;
		// A formatter for counts.
		var formatCount = d3.format(",.0f");

		var margin = {top: 10, right: 30, bottom: 30, left: 30},
		width = singleLen*xlen - margin.left - margin.right,
		height = 350 - margin.top - margin.bottom;

		// create an array with the position of each label
		for (var k =0;k<xlen; k++){
			range.push(k*width/xlen + margin.left);
		}

		//domain is the desired labels in an array, range is the position of each label in an array
		xScale.domain(data[plot]['x'])
		.range(range);

		//axes take a scale object
		var xaxis = d3.svg.axis()
		.scale(xScale)
		.orient("bottom");

		yScale.domain([0,d3.max(data[plot]['y'])])
		.range([0,height-35]);

		var svg = d3.select(typ).append("svg")
		.attr("width", width + margin.left + margin.right)
		.attr("height", height + margin.top + margin.bottom)
		.attr("id","plot"+plot)
		.attr("class","singlePlot");

		svg.append("text")
		.attr("x",width/2+20)
		.attr("y",margin.top*2)
		.attr("text-anchor","middle")
		.attr("font-size","16px")
		.style("fill","black")
		.text(plot);

		var bar = svg.selectAll(".bar")
		.data(data[plot]['y'])
		.enter().append("g")
		.attr("class", "bar")
		.attr("transform", function(d) { return "translate(" + margin.left + "," + margin.top + ")"; });

		bar.append("rect")
		.attr("x", function(d, i) {return i * (width / xlen);})
		.attr("y", function(d){return height - yScale(d);})
		.attr("width", width / xlen - 1 )
		.attr("height", function(d) {return yScale(d);});

		bar.append("text")
		.attr("dy", "10px")
		.attr("y", function(d){return height - yScale(d)+6;})
		.attr("x",function(d, i) {return i * (width / xlen) + (width / xlen)/2;})
		.attr("text-anchor", "middle")
		.text(function(d) { return d; });

		svg.append("g")
		.attr("class", "x axis")
		.attr("transform", "translate(" + margin.left +"," + (height+margin.top) + ")")
		.call(xaxis);
	}	
}
var generatePlotsFromJson = function(data){

	var nodePlots = extractInformationFromJson(data['nodes']);
	var propertyPlots = extractInformationFromJson(data['properties']);

	generatePlotsForType(nodePlots, "#nodeWrapper");
	generatePlotsForType(propertyPlots,"#propertyWrapper");

};