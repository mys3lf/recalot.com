var Recalot = {
	host: "http://api.recalot.com",
	recPath: "/rec",
	sourcePath: "/sources",
	sourceId: "wallpaper-src",
	pathSeparator: "/",
	dataPath: "/data",
	itemsPath: "/items",
	interactionsPath: "/interactions",
	trackPath: "/track",
	usersPath: "/users",
	__userId: null, 
	pageSize: 10, 
	fetch: function() {
		this.getUserId();
		
		var container = $(".recalot-recs");
		if(container.length > 0) {
			for(var i = 0; i < container.length; i++) {
				var c = container.eq(i);
				var type = c.attr("data-recalot-type") || "most-popular";
				var count = c.attr("data-recalot-count") || "10";
				
				if(type != null){
					if(type == "recalot-rated"){
						var page = this.__getQueryVariable("page") || 1;
						
						this.__fetchRatedItems(this.getUserId(), page, c);
					}else {
						this.__fetchRecommendations(type, count, this.getUserId(), 1, c);
					}
				}
			}
		}
	},
	__getQueryVariable : function (variable) {
		   var query = window.location.search.substring(1);
		   var vars = query.split("&");
		   for (var i=0;i<vars.length;i++) {
				   var pair = vars[i].split("=");
				   if(pair[0] == variable){return pair[1];}
		   }
		   return null;
	},
	getUserId: function() {
		var chrome = chrome || {};
		if(this.__userId === null) {
			if(chrome != null && chrome.storage != null && chrome.storage.sync != null) {
				var self = this;
				chrome.storage.sync.get('userid', function(items) {
					var userid = items.userid;
					if (userid) {
						self.__userId = userid;
					} else {
						userid = getRandomToken();
						chrome.storage.sync.set({userid: userid}, function() {
							self.__userId = userid;
							self.__createUser(userid);
						});
					}
				});
			} else {
				var userId = localStorage.getItem("recalot-userid")
				if(userId === null) {
					userId = this.__getRandomToken(16);
					localStorage.setItem("recalot-userid", userId);
					this.__createUser(userId);
				}
				
				this.__userId = userId;
			}
		}
		
		return this.__userId;
	},
	__createUser: function(userId){
		$.ajax({
		  method: "POST",
		  url: this.host + this.dataPath + this.sourcePath + this.pathSeparator + this.sourceId + this.usersPath,
		  data: { "user-id": userId },
		  cache: false,
		  context: {self: this, container: container},
		  success: function(data, status, jqXHR) {
		  }
		}).fail(function( jqXHR, textStatus ) {
			console.error( "Request failed: " + textStatus );
		});
	},
	__fetchRatedItems: function(userId, page, container){
		$.ajax({
		  method: "GET",
		  url: this.host + this.dataPath + this.sourcePath + this.pathSeparator + this.sourceId + this.usersPath + this.pathSeparator + userId + this.interactionsPath,
		  cache: false,
		  data: {type: "rating" },
		  context: {self: this, page: page,  container: container},
		  success: function(data, status, jqXHR) {
			if(data != null &&  data.length != null) {
				var items = [];
				
				for(var i = 0; i < data.length ; i++){
					var contains = false;
					for(var j = 0; j < items.length; j++){
						if(data[i].itemId == items[j].itemId){
							contains = true;
							
							if(data[i].type == "rating" &&  data[i].timeStamp > items[j].timeStamp){
								items[j] = data[i]; 
							}
						}
					}
					
					if(!contains){
						items.push(data[i]);
					}
				}
				var temp = [];
				for(var i = 0; i < items.length; i++) {
					if(items[i].type == "rating"){
						temp.push(items[i]);
					}
				}
				items = temp;	
				
				items = items.sort(function(a, b){return parseInt(b.value, 10) - parseInt(a.value, 10) ;});
				var start = (this.page - 1) * this.self.pageSize;
				var end = this.page * this.self.pageSize;
				
				for(var i = start; i < end; i++){
					if(items.length > i){
						this.self.__fetchItem(items[i].itemId, "rated", this.container); 
					}
				}
				
				var div = $("#paginator, .paginator");
				div.pagination({
					items: items.length,
					itemsOnPage: this.self.pageSize,
					cssStyle: 'dark-theme',
					currentPage: this.page,
					hrefTextPrefix: "?page="
				});
			}
		  }
		}).fail(function( jqXHR, textStatus ) {
			console.error( "Request failed: " + textStatus );
		});
	},	
	__fetchRecommendations: function(type, count, userId, page, container){
		$.ajax({
		  method: "GET",
		  url: this.host + this.recPath + this.pathSeparator + type,
		  data: { "user-id": userId, "count": count, "page": page },
		  cache: false,
		  context: {self: this, container: container},
		  success: function(data, status, jqXHR) {
			if(data != null && data.items != null && data.items.length != null) {
				for(var i = 0; i < data.items.length; i++) {
					this.self.__fetchItem(data.items[i].itemId, data.recommender, this.container); 
				}
			}
		  }
		}).fail(function( jqXHR, textStatus ) {
			console.error( "Request failed: " + textStatus );
		});
	},	
	__fetchItem: function(itemId, type, container){
		$.ajax({
		  method: "GET",
		  url: this.host + this.dataPath + this.sourcePath + this.pathSeparator + this.sourceId + this.itemsPath + this.pathSeparator + itemId,
		  context: {self: this, container: container},
		  success: function(data, status, jqXHR) {
			if(data != null && this.self.renderItem != null && typeof this.self.renderItem == "function") {
				this.container.append(this.self.renderItem(data, this.container));
				this.self.__fetchRating(data.id);
			}
		  }
		}).fail(function( jqXHR, textStatus ) {
			console.error( "Request failed: " + textStatus );
		});
	},	
	__fetchRating: function(itemId){
		$.ajax({
		  method: "GET",
		  url: this.host + this.dataPath + this.sourcePath + this.pathSeparator + this.sourceId + this.usersPath + this.pathSeparator + this.getUserId() + this.itemsPath + this.pathSeparator + itemId + this.interactionsPath,
		  context: {self: this},
		  success: function(data, status, jqXHR) {
			if(data != null && this.self.renderItemRating != null && typeof this.self.renderItemRating == "function") {
				var least = {};
				if(data.length > 0) {
					var timestamp = 0;
					
					for(var i = 0; i < data.length; i++){
						if(data[i].type == "rating" && data[i].timeStamp > timestamp){
							timestamp = data[i].timeStamp;
							least = data[i];
						}
					}
					
					this.self.renderItemRating(least);
				}
			}
		  }
		}).fail(function( jqXHR, textStatus ) {
			console.error( "Request failed: " + textStatus );
		});
	},	
	renderItemRating: null,
	renderItem: null,
	__createUser: function(userId) {
		$.ajax({
		  method: "PUT",
		  url: this.host + this.dataPath + this.sourcePath + this.pathSeparator + this.sourceId + this.usersPath,
		  data: { "user-id": userId },
		  cache: false,
		}).fail(function( jqXHR, textStatus ) {
		  console.error( "Request failed: " + textStatus );
		});
	},
	rateItem: function(itemId, type, value) {
		$.ajax({
		  method: "POST",
		  url: this.host + this.trackPath + this.sourcePath + this.pathSeparator + this.sourceId + this.usersPath + this.pathSeparator + this.getUserId() + this.itemsPath + this.pathSeparator + itemId ,
		  data: { "type": type , "value": value },
		  cache: false,
		}).fail(function( jqXHR, textStatus ) {
		  console.error( "Request failed: " + textStatus );
		});
	},
	__getRandomToken: function(count) {
		// E.g. 8 * 32 = 256 bits token
		var randomPool = new Uint8Array(count);
		crypto.getRandomValues(randomPool);
		var hex = '';
		for (var i = 0; i < randomPool.length; ++i) {
			hex += randomPool[i].toString(16);
		}
		return hex;
	}
	
	
};

