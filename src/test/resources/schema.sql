DROP TABLE IF EXISTS PRICES;
CREATE TABLE PRICES (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand_id BIGINT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    price_list INT NOT NULL,
    product_id BIGINT NOT NULL,
    priority INT NOT NULL default 0,
    price DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL
);

CREATE INDEX idx_product_brand_date ON PRICES (product_id, brand_id, start_date, end_date);


DROP TABLE IF EXISTS PRICE_EVENTS;
CREATE TABLE PRICE_EVENTS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    brand_id BIGINT NOT NULL,
    price_list INT NOT NULL,
    query_date TIMESTAMP NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);