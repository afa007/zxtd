angular.module('myApp')

.controller('appCtrl', function($scope, $state, $ionicPopup, AuthService, AUTH_EVENTS) {
  $scope.username = AuthService.username();

  $scope.$on(AUTH_EVENTS.notAuthorized, function(event) {
    var alertPopup = $ionicPopup.alert({
      title: '无权限!',
      template: '您没有访问该页面的权限.'
    });
  });

  $scope.$on(AUTH_EVENTS.notAuthenticated, function(event) {
    AuthService.logout();
    $state.go('login');
    var alertPopup = $ionicPopup.alert({
      title: '登录过期!',
      template: '对不起，您需要重新登录.'
    });
  });

  $scope.$on(AUTH_EVENTS.authenticated, function(event){
    AuthService.setIsAuthenticated(true);
  });

  $scope.setCurrentUsername = function(name) {
    $scope.username = name;
  };
})

.controller('loginCtrl', function($scope, $state, $ionicPopup, AuthService) {
  $scope.data = {};
  $scope.login = function(data) {
    AuthService.login(data.username, data.password).then(function(message) {
      $state.go('tab.sendmsg', {}, {reload: true});
      $scope.setCurrentUsername(data.username);
    }, function(err) {
      var alertPopup = $ionicPopup.alert({
        title: '登录失败!',
        template: '请检查您的用户名和密码!'+err
      });
    });
  };
})

.controller("sendmsgCtrl",function($scope,$ionicPopup,AuthService){
  $scope.sendmsg = function(msg){
    AuthService.sendmsg(msg.msisdn,AuthService.user_id(),msg.content).then(
        function(response){
          var alertPopup = $ionicPopup.alert({
            title: '发送成功!',
            template: response
          });
          //清除输入框内容
          msg.content = '';
          msg.msisdn = '';
        },
        function(response){
          var alertPopup = $ionicPopup.alert({
            title: '发送失败!',
            template: response
          });
        }
    );

  };
})

.controller("getmsgCtrl",function($scope,$http,$ionicPopup,$timeout,AuthService){
      $scope.items = [];
      $scope.totalnum = 0;//总条数
      $scope.curPageNum = 0;//当前页码
      AuthService.getmsg(AuthService.user_id(),1,5,'').then(
          function(response){
            $scope.items = $scope.items.concat(response.list);
            $scope.totalnum = response.cdn;
            $scope.curPageNum = 1;
          },
          function(response){
            $ionicPopup.alert({
              title: "查询失败",
              template: "查询失败"
            })
          }
      );

  $scope.load_more = function(){
    AuthService.getmsg(AuthService.user_id(),$scope.curPageNum+1,5,'').then(
        function(response){
          $scope.items = $scope.items.concat(response.list);
          $scope.totalnum = response.cdn;
          $scope.curPageNum ++;
        },
        function(response){
          $ionicPopup.alert({
            title: "查询失败",
            template: "查询失败"
          })
        }
    );
  };
})

.controller('myCtrl',function($scope, $state, AuthService){
    $scope.logout = function() {
      AuthService.logout();
      $state.go('login');
    };
});
