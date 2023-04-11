create table if not exists matrix_sequence
(
    sequence_group  varchar(36) not null,
    sequence_key    varchar(36) not null,
    sequence_number int8        not null,
    constraint matrix_sequence_pk primary key (sequence_group, sequence_key)
);
comment on table matrix_sequence is '序列号';

-- column comments

comment on column matrix_sequence.sequence_group is '组标识';
comment on column matrix_sequence.sequence_key is '标识';
comment on column matrix_sequence.sequence_number is '当前序号';

/*==============================================================*/
/* Table: matrix_columns                                        */
/*==============================================================*/
create table if not exists matrix_columns (
    column_id            int8                 not null,
    column_key           varchar(36)          not null,
    column_group         varchar(36)          not null,
    table_name           varchar(36)          not null,
    column_name          varchar(36)          not null,
    data_type            varchar(36)          not null,
    column_default       varchar(100)         not null,
    is_nullable          int2                 not null,
    column_regex         varchar(100)         not null,
    column_comment       varchar(100)         not null
);

comment on table matrix_columns is
'扩展列';

comment on column matrix_columns.column_id is
'PrimaryKey';

comment on column matrix_columns.column_key is
'UniqueKey';

comment on column matrix_columns.column_group is
'分组标识,用于隔离数据';

comment on column matrix_columns.table_name is
'表名';

comment on column matrix_columns.column_name is
'列名';

comment on column matrix_columns.data_type is
'值类型';

comment on column matrix_columns.column_default is
'默认值';

comment on column matrix_columns.is_nullable is
'can be null';

comment on column matrix_columns.column_regex is
'值校验正则';

comment on column matrix_columns.column_comment is
'列说明';

/*==============================================================*/
/* Index: matrix_columns_pk                                     */
/*==============================================================*/
create unique index if not exists matrix_columns_pk on matrix_columns (
    column_id
    );

/*==============================================================*/
/* Index: matrix_columns_ak                                     */
/*==============================================================*/
create unique index if not exists matrix_columns_ak on matrix_columns (
    column_key
    );
