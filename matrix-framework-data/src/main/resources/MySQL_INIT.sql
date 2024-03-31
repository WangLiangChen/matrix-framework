create table if not exists matrix_sequence
(
    sequence_group  varchar(36) not null comment '组标识',
    sequence_key    varchar(36) not null comment '标识',
    sequence_number bigint      not null comment '当前序号',
    primary key(sequence_group,sequence_key)
) comment '序列生成器';