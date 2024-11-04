CREATE TABLE IF NOT EXISTS currency (
	id SERIAL PRIMARY KEY,
	code VARCHAR(3) UNIQUE NOT NULL,
	fullname VARCHAR(255),
	sign VARCHAR(2)
);

CREATE TABLE IF NOT EXISTS exchange_rate (
	id SERIAL PRIMARY KEY,
	base_currency_id INT NOT NULL REFERENCES currency (id),
	target_currency_id INT NOT NULL REFERENCES currency (id),
	rate NUMERIC,
	UNIQUE (base_currency_id, target_currency_id)
);