(ns betbot.dao.models
  "Describes all DB entities and their relations"
  (:require [environ.core :refer [env]]
            [clj-time.jdbc]
            [korma.db :refer [defdb postgres]]
            [betbot.util.db :as db-util]
            [korma.core :refer [entity-fields
                                many-to-many
                                belongs-to
                                defentity
                                has-many
                                has-one
                                table
                                pk]]))

(defdb db (db-util/korma-connection-map (env :database-url)))

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
    :status
    :result_str
    :outcome
    :created_at
    :updated_at))

(defentity bets
  (pk :id)
  (table :bets)
  (belongs-to users {:fk :user_id})
  (belongs-to events {:fk :event_id})
  (entity-fields
    :outcome
    :created_at))

