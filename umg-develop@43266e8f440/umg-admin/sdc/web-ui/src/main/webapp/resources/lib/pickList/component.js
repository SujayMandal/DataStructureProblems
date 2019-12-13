/* global angular */
'use strict';

function ListEntry(originalIndex, data) {
  this.originalIndex = originalIndex;
  this.data = data;
}

angular.module('apicklist', [])
  .factory('_', function () {
    return window._; // assumes underscore has already been loaded on the page
  })

  .directive('picklist', ['_', function (_) {
    return {
      restrict: 'E',
      transclude: true,
      replace: true,
      templateUrl: '../umg-admin/resources/partial/template/picklist.html',
      scope: {
        leftListRowsModel: '=leftListRows',
        rightListRowsModel: '=rightListRows',
        
        listWidth: '@listWidth',//optional, empty by default
        listHeight: '@listHeight',//optional, empty by default
        showMoveAllButtons : '@' //optional, true by default
      },
      link: function (scope) {

        function initializeRowLists() {
          scope.leftListRows = _.map(scope.leftListRowsModel, function (element, index) {
            return new ListEntry(index, element);
          });
          scope.rightListRows = _.map(scope.rightListRowsModel, function (element, index) {
            return new ListEntry(index, element);
          });
          scope.leftCss = {};
          scope.rightCss = {};
          for(k in scope.leftListRowsModel){
        	  if(staticPageList.map(function(e) { return e.permission; }).indexOf(scope.leftListRowsModel[k]) > -1){
        		  scope.leftCss[scope.leftListRowsModel[k]] = "font-weight : 800";
        	  }else{
        		  scope.leftCss[scope.leftListRowsModel[k]] = "padding-left : 20%";
        	  }
          }
          for(k in scope.rightListRowsModel){
        	  if(staticPageList.map(function(e) { return e.permission; }).indexOf(scope.rightListRowsModel[k]) > -1){
        		  scope.rightCss[scope.rightListRowsModel[k]] = "font-weight : 800";
        	  }else{
        		  scope.rightCss[scope.rightListRowsModel[k]] = "padding-left : 20%";
        	  }
          }
    /*      console.warn("Left css: ");
          console.warn(scope.leftCss);
          console.warn("Right css: ");
          console.warn(scope.rightCss);*/
        }
        
        scope.changedValue = function(obj){
        	var tempArr = [];
        	for(k in obj){
        		tempArr[k]=JSON.parse(obj[k]);
        	}
        	return tempArr;
        }
        
        scope.getLeftStyle = function(data){
        	return scope.leftCss[data]; 
        };
        
        scope.getRightStyle = function(data){
        	return scope.rightCss[data]; 
        };

        scope.$on('changeText',function(event, data){
        	scope.leftListRowsModel = data[0].sort();
        	scope.rightListRowsModel = data[1].sort();
        	scope.leftSelected = [];
            scope.rightSelected = [];
        	initializeRowLists();
        });
        
        scope.listCss = {};

        scope.showAllButtons = scope.showMoveAllButtons || true;

        if (scope.listWidth){
          scope.listCss['min-width'] = scope.listWidth + 'px';
        }

        if (scope.listHeight){
          scope.listCss.height = scope.listHeight + 'px';
        }

        initializeRowLists();

        //indices of selected rows
        scope.leftSelected = [];
        scope.rightSelected = [];

        scope.leftFilter = '';
        scope.rightFilter = '';

        /**
         * moves only selected rows from left to right
         */
        scope.moveRightSelected = function () {
        	scope.leftSelected = scope.changedValue(scope.leftSelected);
        	var temp = [];
        	for(i in scope.leftListRows){
        		if(staticPageList.map(function(e) { return e.permission; }).indexOf(scope.leftListRows[i].data)>-1 && scope.leftSelected.map(function(e) { return e.data; }).indexOf(scope.leftListRows[i].data)==-1){
        			temp.push(scope.leftListRows[i]);
        		}
        	}
        	
        	//add parent if any child is selected 
        	for(k in temp){
           	  if(scope.leftListRowsModel.indexOf(temp[k].data)>-1){
           		  for(j in scope.leftSelected){
           			  if(scope.leftSelected[j].data.startsWith(temp[k].data) == true){
           				scope.leftSelected.push(temp[k]);
           				break;
           			  }
           		  }
           	  	}
             }
        	
          //convert selected rows into raw data
          var selectedData = scope.leftSelected.map(function (row) {
            return row.data;
          });

          //add data to the right list
          scope.rightListRowsModel = scope.rightListRowsModel.concat(selectedData);
          
          //sort the right list
          scope.rightListRowsModel.sort();

          //remove from left list
          scope.leftSelected.forEach(function (element) {
            scope.leftListRowsModel.splice(scope.leftListRowsModel.indexOf(element.data), 1);
          });
          
          //sort the left list
          scope.leftListRowsModel.sort();
          
          //reinitialize row models
          initializeRowLists();

          //clear selected lists
          scope.rightSelected = [];
          scope.leftSelected = [];
        };

        /**
         * moves only selected rows from right to left
         */
        scope.moveLeftSelected = function () {
           scope.rightSelected = scope.changedValue(scope.rightSelected);
           var temp = [];
           for(i in scope.rightSelected){
            	if(staticPageList.map(function(e) { return e.permission; }).indexOf(scope.rightSelected[i].data)>-1){
        			temp.push(scope.rightSelected[i]);
        		}
           }
           
          //removing all child permission once parent is removed from right to left
           for(j in temp){
	         for(k in scope.rightListRows){
	        	if(scope.rightListRows[k].data.startsWith(temp[j].data)==true && scope.rightSelected.map(function(e) {return e.data}).indexOf(scope.rightListRows[k].data) == -1){
	        		scope.rightSelected.push(scope.rightListRows[k]);
	        	}   
	         }
           }
           
          //convert selected rows into raw data
          var selectedData = scope.rightSelected.map(function (row) {
            return row.data;
          });

          //add data to the left list
          scope.leftListRowsModel = scope.leftListRowsModel.concat(selectedData);
          
          //sort the left list
          scope.leftListRowsModel.sort();

          //remove from right list
          scope.rightSelected.forEach(function (element) {
            scope.rightListRowsModel.splice(scope.rightListRowsModel.indexOf(element.data), 1);
          });
          
          //sort the right list
          scope.rightListRowsModel.sort();

          //reinitialize row models
          initializeRowLists();

          //clear selected lists
          scope.rightSelected = [];
          scope.leftSelected = [];
        };

        scope.moveRightAll = function () {
          //add data to the right list
          scope.rightListRowsModel = scope.rightListRowsModel.concat(scope.leftListRowsModel);
          
          //sort the right list
          scope.rightListRowsModel.sort();

          //remove data from left list
          scope.leftListRowsModel = [];

          //reinitialize row models
          initializeRowLists();

          //clear selected lists
          scope.rightSelected = [];
          scope.leftSelected = [];
        };


        scope.moveLeftAll = function () {
          //add data to the left list
          scope.leftListRowsModel = scope.leftListRowsModel.concat(scope.rightListRowsModel);
          
          //sort the left list
          scope.leftListRowsModel.sort();

          //remove data from right list
          scope.rightListRowsModel = [];

          //reinitialize row models
          initializeRowLists();

          //clear selected lists
          scope.rightSelected = [];
          scope.leftSelected = [];
        };

      }
    };
  }]);
