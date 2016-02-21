CREATE table events (
  id SERIAL PRIMARY KEY,
  title TEXT NOT NULL,
  starts_at TIMESTAMPTZ NOT NULL,
  ends_at TIMESTAMPTZ NOT NULL,
  status TEXT,
  category TEXT,
  outcome VARCHAR(99),
  subcategory TEXT,
  result_str TEXT,
  updated_at TIMESTAMPTZ NOT NULL,
  created_at TIMESTAMPTZ NOT NULL
);

CREATE UNIQUE INDEX events_title_start ON events USING btree (title, starts_at);

CREATE table users (
 id SERIAL PRIMARY KEY,
 telegram_id INTEGER,
 first_name VARCHAR(64),
 last_name VARCHAR(64),
 username VARCHAR(64),
 created_at TIMESTAMPTZ NOT NULL
);

CREATE table bets (
 id SERIAL PRIMARY KEY,
 event_id INTEGER REFERENCES events(id) ON DELETE CASCADE,
 user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
 outcome VARCHAR(99),
 created_at TIMESTAMPTZ NOT NULL
);
