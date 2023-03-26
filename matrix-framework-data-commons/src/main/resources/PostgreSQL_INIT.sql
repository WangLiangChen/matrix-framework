/*==============================================================*/
/* Table: matrix_dictionary                                     */
/*==============================================================*/
create table if not exists matrix_dictionary (
                                                 dictionary_id        int8                 not null,
                                                 dictionary_group     varchar(36)          not null,
    dictionary_type      varchar(36)          not null,
    dictionary_code      varchar(36)          not null,
    dictionary_name      varchar(36)          not null,
    dictionary_desc      varchar(100)         not null,
    data_type            varchar(6)           not null,
    dictionary_values    varchar(1000)        not null,
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

comment on column matrix_dictionary.dictionary_group is
'字典项分组标识';

comment on column matrix_dictionary.dictionary_type is
'DICTIONARY-字典;TAG-标签';

comment on column matrix_dictionary.dictionary_code is
'字典项标识';

comment on column matrix_dictionary.dictionary_name is
'字典项名称';

comment on column matrix_dictionary.dictionary_desc is
'字典项描述';

comment on column matrix_dictionary.data_type is
'STRING|NUMERIC';

comment on column matrix_dictionary.dictionary_values is
'字典值 son array:[{"value":"","label":"","desc":""}]';

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
    dictionary_group,
    dictionary_code
    );

/*==============================================================*/
/* Table: matrix_tagging                                        */
/*==============================================================*/
create table if not exists matrix_tagging (
                                              tagging_id           int8                 not null,
                                              tagging_group        varchar(36)          not null,
    table_name           varchar(36)          not null,
    data_id              varchar(36)          not null,
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

comment on column matrix_tagging.tagging_group is
'打标分组标识';

comment on column matrix_tagging.table_name is
'数据表名';

comment on column matrix_tagging.data_id is
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
    tagging_group,
    table_name,
    data_id,
    tagging_code
    );
