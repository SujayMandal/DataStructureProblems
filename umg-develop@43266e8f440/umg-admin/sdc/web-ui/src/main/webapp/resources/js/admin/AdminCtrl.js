'use strict';

var AdminCtrl = ['$scope', '$log', '$dialogs', 'raService', function(scope, log, $dialogs, RAService){

	// Date Picker Related Settings
	scope.dateTimeFormat = 'YYYY-MMM-DD HH:mm';
	scope.dateTimeFormatAMPM = 'MMM-DD-YYYY HH:mm a';
	scope.dateFormat = 'MMM-DD-YYYY';
	scope.dateTestBedFormat = 'YYYY-MM-DD';
	/*scope.requestData = requestData;*/
	
	/* Video Related Functions */
	
	scope.change_video = function(video_path,video_info) {
		   document.getElementById('help_video').src=video_path;
		   $('#video_details').text("");
		   $('#video_details').text(video_info);
		   $('#video_draggable').show();
		   $('#video_draggable').draggable({axis: "x,y", containment: "#body"}).resizable();
		   $('#video_draggable').css('z-index','22222');
	};
	
	
	scope.reset_video = function(){
		   document.getElementById('help_video').src="";
		   $('#video_draggable').draggable("destroy");
		   $('#video_draggable').css({'display':'none','position':'fixed','right':0,'bottom':0,'width':'25%','height':'','left':'','top':''});
		   $('#video_draggable').css({'height':''});  
	};
	
	
	/* Breadcrumb and sidebar Functions */
	
	scope.clicked = function(item) {
		   var val1=$('#'+item).children('span').text();
		   var val2=($('#'+item).text()).trim();
		   	$('.breadcrumb').text("");
		    /*$('.breadcrumb').append('<li><i class="fa fa-dashboard"></i> Home</li>');*/
		   	$('.breadcrumb').append("<li class='active'>"+val2+"</li>");
		   	localStorage.setItem('breadcrumb', $('.breadcrumb').html());
	};
	
	scope.clicked_tree = function(item) {
		   	var val1=$('#'+item).parent().parent().siblings('a').children().closest('span').text();
		   	var val2=($('#'+item).text()).trim();
		    $('.breadcrumb').text("");
		    /*$('.breadcrumb').append('<li><i class="fa fa-dashboard"></i> Home</li>');*/
		    $('.breadcrumb').append("<li>"+val1+"</li>");
		   	$('.breadcrumb').append("<li class='active'>"+val2+"</li>");
		   	localStorage.setItem('breadcrumb', $('.breadcrumb').html());
	};

	scope.change_clear = function(item) {
		    $('.breadcrumb').text("");
		    localStorage.setItem('breadcrumb', $('.breadcrumb').html());
		    localStorage.setItem('tenant', "");
	};
	
	//controlling the left sidebar with right side bar
	
	scope.collapse_sidebar = function(){
		   if($("#right-sidebar").attr('class')=="control-sidebar control-sidebar-dark control-sidebar-open")
		   {
			   document.getElementById("left-footer").style.display = "none";
			   $("#body").addClass('skin-blue fixed sidebar-mini sidebar-collapse');
			   
		   }
	};
	
	//for model assumption list button default select
	scope.change_selected = function(){
			$("#md_asmp_lst_0").removeClass();
			$("#md_asmp_lst_0").addClass("btn btn-primary btn-sm design-button2 col-lg-5 pull-left");
	}
	
	scope.switchTenant = function(toTenant,currentTenant){
		if(currentTenant != toTenant){
			var dlg = $dialogs.confirm('Please Confirm','<span class="confirm-body">Do you want to change tenant from ' +currentTenant+' to '+toTenant+' ?</span>');
			dlg.result.then(function(btn){
				RAService.switchTenant(toTenant).then(
						function(responseData){
							scope.error = responseData.error;
							if(!responseData.error){
								//location.reload();
								localStorage.setItem('tenant', toTenant);
								window.location.href = "";
							}
						},
						function(errorData){
							scope.error = true;
						}
				);
			});
		}
	}

}];