create table matrix_lock (
    lock_key varchar not null,
    lock_at timestamp without time zone not null,
    lock_expire timestamp without time zone not null,
    lock_owner varchar not null,
    constraint matrix_lock_pk primary key (lock_key)
);
comment on table matrix_lock is '分布式锁';

-- column comments

comment on column matrix_lock.lock_key is '锁标识';
comment on column matrix_lock.lock_at is '锁定时间';
comment on column matrix_lock.lock_expire is '过期时间';
comment on column matrix_lock.lock_owner is '锁持有者';
