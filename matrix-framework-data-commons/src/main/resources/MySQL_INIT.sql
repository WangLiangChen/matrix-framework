/*==============================================================*/
/* Table: matrix_dictionary                                     */
/*==============================================================*/
create table if not exists matrix_dictionary
(
    dictionary_id        bigint not null  comment 'DataItem PrimaryKey',
    dictionary_group     varchar(36) not null  comment '字典项分组标识',
    dictionary_type      varchar(36) not null  comment 'DICTIONARY-字典;TAG-标签',
    dictionary_code      varchar(36) not null  comment '字典项标识',
    dictionary_name      varchar(36) not null  comment '字典项名称',
    dictionary_desc      varchar(100) not null  comment '字典项描述',
    data_type            varchar(6) not null  comment 'STRING|NUMERIC',
    dictionary_values    varchar(1000) not null  comment '字典值 son array:[{"value":"","label":"","desc":""}]',
    primary key (dictionary_id),
    unique key ak_unique_key (dictionary_group, dictionary_code),
    version int not null default '0' comment 'version',
    owner varchar(64) not null default '' comment 'owner',
    creator varchar(64) not null default '' comment 'creator',
    create_datetime datetime not null comment 'create_datetime',
    modifier varchar(64) not null default '' comment 'modifier',
    modify_datetime datetime not null comment 'modify_datetime',
    summary varchar(500) not null default '' comment 'summary',
    state varchar(36) not null default '' comment 'state'
    );

alter table matrix_dictionary comment '数据字典,如给单选、复选提供数据源';

drop table if exists matrix_tagging;

/*==============================================================*/
/* Table: matrix_tagging                                        */
/*==============================================================*/
create table if not exists matrix_tagging
(
    tagging_id           bigint not null  comment 'PrimaryKey',
    tagging_group        varchar(36) not null  comment '打标分组标识',
    table_name           varchar(36) not null  comment '数据表名',
    data_id              varchar(36) not null  comment '被打标的数据ID',
    tagging_code         varchar(36) not null  comment '字典项标识',
    tagging_value        varchar(36) not null  comment '字典值标识',
    primary key (tagging_id),
    unique key ak_unique_key (tagging_group, table_name, data_id, tagging_code),
    version int not null default '0' comment 'version',
    owner varchar(64) not null default '' comment 'owner',
    creator varchar(64) not null default '' comment 'creator',
    create_datetime datetime not null comment 'create_datetime',
    modifier varchar(64) not null default '' comment 'modifier',
    modify_datetime datetime not null comment 'modify_datetime',
    summary varchar(500) not null default '' comment 'summary',
    state varchar(36) not null default '' comment 'state'
    );

alter table matrix_tagging comment '数据打标';
