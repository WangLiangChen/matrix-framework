/*==============================================================*/
/* Table: matrix_column                                         */
/*==============================================================*/
create table if not exists matrix_column
(
    column_id       int8         not null,
    tenant_key      varchar(36)  not null,
    app_key         varchar(36)  not null,
    env_key         varchar(36)  not null,
    table_name      varchar(36)  not null,
    column_name     varchar(36)  not null,
    data_type       varchar(36)  not null,
    column_default  varchar(100) not null,
    is_nullable     int2         not null,
    column_regex    varchar(100) not null,
    column_comment  varchar(100) not null,
    constraint pk_matrix_column primary key (column_id),
    constraint ak_matrix_column_unique_key unique (tenant_key, app_key, env_key, table_name, column_name),
    version         int4         not null default 0,
    owner           varchar(60)  not null default '',
    creator         varchar(60)  not null default '',
    create_datetime timestamp    not null,
    modifier        varchar(60)  not null default '',
    modify_datetime timestamp    not null,
    summary         varchar(200) not null default '',
    state           varchar(36)  not null
);

comment on table matrix_column is '扩展列';

comment on column matrix_column.column_id is 'PrimaryKey';

comment on column matrix_column.tenant_key is '租户标识';

comment on column matrix_column.app_key is '应用标识';

comment on column matrix_column.env_key is '应用环境标识如Dev/Test/Uat/Prod等';

comment on column matrix_column.table_name is '表名';

comment on column matrix_column.column_name is '列名';

comment on column matrix_column.data_type is '值类型';

comment on column matrix_column.column_default is '默认值';

comment on column matrix_column.is_nullable is 'can be null';

comment on column matrix_column.column_regex is '值校验正则';

comment on column matrix_column.column_comment is '列说明';

/*==============================================================*/
/* Table: matrix_column_value                                   */
/*==============================================================*/
create table if not exists matrix_column_value
(
    value_id        int8         not null,
    column_id       int8         null,
    tenant_key      varchar(36)  not null,
    app_key         varchar(36)  not null,
    env_key         varchar(36)  not null,
    table_name      varchar(36)  not null,
    column_name     varchar(36)  not null,
    row_key         varchar(36)  not null,
    data_type       varchar(36)  not null,
    column_value    varchar(36)  not null,
    constraint pk_matrix_column_value primary key (value_id),
    constraint ak_matrix_column_value_unique_key unique (tenant_key, app_key, env_key, table_name, column_name, row_key),
    version         int4         not null default 0,
    owner           varchar(60)  not null default '',
    creator         varchar(60)  not null default '',
    create_datetime timestamp    not null,
    modifier        varchar(60)  not null default '',
    modify_datetime timestamp    not null,
    summary         varchar(200) not null default '',
    state           varchar(36)  not null
);

comment on table matrix_column_value is '扩展列值';

comment on column matrix_column_value.value_id is 'PrimaryKey';

comment on column matrix_column_value.column_id is 'PrimaryKey';

comment on column matrix_column_value.tenant_key is '租户标识';

comment on column matrix_column_value.app_key is '应用标识';

comment on column matrix_column_value.env_key is '应用环境标识如Dev/Test/Uat/Prod等';

comment on column matrix_column_value.table_name is '表名';

comment on column matrix_column_value.column_name is '列名';

comment on column matrix_column_value.row_key is '表中数据行的标识';

comment on column matrix_column_value.data_type is '值类型';

comment on column matrix_column_value.column_value is '值';

/*==============================================================*/
/* Index: column_value_fk                                       */
/*==============================================================*/
create index if not exists column_value_fk on matrix_column_value (column_id);

/*==============================================================*/
/* Table: matrix_config                                         */
/*==============================================================*/
create table if not exists matrix_config
(
    config_id       int8         not null,
    namespace_id    int8         null,
    tenant_key      varchar(36)  not null,
    app_key         varchar(36)  not null,
    env_key         varchar(36)  not null,
    namespace_code  varchar(36)  not null,
    config_key      varchar(36)  not null,
    config_value    varchar(36)  not null,
    config_comment  varchar(36)  not null,
    constraint pk_matrix_config primary key (config_id),
    constraint ak_matrix_config_unique_key unique (tenant_key, app_key, env_key, namespace_code, config_key),
    version         int4         not null default 0,
    owner           varchar(60)  not null default '',
    creator         varchar(60)  not null default '',
    create_datetime timestamp    not null,
    modifier        varchar(60)  not null default '',
    modify_datetime timestamp    not null,
    summary         varchar(200) not null default '',
    state           varchar(36)  not null
);

comment on table matrix_config is '配置';

comment on column matrix_config.config_id is 'DataItem PrimaryKey';

comment on column matrix_config.namespace_id is 'DataItem PrimaryKey';

comment on column matrix_config.tenant_key is '租户标识';

comment on column matrix_config.app_key is '应用标识';

comment on column matrix_config.env_key is '应用环境标识如Dev/Test/Uat/Prod等';

comment on column matrix_config.namespace_code is '命名空间标识';

comment on column matrix_config.config_key is '配置Key/配置命名空间Key';

comment on column matrix_config.config_value is '配置值/配置命名空间名称';

comment on column matrix_config.config_comment is '配置注释/配置命名空间注释';

/*==============================================================*/
/* Index: namespace_config_fk                                   */
/*==============================================================*/
create index if not exists namespace_config_fk on matrix_config (namespace_id);

/*==============================================================*/
/* Table: matrix_config_namespace                               */
/*==============================================================*/
create table if not exists matrix_config_namespace
(
    namespace_id    int8         not null,
    tenant_key      varchar(36)  not null,
    app_key         varchar(36)  not null,
    env_key         varchar(36)  not null,
    namespace_code  varchar(36)  not null,
    namespace_name  varchar(36)  not null,
    namespace_scope varchar(36)  not null,
    namespace_type  varchar(36)  not null,
    constraint pk_matrix_config_namespace primary key (namespace_id),
    constraint ak_matrix_config_namespace_unique_key unique (tenant_key, app_key, env_key, namespace_code),
    version         int4         not null default 0,
    owner           varchar(60)  not null default '',
    creator         varchar(60)  not null default '',
    create_datetime timestamp    not null,
    modifier        varchar(60)  not null default '',
    modify_datetime timestamp    not null,
    summary         varchar(200) not null default '',
    state           varchar(36)  not null
);

comment on table matrix_config_namespace is '配置命名空间-相当于一个配置文件';

comment on column matrix_config_namespace.namespace_id is 'DataItem PrimaryKey';

comment on column matrix_config_namespace.tenant_key is '租户标识';

comment on column matrix_config_namespace.app_key is '应用标识';

comment on column matrix_config_namespace.env_key is '应用环境标识如Dev/Test/Uat/Prod等';

comment on column matrix_config_namespace.namespace_code is '标识';

comment on column matrix_config_namespace.namespace_name is '名称';

comment on column matrix_config_namespace.namespace_scope is '范围Public;Private';

comment on column matrix_config_namespace.namespace_type is '类型:Properties';

/*==============================================================*/
/* Table: matrix_tree                                           */
/*==============================================================*/
create table if not exists matrix_tree
(
    tree_id         int8         not null,
    parent_id       int8         null,
    tenant_key      varchar(36)  not null,
    app_key         varchar(36)  not null,
    env_key         varchar(36)  not null,
    tree_path       varchar(360) not null,
    tree_code       varchar(36)  not null,
    tree_name       varchar(36)  not null,
    tree_left       int4         not null,
    tree_right      int4         not null,
    tree_level      int4         not null,
    data_type       varchar(6)   not null,
    constraint pk_matrix_tree primary key (tree_id),
    constraint ak_matrix_tree_unique_key unique (tenant_key, app_key, env_key, tree_path),
    version         int4         not null default 0,
    owner           varchar(60)  not null default '',
    creator         varchar(60)  not null default '',
    create_datetime timestamp    not null,
    modifier        varchar(60)  not null default '',
    modify_datetime timestamp    not null,
    summary         varchar(200) not null default '',
    state           varchar(36)  not null
);

comment on table matrix_tree is '无限级分类树,多棵树parent_id=0的为树根;数据字典;商品类目';

comment on column matrix_tree.tree_id is 'PrimaryKey';

comment on column matrix_tree.parent_id is 'PrimaryKey';

comment on column matrix_tree.tenant_key is '租户标识';

comment on column matrix_tree.app_key is '应用标识';

comment on column matrix_tree.env_key is '应用环境标识如Dev/Test/Uat/Prod等';

comment on column matrix_tree.tree_path is '节点code路径:parent_code/code/...';

comment on column matrix_tree.tree_code is '节点标识';

comment on column matrix_tree.tree_name is '节点名称';

comment on column matrix_tree.tree_left is '节点左值';

comment on column matrix_tree.tree_right is '节点右值';

comment on column matrix_tree.tree_level is '节点层级';

comment on column matrix_tree.data_type is 'STRING|NUMERIC';

/*==============================================================*/
/* Index: infinite_self_fk                                      */
/*==============================================================*/
create index if not exists infinite_self_fk on matrix_tree (parent_id);

