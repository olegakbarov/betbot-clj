# `betbot`

This is draft version for a Telegram bot that helps to make bets.

## How to

Local development is done through the figwheel which
auto-reloads both client and server code.
To start, run following command:

    lein figwheel

To auto-compile less:

    lein less auto

To deploy to Heroku:

    git push heroku master


### Datbase

For local development configure your DB with following script:

```sql
CREATE DATABASE betbot;
CREATE USER betbot WITH password 'yolo';
GRANT ALL privileges ON DATABASE betbot TO betbot;
```

*Events table*

```
title
result_str — String with user-friendly result of event;
outcome — CODE for result — 0 for deuce, 1 = hometeam won, 2 awayteam won;
status — enum ["Match is scheduled" "Match is over"]
starts_at
ends_at
category
subcategory
created_at
updated_at|
```

`events` table created with:

```sql
CREATE table events (
  id SERIAL PRIMARY KEY,
  title TEXT NOT NULL,
  starts_at TIMESTAMPTZ NOT NULL,
  ends_at TIMESTAMPTZ NOT NULL,
  status TEXT,
  category TEXT,
  outcome varchar(99),
  subcategory TEXT,
  result_str TEXT,
  updated_at TIMESTAMPTZ NOT NULL,
  created_at TIMESTAMPTZ NOT NULL
);

CREATE UNIQUE INDEX events_title_start ON events USING btree (title, starts_at);
```

## `Wiki:`

##### Events API

- [x] Store `events` in db
- [x] Provide CRUD on `events`
- [ ] Possibly store today's `events` in redis (hi-availablity, lol)

##### Bet API + engine

- [ ] Store `bets` in db
- [ ] Provide CRUD on bets
- [ ] Possibly store ending today `bets` in redis
- [ ] Handle *happened* events — update state and initalize push to users

##### Bot

- [ ] Provide available categories and events to user (via command)
- [ ] Take bets on event
- [ ] Inform about result after event has happened
- [ ] Provide mechanism to create custom bets

##### Scraper

- [x] Scrape 3rth party API's every *some* time and fetch data about upcoming events
- [x] Compare new data with existing and update if needed



### Some useful sports data notes

- MLB 2016 season is (April 3, 2016 – October 2, 2016)
