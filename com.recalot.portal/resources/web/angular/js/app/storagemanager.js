(function(angular) {
      'use strict';
    // declare a module
    angular.module('recalotApp')

 /***********************
  *     PROVIDER
  ***********************/
.factory("storageManager", function () {
        return {
            get: function(key) {
                var item = localStorage.getItem(key);
                if (item === null) {
                    return null;
                } else {
                    return JSON.parse(item);
                }
            },
            put: function(key, item) {
                localStorage.setItem(key, JSON.stringify(item));
            },
            equal: function(key, newItem) {
                var item = localStorage.getItem(key);

                if(item !== null && newItem !== null) {
                    return angular.equals(JSON.parse(item), newItem);
                }

                return false;
            }
         }
    });
})(window.angular);
