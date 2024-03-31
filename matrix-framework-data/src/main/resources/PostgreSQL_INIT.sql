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