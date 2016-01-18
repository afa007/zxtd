/**
 * Created by zkp on 2015/12/26.
 */
angular.module('myApp.services',['ngCookies'])

    .service('AuthService', function($q, $http,$cookies, YIDONG) {
        var username = '';
        var isAuthenticated = false;
        var token = '';
        var user_id = '';

        //查询当前yyyyMMdd格式日期
        var getYyyyMMdd = function(){
            return (new Date()).toISOString().slice(0,10).replace(/-/g,"");
        };
        //拼接Yidong登录Url
        var getLoginUrl = function(username,password){
            var key = username + getYyyyMMdd() + ',fh,';
            var fkey = md5(key);
            var url = YIDONG.YIDONGURL + '/appuser/login?USERNAME='
                + username + '&PASSWORD=' + password + '&FKEY=' + fkey;
            return url;
        };
        //读取cookie
        var load = function(){
            username = $cookies.get('username');
            token = $cookies.get('token');
            user_id = $cookies.get('user_id');
            isAuthenticated = !!username;
        };
        load();
        //清理cookie
        var clearCookie = function(){
            $cookies.remove('username');
            $cookies.remove('token');
            $cookies.remove('user_id');
        }
        //处理登陆
        var login = function(name, pw) {
            var url = getLoginUrl(name,pw);
            return $q(function(resolve, reject) {
                $http.get(url).then(
                    function(response){
                        if(response.data.result == '00'){
                            $cookies.put('username',name);
                            $cookies.put('token','123');
                            $cookies.put('user_id',response.data.pd.USER_ID);
                            load();
                            resolve("1");//测试，返回1，登录成功
                        }else if(response.data.result == '01'){
                            //用户名或密码错误
                            reject('用户名或密码错误');
                        }else if(response.data.result == '02'){
                            //用户名密码校验成功，读取客户信息失败
                            reject('用户名密码校验成功，读取客户信息失败');//测试
                        }else if(response.data.result == '03'){
                            //客户未注册
                            reject('客户未注册');
                        }else if(response.data.result == '04'){
                            //参数缺失
                            reject('参数缺失');
                        }else if(response.data.result == '05'){
                            //FKEY值校验失败
                            reject('FKEY值校验失败');
                        }else{
                            reject('登录失败');
                        }
                    },
                    function(response){
                        reject(response.data);//测试
                    }
                );
            });
        };

        //退出登录
        var logout = function() {
            return $q(function(resolve, reject) {
                $cookies.remove('username');
                $cookies.remove('token');
                load();
                resolve('1');//测试,返回1代表退出成功
            });
        };

        //发送短信
        var sendmsg = function(msisdn, userid, content){
            var url = YIDONG.YIDONGURL + '/appbusi/sendmsg?MSISDN='
                +msisdn+'&CONTENT='+content+'&USERID='+userid+'';
            return $q(function(resolve, reject) {
                $http.get(url).then(
                    function(response){
                        if(response.data.result == '00'){
                            resolve('发送成功');//测试，返回1，登录成功
                        }else if(response.data.result == '01'){
                            //发送失败
                            reject('发送失败');
                        }else{
                            reject('发送失败');
                        }
                    },
                    function(response){
                        reject(response.data);//测试
                    }
                );
            });
        }
        //接收短信
        var getmsg = function(userid, page_num, page_size, date_s){
            url =YIDONG.YIDONGURL + '/appbusi/getmsg?USERID='+userid+'&PAGE_NUM='
                + page_num + '&PAGE_SIZE=' + page_size + '&DATE_S='+date_s;
            return $q(function(resolve, reject) {
                $http.get(url).then(
                    function(response){
                        if(response.data.result && response.data.result == '00'){
                            resolve(response.data);
                        }else if(response.data.result && response.data.result == '01'){
                            reject('接收短信失败');
                        }else{
                            reject(response.data);
                        }
                    },
                    function(response){
                        reject(response.data);
                    }
                );
            });
        };

        return {
            login: login,
            logout: logout,
            sendmsg: sendmsg,
            getmsg: getmsg,
            isAuthenticated: function() {return isAuthenticated;},
            username: function() {return username;},
            user_id: function(){return user_id;}
        };
    })
    .factory('AuthInterceptor', function ($rootScope, $q, AUTH_EVENTS) {
        return {
            responseError: function (response) {
                $rootScope.$broadcast({
                    401: AUTH_EVENTS.notAuthenticated,
                    403: AUTH_EVENTS.notAuthorized
                }[response.status], response);
                return $q.reject(response);
            }
        };
    })
    .config(function ($httpProvider) {
        $httpProvider.interceptors.push('AuthInterceptor');
    });

