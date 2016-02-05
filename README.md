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

*Events table*

| title | starts_at | ends_at | category | subcategory | created_at | updated_at|

`events` table created with:

```sql
create table events (id integer primary key default nextval('event_ids'), title varchar(64), starts_at timestamptz, ends_at timestamptz, category varchar(64), subcategory varchar(64), created_at timestamptz, updated_at timestamptz);
```

## `Wiki:`

##### Events API

1. Store `events` in db

2. Possibly store today's `events` in redis (hi-availablity, lol)

3. Provide CRUD on `events`

##### Bet API + engine

1. Store `bets` in db

2. Provide CRUD on bets

3. Possibly store ending today `bets` in redis

4. Handle *happened* events — update state and initalize push to users

##### Bot

1. Provide available categories and events to user (via command)

2. Take bets on event

3. Inform about result after event has happened

4. Provide mechanism to create custom bets

##### Scraper

1. Scrape 3rth party API's every *some* time and fetch data about upcoming events

2. Compare new data with existing and update if needed

