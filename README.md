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

Curl to check `POST` (notice: the format of dates)

    curl -X POST --header "Content-Type:application/json" localhost:3000/api/events -d '{"result_str":"This string goes to user","category":"Life","subcategory":"Oskar","outcome":"99","title":"wat","ends_at":"2016-03-06T15:30:00","status":"","starts_at":"2016-03-06T13:30:00"}'


# TODO

## Bot

Create set of commands for bot:

### `/today`

- Get today's events
  - select events nearest in time
  - mark them with id
  - create

### `/bet id`

- Place bet on some result
  - create bet table
  - create bet dao
  - create bind command to dao

### broadcast results
  - Scheduler or MQ
  — get current date, update current events
  ...

