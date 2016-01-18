/**
 * Created by zkp on 2015/12/22.
 */
angular.module('myApp',['ionic','ngMockE2E','myApp.services'])
.config(function ($stateProvider, $urlRouterProvider, USER_ROLES) {
    $stateProvider
        .state('tab',{
            url:'/tab',
            abstract:true,
            templateUrl:'tab.html'
        })
        .state('tab.sendmsg', {
            url: '/sendmsg',
            views:{
                'sendmsg':{
                    templateUrl: 'sendmsg.html',
                    controller:'sendmsgCtrl'
                }
            }
        })
        .state('tab.getmsg', {
            url:'/getmsg',
            views:{
                'getmsg':{
                    templateUrl: 'getmsg.html',
                    controller:'getmsgCtrl'
                }
            }
        })
        .state('tab.my', {
            url:'/my',
            views:{
                'my':{
                    templateUrl: 'my.html',
                    controller:'myCtrl'
                }
            }
        })
        .state('login', {
            url:'/login',
            templateUrl: 'login.html',
            controller:'loginCtrl'
        });
    $urlRouterProvider.otherwise('/tab/sendmsg');
})

.run(function($httpBackend){
    $httpBackend.whenPOST('http://localhost:63342/weixin/yidong')
        .respond({message: 'This is my valid response!'});
    $httpBackend.whenGET('http://localhost:8100/notauthenticated')
        .respond(401, {message: "Not Authenticated"});
    $httpBackend.whenGET('http://localhost:8100/notauthorized')
        .respond(403, {message: "Not Authorized"});
    //$httpBackend.whenGET().passThrough();
    $httpBackend.whenGET(/.*/).passThrough();
})

.run(function ($rootScope, $state, AuthService, AUTH_EVENTS) {
    $rootScope.$on('$stateChangeStart', function (event,next, nextParams, fromState) {
        if (!AuthService.isAuthenticated()) {
            if (next.name !== 'login') {
                event.preventDefault();
                $state.go('login');
            }
        }
    });
});
