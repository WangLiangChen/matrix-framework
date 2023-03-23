create table if not exists matrix_sequence
(
    sequence_group  varchar(36) not null comment '组标识',
    sequence_key    varchar(36) not null comment '标识',
    sequence_number bigint      not null comment '当前序号',
    primary key(sequence_group,sequence_key)
) comment '序列生成器';

/*==============================================================*/
/* Table: matrix_columns                                        */
/*==============================================================*/
create table matrix_columns
(
    column_id            bigint not null  comment 'PrimaryKey',
    table_name           varchar(36) not null  comment '表名',
    column_name          varchar(36) not null  comment '列名',
    data_type            varchar(36) not null  comment '值类型',
    column_default       varchar(100) not null  comment '默认值',
    is_nullable          tinyint not null  comment 'can be null',
    column_regex         varchar(100) not null  comment '值校验正则',
    column_comment       varchar(100) not null  comment '列说明',
    primary key (column_id),
    unique key ak_unique_key (table_name, column_name)
);

alter table matrix_columns comment '扩展列';
