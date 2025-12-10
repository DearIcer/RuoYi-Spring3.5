-- ----------------------------
-- 客户端用户表
-- ----------------------------
drop table if exists client_user;
create table client_user (
  user_id           bigint(20)      not null auto_increment    comment '用户ID',
  user_name         varchar(30)     not null                   comment '用户账号',
  nick_name         varchar(30)     not null                   comment '用户昵称',
  email             varchar(50)     default ''                 comment '用户邮箱',
  phonenumber       varchar(11)     default ''                 comment '手机号码',
  sex               char(1)         default '0'                comment '用户性别（0男 1女 2未知）',
  avatar            varchar(100)    default ''                 comment '头像地址',
  password          varchar(100)    default ''                 comment '密码',
  status            char(1)         default '0'                comment '账号状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  login_ip          varchar(128)    default ''                 comment '最后登录IP',
  login_date        datetime                                   comment '最后登录时间',
  pwd_update_date   datetime                                   comment '密码最后更新时间',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (user_id)
) engine=innodb auto_increment=100 comment = '客户端用户表';

-- ----------------------------
-- 初始化-客户端用户表数据
-- ----------------------------
insert into client_user values(1, 'client', '客户端用户', 'client@163.com', '15888888888', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '客户端用户');


-- ----------------------------
-- 菜单 SQL
-- ----------------------------
-- 客户端用户管理菜单
insert into sys_menu values('2000', '客户端用户管理', '0', '4', 'clientuser', 'client/user/index', '', '', 1, 0, 'C', '0', '0', 'client:user:list', 'peoples', 'admin', sysdate(), '', null, '客户端用户管理菜单');

-- 客户端用户管理按钮
insert into sys_menu values('2001', '客户端用户查询', '2000', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'client:user:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2002', '客户端用户新增', '2000', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'client:user:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2003', '客户端用户修改', '2000', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'client:user:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2004', '客户端用户删除', '2000', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'client:user:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2005', '客户端用户导出', '2000', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'client:user:export', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2006', '客户端用户重置密码', '2000', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'client:user:resetPwd', '#', 'admin', sysdate(), '', null, '');
