-- Create initial schema for RideOn application
-- Drop existing tables to start fresh
DROP TABLE IF EXISTS ride_point CASCADE;
DROP TABLE IF EXISTS network_status CASCADE;
DROP TABLE IF EXISTS obstacle_report CASCADE;
DROP TABLE IF EXISTS ride_session CASCADE;
DROP TABLE IF EXISTS inquiry CASCADE;
DROP TABLE IF EXISTS question CASCADE;
DROP TABLE IF EXISTS posts CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Drop ENUM types
DROP TYPE IF EXISTS report_type_enum CASCADE;
DROP TYPE IF EXISTS report_status_enum CASCADE;
DROP TYPE IF EXISTS question_status CASCADE;

-- Create ENUM types
CREATE TYPE question_status AS ENUM ('SOLVED', 'UNSOLVED');
CREATE TYPE report_type_enum AS ENUM ('OBSTACLE', 'ROAD_DAMAGE', 'CONSTRUCTION', 'SLIPPERY', 'ETC');
CREATE TYPE report_status_enum AS ENUM ('UNCONFIRMED', 'CONFIRMED', 'RESOLVED');

-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50),
    gender VARCHAR(10),
    birth_date DATE,
    age INT,
    phone VARCHAR(20),
    profile_image VARCHAR(500),
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now(),
    user_id VARCHAR(255) NOT NULL UNIQUE,
    provider VARCHAR(20) DEFAULT 'original'
);

-- Create posts table
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    image VARCHAR(500),
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT now(),
    update_at TIMESTAMP DEFAULT now()
);

-- Create question table
CREATE TABLE question (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    status question_status DEFAULT 'UNSOLVED',
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT now(),
    update_at TIMESTAMP DEFAULT now()
);

-- Create inquiry table
CREATE TABLE inquiry (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    created_at TIMESTAMP DEFAULT now()
);

-- Create ride_session table
CREATE TABLE ride_session (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    started_at TIMESTAMP NOT NULL,
    ended_at TIMESTAMP,
    total_distance_km DECIMAL(8,2),
    avg_speed_kmh DECIMAL(5,2),
    max_speed_kmh DECIMAL(5,2),
    calories_burned DECIMAL(8,2),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    last_location_lat DECIMAL(9,6),
    last_location_lng DECIMAL(9,6),
    last_location_time TIMESTAMP,
    network_quality VARCHAR(20),
    connection_lost_count INT DEFAULT 0,
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

-- Create ride_point table
CREATE TABLE ride_point (
    id BIGSERIAL PRIMARY KEY,
    ride_session_id BIGINT REFERENCES ride_session(id) ON DELETE CASCADE,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    speed_kmh DECIMAL(5,2),
    altitude DECIMAL(8,2),
    accuracy DECIMAL(5,2),
    heading DECIMAL(5,2),
    recorded_at TIMESTAMP NOT NULL,
    network_quality VARCHAR(50),
    battery_level INTEGER,
    is_offline_sync BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT now()
);

-- Create obstacle_report table
CREATE TABLE obstacle_report (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    report_type report_type_enum NOT NULL,
    description TEXT,
    status report_status_enum NOT NULL,
    image TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_obstacle_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_user_id ON users(user_id);
CREATE INDEX idx_posts_user_id ON posts(user_id);
CREATE INDEX idx_posts_created_at ON posts(created_at);
CREATE INDEX idx_question_user_id ON question(user_id);
CREATE INDEX idx_question_status ON question(status);
CREATE INDEX idx_inquiry_user_id ON inquiry(user_id);
CREATE INDEX idx_ride_session_user_id ON ride_session(user_id);
CREATE INDEX idx_ride_session_status ON ride_session(status);
CREATE INDEX idx_ride_session_started_at ON ride_session(started_at);
CREATE INDEX idx_network_status_session_id ON network_status(ride_session_id);
CREATE INDEX idx_ride_point_session_id ON ride_point(ride_session_id);
CREATE INDEX idx_ride_point_offline_sync ON ride_point(is_offline_sync);
CREATE INDEX idx_obstacle_report_status ON obstacle_report(status);
CREATE INDEX idx_obstacle_report_user_id ON obstacle_report(user_id);
CREATE INDEX idx_obstacle_report_location ON obstacle_report(latitude, longitude);
