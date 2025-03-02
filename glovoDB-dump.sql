--
-- PostgreSQL database dump
--

-- Dumped from database version 15.11
-- Dumped by pg_dump version 15.11

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

ALTER TABLE ONLY public.addresses DROP CONSTRAINT fksv7a6xjwuwlcwxbq98p0gqna;
ALTER TABLE ONLY public.order_items DROP CONSTRAINT fkocimc7dtr037rh4ls4l95nlfi;
ALTER TABLE ONLY public.establishment_adresses DROP CONSTRAINT fkmg0q2k4rsk5x6sn4gvn20r96v;
ALTER TABLE ONLY public.products DROP CONSTRAINT fkk1y014eougo40urpwufyys8fp;
ALTER TABLE ONLY public.order_details DROP CONSTRAINT fkjyu2qbqt8gnvno9oe9j2s2ldk;
ALTER TABLE ONLY public.subcategories DROP CONSTRAINT fkiborb6ptvy1t1n3v6klb56l5s;
ALTER TABLE ONLY public.user_roles DROP CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f;
ALTER TABLE ONLY public.user_roles DROP CONSTRAINT fkh8ciramu9cc9q3qcqiv4ue8a6;
ALTER TABLE ONLY public.establishments DROP CONSTRAINT fkgfsgl8rfxapyxy7d4ti2mxh9b;
ALTER TABLE ONLY public.cart DROP CONSTRAINT fkg5uhi8vpsuy0lgloxk2h4w5o6;
ALTER TABLE ONLY public.cart_items DROP CONSTRAINT fke49uqm63kajrg9gt365roi2gm;
ALTER TABLE ONLY public.discounts DROP CONSTRAINT fke3tqxsyxv7qcy8uvf2lns1hx8;
ALTER TABLE ONLY public.image_associations DROP CONSTRAINT fkdadh9qfc9xfsfdmxuucyp51cj;
ALTER TABLE ONLY public.establishment_filters DROP CONSTRAINT fkcrmgdqsn1ojebiuj6yhvloddq;
ALTER TABLE ONLY public.products DROP CONSTRAINT fk99gib6u0rm71m4vr6qdfj33is;
ALTER TABLE ONLY public.cart_items DROP CONSTRAINT fk99e0am9jpriwxcm6is7xfedy3;
ALTER TABLE ONLY public.payment_details DROP CONSTRAINT fk96rviu3pjlbx2g8g2dx6rpupx;
ALTER TABLE ONLY public.social_accounts DROP CONSTRAINT fk6rmxxiton5yuvu7ph2hcq2xn7;
ALTER TABLE ONLY public.orders DROP CONSTRAINT fk32ql8ubntj5uh44ph9659tiih;
ALTER TABLE ONLY public.cart_items DROP CONSTRAINT fk1re40cjegsfvw58xrkdp6bac6;
ALTER TABLE ONLY public.order_items DROP CONSTRAINT fk1q80b422hoxsjgc259r091peg;
ALTER TABLE ONLY public.refresh_tokens DROP CONSTRAINT fk1lih5y2npsf8u5o3vhdb9y0os;
ALTER TABLE ONLY public.users DROP CONSTRAINT users_username_key;
ALTER TABLE ONLY public.users DROP CONSTRAINT users_pkey;
ALTER TABLE ONLY public.users DROP CONSTRAINT users_phone_number_key;
ALTER TABLE ONLY public.users DROP CONSTRAINT users_email_username_phone_number_key;
ALTER TABLE ONLY public.users DROP CONSTRAINT users_email_key;
ALTER TABLE ONLY public.user_roles DROP CONSTRAINT user_roles_pkey;
ALTER TABLE ONLY public.subcategories DROP CONSTRAINT subcategories_pkey;
ALTER TABLE ONLY public.social_accounts DROP CONSTRAINT social_accounts_pkey;
ALTER TABLE ONLY public.roles DROP CONSTRAINT roles_pkey;
ALTER TABLE ONLY public.roles DROP CONSTRAINT roles_name_key;
ALTER TABLE ONLY public.refresh_tokens DROP CONSTRAINT refresh_tokens_user_id_key;
ALTER TABLE ONLY public.refresh_tokens DROP CONSTRAINT refresh_tokens_pkey;
ALTER TABLE ONLY public.products DROP CONSTRAINT products_pkey;
ALTER TABLE ONLY public.payment_details DROP CONSTRAINT payment_details_pkey;
ALTER TABLE ONLY public.payment_details DROP CONSTRAINT payment_details_order_detail_id_key;
ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_pkey;
ALTER TABLE ONLY public.order_items DROP CONSTRAINT order_items_pkey;
ALTER TABLE ONLY public.order_details DROP CONSTRAINT order_details_pkey;
ALTER TABLE ONLY public.order_details DROP CONSTRAINT order_details_order_id_key;
ALTER TABLE ONLY public.images DROP CONSTRAINT images_pkey;
ALTER TABLE ONLY public.image_associations DROP CONSTRAINT image_associations_pkey;
ALTER TABLE ONLY public.image_associations DROP CONSTRAINT image_associations_image_id_key;
ALTER TABLE ONLY public.establishments DROP CONSTRAINT establishments_pkey;
ALTER TABLE ONLY public.establishment_filters DROP CONSTRAINT establishment_filters_pkey;
ALTER TABLE ONLY public.establishment_adresses DROP CONSTRAINT establishment_adresses_pkey;
ALTER TABLE ONLY public.establishment_adresses DROP CONSTRAINT establishment_adresses_establishment_id_key;
ALTER TABLE ONLY public.discounts DROP CONSTRAINT discounts_product_id_key;
ALTER TABLE ONLY public.discounts DROP CONSTRAINT discounts_pkey;
ALTER TABLE ONLY public.categories DROP CONSTRAINT categories_pkey;
ALTER TABLE ONLY public.cart DROP CONSTRAINT cart_user_id_key;
ALTER TABLE ONLY public.cart DROP CONSTRAINT cart_pkey;
ALTER TABLE ONLY public.cart_items DROP CONSTRAINT cart_items_pkey;
ALTER TABLE ONLY public.addresses DROP CONSTRAINT addresses_pkey;
ALTER TABLE ONLY public.addresses DROP CONSTRAINT addresses_order_id_key;
DROP TABLE public.users;
DROP TABLE public.user_roles;
DROP TABLE public.subcategories;
DROP TABLE public.social_accounts;
DROP TABLE public.roles;
DROP TABLE public.refresh_tokens;
DROP TABLE public.products;
DROP TABLE public.payment_details;
DROP TABLE public.orders;
DROP TABLE public.order_items;
DROP TABLE public.order_details;
DROP TABLE public.images;
DROP TABLE public.image_associations;
DROP TABLE public.establishments;
DROP TABLE public.establishment_filters;
DROP TABLE public.establishment_adresses;
DROP TABLE public.discounts;
DROP TABLE public.categories;
DROP TABLE public.cart_items;
DROP TABLE public.cart;
DROP TABLE public.addresses;
SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: addresses; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.addresses (
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    created_time timestamp(6) without time zone,
    id bigint NOT NULL,
    order_id bigint NOT NULL,
    updated_time timestamp(6) without time zone,
    address_line character varying(500) NOT NULL,
    CONSTRAINT addresses_latitude_check CHECK (((latitude <= (90)::double precision) AND (latitude >= ('-90'::integer)::double precision))),
    CONSTRAINT addresses_longitude_check CHECK (((longitude >= ('-180'::integer)::double precision) AND (longitude <= (180)::double precision)))
);


ALTER TABLE public.addresses OWNER TO "glovoTeam";

--
-- Name: addresses_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.addresses ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.addresses_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: cart; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.cart (
    total_charge numeric(38,2) NOT NULL,
    created_time timestamp(6) without time zone,
    id bigint NOT NULL,
    updated_time timestamp(6) without time zone,
    user_id bigint NOT NULL
);


ALTER TABLE public.cart OWNER TO "glovoTeam";

--
-- Name: cart_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.cart ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.cart_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: cart_items; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.cart_items (
    one_product_price_cart numeric(38,2) NOT NULL,
    quantity integer NOT NULL,
    total_price_cart numeric(38,2) NOT NULL,
    cart_id bigint NOT NULL,
    created_time timestamp(6) without time zone,
    id bigint NOT NULL,
    order_detail_id bigint NOT NULL,
    product_id bigint NOT NULL,
    updated_time timestamp(6) without time zone,
    CONSTRAINT cart_items_quantity_check CHECK ((quantity >= 0))
);


ALTER TABLE public.cart_items OWNER TO "glovoTeam";

--
-- Name: cart_items_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.cart_items ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.cart_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: categories; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.categories (
    created_time timestamp(6) without time zone,
    id bigint NOT NULL,
    updated_time timestamp(6) without time zone,
    name character varying(355) NOT NULL
);


ALTER TABLE public.categories OWNER TO "glovoTeam";

--
-- Name: categories_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.categories ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.categories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: discounts; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.discounts (
    active boolean NOT NULL,
    discount integer NOT NULL,
    created_time timestamp(6) without time zone,
    id bigint NOT NULL,
    product_id bigint NOT NULL,
    updated_time timestamp(6) without time zone
);


ALTER TABLE public.discounts OWNER TO "glovoTeam";

--
-- Name: discounts_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.discounts ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.discounts_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: establishment_adresses; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.establishment_adresses (
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    created_time timestamp(6) without time zone,
    establishment_id bigint NOT NULL,
    id bigint NOT NULL,
    updated_time timestamp(6) without time zone,
    address_line character varying(700) NOT NULL,
    CONSTRAINT establishment_adresses_latitude_check CHECK (((latitude <= (90)::double precision) AND (latitude >= ('-90'::integer)::double precision))),
    CONSTRAINT establishment_adresses_longitude_check CHECK (((longitude >= ('-180'::integer)::double precision) AND (longitude <= (180)::double precision)))
);


ALTER TABLE public.establishment_adresses OWNER TO "glovoTeam";

--
-- Name: establishment_adresses_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.establishment_adresses ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.establishment_adresses_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: establishment_filters; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.establishment_filters (
    created_time timestamp(6) without time zone,
    establishment_id bigint NOT NULL,
    id bigint NOT NULL,
    updated_time timestamp(6) without time zone,
    name character varying(355) NOT NULL
);


ALTER TABLE public.establishment_filters OWNER TO "glovoTeam";

--
-- Name: establishment_filters_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.establishment_filters ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.establishment_filters_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: establishments; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.establishments (
    close_time time(6) without time zone NOT NULL,
    open_time time(6) without time zone NOT NULL,
    price_of_delivery double precision NOT NULL,
    quantity_of_ratings integer NOT NULL,
    time_of_delivery integer NOT NULL,
    total_rating double precision NOT NULL,
    created_time timestamp(6) without time zone,
    id bigint NOT NULL,
    sub_category_id bigint,
    subcategory_id bigint NOT NULL,
    updated_time timestamp(6) without time zone,
    name character varying(455) NOT NULL,
    CONSTRAINT establishments_price_of_delivery_check CHECK ((price_of_delivery >= (50)::double precision)),
    CONSTRAINT establishments_quantity_of_ratings_check CHECK (((quantity_of_ratings >= 0) AND (quantity_of_ratings <= 500))),
    CONSTRAINT establishments_time_of_delivery_check CHECK (((time_of_delivery <= 60) AND (time_of_delivery >= 10)))
);


ALTER TABLE public.establishments OWNER TO "glovoTeam";

--
-- Name: establishments_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.establishments ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.establishments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: image_associations; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.image_associations (
    id bigint NOT NULL,
    image_id bigint,
    owner_id bigint,
    entity_type character varying(255),
    CONSTRAINT image_associations_entity_type_check CHECK (((entity_type)::text = ANY ((ARRAY['Category'::character varying, 'SubCategory'::character varying, 'Establishment'::character varying, 'Product'::character varying])::text[])))
);


ALTER TABLE public.image_associations OWNER TO "glovoTeam";

--
-- Name: image_associations_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.image_associations ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.image_associations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: images; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.images (
    created_time timestamp(6) without time zone,
    id bigint NOT NULL,
    size bigint NOT NULL,
    updated_time timestamp(6) without time zone,
    filename character varying(200) NOT NULL,
    url character varying(800) NOT NULL,
    bucket character varying(255) NOT NULL,
    content_type character varying(255) NOT NULL,
    original_filename character varying(255) NOT NULL
);


ALTER TABLE public.images OWNER TO "glovoTeam";

--
-- Name: images_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.images ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.images_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: order_details; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.order_details (
    cost_of_delivery integer NOT NULL,
    distance double precision NOT NULL,
    time_of_delivery integer NOT NULL,
    created_time timestamp(6) without time zone,
    id bigint NOT NULL,
    order_id bigint NOT NULL,
    updated_time timestamp(6) without time zone
);


ALTER TABLE public.order_details OWNER TO "glovoTeam";

--
-- Name: order_details_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.order_details ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.order_details_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: order_items; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.order_items (
    one_product_price_order numeric(38,2) NOT NULL,
    quantity integer NOT NULL,
    created_time timestamp(6) without time zone,
    id bigint NOT NULL,
    order_detail_id bigint NOT NULL,
    product_id bigint NOT NULL,
    updated_time timestamp(6) without time zone,
    CONSTRAINT order_items_quantity_check CHECK ((quantity >= 0))
);


ALTER TABLE public.order_items OWNER TO "glovoTeam";

--
-- Name: order_items_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.order_items ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.order_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: orders; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.orders (
    total_amount numeric(38,2) NOT NULL,
    created_time timestamp(6) without time zone,
    id bigint NOT NULL,
    updated_time timestamp(6) without time zone,
    user_id bigint NOT NULL,
    status character varying(255) NOT NULL,
    CONSTRAINT orders_status_check CHECK (((status)::text = ANY ((ARRAY['ORDER_STATUS_CREATED'::character varying, 'ORDER_STATUS_IN_PROGRESS'::character varying, 'ORDER_STATUS_READY_TO_DELIVER'::character varying, 'ORDER_STATUS_DELIVERING'::character varying, 'ORDER_STATUS_DELIVERED'::character varying])::text[])))
);


ALTER TABLE public.orders OWNER TO "glovoTeam";

--
-- Name: orders_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.orders ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.orders_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: payment_details; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.payment_details (
    is_paid boolean NOT NULL,
    total_amount numeric(38,2) NOT NULL,
    created_time timestamp(6) without time zone,
    id bigint NOT NULL,
    order_detail_id bigint NOT NULL,
    updated_time timestamp(6) without time zone,
    currency character varying(255) NOT NULL,
    provider character varying(255) NOT NULL,
    session_id character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    transaction_id character varying(255) NOT NULL
);


ALTER TABLE public.payment_details OWNER TO "glovoTeam";

--
-- Name: payment_details_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.payment_details ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.payment_details_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: products; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.products (
    active boolean NOT NULL,
    price numeric(38,2) NOT NULL,
    created_time timestamp(6) without time zone,
    establishment_filter_id bigint,
    establishment_id bigint,
    id bigint NOT NULL,
    updated_time timestamp(6) without time zone,
    name character varying(266) NOT NULL,
    description character varying(1000) NOT NULL
);


ALTER TABLE public.products OWNER TO "glovoTeam";

--
-- Name: products_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.products ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.products_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: refresh_tokens; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.refresh_tokens (
    created_time timestamp(6) without time zone,
    expiry_date timestamp(6) with time zone NOT NULL,
    id bigint NOT NULL,
    updated_time timestamp(6) without time zone,
    user_id bigint NOT NULL,
    token text NOT NULL
);


ALTER TABLE public.refresh_tokens OWNER TO "glovoTeam";

--
-- Name: refresh_tokens_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.refresh_tokens ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.refresh_tokens_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: roles; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.roles (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    CONSTRAINT roles_name_check CHECK (((name)::text = ANY ((ARRAY['ROLE_CUSTOMER'::character varying, 'ROLE_ADMIN'::character varying, 'ROLE_ESTABLISHMENT'::character varying, 'ROLE_COURIER'::character varying])::text[])))
);


ALTER TABLE public.roles OWNER TO "glovoTeam";

--
-- Name: roles_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.roles ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: social_accounts; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.social_accounts (
    created_time timestamp(6) without time zone,
    id bigint NOT NULL,
    updated_time timestamp(6) without time zone,
    user_id bigint,
    provider_id character varying(255) NOT NULL,
    providers character varying(255) NOT NULL,
    CONSTRAINT social_accounts_providers_check CHECK (((providers)::text = ANY ((ARRAY['AUTH_PROVIDERS_GOOGLE'::character varying, 'AUTH_PROVIDERS_FACEBOOK'::character varying, 'AUTH_PROVIDERS_GITHUB'::character varying])::text[])))
);


ALTER TABLE public.social_accounts OWNER TO "glovoTeam";

--
-- Name: social_accounts_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.social_accounts ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.social_accounts_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: subcategories; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.subcategories (
    category_id bigint NOT NULL,
    created_time timestamp(6) without time zone,
    id bigint NOT NULL,
    updated_time timestamp(6) without time zone,
    name character varying(355) NOT NULL
);


ALTER TABLE public.subcategories OWNER TO "glovoTeam";

--
-- Name: subcategories_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.subcategories ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.subcategories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: user_roles; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.user_roles (
    role_id bigint NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE public.user_roles OWNER TO "glovoTeam";

--
-- Name: users; Type: TABLE; Schema: public; Owner: glovoTeam
--

CREATE TABLE public.users (
    created_time timestamp(6) without time zone,
    id bigint NOT NULL,
    last_login_date timestamp(6) without time zone,
    updated_time timestamp(6) without time zone,
    phone_number character varying(20),
    username character varying(50) NOT NULL,
    email character varying(122),
    chat_id character varying(255),
    login character varying(255),
    password character varying(255),
    status character varying(255) NOT NULL,
    CONSTRAINT users_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'PENDING_EMAIL_VERIFICATION'::character varying, 'BLOCKED_BY_ADMIN'::character varying, 'PENDING_TELEGRAM_BINDING'::character varying, 'PENDING_LOGIN_TO_THE_SYSTEM'::character varying])::text[])))
);


ALTER TABLE public.users OWNER TO "glovoTeam";

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: glovoTeam
--

ALTER TABLE public.users ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: addresses addresses_order_id_key; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.addresses
    ADD CONSTRAINT addresses_order_id_key UNIQUE (order_id);


--
-- Name: addresses addresses_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.addresses
    ADD CONSTRAINT addresses_pkey PRIMARY KEY (id);


--
-- Name: cart_items cart_items_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.cart_items
    ADD CONSTRAINT cart_items_pkey PRIMARY KEY (id);


--
-- Name: cart cart_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.cart
    ADD CONSTRAINT cart_pkey PRIMARY KEY (id);


--
-- Name: cart cart_user_id_key; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.cart
    ADD CONSTRAINT cart_user_id_key UNIQUE (user_id);


--
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);


--
-- Name: discounts discounts_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.discounts
    ADD CONSTRAINT discounts_pkey PRIMARY KEY (id);


--
-- Name: discounts discounts_product_id_key; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.discounts
    ADD CONSTRAINT discounts_product_id_key UNIQUE (product_id);


--
-- Name: establishment_adresses establishment_adresses_establishment_id_key; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.establishment_adresses
    ADD CONSTRAINT establishment_adresses_establishment_id_key UNIQUE (establishment_id);


--
-- Name: establishment_adresses establishment_adresses_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.establishment_adresses
    ADD CONSTRAINT establishment_adresses_pkey PRIMARY KEY (id);


--
-- Name: establishment_filters establishment_filters_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.establishment_filters
    ADD CONSTRAINT establishment_filters_pkey PRIMARY KEY (id);


--
-- Name: establishments establishments_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.establishments
    ADD CONSTRAINT establishments_pkey PRIMARY KEY (id);


--
-- Name: image_associations image_associations_image_id_key; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.image_associations
    ADD CONSTRAINT image_associations_image_id_key UNIQUE (image_id);


--
-- Name: image_associations image_associations_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.image_associations
    ADD CONSTRAINT image_associations_pkey PRIMARY KEY (id);


--
-- Name: images images_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.images
    ADD CONSTRAINT images_pkey PRIMARY KEY (id);


--
-- Name: order_details order_details_order_id_key; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.order_details
    ADD CONSTRAINT order_details_order_id_key UNIQUE (order_id);


--
-- Name: order_details order_details_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.order_details
    ADD CONSTRAINT order_details_pkey PRIMARY KEY (id);


--
-- Name: order_items order_items_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.order_items
    ADD CONSTRAINT order_items_pkey PRIMARY KEY (id);


--
-- Name: orders orders_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);


--
-- Name: payment_details payment_details_order_detail_id_key; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.payment_details
    ADD CONSTRAINT payment_details_order_detail_id_key UNIQUE (order_detail_id);


--
-- Name: payment_details payment_details_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.payment_details
    ADD CONSTRAINT payment_details_pkey PRIMARY KEY (id);


--
-- Name: products products_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);


--
-- Name: refresh_tokens refresh_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT refresh_tokens_pkey PRIMARY KEY (id);


--
-- Name: refresh_tokens refresh_tokens_user_id_key; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT refresh_tokens_user_id_key UNIQUE (user_id);


--
-- Name: roles roles_name_key; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_name_key UNIQUE (name);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- Name: social_accounts social_accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.social_accounts
    ADD CONSTRAINT social_accounts_pkey PRIMARY KEY (id);


--
-- Name: subcategories subcategories_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.subcategories
    ADD CONSTRAINT subcategories_pkey PRIMARY KEY (id);


--
-- Name: user_roles user_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (role_id, user_id);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_email_username_phone_number_key; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_username_phone_number_key UNIQUE (email, username, phone_number);


--
-- Name: users users_phone_number_key; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_phone_number_key UNIQUE (phone_number);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- Name: refresh_tokens fk1lih5y2npsf8u5o3vhdb9y0os; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT fk1lih5y2npsf8u5o3vhdb9y0os FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: order_items fk1q80b422hoxsjgc259r091peg; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.order_items
    ADD CONSTRAINT fk1q80b422hoxsjgc259r091peg FOREIGN KEY (order_detail_id) REFERENCES public.orders(id);


--
-- Name: cart_items fk1re40cjegsfvw58xrkdp6bac6; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.cart_items
    ADD CONSTRAINT fk1re40cjegsfvw58xrkdp6bac6 FOREIGN KEY (product_id) REFERENCES public.products(id);


--
-- Name: orders fk32ql8ubntj5uh44ph9659tiih; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT fk32ql8ubntj5uh44ph9659tiih FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: social_accounts fk6rmxxiton5yuvu7ph2hcq2xn7; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.social_accounts
    ADD CONSTRAINT fk6rmxxiton5yuvu7ph2hcq2xn7 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: payment_details fk96rviu3pjlbx2g8g2dx6rpupx; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.payment_details
    ADD CONSTRAINT fk96rviu3pjlbx2g8g2dx6rpupx FOREIGN KEY (order_detail_id) REFERENCES public.orders(id);


--
-- Name: cart_items fk99e0am9jpriwxcm6is7xfedy3; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.cart_items
    ADD CONSTRAINT fk99e0am9jpriwxcm6is7xfedy3 FOREIGN KEY (cart_id) REFERENCES public.cart(id);


--
-- Name: products fk99gib6u0rm71m4vr6qdfj33is; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT fk99gib6u0rm71m4vr6qdfj33is FOREIGN KEY (establishment_id) REFERENCES public.establishments(id);


--
-- Name: establishment_filters fkcrmgdqsn1ojebiuj6yhvloddq; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.establishment_filters
    ADD CONSTRAINT fkcrmgdqsn1ojebiuj6yhvloddq FOREIGN KEY (establishment_id) REFERENCES public.establishments(id);


--
-- Name: image_associations fkdadh9qfc9xfsfdmxuucyp51cj; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.image_associations
    ADD CONSTRAINT fkdadh9qfc9xfsfdmxuucyp51cj FOREIGN KEY (image_id) REFERENCES public.images(id);


--
-- Name: discounts fke3tqxsyxv7qcy8uvf2lns1hx8; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.discounts
    ADD CONSTRAINT fke3tqxsyxv7qcy8uvf2lns1hx8 FOREIGN KEY (product_id) REFERENCES public.products(id);


--
-- Name: cart_items fke49uqm63kajrg9gt365roi2gm; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.cart_items
    ADD CONSTRAINT fke49uqm63kajrg9gt365roi2gm FOREIGN KEY (order_detail_id) REFERENCES public.orders(id);


--
-- Name: cart fkg5uhi8vpsuy0lgloxk2h4w5o6; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.cart
    ADD CONSTRAINT fkg5uhi8vpsuy0lgloxk2h4w5o6 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: establishments fkgfsgl8rfxapyxy7d4ti2mxh9b; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.establishments
    ADD CONSTRAINT fkgfsgl8rfxapyxy7d4ti2mxh9b FOREIGN KEY (subcategory_id) REFERENCES public.subcategories(id);


--
-- Name: user_roles fkh8ciramu9cc9q3qcqiv4ue8a6; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fkh8ciramu9cc9q3qcqiv4ue8a6 FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- Name: user_roles fkhfh9dx7w3ubf1co1vdev94g3f; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: subcategories fkiborb6ptvy1t1n3v6klb56l5s; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.subcategories
    ADD CONSTRAINT fkiborb6ptvy1t1n3v6klb56l5s FOREIGN KEY (category_id) REFERENCES public.categories(id);


--
-- Name: order_details fkjyu2qbqt8gnvno9oe9j2s2ldk; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.order_details
    ADD CONSTRAINT fkjyu2qbqt8gnvno9oe9j2s2ldk FOREIGN KEY (order_id) REFERENCES public.orders(id);


--
-- Name: products fkk1y014eougo40urpwufyys8fp; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT fkk1y014eougo40urpwufyys8fp FOREIGN KEY (establishment_filter_id) REFERENCES public.establishment_filters(id);


--
-- Name: establishment_adresses fkmg0q2k4rsk5x6sn4gvn20r96v; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.establishment_adresses
    ADD CONSTRAINT fkmg0q2k4rsk5x6sn4gvn20r96v FOREIGN KEY (establishment_id) REFERENCES public.establishments(id);


--
-- Name: order_items fkocimc7dtr037rh4ls4l95nlfi; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.order_items
    ADD CONSTRAINT fkocimc7dtr037rh4ls4l95nlfi FOREIGN KEY (product_id) REFERENCES public.products(id);


--
-- Name: addresses fksv7a6xjwuwlcwxbq98p0gqna; Type: FK CONSTRAINT; Schema: public; Owner: glovoTeam
--

ALTER TABLE ONLY public.addresses
    ADD CONSTRAINT fksv7a6xjwuwlcwxbq98p0gqna FOREIGN KEY (order_id) REFERENCES public.orders(id);


--
-- PostgreSQL database dump complete
--

