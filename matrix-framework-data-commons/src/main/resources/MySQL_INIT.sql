/*==============================================================*/
/* Table: matrix_dictionary                                     */
/*==============================================================*/
create table if not exists matrix_dictionary
(
    dictionary_id        bigint not null  comment 'DataItem PrimaryKey',
    dictionary_key       varchar(80) not null  comment 'UniqueKey:{dictionary_group}-{dictionary_code}',
    dictionary_group     varchar(36) not null  comment '字典项分组标识',
    dictionary_code      varchar(36) not null  comment '字典项标识',
    dictionary_name      varchar(36) not null  comment '字典项名称',
    dictionary_type      varchar(36) not null  comment 'DICTIONARY-字典;TAG-标签',
    data_type            varchar(6) not null  comment 'STRING|NUMERIC',
    primary key (dictionary_id),
    unique key ak_unique_key (dictionary_key),
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

/*==============================================================*/
/* Table: matrix_dictionary_value                               */
/*==============================================================*/
create table if not exists matrix_dictionary_value
(
    value_id             bigint not null  comment 'PrimaryKey',
    dictionary_id        bigint  comment 'DataItem PrimaryKey',
    value_key            varchar(120) not null  comment 'UniqueKey:{dictionary_key}-{value_code}',
    dictionary_key       varchar(80) not null  comment 'UniqueKey:{dictionary_group}-{dictionary_code}',
    dictionary_group     varchar(36) not null  comment '字典项分组标识',
    dictionary_code      varchar(36) not null  comment '字典项标识',
    value_code           varchar(36) not null  comment '值标识',
    value_label          varchar(36) not null  comment '值名称',
    primary key (value_id),
    unique key ak_unique_key (value_key),
    version int not null default '0' comment 'version',
    owner varchar(64) not null default '' comment 'owner',
    creator varchar(64) not null default '' comment 'creator',
    create_datetime datetime not null comment 'create_datetime',
    modifier varchar(64) not null default '' comment 'modifier',
    modify_datetime datetime not null comment 'modify_datetime',
    summary varchar(500) not null default '' comment 'summary',
    state varchar(36) not null default '' comment 'state'
    );

alter table matrix_dictionary_value comment '字典值';

/*==============================================================*/
/* Index: matrix_dictionary_value_fk                            */
/*==============================================================*/
create index matrix_dictionary_value_fk on matrix_dictionary_value
    (
     dictionary_id
        );

/*==============================================================*/
/* Table: matrix_tagging                                        */
/*==============================================================*/
create table if not exists matrix_tagging
(
    tagging_id           bigint not null  comment 'PrimaryKey',
    tagging_key          varchar(36) not null  comment 'UniqueKey: tagging_group,busiess_type,business_id,tagging_code,tagging_value拼接后MD5',
    tagging_group        varchar(36) not null  comment '打标分组标识',
    business_type        varchar(36) not null  comment '被打标的业务标识',
    business_id          varchar(36) not null  comment '被打标的数据ID',
    tagging_code         varchar(36) not null  comment '字典项标识',
    tagging_value        varchar(36) not null  comment '字典值标识',
    primary key (tagging_id),
    unique key ak_unique_key (tagging_key),
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

/*==============================================================*/
/* Table: matrix_infinite                                       */
/*==============================================================*/
create table if not exists matrix_infinite
(
    infinite_id          bigint not null  comment 'PrimaryKey',
    parent_id            bigint  comment 'PrimaryKey',
    infinite_key         varchar(36) not null  comment 'UniqueKey:infinite_group,infinite_code 一棵树的code不能重复',
    infinite_group       varchar(36) not null  comment '分组标识,确定一棵树',
    parent_code          varchar(36) not null  comment '父级节点标识',
    infinite_code        varchar(36) not null  comment '节点标识',
    infinite_name        varchar(36) not null  comment '节点名称',
    infinite_left        int not null  comment '节点左值',
    infinite_right       int not null  comment '节点右值',
    infinite_level       int not null  comment '节点层级',
    primary key (infinite_id),
    unique key ak_unique_key (infinite_key),
    version int not null default '0' comment 'version',
    owner varchar(64) not null default '' comment 'owner',
    creator varchar(64) not null default '' comment 'creator',
    create_datetime datetime not null comment 'create_datetime',
    modifier varchar(64) not null default '' comment 'modifier',
    modify_datetime datetime not null comment 'modify_datetime',
    summary varchar(500) not null default '' comment 'summary',
    state varchar(36) not null default '' comment 'state'
    );

alter table matrix_infinite comment '无限级分类字典';

/*==============================================================*/
/* Index: infinite_self_fk                                      */
/*==============================================================*/
create index infinite_self_fk on matrix_infinite
    (
     parent_id
        );
