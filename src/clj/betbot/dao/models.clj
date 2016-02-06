(ns betbot.dao.models
  "Describes all DB entities and their relations"
  (:require [environ.core :refer [env]]
            [clj-time.jdbc]
            [korma.db :refer [defdb postgres]]
            [korma.core :refer [defentity pk table has-many has-one entity-fields]]))

(defdb db (postgres {:db (env :betbot-db)
                     :user (env :betbot-db-user)
                     :password (env :betbot-db-pass)
                     :host (env :betbot-db-host "localhost")
                     :port (Integer/parseInt (env :betbot-db-port "5432"))}))

(declare users events bets)

(defentity users
  (pk :id)
  (table :users)
  (has-many bets)
  (entity-fields :email :role))

(defentity events
  (pk :id)
  (table :events)
  (has-many bets)
  (entity-fields
    :title
    :starts_at
    :ends_at
    :category
    :subcategory
    :created_at
    :updated_at))

(defentity bets
  (pk :id)
  (table :bets)
  (has-many users)
  (has-one events)
  (entity-fields :result :value))