(ns betbot.dao.bets
  "Describes how to operate with bets objects"
  (:require [betbot.dao.models :as m]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.coerce :refer [to-sql-time]]
            [korma.core :as k]
            [cheshire.core :as json]
            [taoensso.timbre :as log]))

(defn create
  "Place bet on event"
  [user-id event-id outcome]
  ;; handle errors here (try/catch maybe?)
  (let [event (k/select m/events
                 (k/where {:id event-id}))
        sql (str "INSERT INTO bets ("
                 "user_id, event_id, outcome, created_at"
                 ") VALUES ("
                 "?, ?, ?, NOW()"
                 ");")]
    (k/exec-raw [sql [user-id event-id outcome] :results])))
