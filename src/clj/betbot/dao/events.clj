(ns betbot.dao.events
  "Describes how to operate with event objects"
  (:require [betbot.dao.models :as m]
            [clj-time.core :as t]
            [korma.core :as k]))

(defn create
  "Creates event in database"
  [event-param]
  (let [event (into event-param {:created_at (t/now)
                                 :updated_at (t/now)})
        result (k/insert m/events
                 (k/values event))]
    {:status 200
     :result result}))

(defn find-one
  "go to db and get one event"
  [id]
  (let [result (k/select m/events
                 (k/where {:id id}))]
    (if (empty? result)
      {:status 404
       :body "No eventh with this id"}
      {:status 200
       :body (peek result)})))

(defn update
  "go to db and update event"
  [id event]
  {:status 200
   :body "Not implemented yet"})

(defn delete
  "go to db and delete event"
  [id]
  {:status 200
   :body "Not implemented yet"})

(defn- query->criteria
  "Transforms ring string-based query to criteria object"
  [query]
  {:sort_by (:sort_by query "created_at")
   :order   (:order query "asc")
   :limit   (read-string (:limit query "10"))
   :offset  (read-string (:offset query "0"))})

(defn search
  "Searches backend for events"
  [query]
  (let [criteria (query->criteria query)
        events (k/select m/events)]
    {:status 200
     :body {:criteria criteria
            :results events}}))