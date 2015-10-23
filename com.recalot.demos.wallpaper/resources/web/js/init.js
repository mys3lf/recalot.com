var ApiHost = "http://api.recalot.com";

var PageSize = 10;

function adjustExperiment(){
	var survey = $(".survey");

	if(survey != null){
		var children = survey.children(".wallpaper");
		$("#page-count").text(children.length);
		var obj = survey.children(".wallpaper.shown");
		var n = children.index(obj);
		$("#current-page").text(n + 1);
	}
}

function showNext(details){
	if(details.next().length == 0){
		alert("Thank you");
	} else {
		details.removeClass("shown");
		details.addClass("hidden");
			
		details.next().removeClass("hidden").addClass("shown");
		adjustExperiment();
	}	
};

function starExperimentClick(e){
	var target = $(e.target);
	if(target.hasClass("star")){
		var details = target.closest(".wallpaper");
		var id = details.attr("id");
		
		var star = target.attr("data-star");
		
		if(id != null && star != null){
			id = id.replace("recalot-", "");
			Recalot.rateItem(id, "rating", star);
			details.find("input.star-" + star).attr("checked", "checked");

			window.setTimeout(function(){
				showNext(details);
			}, 500);
		}
				
		e.preventDefault();
		e.stopPropagation();
		return false;
	}
};

function starClick(e){
	var target = $(e.target);
	if(target.hasClass("star")){
		var details = target.closest(".details");
		var id = details.attr("id");
		
		var star = target.attr("data-star");
		
		if(id != null && star != null){
			id = id.replace("recalot-", "");
			Recalot.rateItem(id, "rating", star);
			
			details.find("input.star-" + star).attr("checked", "checked");
		}
				
		e.preventDefault();
		e.stopPropagation();
		return false;
	}
};

function downloadClick(e){
	if(e.data != null && e.data.id != null){
		Recalot.rateItem(e.data.id , "download", "1");
	}
};

function dismissClick(e){
	var target = $(e.target);
	if(target.length > 0 && e.data != null && e.data.id != null){
		Recalot.rateItem(e.data.id , "dismiss", "1");
		
		var container = target.closest(".wallpaper-small");
		container.fadeOut(750, function(){
			container.remove();
		});
	}
};

function fillEntries(){
	var entries = $(".fill-entries");
		
	for(var i = 0; i < entries.length; i++){
		var entry = entries.eq(i);
		var controller = entry.attr("data-controller");
		var entryUrl = entry.attr("data-entry-url");
		var dataType = entry.attr("data-type");
		
		if(controller != null && entryUrl != null){
			
			if(localStorage.getItem(dataType) != null){
				var data = JSON.parse(localStorage.getItem(dataType));
								
				for(var j = 0; j < 15; j++){
					if(data.length > j){
						var li = $("<li><a href='" + entryUrl.replace("{0}", data[j].name) + "'>" + data[j].name + " (" + data[j].count + ")</a></li>");
						li.appendTo(entry);
					}
				}

				$.ajax({
					method: "GET",
					url: ApiHost + "/sample/wallpaper/" + controller,
					cache: true,
					context: {entryUrl: entryUrl, entry: entry, dataType: dataType},
					success: function(data, status, jqXHR) {
						data.sort(function(a, b){return b.count - a.count;})
						
						if(this.dataType != null){
							localStorage.setItem(this.dataType , JSON.stringify(data));
						}
					}
				}).fail(function( jqXHR, textStatus ) {
				});
			} else {
				$.ajax({
					method: "GET",
					url: ApiHost + "/sample/wallpaper/" + controller,
					cache: true,
					context: {entryUrl: entryUrl, entry: entry, dataType: dataType},
					success: function(data, status, jqXHR) {
						data.sort(function(a, b){return b.count - a.count;})
						
						if(this.dataType != null){
							localStorage.setItem(this.dataType , JSON.stringify(data));
						}
						
						for(var j = 0; j < 15; j++){
							if(data.length > j){
								var li = $("<li><a href='" + this.entryUrl.replace("{0}", data[j].name) + "'>" + data[j].name + " (" + data[j].count + ")</a></li>");
								li.appendTo(this.entry);
							}
						}
					}
				}).fail(function( jqXHR, textStatus ) {
				});
			}
		}
	}
}

function showCategoriesPage(){
	var entries = $(".categories");
	var cat = Recalot.__getQueryVariable("cat");
	
	for(var i = 0; i < entries.length; i++){
		var entry = entries.eq(i);
		
		var categoriesController = entry.attr("data-controller-categories");
		var itemsController = entry.attr("data-controller-items");
						
		var page = Recalot.__getQueryVariable("page");
		if(page == null) page = 1;
		else page = parseInt(page, 10);
		if(page != page) page = 1;
						
		if(categoriesController != null && itemsController != null){
			if(cat == null){
				if(localStorage.getItem("categories") != null) {
					var data = JSON.parse(localStorage.getItem("categories"));
					
					var start = (page - 1) * PageSize;
					var end = page * PageSize;
		
					for(var j = start; j < end; j++){
						if(data.length > j){
							entry.append(renderCategory(data[j]));
						}
					}
					var div = $(".paginator");
					div.pagination({
						items: data.length,
						itemsOnPage: PageSize,
						cssStyle: 'dark-theme',
						currentPage: page,
						hrefTextPrefix: "?page="
					});
					
				} else {
					$.ajax({
						method: "GET",
						url: ApiHost + "/sample/wallpaper/" + categoriesController,
						cache: true,
						context: {entry: entry, page: page},
						success: function(data, status, jqXHR) {
							
							var start = (this.page - 1) * PageSize;
							var end = this.page * PageSize;
				
							for(var j = start; j < end; j++){
								if(data.length > j){
									this.entry.append(renderCategory(data[j]));
								}
							}
							
							var div = $(".paginator");
							div.pagination({
								items: data.length,
								itemsOnPage: PageSize,
								cssStyle: 'dark-theme',
								currentPage: this.page,
								hrefTextPrefix: "?page="
							});
						}
					}).fail(function( jqXHR, textStatus ) {
					});	
				}	
			} else {
			
				$(".label").text("Category " + cat);
				$.ajax({
					method: "GET",
					url: ApiHost + "/sample/wallpaper/" + itemsController,
					cache: true,
					data: {cat: cat, page: page},
					context: {cat: cat, entry: entry},
					success: function(data, status, jqXHR) {
						
						for(var j = 0; j < data.items.length; j++){
							this.entry.append(Recalot.renderItem(data.items[j], this.entry));
							Recalot.__fetchRating(data.items[j].id);
						}

						var div = $(".paginator");
						div.pagination({
							items: data.count,
							itemsOnPage: data.pageSize,
							cssStyle: 'dark-theme',
							currentPage: data.page,
							hrefTextPrefix: "?cat=" + this.cat + "&page="
						});
					}
				}).fail(function( jqXHR, textStatus ) {
				});	
			}
		}
	}
}

function renderCategory(data){
	if(data != null && data.name != null && data.preview != null && data.count != null) {
	
		var div = $("<a class='wallpaper-small' href='?cat=" + data.name+ "'></a>");
		div.append("<h4>" + data.name +  "</h4>");
		var img = $("<img />");
		img.attr("src", data.preview);
		img.attr("title", data.name);
		img.attr("alt", data.name);
		div.append(img);
		div.append("<h5>" + data.count +  " Wallpaper</h5>");
		
		return div;
	}
};

function renderWallpaper(data){
	if(data != null && data.content != null && data.content != null) {
		var div = $("<div class='wallpaper-small'></div>");
		div.append("<h4>" + (data.content.title || "[[no-title]]") +  "</h4>");
		div.append("<a href='#' class='dismiss' title='Not relevant for me!'>x</a>");
		var img = $("<img />");

		img.attr("src", data.content.src);
		img.attr("title", data.content.title);
		img.attr("alt", data.content.title);
		
		div.append(img);
		var details = "<div class='details' id='recalot-" + data.id + "'>" +
				"<div>Categories:" +
				"<ul class='tags'></ul>" + 
				"</div>" +
				"<div class='stars'>" +
					"<form action=''>" + 
					  "<input class='star star-5' data-star='5' id='star-5" + data.Id + "' type='radio' name='star'>" +
					  "<label class='star star-5' data-star='5' for='star-5" + data.Id + "'></label>" +
					  "<input class='star star-4' data-star='4' id='star-4" + data.Id + "' type='radio' name='star'>" +
					  "<label class='star star-4' data-star='4' for='star-4" + data.Id + "'></label>" +
					  "<input class='star star-3' data-star='3' id='star-3" + data.Id + "' type='radio' name='star'>" +
					  "<label class='star star-3' data-star='3' for='star-3" + data.Id + "'></label>"+
					  "<input class='star star-2' data-star='2' id='star-2" + data.Id + "' type='radio' name='star'>" +
					  "<label class='star star-2' data-star='2' for='star-2" + data.Id + "'></label>" +
					  "<input class='star star-1' data-star='1' id='star-1" + data.Id + "' type='radio' name='star'>" +
					  "<label class='star star-1' data-star='1' for='star-1" + data.Id + "'></label>" +
					"</form>"+
				"</div>"+
				"<div>"+
					"<a class='download' href='javascript:void(0)' target='_blank'>Download</a>" +
				"</div>"+
			"</div>";
		
		var $details = $(details);
		
		var download = $details.find(".download");

		download.attr("href", data.content.url); 
		download.click(data, downloadClick);
		$details.find(".star").click(starClick);
		div.append($details);

		var dismissLink = div.find(".dismiss");
		dismissLink.click(data, dismissClick);
		
		var cats = $details.children("div").children("ul.tags");
		
		if(data.content.categories != null) {
			var catsSplit = data.content.categories.split(",");
			for(var i = 0; i < catsSplit.length; i++) {
				var li = $("<li><a >" +  catsSplit[i] + "</a></li>");
				li.children("a").attr("href", "cats.html?cat=" + encodeURIComponent(catsSplit[i].toLowerCase()));
				cats.append(li);
			}
		}
		
		return div;
	}
};

function initializeNewlyAdded(){
	var container = $(".wallpaper-newlyadded");
	if(container.length > 0){
		$.ajax({
		  method: "GET",
		  url: ApiHost + "/sample/wallpaper-controller/items",
		  cache: false,
		  context: {self: Recalot, container: container},
		  success: function(data, status, jqXHR) {
			if(data != null && data.items != null && data.items.length != null) {
				for(var i = 0; i < data.items.length; i++) {
					if(data.items[i] != null){
						this.container.append(this.self.renderItem(data.items[i], this.container));
						this.self.__fetchRating(data.items[i].id);
					} 
				}
			}
		  }
		}).fail(function( jqXHR, textStatus ) {
			console.error( "Request failed: " + textStatus );
		});
	}
}

$(window).load(function(){
	fillEntries();	
	
	Nav.init("navigation");
		
	if(Recalot.__getQueryVariable("user") == "new"){
		localStorage.removeItem("recalot-userid");
		alert("New user created");
	}
	

	var $body = $("body");
	var type = $body.attr("data-recalot-type");
	
	Recalot.renderItem = renderWallpaper;
	
	
	Recalot.renderItemRating = function(rating){
		if(rating != null && rating.type == "rating"){
			var $item = $("#recalot-" + rating.itemId);

			if($item.length > 0){
				$item.find("input.star-" + rating.value).attr("checked", "checked");
			}
		}
	};
	
	if($(".categories").length > 0){
		showCategoriesPage();
	}	
	
	Recalot.fetch();
	
	initializeNewlyAdded();
	
});



