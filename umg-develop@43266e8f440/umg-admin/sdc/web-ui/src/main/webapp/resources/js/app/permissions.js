
function permissionMappingTab(){
	          for(k in pageMap){
	              if(finalPages.indexOf(pageMap[k]) == -1){
	                  $('#'+pageMap[k]).addClass("disable-html-permission");
	              }
	          }
	          if(sysAdmin == false)
	          {
	        	  $('[id^="superAdminPanel"]').remove();
	          }
	      }

function permissionMapping(){
	    for(k in buttons){
	        if(pages.indexOf(k) == -1){
	        	if(buttons[k] != null && buttons[k]!=undefined && buttons[k]!=""){
	        		$('[id^='+buttons[k]+']').remove();
	        	}
	        }
	    }
    }
