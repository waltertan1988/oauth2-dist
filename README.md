# oauth2-dist
一个基于OAuth2协议（授权码模式）、SpringBoot2.x、Spring Security和Redis的自定义授权服务器、资源服务器整合的SSO单点授权登录例子。

## 开始使用
### 依赖中间件
* mysql
* redis

### mysql初始化数据
参看oauth2-doc/db/schema.sql

登录用户名：0009785或walter
密码：123456

### 如何访问
* Step1: 客户端请求获取资源http://localhost:7081/resServer/resource  
此时无权限，无法访问，提示要先授权

* Step2：点击“授权”，尝试进入授权页面，此时发现未登录并重定向到登录页面。可选择不同模式获取访问令牌。
> 密码模式：登录成功后立即返回生成的访问令牌、刷新令牌  
>
> 授权码模式：登录成功后重定向回到授权页面，选择同意。此时页面会返回生成的授权码、访问令牌、刷新令牌。  

* Step3: 使用访问令牌再次访问资源http://localhost:7081/resServer/resource  
> 访问资源时，注意要在header带上Authorization请求头，其值为bearer <访问令牌值>  
> 例如：  
> bearer eyJvQWNjZXNzVG9rZW5WYWx1ZSI6IjdiMmJhMTZkLWM3OWQtNDcxOC1hNDU1LWU4ZmM4ZWQ3MTI2NyIsInVzZXJuYW1lIjoiMDAwOTc4NSJ9

此时可以成功访问资源了

## 系统说明
### OAuth2授权登录的时序交互设计图
![Pandao editor.md](https://github.com/waltertan1988/oauth2-dist/blob/master/oauth2-doc/charts/oauth2.png?raw=true "oauth2.png")

### 授权服务器（oauth2-authorization-server）

### 资源服务器（oauth2-resource-server）及客户端SDK（oauth2-client-sdk）

### 公共依赖模块（oauth2-commons）

### 关于刷新令牌（RefreshToken）
在取得访问令牌串和刷新令牌串且访问令牌串已失效时，可通过以下请求进行刷新令牌（即获取新的访问令牌串和刷新令牌串）：  
[Method]POST  
[Header]Authorization Bearer <原访问令牌值(或原刷新令牌值)>  
[URL] http://localhost:7080/authServer/oauth/token?grant_type=refresh_token&refresh_token=<旧的刷新令牌串>&scope=<预定义的scope>  
> grant_type：固定写死为refresh_token  
> {预定义的scope}：这里填all即可  
> 注：请求体带上访问令牌，是因为访问令牌里含username信息，用于检查用户在刷该次新请求时依然处于登录中的状态
