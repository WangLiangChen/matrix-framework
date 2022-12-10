create table if not exists matrix_lock
(
    lock_group  varchar(36) not null,
    lock_key    varchar(36) not null,
    lock_at     timestamp   not null,
    lock_expire timestamp   not null,
    lock_owner  varchar(36) not null,
    constraint matrix_lock_pk primary key(lock_group,lock_key)
);
comment
    on table matrix_lock is '分布式锁';
-- column comments
comment
    on column matrix_lock.lock_group is '组标识';
comment
    on column matrix_lock.lock_key is '锁标识';
comment
    on column matrix_lock.lock_at is '锁定时间';
comment
    on column matrix_lock.lock_expire is '过期时间';
comment
    on column matrix_lock.lock_owner is '锁持有者';
