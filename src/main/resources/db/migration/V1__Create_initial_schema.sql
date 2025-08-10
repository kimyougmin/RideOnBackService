-- Create ENUM types (H2에서는 지원하지 않으므로 주석 처리)
-- CREATE TYPE "question_status" AS ENUM ('SOLVED', 'UNSOLVED');
-- CREATE TYPE "report_type" AS ENUM ('OBSTACLE', 'ROAD_DAMAGE', 'CONSTRUCTION', 'SLIPPERY', 'ETC');
-- CREATE TYPE "report_status" AS ENUM ('UNCONFIRMED', 'CONFIRMED', 'RESOLVED');

-- Create users table (먼저 생성)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50),
    gender VARCHAR(10),
    birth_date VARCHAR(255),
    age INT,
    phone VARCHAR(20),
    profile_image VARCHAR(500),
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now(),
    user_id VARCHAR(255) NOT NULL UNIQUE,
    provider VARCHAR(20) DEFAULT 'original'
);

-- Create posts table (users 테이블 이후에 생성)
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    image VARCHAR(500) NOT NULL,
    view_count INTEGER NOT NULL DEFAULT 0,
    like_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT now(),
    update_at TIMESTAMP DEFAULT now()
);

-- Create post_like table
CREATE TABLE post_like (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT REFERENCES posts(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT now()
);

-- Create category table
CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(10) NOT NULL
);

-- Create post_category table
CREATE TABLE post_category (
    post_id BIGINT REFERENCES posts(id) ON DELETE CASCADE,
    category_id BIGINT REFERENCES category(id) ON DELETE CASCADE
);

-- Create post_comment table
CREATE TABLE post_comment (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT REFERENCES posts(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT now()
);

-- Create riding_group table
CREATE TABLE riding_group (
    id BIGSERIAL PRIMARY KEY,
    group_name VARCHAR(100) NOT NULL,
    region VARCHAR(6) NOT NULL,
    image_url VARCHAR(500),
    latitude DECIMAL(9,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL,
    limit_members INT NOT NULL,
    description TEXT NOT NULL,
    deadline DATE NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    update_at TIMESTAMP DEFAULT now()
);

-- Create group_member table
CREATE TABLE group_member (
    group_id BIGINT REFERENCES riding_group(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    joined_at TIMESTAMP DEFAULT now()
);

-- Create group_message table
CREATE TABLE group_message (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    group_id BIGINT REFERENCES riding_group(id) ON DELETE CASCADE,
    message TEXT,
    image_url VARCHAR(500),
    sent_at TIMESTAMP DEFAULT now()
);

-- Create question table (ENUM을 VARCHAR로 변경)
CREATE TABLE question (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'UNSOLVED',
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- Create tags table
CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Create question_tag table
CREATE TABLE question_tag (
    question_id BIGINT REFERENCES question(id) ON DELETE CASCADE,
    tag_id BIGINT REFERENCES tags(id) ON DELETE CASCADE
);

-- Create question_like table
CREATE TABLE question_like (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT REFERENCES question(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT now(),
    UNIQUE (question_id, user_id)
);

-- Create question_comment table
CREATE TABLE question_comment (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT REFERENCES question(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT now()
);

-- Create bike_rental_station table
CREATE TABLE bike_rental_station (
    id BIGSERIAL PRIMARY KEY,
    rental_station_name VARCHAR(255) NOT NULL,
    rental_station_type VARCHAR(100),
    address_street TEXT,
    address_jibun TEXT,
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    operation_start_time TIMESTAMP,
    operation_end_time TIMESTAMP,
    closed_days TIMESTAMP,
    fee_type VARCHAR(50),
    rental_fee TEXT,
    bicycle_count INTEGER,
    parking_spots INTEGER,
    air_pump_available BOOLEAN,
    air_pump_type VARCHAR(100),
    repair_station_available BOOLEAN,
    management_phone VARCHAR(50),
    management_name VARCHAR(255),
    data_date DATE,
    provider_code VARCHAR(50),
    provider_name VARCHAR(255),
    province VARCHAR(100) NOT NULL
);

-- Create ride_session table with additional columns
CREATE TABLE ride_session (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    started_at TIMESTAMP NOT NULL,
    ended_at TIMESTAMP,
    total_distance_km FLOAT,
    avg_speed_kmh FLOAT,
    max_speed_kmh FLOAT,
    calories_burned FLOAT,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    last_location_lat FLOAT,
    last_location_lng FLOAT,
    last_location_time TIMESTAMP,
    network_quality VARCHAR(50),
    connection_lost_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT now()
);

-- Create network_status table
CREATE TABLE network_status (
    id BIGSERIAL PRIMARY KEY,
    ride_session_id BIGINT REFERENCES ride_session(id) ON DELETE CASCADE,
    connection_type VARCHAR(20),
    signal_strength INTEGER,
    is_connected BOOLEAN,
    latency_ms BIGINT,
    upload_speed_mbps FLOAT,
    download_speed_mbps FLOAT,
    packet_loss_percentage FLOAT,
    recorded_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT now()
);

-- Create PostGIS extension (H2에서는 지원하지 않으므로 주석 처리)
-- CREATE EXTENSION IF NOT EXISTS postgis;

-- Create ride_point table with additional columns (H2 호환성 위해 수정)
CREATE TABLE ride_point (
    id BIGSERIAL PRIMARY KEY,
    ride_session_id BIGINT REFERENCES ride_session(id) ON DELETE CASCADE,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    speed_kmh FLOAT,
    altitude FLOAT,
    accuracy FLOAT,
    heading FLOAT,
    recorded_at TIMESTAMP NOT NULL,
    network_quality VARCHAR(50),
    battery_level INTEGER,
    is_offline_sync BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT now()
);

-- Create obstacle_report table (H2 호환성 위해 수정)
CREATE TABLE obstacle_report (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    report_type VARCHAR(20) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'UNCONFIRMED',
    image VARCHAR(500),
    created_at TIMESTAMP DEFAULT now()
);

-- Create inquiry table
CREATE TABLE inquiry (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    title TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT now()
);

-- Create indexes
CREATE INDEX idx_question_status ON question(status);
CREATE INDEX idx_question_created_at ON question(created_at);
CREATE INDEX idx_obstacle_report_status ON obstacle_report(status);
CREATE INDEX idx_ride_session_user_id ON ride_session(user_id);
CREATE INDEX idx_ride_session_status ON ride_session(status);
CREATE INDEX idx_network_status_session_id ON network_status(ride_session_id);
CREATE INDEX idx_ride_point_session_id ON ride_point(ride_session_id);
CREATE INDEX idx_ride_point_offline_sync ON ride_point(is_offline_sync); 