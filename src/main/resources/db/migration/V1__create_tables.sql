create table jobs(
    id uuid primary key,
    type varchar(32) not null, -- тип операции (PDF_MERGE, PDF_SPLIT, etc)
    status varchar(16) not null, -- PENDING, RUNNING, DONE, FAILED
    progress int not null default 0, -- процент 0..100

    options_json text null, -- настройки операции, хранит

    error_code varchar(64) null, -- короткий код ошибки, varchar(n) - хранение строк конкретной длины
    error_message text null,

    created_at timestamptz not null default now(),
    started_at timestamptz null,
    finished_at timestamptz null
);

create index idx_jobs_status_created_at on jobs(status, created_at);


create table job_files (
    id uuid primary key,
    -- job_id типа uuid ссылаются на таблицу jobs столбец id;
    -- on delete cascade - при удалении job, удаляются все job_files;
    -- связь one to many, один в jobs, много в files, и эти много могут ссылаться на один один
    job_id uuid not null references jobs(id) on delete cascade,

    role varchar(16) not null, -- INPUT / OUTPUT
    original_name text not null, -- имя файла, которое загрузил пользователь
    content_type varchar(128) null, -- тип файла
    size_bytes bigint not null, -- размер файла
    storage_key text not null, -- путь/ключ в storage, где физически хранится файл

    created_at timestamptz not null default now()
);

-- Мы записываем в индекс job_id (наши файлы для работы) и ссылки на них
-- Прилетает задача на конкретный файл, Postgres переходит по ссылке
-- Взял все нужные файлы, как только индекс поменялся, перестал работать (т.к. индекс - отсортированный справочник)
create index idx_job_files_job_id
    on job_files(job_id);