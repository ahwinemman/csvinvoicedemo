(function () {
    'use strict';

    var API_BASE = '';

    /**
     * Converts files to base64 encoded string
     */
    function getBase64($q, file) {
        return $q((resolve, reject) => {
            const reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = () => resolve(reader.result.split(',')[1]);
            reader.onerror = error => reject(error);
        });
    }

    const app = angular.module('invoiceApp', ['ngRoute']);

    app.directive("selectNgFiles", function () {
        return {
            require: "ngModel",
            link: function postLink(scope, elem, attrs, ngModel) {
                elem.on("change", function (e) {
                    var files = elem[0].files[0];
                    ngModel.$setViewValue(files);
                })
            }
        }
    });

    app.factory('ParseResultService', ['$http', '$q', function ($http, $q) {
        const store = {};

        return {
            uploadFile: function (fileData) {
                return $http.post(API_BASE + '/invoice/parse', { payload: fileData })
                    .then(function (resp) {
                        store[resp.data.id] = resp.data;
                        return resp.data.id;
                    });
            },

            getParseResult: function (id) {
                if (store.hasOwnProperty(id)) {
                    return $q.resolve(store[id]);
                }

                return $http.get(API_BASE + '/invoice/' + id)
                    .then(function (resp) {
                        store[resp.data.id] = resp.data;
                        return resp.data;
                    });
            },

            getCompanyInvoice: function (id, companyName) {
                return $http.get(API_BASE + '/invoice/' + id + '/company', {
                    params: { companyName: companyName }
                })
                    .then(function (resp) {
                        return resp.data;
                    });
            }

        };
    }]);

    app.config(function ($routeProvider) {
        $routeProvider
            .when("/invoice-result/:resultId", {
                templateUrl: "parse-result.html",
                controller: "ParseResultCtrl"
            })
            .when("/invoice/:resultId/company", {
                templateUrl: "company-invoice.html",
                controller: "CompanyInvoiceCtrl"
            })
            .otherwise({
                templateUrl: "home.html",
                controller: "HomeCtrl"
            });
    });

    app.controller('ParseResultCtrl', ['$scope', 'ParseResultService', '$routeParams', function ($scope, ParseResultService, $routeParams) {
            var vm = $scope.vm = this, resultId = $routeParams.resultId;
            vm.resultId = resultId;
            vm.isLoading = true;
            vm.hasError = false;
            vm.companies = [];
            ParseResultService.getParseResult(resultId)
                .then(function(data) {
                    vm.isLoading = false;
                    for(var k in data.companies) {
                        if(data.companies.hasOwnProperty(k)) {
                            vm.companies.push({
                                name: k,
                                amount: parseFloat(data.companies[k])
                            });
                        }
                    }
                })
                .catch(function() {
                    vm.hasError = true;
                    vm.isLoading = false;
                });
            
        }
    ]);

    app.controller('CompanyInvoiceCtrl', ['$scope', 'ParseResultService', '$routeParams', function ($scope, ParseResultService, $routeParams) {
            var vm = $scope.vm = this;
            vm.companyName = $routeParams.companyName;
            vm.resultId = $routeParams.resultId;
            vm.invoice = null;
            ParseResultService.getCompanyInvoice($routeParams.resultId, vm.companyName)
                .then(function(invoice) {
                    vm.invoice = invoice;
                })
                .catch(function(err) {
                    console.error('Could not fetch invoice,', err);
                    vm.invoice = false;
                });
        }
    ]);

    app.controller('HomeCtrl', ['$scope', '$q', 'ParseResultService', '$location', function ($scope, $q, ParseResultService, $location) {
        var vm = $scope.vm = this;
        console.log('HomeController');
        vm.uploadFile = function () {
            vm.isLoading = true;
            vm.errorMessage = null;
            getBase64($q, vm.csvFile)
                .then(ParseResultService.uploadFile)
                .then(function (id) {
                    vm.isLoading = false;
                    document.getElementById('statement-file').value = ''
                    vm.csvFile = null;
                    $location.path('/invoice-result/'+id);
                })
                .catch(function (resp) {
                    vm.isLoading = false;
                    var data = resp.data;
                    vm.errorMessage = data.error;
                })
        }

    }
    ])

})();