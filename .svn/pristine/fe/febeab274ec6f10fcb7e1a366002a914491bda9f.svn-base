$(window).load(function(){
	
	Nav.init("navigation");

		
	if(Recalot.__getQueryVariable("user") == "new"){
		localStorage.removeItem("recalot-userid");
		alert("New user created");
	}
	
	var $body = $("body");
	var type = $body.attr("data-recalot-type");
	if(type == "experiment"){
		Recalot.renderItem = function(data){
			if(data != null && data.content != null && data.content.content != null) {
				var item = JSON.parse(data.content.content);
				
				var div = $("<div class='wallpaper' id='recalot-" + data.id + "'></div>");
				
				if($(".wallpaper.shown").length > 0){
					div.addClass("hidden");
				} else {
					div.addClass("shown");
				}
				
				div.append("<h2>" + (item.Title || "[[no-title]]") +  "</h2>");
				var img = $("<img />");
				img.attr("src", item.Src);
				img.attr("title", item.Title);
				img.attr("alt", item.Title);
				
				div.append(img);
				var details = "<div class='details' >" +
						"<div>Categories:" +
						"<ul class='tags'></ul>" + 
						"</div>" +
						"<div class='stars'>Rate it:" +
							"<form action=''>" + 
							  "<input class='star star-5' data-star='5' id='star-5" + item.Id + "' type='radio' name='star'>" +
							  "<label class='star star-5' data-star='5' for='star-5" + item.Id + "'></label>" +
							  "<input class='star star-4' data-star='4' id='star-4" + item.Id + "' type='radio' name='star'>" +
							  "<label class='star star-4' data-star='4' for='star-4" + item.Id + "'></label>" +
							  "<input class='star star-3' data-star='3' id='star-3" + item.Id + "' type='radio' name='star'>" +
							  "<label class='star star-3' data-star='3' for='star-3" + item.Id + "'></label>"+
							  "<input class='star star-2' data-star='2' id='star-2" + item.Id + "' type='radio' name='star'>" +
							  "<label class='star star-2' data-star='2' for='star-2" + item.Id + "'></label>" +
							  "<input class='star star-1' data-star='1' id='star-1" + item.Id + "' type='radio' name='star'>" +
							  "<label class='star star-1' data-star='1' for='star-1" + item.Id + "'></label>" +
							"</form>"+
						"</div>"+
					"</div>";
				
				var $details = $(details);
				
				$details.click(starExperimentClick);
				div.append($details);
				
				var cats = $details.children("div").children("ul.tags");
				
				if(item.Categories != null) {
					for(var i = 0; i < item.Categories.length; i++) {
						var li = $("<li><a >" +  item.Categories[i] + "</a></li>");
						li.children("a").attr("href", "cats.html?cat=" + encodeURIComponent(item.Categories[i].toLowerCase()));
						cats.append(li);
					}
				}
			
				window.setTimeout(adjustExperiment, 100);
				return div;
			}
		};
	
		Recalot.renderItemRating = function(data){
			if(data != null && data.length > 0){
				var last = data[0];
				var $item = $("#recalot-" + last.itemId);
		
				if($item.length > 0){
					$item.find("input.star-" + last.value).attr("checked", "checked");
				}
			}
		};
	} else {
		Recalot.renderItem = function(data){
			if(data != null && data.content != null && data.content.content != null) {
				var item = JSON.parse(data.content.content);
				var div = $("<div class='wallpaper-small'></div>");
				div.append("<h4>" + (item.Title || "[[no-title]]") +  "</h4>");
				var img = $("<img />");
				img.attr("src", item.Src);
				img.attr("title", item.Title);
				img.attr("alt", item.Title);
				
				div.append(img);
				var details = "<div class='details' id='recalot-" + data.id + "'>" +
						"<div>Categories:" +
						"<ul class='tags'></ul>" + 
						"</div>" +
						"<div class='stars'>" +
							"<form action=''>" + 
							  "<input class='star star-5' data-star='5' id='star-5" + item.Id + "' type='radio' name='star'>" +
							  "<label class='star star-5' data-star='5' for='star-5" + item.Id + "'></label>" +
							  "<input class='star star-4' data-star='4' id='star-4" + item.Id + "' type='radio' name='star'>" +
							  "<label class='star star-4' data-star='4' for='star-4" + item.Id + "'></label>" +
							  "<input class='star star-3' data-star='3' id='star-3" + item.Id + "' type='radio' name='star'>" +
							  "<label class='star star-3' data-star='3' for='star-3" + item.Id + "'></label>"+
							  "<input class='star star-2' data-star='2' id='star-2" + item.Id + "' type='radio' name='star'>" +
							  "<label class='star star-2' data-star='2' for='star-2" + item.Id + "'></label>" +
							  "<input class='star star-1' data-star='1' id='star-1" + item.Id + "' type='radio' name='star'>" +
							  "<label class='star star-1' data-star='1' for='star-1" + item.Id + "'></label>" +
							"</form>"+
						"</div>"+
						"<div>"+
							"<a class='download' href='javascript:void(0)' target='_blank'>Download</a>" +
						"</div>"+
					"</div>";
				
				var $details = $(details);
				
				var download = $details.find(".download");
				download.attr("href", item.Url); 
				download.click(data, downloadClick);
				$details.find(".class").click(starClick);
				div.append($details);
				
				var cats = $details.children("div").children("ul.tags");
				
				if(item.Categories != null) {
					for(var i = 0; i < item.Categories.length; i++) {
						var li = $("<li><a >" +  item.Categories[i] + "</a></li>");
						li.children("a").attr("href", "cats.html?cat=" + encodeURIComponent(item.Categories[i].toLowerCase()));
						cats.append(li);
					}
				}
				
				return div;
			}
		};
	
		Recalot.renderItemRating = function(rating){
			if(rating != null ){
				var $item = $("#recalot-" + rating.itemId);
		
				if($item.length > 0){
					$item.find("input.star-" + rating.value).attr("checked", "checked");
				}
			}
		};
	}
	
	
	Recalot.fetch();

});

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



