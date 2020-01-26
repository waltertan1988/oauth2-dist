# oauth2-dist
一个基于OAuth2协议（授权码模式）的自定义授权服务器、资源服务器整合例子
## 时序交互设计图
![Pandao editor.md](https://github.com/waltertan1988/oauth2-dist/blob/master/oauth2-doc/charts/oauth2.png?raw=true "oauth2.png")
## 开始使用

### 授权服务器（oauth2-authorization-server）

### 资源服务器（oauth2-resource-server）及客户端SDK（oauth2-client-sdk）

### 公共依赖模块（oauth2-commons）

### 关于刷新令牌（RefreshToken）
在取得访问令牌串和刷新令牌串且访问令牌串已失效时，可通过以下请求进行刷新令牌（即获取新的访问令牌串和刷新令牌串）：  
[METHOD]POST  
[HEADER] Authorization:bearer {刷新令牌串}  
[URL] http://localhost:7080/authServer/oauth/token  
[BODY]  
grant_type:refresh_token  
refresh_token: {刷新令牌串}  
scope: {预定义的scope}  
