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
    .directive("detailWindow", function () {
                  return {
                          restrict: "E",
                          replace: true,
                          scope: {
                              type: "@",
                              state: "@"
                          },
                          templateUrl: "views/snippet/detailWindow.html",
                          controller: "detailWindowCtrl"
                  };
              })
    .directive("experimentsTable", function () {
          return {
                  restrict: "E",
                  replace: true,
                  templateUrl: "views/snippet/experimentsTable.html",
                  controller: "experimentsTableCtrl"
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
      .controller("smallListCtrl", function ($rootScope, $scope, $http, $location, $routeParams) {

        $scope.$routeParams = $routeParams;
        if($scope.type != null && $scope.state != null) {
            if($rootScope.requests != null && $rootScope.requests[$scope.type] != null && $rootScope.requests[$scope.type]["get"] != null && $rootScope.requests[$scope.type]["get"][$scope.state] != null)
              $http.get($rootScope.requests.host + $rootScope.requests[$scope.type]["get"][$scope.state]).then(function (data) {
                $scope.items = data.data;

                $scope.readOnly = $rootScope.requests[$scope.type]["delete"] == undefined || $rootScope.requests[$scope.type]["delete"][$scope.state]== undefined;
                $scope.navigateTo = function(item) {
                    $location.url("/" + $routeParams.prefixId + "/" + $routeParams.contentId + "/" + item.id);
                }
                Sortable.init()
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
      .controller("detailWindowCtrl", function ($scope, $rootScope, $http, $routeParams) {


        function enrichContent(data) {

        }

        $rootScope.setDetailData = function(itemId){

            $scope.isArray = angular.isArray;
            $scope.isObject = angular.isObject;

            $http.get($rootScope.requests.host + $rootScope.requests[$scope.type]["get"][$scope.state] + itemId).then(function (data) {
                    $scope.detail = {
                        "label": data.data.id,
                        "content": data.data
                    };

                    enrichContent(data.data);
                });

                $scope.canCreate = $rootScope.requests[$scope.type]["put"] != null &&  $rootScope.requests[$scope.type]["put"][$scope.state] != null;
                $scope.canDelete = $rootScope.requests[$scope.type]["delete"] != null && $rootScope.requests[$scope.type]["delete"][$scope.state] != null;
           };

            if($routeParams.instanceId != null) {
                $rootScope.setDetailData($routeParams.instanceId);
            }

      })
      .controller("experimentsTableCtrl", function ($scope, $rootScope, $http, $routeParams) {
            function contains(a, obj) {
                for (var i = 0; i < a.length; i++) {
                    if (a[i] === obj) {
                        return true;
                    }
                }
                return false;
            }

            function getExperiments(experimentIds, $scope){
                $scope.sources = {};
                for(var i in experimentIds) {
                    var id = experimentIds[i];

                    $http.get($rootScope.requests.host + $rootScope.requests["experiments"]["get"]["item"] + id).then(function (data) {

                        var exp = data.data;

                        if($scope.sources[exp.dataSourceId]== undefined) {
                            $scope.sources[exp.dataSourceId] = {"name": exp.dataSourceId, "metrics": [], algorithms: []};
                        }


                        //fetch metrics
                        for(var n in exp.results) {
                            var result = exp.results[n];

                            for(var m in result) {
                                if(!contains($scope.sources[exp.dataSourceId].metrics, m)) {
                                    $scope.sources[exp.dataSourceId].metrics.push(m);
                                }
                            }
                        }

                        //insert results
                        for(var n in exp.results) {
                            var result = exp.results[n];

                            var r = [];
                            for(var mi in $scope.sources[exp.dataSourceId].metrics) {
                                var m = $scope.sources[exp.dataSourceId].metrics[mi];

                                if(result[m] != undefined) {
                                    r.push(result[m]);
                                } else {
                                    r.push(" - ");
                                }
                            }

                            $scope.sources[exp.dataSourceId].algorithms.push({
                                "name": n,
                                "results": r
                            })
                        }

                        Sortable.init()
                    });
                }
            }


            if($routeParams.experimentIds != null && $routeParams.experimentIds.length > 0) {
                getExperiments($routeParams.experimentIds.split(","), $scope);
            } else {
                $http.get($rootScope.requests.host + $rootScope.requests["experiments"]["get"]["finished"]).then(function (data) {
                    var ids = [];
                    for(var i in data.data) {
                        var exp = data.data[i];
                        ids.push(exp.id);
                    }

                    getExperiments(ids, $scope);
                });
            }
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