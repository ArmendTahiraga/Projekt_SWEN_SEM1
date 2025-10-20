create table if not exists user (
    user_id           serial primary key,
    username          text not null unique,
    password_hash     text not null,
    email             text unique,
    favorite_genre_id integer
);


create table if not exists media (
    media_id        serial primary key,
    creator_user_id integer not null references user(user_id) on delete cascade,
    title           text not null,
    description     text,
    media_type      media_type not null,
    release_year    integer,
    age_restriction integer,
    average_score   numeric(3,2) not null default 0.00
);


create table if not exists rating (
    rating_id   serial primary key,
    media_id    integer not null references media(media_id) on delete cascade,
    user_id     integer not null references user(user_id) on delete cascade,
    stars       integer not null check (stars between 1 and 5),
    comment     text,
    confirmed   boolean not null default false,
    likes       integer not null default 0,
    unique (media_id, user_id)
);

create table if not exists rating_like (
    rating_id integer not null references rating(rating_id) on delete cascade,
    user_id   integer not null references user(user_id) on delete cascade,
    primary key (rating_id, user_id)
);

create table if not exists favorite (
    user_id  integer not null references user(user_id) on delete cascade,
    media_id integer not null references media(media_id) on delete cascade,
    primary key (user_id, media_id)
    );

create table if not exists user_recommendation_cache (
    rec_id      uuid primary key default uuid_generate_v4(),
    user_id     integer not null references user(user_id) on delete cascade,
    rec_type    text not null check (rec_type in ('genre','content')),
    payload     jsonb not null,
    generated_at timestamptz not null default now()
);