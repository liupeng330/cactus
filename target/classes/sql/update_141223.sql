drop table if exists `special_group`;
create table `special_group`(
  `id` int unsigned auto_increment,
  `group_name` varchar(250) not null comment '特殊group',
  primary key(`id`),
  unique key `uniq_group` (group_name)
) engine=innodb default charset=utf8 comment '特殊group表';

drop table if exists `group_cluster`;
create table `group_cluster`(
  `id` int unsigned auto_increment,
  `cluster` varchar(200) not null comment 'cluster名称',
  primary key(`id`),
  unique key `uniq_cluster` (cluster)
) engine=innodb default charset=utf8 comment 'group cluster表';

update governance set group_name=if(mid_path='',group_name,concat(group_name,'/',mid_path));
alter table governance drop index idx_group_name_mid_path_service_name, add index idx_group_name_service_name(group_name, service_name), drop column mid_path;

update ca_log set app=if(mid_path='',app,concat(app,'/',mid_path));
alter table ca_log drop column mid_path,
add column zk_id int unsigned not null default 0 comment 'zk机房id',
add column service_group varchar(100) not null default '' comment 'serviceGroup名',
add column version varchar(10) not null default '' comment '服务版本号',
add column ip int unsigned not null default 0 comment 'ip',
add column port int unsigned not null default 0 comment '端口号',
add index idx_group_service_zk_ip_port(app, service, zk_id, ip, port);