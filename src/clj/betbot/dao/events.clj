(ns betbot.dao.events
  "Describes how to operate with event objects"
  (:require [betbot.dao.models :as m]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.coerce :refer [to-sql-time]]
            [korma.core :as k]
            [taoensso.timbre :as log]))

(def ^:private iso-8601 (f/formatter "yyyy-MM-dd'T'HH:mm:ss"))

(defn create
  "Creates event in database"
  [{:keys [starts_at ends_at] :as event-param}]
  (log/debug "Incomming event: " event-param)
  (let [event-gen {:created_at (t/now)
                   :updated_at (t/now)
                   :starts_at  (f/parse iso-8601 starts_at)
                   :ends_at    (f/parse iso-8601 ends_at)}
        event (into event-param event-gen)
        result (k/insert m/events (k/values event))]
    {:status 200
     :body result}))

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

;; TODO: add validation that will allow only fields from whitelist to be updated
(defn update-one
  "go to db and update event"
  [id {:keys [starts_at ends_at] :as event}]
  (let [result (k/update m/events
                 (k/set-fields (merge event {:updated_at (t/now)
                                             :starts_at  (f/parse iso-8601 starts_at)
                                             :ends_at    (f/parse iso-8601 ends_at)}))
                 (k/where {:id (Integer/parseInt id)}))]
    {:status 200
     :body {:updated result}}))

(defn delete
  "go to db and delete event"
  [id]
  (log/debug "Deleting the event with id: " id)
  (let [result (k/delete m/events
                 (k/where {:id (Integer/parseInt id)}))]
    ;; k/delete returns number of deleted items
    (if (= 0 result)
      {:status 404
       :body (str "Event with id " id " haven't been found.")}
      {:status 200
       :body (str "Event with " id " has been deleted.")})))

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