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

| title | starts_at | ends_at | category | subcategory | created_at | updated_at|

`events` table created with:

```sql
CREATE table events (
  id SERIAL PRIMARY KEY,
  title TEXT NOT NULL, 
  starts_at TIMESTAMPTZ NOT NULL,
  ends_at TIMESTAMPTZ NOT NULL,
  category TEXT,
  subcategory TEXT,
  created_at TIMESTAMPT WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMPT WITH TIME ZONE NOT NULL
  );
```

## `Wiki:`

##### Events API

1. Store `events` in db
1. Provide CRUD on `events`
1. Possibly store today's `events` in redis (hi-availablity, lol)

##### Bet API + engine

1. Store `bets` in db
1. Provide CRUD on bets
1. Possibly store ending today `bets` in redis
1. Handle *happened* events — update state and initalize push to users

##### Bot

1. Provide available categories and events to user (via command)
1. Take bets on event
1. Inform about result after event has happened
1. Provide mechanism to create custom bets

##### Scraper

1. Scrape 3rth party API's every *some* time and fetch data about upcoming events
1. Compare new data with existing and update if needed

