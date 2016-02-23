(ns betbot.telegram.logic
  "Describes how bot should handle incomming messages"
  (:require [taoensso.timbre :as log]
            [cheshire.core :as json]
            [betbot.dao.models :as m]
            [cheshire.core :as json]
            [korma.core :as k]
            [clojure.string :as str]
            [betbot.dao.bets :as bets]
            [betbot.dao.users :as users]
            [betbot.dao.events :as events]
            [clojure.core.match :refer [match]]
            [betbot.telegram.api :as api]))


(def templates {:welcome "Hello! Here's how i work:"
                :bet-accepted "Bet accepted! 😎 We'll notify you."
                :error "Woops.. something went wrong 😱"
                :dunno "I don't know this command 🙈"})


(defn process-event
  "Formats the item from db"
  [item]
  (str
    "EPL001 " (-> item :title) "\n"))


(defn get-events
  "Get top 5 events from db and format them for sending out to user"
  []
  (let [events (events/get-hot-events)]
   (str
      "Next matches available for betting are:\n"
      (reduce str (mapv process-event events)))))


(defn get-keyboard
  "Generates options map with custom keyboard"
  [& args]
  {:keyboard (vec (map #(vector %) args))
   :one_time_keyboard true})


(defn command-trap
  "Generates command-recognition patterns"
  [text telegram_id chat event-id]
  (let [outcome 2
        ;; for some bizarro reason k/select retruns everything but the id
        sql (str "SELECT * FROM users WHERE telegram_id = ?;")
        user-id (:id (into {} (k/exec-raw [sql [telegram_id]] :results)))]
        (log/debug (get-events))
    (match [text]
           ["/start"] (api/send-message (:id chat)
                                        (:welcome templates)
                                        (get-keyboard "Soccer" "Misc"))
           ["/events"] (api/send-message (:id chat)
                                         (get-events))
           ["bet"] (doseq []
                    (if (= 1 (first (bets/create user-id event-id outcome)))
                      (api/send-message (:id chat)
                                        (:bet-accepted templates)
                                        (get-keyboard "A" "B"))
                      (api/send-message (:id chat)
                                        (:error templates))))
           :else (api/send-message (:id chat)
                                   (:dunno templates)))))


(defn update-handler
  "Parses the message from user, and generates the response"
  [update]
  (log/debug "Got update from bot:\n"
             (json/generate-string update {:pretty true}))
  (let [chat (-> update :message :chat)
        text (-> update :message :text)
        telegram_id (-> update :message :from :id)]
    (command-trap text telegram_id chat 111)))
