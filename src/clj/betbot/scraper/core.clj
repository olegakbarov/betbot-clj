(ns betbot.scraper.core
  (:require [clojure.string :as str]
            [clj-time.core :as t]
            [clj-http.client :as http]
            [clojure.data.json :as json]
            [cheshire.core :refer :all]
            [taoensso.timbre :as log]
            [betbot.dao.models :as m]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.coerce :refer [to-sql-time]]
            [korma.core :as k]
            [cronj.core :refer :all]))

(def ^:private iso-8601 (f/formatter "yyyy-MM-dd'T'HH:mm:ss"))

;; ad-hoc
(def root-url "http://live.premierleague.com/syndicationdata")
(def id 8)
(def season 2015)
(def week 25)

;; ad-hoc
(defn url-constructor
  "Creates a url either with timestamp param or gets the timestamp"
  [id season week timestamp]
  (str
    root-url
    "/competitionId=" id
    "/seasonId=" season
    "/gameWeekId=" week
    (if (nil? timestamp)
      "/sentinel.json"
      (str "/scores.json?" timestamp))))

;; ad-hoc
(defn process-results
  "Processes and stores the the resluts to db"
  [data]
  (let [scores (into [] (flatten (map #(get % "Scores") data)))
        result (into [] (map (fn [scores] {:title (str (-> scores (get "HomeTeam")(get "Name")) " vs " (-> scores (get "AwayTeam") (get "Name")))
                                           :starts_at (get scores "DateTime")
                                           :ends_at (t/plus (f/parse iso-8601 (get scores "DateTime")) (t/hours 2))
                                           :category "Sport"
                                           :subcategory "Soccer"}) scores))]
        (log/debug result))) ;; from this point ready to save in DB

;; ad-hoc (because of timestamps)
(defn get-data
  [timestamp]
  (log/debug "Get actual data with the timestamp: " timestamp)
  (let [data (-> (url-constructor id season week timestamp)
                       http/get
                       :body
                       parse-string
                       (get "Data"))]
    (process-results data)))

(defn get-timestamp
  [t opts]
  (log/debug "Start http-ing the endpoint")
  (let [timestamp (-> (url-constructor id season week nil)
                       http/get
                       :body
                       parse-string
                       (get "scores"))]
    (if (not= 0 timestamp)
      (get-data timestamp)
      (log/error ("Wrong timestamp! — " timestamp)))))

;; config of cronj task
(def task
  {:id "get FA Premiership data"
   :handler get-timestamp
   :schedule "/2 * * * * * *"
   :opts {:name "fa premiership"}})

(def cj (cronj :entries [task]))

(defn launch
  "Launch scraper with schedule"
  [& args]
  (start! cj))
