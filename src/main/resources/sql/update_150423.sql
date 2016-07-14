create table `drainage_info`(
  `id` int unsigned auto_increment,
  `drainage_key` varchar(100) not null comment '引流唯一键',
  `service_name` varchar(100) not null default '' comment '引流服务名',
  `method_name` varchar(100) not null default '' comment '引流方法名',
  `service_ip` varchar(100) not null default '' comment '服务提供者IP',
  `service_port` int not null default 0 comment '服务提供者port',
  primary key(`id`),
  unique key `uniq_drainage_key` (drainage_key)
) engine=innodb default charset=utf8 comment '引流信息表';

