/**
 * 
 */
var sharedPropertiesService = function() {
	var stringValue = null;
	var objectValue = {};
	var map = {};
	return {
		getValue : function() {
			return stringValue;
		},
		setValue : function(value) {
			stringValue = value;
		},
		getObject : function() {
			return objectValue;
		},
		setObject : function(value) {
			objectValue = value;
		},
		clearValue : function(){
			stringValue = null;
			objectValue = {};
		},
		
		get: function(id) {
		    return (map[id]) ? map[id] : null;
		},

		put: function(id, value) {
		    map[id] = value;
		},
		remove: function(id) {
			if (!map[id])
				return null;
			var value = map[id];
			delete map[id];
			return value;
		},
		contains: function(id) {
			return (map[id]) ? true : false;
		}
		
	};
};