(ns betbot.dao.events
  "Describes how to operate with event objects"
  (:require [betbot.dao.models :as m]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.coerce :refer [to-sql-time]]
            [korma.core :as k]
            [cheshire.core :as json]
            [taoensso.timbre :as log]))

(def ^:private iso-8601 (f/formatter "yyyy-MM-dd'T'HH:mm:ss"))

(defn wrap-in-parens [item] (str \' item \'))

(defn serialize [m sep] (apply str (concat (interpose sep (map wrap-in-parens (vals m))))))

(defn keys->str [m] (clojure.string/replace (clojure.string/join ", " (keys m)) #":" ""))

(defn upsert
  "Creates event only in event with this title do not exists"
  [{:keys [starts_at ends_at] :as event}]
  (let [event-gen {:created_at (t/now)
                   :updated_at (t/now)
                   :ends_at ends_at ;; :ends_at already formatted in core/process-results
                   :starts_at (f/parse iso-8601 starts_at)}
        res (merge event event-gen)
        keys (str "(" (keys->str res) ")")
        values (str "(" (serialize (merge res) ", ") ")" )
        ;; when there's a conflict in starts_at (aka match rescheduled) — we simply
        ;; update row with new starts_at
        sql-str (str
                  "INSERT INTO events " keys
                  " VALUES " values
                  " ON CONFLICT (title) DO UPDATE SET starts_at = EXCLUDED.starts_at;")
        result (k/exec-raw [sql-str])]
    result))

(defn create
  "Creates event in database"
  [{:keys [starts_at ends_at] :as event-param}]
  (let [event-gen {:created_at (t/now)
                   :updated_at (t/now)
                   :starts_at  (f/parse iso-8601 starts_at)
                   :ends_at    (f/parse iso-8601 ends_at)}
        event (into event-param event-gen)
        result (k/insert m/events (k/values event))]
    result))

(defn find-one
  "go to db and get one event"
  [id]
  (let [result (k/select m/events
                 (k/where {:id (Integer/parseInt id)}))]
    result))

;; TODO: add validation that will allow only fields from whitelist to be updated
(defn update-one
  "Updates single event with provided id"
  [id {:keys [starts_at ends_at] :as event}]
  (let [result (k/update m/events
                 (k/set-fields (merge event {:updated_at (t/now)
                                             :starts_at  (f/parse iso-8601 starts_at)
                                             :ends_at    (f/parse iso-8601 ends_at)}))
                 (k/where {:id (Integer/parseInt id)}))]
    result))

(defn delete
  "Deletes single event with provided id"
  [id]
  (let [result (k/delete m/events
                  (k/where {:id (Integer/parseInt id)}))]
    (log/debug result)
    result))

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
     {:criteria criteria
      :results events}))

;; TODO remove dummy data and add today's events logic
(defn get-hot-events
  "Finds hot events for today"
  []
  (let [item (k/select m/events
                 (k/where {:starts_at [>= (k/sqlfn now)]}))
        ;; generete nice response
        result (-> (into {} item)
                   :result_str
                   json/generate-string)]
     result))
