--
-- PostgreSQL database dump
--

--
-- Dumped from database version 17.11
-- Dumped by pg_dump version 17.11

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: athletes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.athletes (
    id uuid NOT NULL,
    biography text,
    birth_date date,
    birth_place character varying(150),
    created_at timestamp(6) with time zone,
    height integer,
    instagram character varying(100),
    is_active boolean NOT NULL,
    name character varying(150) NOT NULL,
    nickname character varying(100),
    past_clubs jsonb,
    photo_url character varying(1000),
    preferred_foot character varying(20) NOT NULL,
    primary_position character varying(80) NOT NULL,
    secondary_positions jsonb,
    slug character varying(150) NOT NULL,
    updated_at timestamp(6) with time zone,
    weight integer,
    created_by_id character varying(36),
    CONSTRAINT athletes_preferred_foot_check CHECK (((preferred_foot)::text = ANY ((ARRAY['RIGHT'::character varying, 'LEFT'::character varying, 'BOTH'::character varying])::text[])))
);


--
-- Name: idols; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.idols (
    id character varying(36) NOT NULL,
    biography text NOT NULL,
    birth_date date,
    birth_place character varying(100),
    category character varying(20) NOT NULL,
    coach_stats jsonb,
    created_at timestamp(6) with time zone,
    created_by character varying(36),
    death_date date,
    display_order integer NOT NULL,
    end_year integer,
    fan_stats jsonb,
    gallery jsonb,
    gallery_items jsonb,
    is_active boolean NOT NULL,
    is_active_in_club boolean NOT NULL,
    is_featured boolean NOT NULL,
    last_edited_by character varying(36),
    name character varying(150) NOT NULL,
    nickname character varying(100),
    periods jsonb,
    photo_url character varying(500),
    player_stats jsonb,
    quote character varying(500),
    slug character varying(150) NOT NULL,
    staff_stats jsonb,
    start_year integer NOT NULL,
    updated_at timestamp(6) with time zone,
    CONSTRAINT idols_category_check CHECK (((category)::text = ANY ((ARRAY['PLAYER'::character varying, 'COACH'::character varying, 'STAFF'::character varying, 'FAN'::character varying])::text[])))
);


--
-- Name: news_articles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.news_articles (
    id uuid NOT NULL,
    allow_comments boolean NOT NULL,
    allow_reactions boolean NOT NULL,
    content jsonb,
    cover_caption character varying(150),
    cover_image character varying(1000) NOT NULL,
    created_at timestamp(6) with time zone,
    featured_slot character varying(20) NOT NULL,
    gallery jsonb,
    is_active boolean NOT NULL,
    is_featured boolean NOT NULL,
    is_pinned boolean NOT NULL,
    photographer_name character varying(120),
    previous_slugs jsonb,
    published_at timestamp(6) with time zone,
    reactions jsonb,
    reporter_name character varying(120),
    slug character varying(220) NOT NULL,
    status character varying(20) NOT NULL,
    subtitle character varying(300),
    summary character varying(500),
    tags jsonb,
    title character varying(200) NOT NULL,
    updated_at timestamp(6) with time zone,
    views_count bigint NOT NULL,
    youtube_video_url character varying(500),
    author_id character varying(36) NOT NULL,
    category_id uuid NOT NULL,
    last_edited_by_id character varying(36),
    CONSTRAINT news_articles_featured_slot_check CHECK (((featured_slot)::text = ANY ((ARRAY['MAIN'::character varying, 'SECONDARY'::character varying, 'NONE'::character varying])::text[]))),
    CONSTRAINT news_articles_status_check CHECK (((status)::text = ANY ((ARRAY['DRAFT'::character varying, 'PUBLISHED'::character varying, 'SCHEDULED'::character varying, 'ARCHIVED'::character varying])::text[])))
);


--
-- Name: news_categories; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.news_categories (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone,
    description character varying(255),
    display_order integer NOT NULL,
    is_active boolean NOT NULL,
    name character varying(80) NOT NULL,
    slug character varying(100) NOT NULL,
    updated_at timestamp(6) with time zone
);


--
-- Name: news_comments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.news_comments (
    id uuid NOT NULL,
    content character varying(1000) NOT NULL,
    created_at timestamp(6) with time zone,
    is_active boolean NOT NULL,
    status character varying(20) NOT NULL,
    updated_at timestamp(6) with time zone,
    article_id uuid NOT NULL,
    author_id character varying(36) NOT NULL,
    CONSTRAINT news_comments_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'HIDDEN'::character varying])::text[])))
);


--
-- Name: news_reactions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.news_reactions (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone,
    reaction_type character varying(20) NOT NULL,
    updated_at timestamp(6) with time zone,
    article_id uuid NOT NULL,
    user_id character varying(36) NOT NULL,
    CONSTRAINT news_reactions_reaction_type_check CHECK (((reaction_type)::text = ANY ((ARRAY['WOLF'::character varying, 'FIRE'::character varying, 'CLAP'::character varying, 'HEART'::character varying])::text[])))
);


--
-- Name: role_permissions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.role_permissions (
    role_id character varying(36) NOT NULL,
    permission character varying(255)
);


--
-- Name: roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.roles (
    id character varying(36) NOT NULL,
    created_at timestamp(6) with time zone,
    description character varying(255),
    is_immutable boolean NOT NULL,
    label character varying(100),
    name character varying(50) NOT NULL,
    updated_at timestamp(6) with time zone
);


--
-- Name: settings; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.settings (
    id bigint NOT NULL,
    club_logo_gallery jsonb,
    club_logo_url character varying(255),
    club_name character varying(255) NOT NULL,
    contact_emails jsonb,
    contact_phones jsonb,
    copyright_text character varying(255),
    created_at timestamp(6) with time zone NOT NULL,
    footer_navigation_links jsonb,
    google_maps_embed_url character varying(1000),
    opening_hours jsonb,
    social_links jsonb,
    stadium_address character varying(500),
    stadium_name character varying(255),
    updated_at timestamp(6) with time zone
);


--
-- Name: settings_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.settings ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.settings_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: squad_members; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.squad_members (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone,
    display_order integer NOT NULL,
    jersey_number integer NOT NULL,
    role character varying(30) NOT NULL,
    sector character varying(30) NOT NULL,
    updated_at timestamp(6) with time zone,
    athlete_id uuid NOT NULL,
    squad_id uuid NOT NULL,
    CONSTRAINT squad_members_role_check CHECK (((role)::text = ANY ((ARRAY['STARTER'::character varying, 'SUBSTITUTE'::character varying, 'CAPTAIN'::character varying, 'INJURED'::character varying])::text[]))),
    CONSTRAINT squad_members_sector_check CHECK (((sector)::text = ANY ((ARRAY['GOALKEEPER'::character varying, 'DEFENDER'::character varying, 'MIDFIELDER'::character varying, 'FORWARD'::character varying])::text[])))
);


--
-- Name: squads; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.squads (
    id uuid NOT NULL,
    category character varying(30) NOT NULL,
    coach_name character varying(150),
    cover_url character varying(1000),
    created_at timestamp(6) with time zone,
    is_active boolean NOT NULL,
    is_current boolean NOT NULL,
    season_name character varying(150) NOT NULL,
    staff jsonb,
    updated_at timestamp(6) with time zone,
    year integer NOT NULL,
    created_by_id character varying(36),
    CONSTRAINT squads_category_check CHECK (((category)::text = ANY ((ARRAY['PROFESSIONAL'::character varying, 'FEMININE'::character varying, 'SUB_20'::character varying, 'SUB_17'::character varying, 'SUB_15'::character varying])::text[])))
);


--
-- Name: system_modules; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.system_modules (
    id character varying(36) NOT NULL,
    created_at timestamp(6) with time zone,
    description character varying(255),
    is_active boolean NOT NULL,
    name character varying(100) NOT NULL,
    slug character varying(50) NOT NULL,
    updated_at timestamp(6) with time zone
);


--
-- Name: themes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.themes (
    id bigint NOT NULL,
    campaign_color character varying(255),
    created_at timestamp(6) with time zone NOT NULL,
    dark_palette jsonb,
    end_date timestamp(6) with time zone,
    is_active boolean NOT NULL,
    is_campaign boolean NOT NULL,
    is_default boolean NOT NULL,
    light_palette jsonb,
    name character varying(255) NOT NULL,
    start_date timestamp(6) with time zone,
    updated_at timestamp(6) with time zone
);


--
-- Name: themes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.themes ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.themes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id character varying(36) NOT NULL,
    addresses jsonb,
    avatar_url character varying(500),
    birth_date date,
    cpf character varying(20) NOT NULL,
    created_at timestamp(6) with time zone,
    email character varying(150) NOT NULL,
    email_verified boolean NOT NULL,
    failed_login_attempts integer NOT NULL,
    first_name character varying(100) NOT NULL,
    is_active boolean NOT NULL,
    last_name character varying(100) NOT NULL,
    lockout_until timestamp(6) with time zone,
    must_change_password boolean NOT NULL,
    password_hash character varying(255) NOT NULL,
    pending_changes jsonb,
    phone character varying(20),
    phones jsonb,
    preferences jsonb,
    reset_password_expires timestamp(6) with time zone,
    reset_password_token character varying(100),
    status character varying(20) NOT NULL,
    token_version integer NOT NULL,
    updated_at timestamp(6) with time zone,
    verification_code character varying(10),
    verification_code_expires timestamp(6) with time zone,
    role_id character varying(36) NOT NULL,
    CONSTRAINT users_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'PENDING'::character varying, 'INACTIVE'::character varying, 'BLOCKED'::character varying])::text[])))
);


--
-- Name: athletes athletes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.athletes
    ADD CONSTRAINT athletes_pkey PRIMARY KEY (id);


--
-- Name: idols idols_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.idols
    ADD CONSTRAINT idols_pkey PRIMARY KEY (id);


--
-- Name: athletes idx_athletes_slug; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.athletes
    ADD CONSTRAINT idx_athletes_slug UNIQUE (slug);


--
-- Name: news_articles idx_news_articles_slug; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.news_articles
    ADD CONSTRAINT idx_news_articles_slug UNIQUE (slug);


--
-- Name: news_articles news_articles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.news_articles
    ADD CONSTRAINT news_articles_pkey PRIMARY KEY (id);


--
-- Name: news_categories news_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.news_categories
    ADD CONSTRAINT news_categories_pkey PRIMARY KEY (id);


--
-- Name: news_comments news_comments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.news_comments
    ADD CONSTRAINT news_comments_pkey PRIMARY KEY (id);


--
-- Name: news_reactions news_reactions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.news_reactions
    ADD CONSTRAINT news_reactions_pkey PRIMARY KEY (id);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- Name: settings settings_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.settings
    ADD CONSTRAINT settings_pkey PRIMARY KEY (id);


--
-- Name: squad_members squad_members_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.squad_members
    ADD CONSTRAINT squad_members_pkey PRIMARY KEY (id);


--
-- Name: squads squads_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.squads
    ADD CONSTRAINT squads_pkey PRIMARY KEY (id);


--
-- Name: system_modules system_modules_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.system_modules
    ADD CONSTRAINT system_modules_pkey PRIMARY KEY (id);


--
-- Name: themes themes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.themes
    ADD CONSTRAINT themes_pkey PRIMARY KEY (id);


--
-- Name: themes uk3estny12ybh85k7y8j6gyyrep; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.themes
    ADD CONSTRAINT uk3estny12ybh85k7y8j6gyyrep UNIQUE (name);


--
-- Name: users uk6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- Name: users uk7kqluf7wl0oxs7n90fpya03ss; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk7kqluf7wl0oxs7n90fpya03ss UNIQUE (cpf);


--
-- Name: news_categories uk7ve269hm7tylvvdn9alu5fvqr; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.news_categories
    ADD CONSTRAINT uk7ve269hm7tylvvdn9alu5fvqr UNIQUE (slug);


--
-- Name: news_reactions uk_news_reactions_article_user; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.news_reactions
    ADD CONSTRAINT uk_news_reactions_article_user UNIQUE (article_id, user_id);


--
-- Name: idols ukgpwkdgh1e1b1sacexmnpmaia6; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.idols
    ADD CONSTRAINT ukgpwkdgh1e1b1sacexmnpmaia6 UNIQUE (slug);


--
-- Name: system_modules uknml1lhqmcthxu8cf8qk14nyun; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.system_modules
    ADD CONSTRAINT uknml1lhqmcthxu8cf8qk14nyun UNIQUE (name);


--
-- Name: system_modules uko7uyinylqv85gvv958p8oodkk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.system_modules
    ADD CONSTRAINT uko7uyinylqv85gvv958p8oodkk UNIQUE (slug);


--
-- Name: roles ukofx66keruapi6vyqpv6f2or37; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT ukofx66keruapi6vyqpv6f2or37 UNIQUE (name);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: idx_athletes_active_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_athletes_active_name ON public.athletes USING btree (is_active, name);


--
-- Name: idx_athletes_position; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_athletes_position ON public.athletes USING btree (primary_position);


--
-- Name: idx_news_articles_featured; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_news_articles_featured ON public.news_articles USING btree (is_active, status, featured_slot, published_at);


--
-- Name: idx_news_articles_feed; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_news_articles_feed ON public.news_articles USING btree (is_active, status, is_pinned, published_at);


--
-- Name: idx_news_comments_article_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_news_comments_article_status ON public.news_comments USING btree (article_id, is_active, status, created_at);


--
-- Name: idx_squad_members_squad_order; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_squad_members_squad_order ON public.squad_members USING btree (squad_id, display_order);


--
-- Name: idx_squads_current_cat; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_squads_current_cat ON public.squads USING btree (is_current, category);


--
-- Name: idx_squads_year_cat_active; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_squads_year_cat_active ON public.squads USING btree (year, category, is_active);


--
-- Name: news_reactions fk3lkjo4osn8wxsb1rto4u7cfx; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.news_reactions
    ADD CONSTRAINT fk3lkjo4osn8wxsb1rto4u7cfx FOREIGN KEY (article_id) REFERENCES public.news_articles(id);


--
-- Name: news_comments fka28nxjhheld1f2b7nrg50tx45; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.news_comments
    ADD CONSTRAINT fka28nxjhheld1f2b7nrg50tx45 FOREIGN KEY (article_id) REFERENCES public.news_articles(id);


--
-- Name: squads fkark0ps6s4f4gtnug4gi8cyxe5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.squads
    ADD CONSTRAINT fkark0ps6s4f4gtnug4gi8cyxe5 FOREIGN KEY (created_by_id) REFERENCES public.users(id);


--
-- Name: news_articles fkdem41d1qu9pv5q8av95fgobk0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.news_articles
    ADD CONSTRAINT fkdem41d1qu9pv5q8av95fgobk0 FOREIGN KEY (last_edited_by_id) REFERENCES public.users(id);


--
-- Name: news_articles fkefxnrohhfwdfhesbyp63f54rh; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.news_articles
    ADD CONSTRAINT fkefxnrohhfwdfhesbyp63f54rh FOREIGN KEY (category_id) REFERENCES public.news_categories(id);


--
-- Name: squad_members fkg7nlpnc8epv82gh7sra8eykbv; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.squad_members
    ADD CONSTRAINT fkg7nlpnc8epv82gh7sra8eykbv FOREIGN KEY (athlete_id) REFERENCES public.athletes(id);


--
-- Name: squad_members fkgj2i599t0hr7iuiebvc95xcws; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.squad_members
    ADD CONSTRAINT fkgj2i599t0hr7iuiebvc95xcws FOREIGN KEY (squad_id) REFERENCES public.squads(id);


--
-- Name: athletes fkkasos296qhyptccii9xfjrmuh; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.athletes
    ADD CONSTRAINT fkkasos296qhyptccii9xfjrmuh FOREIGN KEY (created_by_id) REFERENCES public.users(id);


--
-- Name: role_permissions fkn5fotdgk8d1xvo8nav9uv3muc; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.role_permissions
    ADD CONSTRAINT fkn5fotdgk8d1xvo8nav9uv3muc FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- Name: news_articles fknjd1vm54yix5sgiqkl19x7wxe; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.news_articles
    ADD CONSTRAINT fknjd1vm54yix5sgiqkl19x7wxe FOREIGN KEY (author_id) REFERENCES public.users(id);


--
-- Name: news_reactions fko090stcot08mpiy4g7twpv9bv; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.news_reactions
    ADD CONSTRAINT fko090stcot08mpiy4g7twpv9bv FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: users fkp56c1712k691lhsyewcssf40f; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fkp56c1712k691lhsyewcssf40f FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- Name: news_comments fkqt6tyhv6weekwcr8ej4xyuj0l; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.news_comments
    ADD CONSTRAINT fkqt6tyhv6weekwcr8ej4xyuj0l FOREIGN KEY (author_id) REFERENCES public.users(id);


--
-- PostgreSQL database dump complete
--

