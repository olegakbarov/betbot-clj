(ns betbot.scraper.premiership
  (:require [clojure.string :as str]
            [clj-time.core :as t]
            [clj-http.client :as http]
            [clojure.data.json :as json]
            [cheshire.core :refer :all]
            [taoensso.timbre :as log]

            [betbot.dao.models :as m]
            [betbot.dao.events :as events]
            [korma.core :as k]

            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.coerce :refer [to-sql-time]]))

(def ^:private iso-8601 (f/formatter "yyyy-MM-dd'T'HH:mm:ss"))
(def root-url "http://live.premierleague.com/syndicationdata")
;; TODO Hardcoded week
(def week 29)

(defn store-results
  "Check if event is already in DB and if not — store it"
  [events]
  (log/debug events)
  (doseq [event events]
    (try
      (events/insert event)
    ;; TODO! custom Exception on duplicate
    (catch Exception e
      (log/warn "Was trying to save duplicate, but we are fine", (.getNextException e)))
    (catch Exception e
      (log/error "Ouch, save failed")))))

(defn url-constructor
  "Creates a url either with timestamp param or gets the timestamp"
  [week timestamp]
  (str
    root-url
    "/competitionId=8"
    "/seasonId=2015"
    "/gameWeekId=" week
    (if (nil? timestamp)
      "/sentinel.json"
      (str "/scores.json?" timestamp))))

(defn process-results
  "Processes received date"
  [data]
  (let [scores (into [] (flatten (map #(get % "Scores") data)))
        result (into [] (map (fn [scores]
          (let [title (str (-> scores (get "HomeTeam")(get "Name")) " vs " (-> scores (get "AwayTeam") (get "Name")))
                starts_at (get scores "DateTime")
                ends_at (t/plus (f/parse iso-8601 (get scores "DateTime")) (t/hours 2))
                hometeam (-> scores (get "HomeTeam")(get "Name"))
                awayteam (-> scores (get "AwayTeam")(get "Name"))
                hometeam-score (-> scores (get "HomeTeam")(get "Score"))
                awayteam-score (-> scores (get "AwayTeam")(get "Score"))]
               {:title title
                :starts_at starts_at
                :ends_at ends_at
                :subcategory "Soccer"
                :category "Sport"
                :status (if (t/before? (t/now) (f/parse iso-8601 starts_at)) "Match is scheduled" "Match is over")
                :result_str (str hometeam " " hometeam-score":" awayteam-score" " awayteam)
                :outcome (case
                           (= hometeam-score awayteam-score) 0
                           (> hometeam-score awayteam-score) 1
                           (< hometeam-score awayteam-score) 2
                           (t/before? (t/now) (f/parse iso-8601 starts_at)) 99)
                })) scores))]
        (store-results result)))

(defn get-data
  [timestamp week]
  (log/debug "Getting actual data with the timestamp: " timestamp)
  (log/debug (url-constructor week timestamp))
  (let [data (-> (url-constructor week timestamp)
                  http/get
                  :body
                  parse-string
                  (get "Data"))]
    (process-results data)))

(defn begin-scrape
  [t opts]
  (log/debug "im alive")
  (let [timestamp (-> (url-constructor week nil)
                       http/get
                       :body
                       parse-string
                       (get "scores"))]
    (if (not= 0 timestamp)
      ;; TODO Hardcoded week
      (get-data timestamp week)
      (log/error ("Wrong timestamp! — " timestamp)))))

