/*==============================================================*/
/* Table: matrix_lock                                            */
/*==============================================================*/
/*
create table if not exists matrix_lock (
    lock_key varchar(36) not null comment '',
    lock_datetime datetime(6) not null comment '锁定时间',
    lock_until datetime(6) not null comment '锁结束时间',
    lock_owner varchar(36) not null comment '锁获得者的标识',
    primary key(lock_key)
) comment '数据库分布式锁';
*/
/*==============================================================*/
/* Table: matrix_sequence                                        */
/*==============================================================*/
/*
create table if not exists matrix_sequence (
    sequence_key varchar(36) not null comment '要生成序列ID的Key',
    sequence_number bigint not null comment '该序列key当前的序号',
    primary key(sequence_key)
) comment '序列号生成器';
*/