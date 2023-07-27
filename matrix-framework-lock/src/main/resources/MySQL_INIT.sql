create table if not exists matrix_lock
(
    lock_group  varchar(36) not null comment '组标识',
    lock_key    varchar(360) not null comment '锁标识',
    lock_at     datetime(6) not null comment '锁定时间',
    lock_expire datetime(6) not null comment '锁过期时间',
    lock_owner  varchar(36) not null comment '锁持有者',
    constraint matrix_lock_pk primary key(lock_group,lock_key)
) comment ='分布式锁';
