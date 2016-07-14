drop table if exists `special_path`;
create table `special_path`(
  `id` int unsigned auto_increment,
  `group_name` varchar(100) not null comment '特殊路径的group名称',
  `mid_path` varchar(150) not null comment '介于group和service之间的路径',
  primary key(`id`),
  unique key `uniq_group_mid_path` (group_name, mid_path)
) engine=innodb default charset=utf8 comment '特殊路径表';

alter table `governance` add column `mid_path` varchar(150)  not null default '' comment 'group和service之间的路径' after `group_name`;
alter table `ca_log` add column `mid_path` varchar(150)  not null default '' comment 'group和service之间的路径' after `app`;