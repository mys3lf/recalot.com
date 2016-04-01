(function(angular) {
      'use strict';
    // declare a module
    var module = angular.module('recalotApp', ['ngRoute', 'ngAnimate', 'ui.bootstrap']);

 /***********************
  *     ROUTES
  ***********************/
    module.config(function($routeProvider) {
        $routeProvider
            // route for the home page
            .when('/', {
                templateUrl : 'views/pages/home.html'
            })
            .when('/:prefixId', {
                templateUrl : 'views/pages/start.html',
                controller: "startCtrl"
            })
            .when('/:prefixId/:contentId', {
                templateUrl : 'views/pages/start.html',
                controller: "startCtrl"
            })
            .when('/:prefixId/:contentId/:instanceId', {
                templateUrl : 'views/pages/start.html',
                controller: "startCtrl"
            })
    })

 /***********************
  *     DIRECTIVES
  ***********************/
    .directive("myFooter", function () {
                      return {
                              restrict: "E",
                              replace: true,
                              templateUrl: "views/frame/footer.html",
                              controller: "footerCtrl"
                      };
              })
    .directive("navTop", function () {
                  return {
                          restrict: "E",
                          replace: true,
                          templateUrl: "views/frame/topNav.html",
                          controller: "topNavCtrl"
                  };
              })
    .directive("navMain", function () {
                  return {
                          restrict: "E",
                          replace: true,
                          templateUrl: "views/frame/mainNav.html"
                  };
              })
    .directive("smallList", function () {
          return {
                  restrict: "E",
                  replace: true,
                  scope: {
                      type: "@",
                      state: "@",
                      label: "@",
                      columns: "@"

                  },
                  templateUrl: "views/snippet/smallList.html",
                  controller: "smallListCtrl"
          }
    })
    .directive("mediumList", function () {
          return {
                  restrict: "E",
                  replace: true,
                  scope: {
                      type: "@",
                      state: "@",
                      label: "@",
                      columns: "@"

                  },
                  templateUrl: "views/snippet/mediumList.html",
                  controller: "smallListCtrl"
          }
    })

   /***********************
    *     CONTROLLER
    ***********************/
      .controller("startCtrl", function($scope,  $rootScope, $routeParams) {

          $scope.config = {
              template : "views/pages/" + $routeParams.prefixId + "/" + ($routeParams.contentId != null ? $routeParams.contentId : "index")+  ".html",
          };

          $rootScope.setActiveById($routeParams.prefixId);

        })
      .controller("smallListCtrl", function ($rootScope, $scope, $http) {
        if($scope.type != null && $scope.state != null) {
            if($rootScope.requests != null && $rootScope.requests[$scope.type] != null && $rootScope.requests[$scope.type]["get"] != null && $rootScope.requests[$scope.type]["get"][$scope.state] != null)
              $http.get($rootScope.requests.host + $rootScope.requests[$scope.type]["get"][$scope.state]).then(function (data) {
                $scope.items = data.data;
            });
        }
      })
      .controller("footerCtrl", function ($scope, $http) {
           $scope.footer = {};

           if(localStorage.getItem("footer") !== null) {
                $scope.footer =  JSON.parse(localStorage.getItem("footer"));
           }

          $http.get("data/footer.json").then(function (data) {
                  $scope.footer = data.data;
                  localStorage.setItem("footer", JSON.stringify(data.data));
          });
      })
      .controller("topNavCtrl", function ($rootScope, $http, $routeParams) {
           $rootScope.navigation = {};
           $rootScope.requests = {};

          $rootScope.setActiveById = function(prefixId) {
              for(var i in  $rootScope.navigation.items) {
                $rootScope.navigation.items[i].active = false;
                if($rootScope.navigation.items[i].link != null) {
                    if($rootScope.navigation.items[i].link == prefixId) {

                        $rootScope.setActive($rootScope.navigation.items[i]);
                        break;
                    }
                 }
              }
          }


           if(localStorage.getItem("requests") !== null) {
                $rootScope.requests =  JSON.parse(localStorage.getItem("requests"));
           }

          $http.get("data/requests.json").then(function (data) {
                  $rootScope.requests = data.data;
                  localStorage.setItem("requests", JSON.stringify(data.data));
          });

           if(localStorage.getItem("navigation") !== null) {
                $rootScope.navigation =  JSON.parse(localStorage.getItem("navigation"));
           }

          $http.get("data/navigation.json").then(function (data) {
                  $rootScope.navigation = data.data;
                  localStorage.setItem("navigation", JSON.stringify(data.data));
          });

          $rootScope.setActive = function(item) {
              for(var i in  $rootScope.navigation.items) {
                $rootScope.navigation.items[i].active = false;
              }

              $rootScope.subNavigation = item;
              if(item != null) item.active = true;
          };
      })

  })(window.angular);;