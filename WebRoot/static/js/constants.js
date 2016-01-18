angular.module('myApp')

.constant('AUTH_EVENTS', {
  notAuthenticated: 'auth-not-authenticated',
  notAuthorized: 'auth-not-authorized',
  authenticated: 'auth-authenticated'
})
.constant('YIDONG',{
  YIDONGURL:'http://101.200.176.31/zxtd'
})
.constant('USER_ROLES', {
  admin: 'admin_role',
  public: 'public_role',
  registered: "registered_role"
});
