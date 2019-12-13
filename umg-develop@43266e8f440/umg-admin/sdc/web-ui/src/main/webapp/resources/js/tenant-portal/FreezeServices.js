'use strict';
var FreezeServices = function() {

	/*********************************************
	 * This method is for freeze the screen
	 * while ajax call happens
	 ***********************/
	this.freezeScreen= function(divId){
		var elm=$('#'+divId);
		elm.show();
		if((screen.height-200) < document.body.scrollHeight) {
			elm.css('width',document.body.scrollWidth);
			elm.css('height',document.body.scrollHeight+25);
		}
	};
	/*********************************************
	 * This method is for unfreeze the screen
	 * while ajax call completes
	 ***********************/
	this.unfreezeScreen=function(divId){
		var elm=$('#'+divId);
		elm.hide();
	};
	
	 
};



