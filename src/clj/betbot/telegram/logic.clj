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


(def templates {:welcome (str "Hello! Here's how i work:\n\n"
                               "/events — get list of events available for betting\n\n"
                               "/about — learn more about team behind @bet2bot\n\n")
                :bet-accepted "Bet accepted! 😎 We'll notify you."
                :error "Woops.. something went wrong 😱 It's not you — it's us. Please get back a little later."
                :dunno "I don't know this command 🙈"})


(defn process-event
  "Formats the item from db"
  [item]
  (str "[" (-> item :id) "]" " " (-> item :title) "\n"))


(defn get-ids
  "Generate vector of ids"
  [item]
  (vector (str (-> item :id))))


(defn generate-keyboard
  "Generates custom keyboard"
  [& args]
  ;; this peek-peek is shitty.
  (let [items (peek (peek (mapv #(vector %) args)))]
    {:keyboard (into [] (map #(into [] (flatten %)) (partition 3 items)))
     :one_time_keyboard true}))


(defn get-events
  "Get 9 events from db and format them for sending out to user"
  []
  (let [events (events/get-hot-events)]
   {:keyboard (generate-keyboard (mapv get-ids events))
    :string (str
              "Next matches are available for betting:\n"
              (reduce str (mapv process-event events)))}))


(defn parse-command
  "Parse non-standart command"
  [msg chat-id user-id]
  (let [outcome 2
        event-id 111]
    ;; TODO remove hardcode
    ;; handle parsing of command and betting logic here
    (doseq []
      (if (= 1 (first (bets/create user-id event-id outcome)))
        (api/send-message (:id chat-id)
                          (:bet-accepted templates))
        (api/send-message (:id chat-id)
                          (:error templates))))))


(defn command-trap
  "This catches basic command, and if none caught pass message to next handler"
  [msg telegram_id chat]
  ;; For some bizarro reason k/select retruns everything but the id
  (let [sql (str "SELECT * FROM users WHERE telegram_id = ?;")
        user-id (:id (into {} (k/exec-raw [sql [telegram_id]] :results)))]
    (match [msg]
           ["/start"] (api/send-message (:id chat)
                                        (:welcome templates)
                                        {:keyboard (vector [["/events"]["/about"]])
                                         :one_time_keyboard true})
           ["/about"] (api/send-message (:id chat)
                                         "Made by Oleg Akbarov & Anton Chebotaev")
           ["/events"] (api/send-message (:id chat)
                                         (-> (get-events) :string)
                                         (-> (get-events) :keyboard))
           :else (parse-command msg chat user-id))))


(defn update-handler
  "Parses the message from user, and generates the response"
  [update]
  (log/debug "Got update from bot:\n"
             (json/generate-string update {:pretty true}))
  (let [chat (-> update :message :chat)
        msg (-> update :message :text)
        telegram_id (-> update :message :from :id)]
    ;; TODO Remove hardcode
    (command-trap msg telegram_id chat)))
