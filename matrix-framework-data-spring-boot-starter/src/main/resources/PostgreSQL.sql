create table if not exists matrix_sequence (
    sequence_key varchar not null,
    sequence_number int8 not null,
    constraint matrix_sequence_pk primary key (sequence_key)
);
comment on table matrix_sequence is '序列生成器';

-- column comments

comment on column matrix_sequence.sequence_key is '要生成序列的标识';
comment on column matrix_sequence.sequence_number is '当前序列';
