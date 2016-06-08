$(document).ready(function(){
	var tour = new Shepherd.Tour({
		defaults: {
			classes: 'shepherd-element shepherd-open shepherd-theme-arrows',
			showCancelLink: true
		}
	});

	tour.addStep('s0', {
		title: 'Welcome to Saul Webapp',
		text: 'A tool for visualizing machine learning program written using Saul. Please visit our github wiki for more detail.',
		attachTo: 'h1 bottom',
	  	//advanceOn: '#compileBtn click',
	  	buttons: [
			{
	  			text: 'Exit',
	  			classes: 'shepherd-button-secondary',
	  			action: tour.cancel
	  		},
	  		{
	  			text: 'Next',
	  			action: tour.next,
	  			classes: 'shepherd-button-example-primary'
	  		}
	  	]
	});

	tour.addStep('s1', {
		title: 'Editor',
		text: 'This is your workspace a.k.a. code editor.',
		attachTo: '.panel-body right',
	  	//advanceOn: '#compileBtn click',
	  	buttons: [
	  		{
	  			text: 'Exit',
	  			classes: 'shepherd-button-secondary',
	  			action: tour.cancel
	  		},
	  		{
	  			text: 'Next',
	  			action: tour.next,
	  			classes: 'shepherd-button-example-primary'
	  		}
	  	]
	});

	tour.addStep('s11', {
		title: 'Buttons',
		text: 'You can create a new file choosing from 3 code templates, uploade an exsiting file on your computer or delete a file in the code editor',
		attachTo: '#plusFile bottom',
	  	//advanceOn: '#compileBtn click',
	  	buttons: [
	  		{
	  			text: 'Exit',
	  			classes: 'shepherd-button-secondary',
	  			action: tour.cancel
	  		},
	  		{
	  			text: 'Next',
	  			action: tour.next,
	  			classes: 'shepherd-button-example-primary'
	  		}
	  	]
	});

	tour.addStep('s2', {
		title: 'Visualization',
		text: 'This is where the visualization graphs and running results will be showing.',
		attachTo: '.tab-content left',
	  	//advanceOn: '#compileBtn click',
	  	buttons: [
	  		{
	  			text: 'Exit',
	  			classes: 'shepherd-button-secondary',
	  			action: tour.cancel
	  		},
	  		{
	  			text: 'Next',
	  			action: tour.next,
	  			classes: 'shepherd-button-example-primary'
	  		}
	  	]
	});

	tour.addStep('s3', {
		title: 'Example',
		text: 'Let\'s run an example program and demonstrate the utilities of the webapp. Click on ToyExample to advance',
		attachTo: '#ToyExample right',
		advanceOn: '#ToyExample click',
		buttons: []
	});

	tour.addStep('s4', {
		title: 'Compile',
		text: 'Compiling the code will show the datamodel schema graph. This requires at least one class in the editor to be a subclass of edu.illinois.cs.cogcomp.saul.datamodel.DataModel.',
		attachTo: '#compileBtn top',
		advanceOn: '#compileBtn click',
		buttons: []
	});

	tour.addStep('s5', {
		title: 'Populate',
		text: 'Populating the code will try to show the populated graph of the trained datamodel. This requires a main method and at least one class in the editor to be a subclass of edu.illinois.cs.cogcomp.saul.datamodel.DataModel',
		attachTo: '#populateBtn top',
		advanceOn: '#populateBtn click',
		buttons: []
	});

	tour.addStep('s6', {
		title: 'Manipulate graph',
		text: 'You should be able to see two graphs each in the first two tabs. Hover on the nodes to see the values on the left side green panel. You can also click on the nodes to show adjacent nodes and edges. If you have trouble seeing one of the nodes, you can alwasy drag it out. The graph also supports zoom in and out.',
		advanceOn: '#populateBtn click',
		buttons: [
		{
			text: 'Next',
			action: tour.next,
			classes: 'shepherd-button-example-primary'
		}
		]
	});

	tour.addStep('s7', {
		title: 'Run',
		text: 'Running the code will try to show the command line output of the program. This requires a main method in the editor.',
		attachTo: '#runBtn top',
		advanceOn: '#runBtn click',
		buttons: []
	});
	tour.start();
});