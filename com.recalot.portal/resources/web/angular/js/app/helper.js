(function(angular) {
      'use strict';
    // declare a module
    angular.module('recalotApp')

 /***********************
  *     PROVIDER
  ***********************/
.factory("helper", function () {
        return {
            sortById: function (a, b) {
                if(a.id < b.id) return -1;
                if(a.id > b.id) return 1;
                return 0
            }
         }
    });
})(window.angular);
