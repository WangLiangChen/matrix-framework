/*==============================================================*/
/* Table: matrix_dictionary                                     */
/*==============================================================*/
create table if not exists matrix_dictionary (
    dictionary_id        int8                 not null,
    dictionary_key       varchar(80)          not null,
    dictionary_group     varchar(36)          not null,
    dictionary_code      varchar(36)          not null,
    dictionary_name      varchar(36)          not null,
    dictionary_type      varchar(36)          not null,
    data_type            varchar(6)           not null,
    constraint pk_matrix_dictionary primary key (dictionary_id),
    version         int4          not null default 0,
    owner           varchar(60)   not null default '',
    creator         varchar(60)   not null default '',
    create_datetime timestamp     not null,
    modifier        varchar(60)   not null default '',
    modify_datetime timestamp     not null,
    summary         varchar(200)  not null default '',
    state           varchar(36)   not null
    );

comment on table matrix_dictionary is
'数据字典,如给单选、复选提供数据源';

comment on column matrix_dictionary.dictionary_id is
'DataItem PrimaryKey';

comment on column matrix_dictionary.dictionary_key is
'UniqueKey:{dictionary_group}-{dictionary_code}';

comment on column matrix_dictionary.dictionary_group is
'字典项分组标识';

comment on column matrix_dictionary.dictionary_code is
'字典项标识';

comment on column matrix_dictionary.dictionary_name is
'字典项名称';

comment on column matrix_dictionary.dictionary_type is
'DICTIONARY-字典;TAG-标签';

comment on column matrix_dictionary.data_type is
'STRING|NUMERIC';

/*==============================================================*/
/* Index: matrix_dictionary_pk                                  */
/*==============================================================*/
create unique index if not exists matrix_dictionary_pk on matrix_dictionary (
    dictionary_id
    );

/*==============================================================*/
/* Index: matrix_dictionary_ak                                  */
/*==============================================================*/
create unique index if not exists matrix_dictionary_ak on matrix_dictionary (
    dictionary_key
    );

/*==============================================================*/
/* Table: matrix_dictionary_value                               */
/*==============================================================*/
create table if not exists matrix_dictionary_value (
                                                       value_id             int8                 not null,
                                                       dictionary_id        int8                 null,
                                                       value_key            varchar(120)         not null,
    dictionary_key       varchar(80)          not null,
    dictionary_group     varchar(36)          not null,
    dictionary_code      varchar(36)          not null,
    value_code           varchar(36)          not null,
    value_label          varchar(36)          not null,
    constraint pk_matrix_dictionary_value primary key (value_id),
    version         int4          not null default 0,
    owner           varchar(60)   not null default '',
    creator         varchar(60)   not null default '',
    create_datetime timestamp     not null,
    modifier        varchar(60)   not null default '',
    modify_datetime timestamp     not null,
    summary         varchar(200)  not null default '',
    state           varchar(36)   not null
    );

comment on table matrix_dictionary_value is
'字典值';

comment on column matrix_dictionary_value.value_id is
'PrimaryKey';

comment on column matrix_dictionary_value.dictionary_id is
'DataItem PrimaryKey';

comment on column matrix_dictionary_value.value_key is
'UniqueKey:{dictionary_key}-{value_code}';

comment on column matrix_dictionary_value.dictionary_key is
'UniqueKey:{dictionary_group}-{dictionary_code}';

comment on column matrix_dictionary_value.dictionary_group is
'字典项分组标识';

comment on column matrix_dictionary_value.dictionary_code is
'字典项标识';

comment on column matrix_dictionary_value.value_code is
'值标识';

comment on column matrix_dictionary_value.value_label is
'值名称';

/*==============================================================*/
/* Index: matrix_dictionary_value_pk                            */
/*==============================================================*/
create unique index if not exists matrix_dictionary_value_pk on matrix_dictionary_value (
    value_id
    );

/*==============================================================*/
/* Index: matrix_dictionary_value_fk                            */
/*==============================================================*/
create  index if not exists matrix_dictionary_value_fk on matrix_dictionary_value (
    dictionary_id
    );

/*==============================================================*/
/* Index: matrix_dictionary_value_ak                            */
/*==============================================================*/
create unique index if not exists matrix_dictionary_value_ak on matrix_dictionary_value (
    value_key
    );

/*==============================================================*/
/* Table: matrix_tagging                                        */
/*==============================================================*/
create table if not exists matrix_tagging (
                                              tagging_id           int8                 not null,
                                              tagging_key          varchar(36)          not null,
    tagging_group        varchar(36)          not null,
    business_type        varchar(36)          not null,
    business_id          varchar(36)          not null,
    tagging_code         varchar(36)          not null,
    tagging_value        varchar(36)          not null,
    constraint pk_matrix_tagging primary key (tagging_id),
    version         int4          not null default 0,
    owner           varchar(60)   not null default '',
    creator         varchar(60)   not null default '',
    create_datetime timestamp     not null,
    modifier        varchar(60)   not null default '',
    modify_datetime timestamp     not null,
    summary         varchar(200)  not null default '',
    state           varchar(36)   not null
    );

comment on table matrix_tagging is
'数据打标';

comment on column matrix_tagging.tagging_id is
'PrimaryKey';

comment on column matrix_tagging.tagging_key is
'UniqueKey: tagging_group,busiess_type,business_id,tagging_code,tagging_value拼接后MD5';

comment on column matrix_tagging.tagging_group is
'打标分组标识';

comment on column matrix_tagging.business_type is
'被打标的业务标识';

comment on column matrix_tagging.business_id is
'被打标的数据ID';

comment on column matrix_tagging.tagging_code is
'字典项标识';

comment on column matrix_tagging.tagging_value is
'字典值标识';

/*==============================================================*/
/* Index: matrix_tagging_pk                                     */
/*==============================================================*/
create unique index if not exists matrix_tagging_pk on matrix_tagging (
    tagging_id
    );

/*==============================================================*/
/* Index: matrix_tagging_ak                                     */
/*==============================================================*/
create unique index if not exists matrix_tagging_ak on matrix_tagging (
    tagging_key
    );

/*==============================================================*/
/* Table: matrix_infinite                                       */
/*==============================================================*/
create table if not exists matrix_infinite (
                                               infinite_id          int8                 not null,
                                               parent_id            int8                 null,
                                               infinite_key         varchar(36)          not null,
    infinite_group       varchar(36)          not null,
    parent_code          varchar(36)          not null,
    infinite_code        varchar(36)          not null,
    infinite_name        varchar(36)          not null,
    infinite_left        int4                 not null,
    infinite_right       int4                 not null,
    infinite_level       int4                 not null,
    constraint pk_matrix_infinite primary key (infinite_id),
    version         int4          not null default 0,
    owner           varchar(60)   not null default '',
    creator         varchar(60)   not null default '',
    create_datetime timestamp     not null,
    modifier        varchar(60)   not null default '',
    modify_datetime timestamp     not null,
    summary         varchar(200)  not null default '',
    state           varchar(36)   not null
    );

comment on table matrix_infinite is
'无限级分类字典';

comment on column matrix_infinite.infinite_id is
'PrimaryKey';

comment on column matrix_infinite.parent_id is
'PrimaryKey';

comment on column matrix_infinite.infinite_key is
'UniqueKey:infinite_group,infinite_code 一棵树的code不能重复';

comment on column matrix_infinite.infinite_group is
'分组标识,确定一棵树';

comment on column matrix_infinite.parent_code is
'父级节点标识';

comment on column matrix_infinite.infinite_code is
'节点标识';

comment on column matrix_infinite.infinite_name is
'节点名称';

comment on column matrix_infinite.infinite_left is
'节点左值';

comment on column matrix_infinite.infinite_right is
'节点右值';

comment on column matrix_infinite.infinite_level is
'节点层级';

/*==============================================================*/
/* Index: matrix_infinite_pk                                    */
/*==============================================================*/
create unique index if not exists matrix_infinite_pk on matrix_infinite (
    infinite_id
    );

/*==============================================================*/
/* Index: infinite_self_fk                                      */
/*==============================================================*/
create  index if not exists infinite_self_fk on matrix_infinite (
    parent_id
    );

/*==============================================================*/
/* Index: matrix_infinite_ak                                    */
/*==============================================================*/
create unique index if not exists matrix_infinite_ak on matrix_infinite (
    infinite_key
    );

