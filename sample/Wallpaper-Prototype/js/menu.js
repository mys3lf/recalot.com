var Nav = {
	init: function(menu){
		this.__menu = $("#" + menu );
		this.__menuId = "#" + menu;
		this.__window = $(window);
		
		this.__widthSwitch = 800;

		this.__isHidden = true;
		this.__isFixed = this.__window.outerWidth() < this.__widthSwitch;
		var self = this;
				
		this.__checkResolution = function(){
			if(this.__window.outerWidth() > this.__widthSwitch) {
				if(!this.__isFixed) {
					
					this.__window.unbind("click", this.__windowClick);
					this.__menu.css("float", "left");
					this.__menu.css("position", "relative");
					this.__menu.css("left", 0);
					this.__isFixed = true;
				}
			} else {
				if(this.__isFixed) {
					
					this.__window.click(this, this.__windowClick);
					this.__menu.css("position", "absolute");
					this.__menu.css("float", "none");
					this.__menu.css("left", -1 * this.__menu.outerWidth(true));
		
					this.__isFixed = false;
				}
			}
		};
		this.__show = function(){
			this.__menu.stop().animate({ left: "0"}, { complete: function() { 
					self.__isHidden = false;
				}
			});
		};
		
		this.__hide = function(){
			this.__menu.stop().animate({ left: -1 * this.__menu.outerWidth(true)}, {complete: function() { 
					self.__isHidden = true;
				}
			});
		};
		
		this.__windowClick  = function(e){
			if($(e.target).closest(".clickable").length > 0){
			} else if($(e.target).closest(e.data.__menuId).length === 0){
				if(e.data.__isHidden){
					e.data.__show();
				} else {
					e.data.__hide();
				}
			}
		};
		
		this.__checkResolution();
		
		this.__window.resize(this, function(e){
			e.data.__checkResolution();
		});
	}
};