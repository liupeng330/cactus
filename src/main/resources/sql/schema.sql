create database cactus;
USE cactus;

drop table if exists `zkcluster`;
create table `zkcluster`(
  `id` int unsigned auto_increment ,
  `name` varchar(50) not null comment '机房名',
  `address` varchar(1000) not null comment '地址',
  primary key (`id`)
)engine=innodb default charset=utf8 comment '机房表';

drop table if exists `ca_user`;
create table `ca_user`(
  `id` int unsigned auto_increment ,
  `username` varchar(20) not null comment '用户名',
  `role` tinyint not null default 0 comment '用户角色（0：普通用户;1：owner;2：超级管理员）',
  primary key (`id`)
)engine=innodb default charset=utf8 comment '用户表';

drop table if exists `ca_log`;
create table `ca_log`(
  `id` int unsigned auto_increment,
  `uid` int unsigned not null comment '用户id',
  `app` varchar(100) not null comment '应用名',
  `service` varchar(100) not null comment '服务名',
  `hostname` varchar(100) not null comment '机器名和端口号',
  `msg` varchar(2000) not null comment '操作信息',
  `operate_time` datetime not null comment '操作时间',
  primary key (`id`)
)engine=innodb default charset=utf8 comment '日志表';

drop table if exists `governance`;
create table `governance`(
  `id` int unsigned auto_increment,
  `zk_id` int unsigned not null comment 'zk地址的id，与zkcluster表对应',
  `name` varchar(50) not null default '' comment '配置名称',
  `group_name` varchar(100) not null comment 'group名',
  `service_name` varchar(100) not null comment '服务名',
  `service_group` varchar(100) not null default '' comment 'serviceGroup名',
  `version` varchar(10) not null default '' comment '服务版本号',
  `ip` int unsigned not null comment 'ip',
  `port` int unsigned not null comment '端口号',
  `url` varchar(1000) not null comment '动态配置或路由规则的url',
  `path_type` tinyint not null comment 'url的类型,可选值：1(provider)、2(consumer)、3(router)、4(override)',
  `last_operator` varchar(20) not null comment '最后操作者',
  `status` tinyint not null comment 'url状态，可选值：0(online)、1(offline)、2(delete)',
  `data_type` tinyint not null comment 'url类型，标识是用户原始录入的还是经过系统合并过的，可选值：0(userData)、1(zkData)、2(apiData)',
  `create_time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '提交时间' ,
  `last_update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT  '最后更新时间' ,
  primary key (`id`),
  index `idx_name`(`name`),
  index `idx_service_sign`(`service_name`,`group_name`),
  index `idx_last_update_time`(`last_update_time`)
)engine=innodb default charset=utf8 comment '治理表，用于保存dubbo配置信息';

create index idx_ca_user_username on ca_user(`username`);

create index idx_ca_log_uid on ca_log(`uid`);

create index idx_ca_log_operate_time on ca_log(`operate_time`);
