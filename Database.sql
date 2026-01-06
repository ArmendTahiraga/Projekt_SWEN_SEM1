CREATE TABLE public.favorite (
    user_id integer NOT NULL,
    media_id integer NOT NULL,
    CONSTRAINT favorite_pkey PRIMARY KEY (user_id, media_id),
    CONSTRAINT favorite_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user(user_id),
    CONSTRAINT favorite_media_id_fkey FOREIGN KEY (media_id) REFERENCES public.media(media_id)
);

CREATE TABLE public.media (
    media_id integer NOT NULL DEFAULT nextval('media_media_id_seq'::regclass),
    creator_user_id integer NOT NULL,
    title text NOT NULL,
    description text,
    media_type text NOT NULL,
    release_year integer,
    genres text,
    age_restriction integer,
    CONSTRAINT media_pkey PRIMARY KEY (media_id),
    CONSTRAINT media_creator_user_id_fkey FOREIGN KEY (creator_user_id) REFERENCES public.user(user_id)
);

CREATE TABLE public.rating (
    rating_id integer NOT NULL DEFAULT nextval('rating_rating_id_seq'::regclass),
    media_id integer NOT NULL,
    user_id integer NOT NULL,
    stars integer NOT NULL CHECK (stars >= 1 AND stars <= 5),
    comment text,
    confirmed boolean NOT NULL DEFAULT false,
    likes integer NOT NULL DEFAULT 0,
    timestamp text,
    CONSTRAINT rating_pkey PRIMARY KEY (rating_id),
    CONSTRAINT rating_media_id_fkey FOREIGN KEY (media_id) REFERENCES public.media(media_id),
    CONSTRAINT rating_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user(user_id)
);

CREATE TABLE public.user (
    user_id integer NOT NULL DEFAULT nextval('app_user_user_id_seq'::regclass),
    username text NOT NULL UNIQUE,
    password_hash text NOT NULL,
    email text UNIQUE,
    favorite_genre text,
    CONSTRAINT user_pkey PRIMARY KEY (user_id)
);