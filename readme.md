# 专门用于产生测试数据的子模块

```bash
cd scripts
./start_kafka.sh # 启动本地kafka集群
#docker image pull慢， 可以参考这个: https://dockerproxy.com/docs

mvn clean package #启动数据写入程序
```

Kafdrop url: http://localhost:9000
kafka broker: localhost:9092

## IDEA Spring依赖包找不到

```bash
#更新idea配置文件
mvn -U idea:idea
```

## 本地连接VPC内MSK

```bash
#挑选其中一个broker做隧道连接
b-3.rdskafkareplication.qh77pm.c1.kafka.us-east-1.amazonaws.com:9092
ssh -i us-east-1.pem -L 9092:b-3.rdskafkareplication.qh77pm.c1.kafka.us-east-1.amazonaws.com:9092 ec2-user@54.152.225.130
#然后在本地localhost:9092
```

### 安装和访问kafka管理界面

```bash
ssh -i us-east-1.pem -L 9000:54.152.225.130:9000 ec2-user@54.152.225.130
```

## 连接MSK错误排除

[Producer clientId=producer-1] Error while fetching metadata with correlation id 143 :
{flink-user-behavior-data=UNKNOWN_TOPIC_OR_PARTITION}

### brew 安装的mariadb访问

> https://www.twle.cn/t/803

```bash
mysql -u yexw #没有密码
create database uv;
grant all privileges on uv.* to test@'%' identified by 'newpass';
flush privileges;

#等操作
brew brew services info/restart/stop/start mariadb
```
