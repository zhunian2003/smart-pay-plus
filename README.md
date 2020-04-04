# smart-pay-plus 【待完善】

#### 项目简介
一个基于 Spring Boot 2.2.5 、 Mybaties Plus、 JWT、Spring Security、Redis、Vue的前后端分离的支付系统.
目前已经接入支付渠道：微信(条码支付、扫码支付)、支付宝(条码支付、扫码支付).

#### 演示地址
>陆陆续续收到一些打赏，为了更好的体验已用于演示服务器升级。谢谢各位小伙伴。
[在线体验](http://122.51.99.227:8088/)

#### 技术选型
-核心框架：Spring Boot 2.x
-安全框架：Apache Shiro 1.4
-持久层框架：MyBatis 3.x
-数据库连接池：Druid 1.x
-前台界面 Element UI

#### 系统功能
1.  用户管理：用户是系统操作者，该功能主要完成系统用户配置。
2.  部门管理：配置系统组织机构（公司、部门、小组），树结构展现支持数据权限。
3.  岗位管理：配置系统用户所属担任职务。
4.  菜单管理：配置系统菜单，操作权限，按钮权限标识等。
5.  角色管理：角色菜单权限分配、设置角色按机构进行数据范围权限划分。
6.  字典管理：对系统中经常使用的一些较为固定的数据进行维护。
7.  参数管理：对系统动态配置常用参数。
8.  通知公告：系统通知公告信息发布维护。
9.  操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。
10. 登录日志：系统登录日志记录查询包含登录异常。
11. 在线用户：当前系统中活跃用户状态监控。
12. 定时任务：在线（添加、修改、删除)任务调度包含执行结果日志。
13. 服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。

#### 系统预览
<table>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2020/0404/210648_7884cb8c_535810.jpeg"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2020/0404/210719_980450a5_535810.jpeg"/></td>
    </tr>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2020/0404/210729_26a6d8ca_535810.jpeg"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2020/0404/210745_39283dd3_535810.jpeg"/></td>
    </tr>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2020/0404/210754_2b564945_535810.jpeg"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2020/0404/210802_47511828_535810.jpeg"/></td>
    </tr>
</table>

#### 软件需求
-JDK1.8
-Maven3.0+
-MySQL5.5+或者MsSQL(可扩展支持Oracle数据库等)

#### 本地部署

1.  Fork 本仓库
2.  通过git下载源码

>项目的发展离不开您的支持，请作者喝杯咖啡吧。

<table>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2020/0404/212505_384f0630_535810.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2020/0404/212518_d36802e0_535810.png"/></td>
    </tr>
    <tr>
        <td align="center">微信</td>
        <td align="center">支付宝</td>
    </tr>
</table>

#### 码云特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5.  码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
