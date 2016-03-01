(ns betbot.dao.models
  "Describes all DB entities and their relations"
  (:require [omniconf.core :as cfg]
            [clj-time.jdbc]
            [korma.db :refer [defdb postgres]]
            [betbot.util.db :as db-util]
            [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]
            [korma.db :as korma]
            [taoensso.timbre :as log]
            [korma.core :refer [entity-fields
                                many-to-many
                                belongs-to
                                defentity
                                has-many
                                has-one
                                table
                                pk]]))

(defn init-korma []
  (let [url  (cfg/get :database-url)
        spec (db-util/korma-connection-map url)
        db   (korma/create-db spec)]
    (log/debug db)
    (korma/default-connection db)))

(defn migrate []
  (let [url  (cfg/get :database-url)
        config {:datastore (jdbc/sql-database url)
                :migrations (jdbc/load-resources "migrations")}]
    (repl/migrate config)))

; Can't use defdb, because url is not available at compile-time
(defn init-db []
  (migrate)
  (init-korma))

(declare users events bets)

(defentity users
  (pk :id)
  (table :users)
  (has-many bets)
  ;; Based on Telegram API
  (entity-fields
    :telegram_id
    :first_name
    :last_name
    :username))

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
