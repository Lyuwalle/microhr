# microhr
微人事项目: vhr是后端项目，vuehr是前端项目
## 部署项目
打开Oracle VM VirtualBox启动Linux服务器;
打开终端ifconfig命令得到ip地址;
使用smarTTY连接linux（hostname是linux的ip地址，hostname是root，密码：l）;
启动docker：systemctl start docker     system ps -a   查看所有的容器;
- 空的数据库（第一次启动的时候需要）
- redis（要用docker打开    docker start containnerID）
- rabbitmq（要用docker打开）
application.properties中的host都修改成当前linux中的ip地址(修改vhr-web和vhrmailserver中的配置文件)
如果不运行mailserver，那么不必要修改配置文件，如果运行mailserver，还要访问localhost:15672（guest，guest）中新建一个队列nurmann.mail.queue
## 启动redis
项目中配置的redis端口是6379，密码是123456
在docker中启动redis之后还要重新授权123456密码
首先打开一个新的端口
[root@localhost ~]# cd redis-5.0.7
[root@localhost redis-5.0.7]# redis-server redis.conf 
[root@localhost redis-5.0.7]# src/redis-cli
127.0.0.1:6379> config set requirepass 123456
OK
127.0.0.1:6379> auth 123456
OK
或者：
docker exec -it c1d030a37c09 redis-cli（c1d030a37c09为id）
## 启动项目
后端端口：8081，前端端口：8080

### 终端查看mysql数据库
mysql -u root -p
输入密码就可以进入，命令加分号
