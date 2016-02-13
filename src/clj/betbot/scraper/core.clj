(ns betbot.scraper.core
  (:require [clj-time.core :as t]
            [taoensso.timbre :as log]
            [betbot.scraper.premiership :as premiership]
            [cronj.core :refer :all]))


;; this task fetch data every two seconds
(def monitor-api
  {:id "Periodically fetch data from API and compare it with existing in DB"
   :handler premiership/begin-scrape
   :schedule "/2 * * * * * *"
   :opts {:nope "nope"}})

(def cj (cronj :entries [monitor-api]))

(defn launch
  "Launch scraper with schedule"
  [& args]
  (start! cj))
