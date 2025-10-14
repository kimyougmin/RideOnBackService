-- Reset and create enums/tables per new simplified schema
DROP TABLE IF EXISTS question_comment CASCADE;
DROP TABLE IF EXISTS post_comment CASCADE;
DROP TABLE IF EXISTS group_message CASCADE;
DROP TABLE IF EXISTS group_member CASCADE;
DROP TABLE IF EXISTS riding_group CASCADE;
DROP TABLE IF EXISTS bike_rental_station CASCADE;
DROP TABLE IF EXISTS obstacle_report CASCADE;
DROP TABLE IF EXISTS image_assets CASCADE;
DROP TABLE IF EXISTS network_status CASCADE;
DROP TABLE IF EXISTS ride_point CASCADE;
DROP TABLE IF EXISTS ride_session CASCADE;
DROP TABLE IF EXISTS post_tag CASCADE;
DROP TABLE IF EXISTS question_tag CASCADE;
DROP TABLE IF EXISTS tags CASCADE;
DROP TABLE IF EXISTS inquiry CASCADE;
DROP TABLE IF EXISTS question CASCADE;
DROP TABLE IF EXISTS posts CASCADE;
DROP TABLE IF EXISTS members CASCADE;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'provider_type') THEN
    CREATE TYPE provider_type AS ENUM ('original', 'kakao', 'naver', 'google');
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'ride_status') THEN
    CREATE TYPE ride_status AS ENUM ('ACTIVE', 'COMPLETED', 'PAUSED', 'CANCELLED');
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'report_type') THEN
    CREATE TYPE report_type AS ENUM ('OBSTACLE', 'ROAD_DAMAGE', 'CONSTRUCTION', 'SLIPPERY', 'ETC');
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'report_status') THEN
    CREATE TYPE report_status AS ENUM ('UNCONFIRMED', 'CONFIRMED', 'RESOLVED');
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'network_quality') THEN
    CREATE TYPE network_quality AS ENUM ('POOR', 'FAIR', 'GOOD', 'EXCELLENT');
  END IF;
END$$;

CREATE TABLE members (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(255) UNIQUE,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(100),
  gender VARCHAR(8),
  height DOUBLE PRECISION,
  weight DOUBLE PRECISION,
  birth_date DATE,
  phone VARCHAR(30),
  profile_image TEXT,
  provider provider_type DEFAULT 'original',
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP
);
CREATE INDEX idx_users_email ON members(email);

CREATE TABLE posts (
  id BIGSERIAL PRIMARY KEY,
  member_id BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
  title TEXT NOT NULL,
  content TEXT,
  view_count BIGINT DEFAULT 0,
  like_count BIGINT DEFAULT 0,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP
);
CREATE INDEX idx_posts_member_id ON posts(member_id);
CREATE INDEX idx_posts_created_at ON posts(created_at);

CREATE TABLE question (
  id BIGSERIAL PRIMARY KEY,
  member_id BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
  title TEXT NOT NULL,
  content TEXT,
  status VARCHAR(32) DEFAULT 'UNSOLVED' NOT NULL,
  view_count BIGINT DEFAULT 0,
  like_count BIGINT DEFAULT 0,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP
);
CREATE INDEX idx_question_member_id ON question(member_id);

CREATE TABLE inquiry (
  id BIGSERIAL PRIMARY KEY,
  member_id BIGINT REFERENCES members(id) ON DELETE SET NULL,
  title TEXT,
  content TEXT,
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE ride_session (
  id BIGSERIAL PRIMARY KEY,
  member_id BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
  started_at TIMESTAMP,
  ended_at TIMESTAMP,
  total_distance_km NUMERIC(10,2),
  avg_speed_kmh NUMERIC(7,2),
  max_speed_kmh NUMERIC(7,2),
  calories_burned NUMERIC(9,2),
  status ride_status DEFAULT 'ACTIVE',
  last_location_lat DOUBLE PRECISION,
  last_location_lng DOUBLE PRECISION,
  last_location_time TIMESTAMP,
  network_quality network_quality,
  connection_lost_count INT,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP
);
CREATE INDEX idx_ride_session_member_id ON ride_session(member_id);

CREATE TABLE ride_point (
  id BIGSERIAL PRIMARY KEY,
  ride_session_id BIGINT NOT NULL REFERENCES ride_session(id) ON DELETE CASCADE,
  latitude DOUBLE PRECISION,
  longitude DOUBLE PRECISION,
  speed_kmh NUMERIC(7,2),
  altitude NUMERIC(9,2),
  accuracy NUMERIC(9,3),
  heading NUMERIC(7,3),
  recorded_at TIMESTAMP,
  network_quality network_quality,
  battery_level INT,
  is_offline_sync BOOLEAN
);
CREATE INDEX idx_ride_point_session ON ride_point(ride_session_id);
CREATE INDEX idx_ride_point_location ON ride_point(latitude, longitude);

CREATE TABLE tags (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE post_tag (
  post_id BIGINT REFERENCES posts(id) ON DELETE CASCADE,
  tag_id BIGINT REFERENCES tags(id) ON DELETE CASCADE,
  PRIMARY KEY (post_id, tag_id)
);

CREATE TABLE question_tag (
  question_id BIGINT REFERENCES question(id) ON DELETE CASCADE,
  tag_id BIGINT REFERENCES tags(id) ON DELETE CASCADE,
  PRIMARY KEY (question_id, tag_id)
);

CREATE TABLE network_status (
  id BIGSERIAL PRIMARY KEY,
  ride_session_id BIGINT NOT NULL REFERENCES ride_session(id) ON DELETE CASCADE,
  connection_type VARCHAR(16),
  signal_strength INT,
  is_connected BOOLEAN,
  latency_ms INT,
  upload_speed_mbps NUMERIC(9,3),
  download_speed_mbps NUMERIC(9,3),
  packet_loss_percentage NUMERIC(5,2),
  recorded_at TIMESTAMP
);
CREATE INDEX idx_network_status_session ON network_status(ride_session_id);

CREATE TABLE image_assets (
  id BIGSERIAL PRIMARY KEY,
  url TEXT NOT NULL,
  file_name VARCHAR(255),
  mime_type VARCHAR(64),
  size_bytes BIGINT,
  member_id BIGINT REFERENCES members(id) ON DELETE SET NULL,
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE obstacle_report (
  id BIGSERIAL PRIMARY KEY,
  member_id BIGINT REFERENCES members(id) ON DELETE SET NULL,
  latitude DOUBLE PRECISION,
  longitude DOUBLE PRECISION,
  report_type report_type,
  description TEXT,
  status report_status DEFAULT 'UNCONFIRMED',
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP,
  image TEXT
);
CREATE INDEX idx_obstacle_location ON obstacle_report(latitude, longitude);

CREATE TABLE bike_rental_station (
  id BIGSERIAL PRIMARY KEY,
  rental_station_name VARCHAR(255) NOT NULL,
  rental_station_type VARCHAR(100),
  address_street TEXT,
  address_jibun TEXT,
  latitude NUMERIC(10,7),
  longitude NUMERIC(10,7),
  operation_start_time TIMESTAMP,
  operation_end_time TIMESTAMP,
  closed_days TIMESTAMP,
  fee_type VARCHAR(50),
  rental_fee TEXT,
  bicycle_count INT,
  parking_spots INT,
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
CREATE INDEX idx_bike_station_location ON bike_rental_station(latitude, longitude);

CREATE TABLE riding_group (
  id BIGSERIAL PRIMARY KEY,
  group_name VARCHAR(100) NOT NULL,
  region VARCHAR(6) NOT NULL,
  image TEXT,
  latitude NUMERIC(9,6) NOT NULL,
  longitude NUMERIC(9,6) NOT NULL,
  limit_members INT NOT NULL,
  description TEXT NOT NULL,
  deadline DATE NOT NULL,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP
);

CREATE TABLE group_member (
  member_id BIGINT REFERENCES members(id) ON DELETE CASCADE,
  group_id BIGINT REFERENCES riding_group(id) ON DELETE CASCADE,
  joined_at TIMESTAMP DEFAULT NOW(),
  PRIMARY KEY (member_id, group_id)
);

CREATE TABLE group_message (
  id BIGSERIAL PRIMARY KEY,
  sender_id BIGINT REFERENCES members(id) ON DELETE SET NULL,
  message TEXT,
  image_url VARCHAR(500),
  sent_at TIMESTAMP DEFAULT NOW(),
  group_id BIGINT REFERENCES riding_group(id) ON DELETE CASCADE
);

CREATE TABLE post_comment (
  id BIGSERIAL PRIMARY KEY,
  content TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT NOW(),
  post_id BIGINT REFERENCES posts(id) ON DELETE CASCADE,
  member_id BIGINT REFERENCES members(id) ON DELETE SET NULL
);

CREATE TABLE question_comment (
  id BIGSERIAL PRIMARY KEY,
  content TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT NOW(),
  member_id BIGINT REFERENCES members(id) ON DELETE SET NULL,
  question_id BIGINT REFERENCES question(id) ON DELETE CASCADE
);

CREATE TABLE question_like (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT NOW(),
    question_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    CONSTRAINT uq_question_like UNIQUE (question_id, member_id),
    CONSTRAINT fk_question_like_question FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE,
    CONSTRAINT fk_question_like_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

CREATE TABLE post_like (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT NOW(),
    post_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    CONSTRAINT uq_post_like UNIQUE (post_id, member_id),
    CONSTRAINT fk_post_like_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_post_like_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);
CREATE INDEX idx_post_comment_post_id ON post_comment(post_id);
CREATE INDEX idx_post_like_post_id ON post_like(post_id);
CREATE INDEX idx_question_comment_question_id ON question_comment(question_id);
-- 회원 FK 인덱스 (member_id)로 교체
-- CREATE INDEX idx_question_comment_user_id ON question_comment(user_id);
CREATE INDEX idx_question_like_question_id ON question_like(question_id);
CREATE INDEX idx_question_like_user_id ON question_like(user_id);
CREATE INDEX idx_question_tag_question_id ON question_tag(question_id);
CREATE INDEX idx_question_tag_tag_id ON question_tag(tag_id);
CREATE INDEX idx_tags_name ON tags(name);
CREATE INDEX idx_member_email ON member(email);
