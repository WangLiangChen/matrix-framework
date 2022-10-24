create table if not exists matrix_sequence
(
    sequence_key    varchar(36) not null comment '要生成序列的标识',
    sequence_number bigint      not null comment '当前序列',
    primary key (sequence_key)
) comment '序列生成器';