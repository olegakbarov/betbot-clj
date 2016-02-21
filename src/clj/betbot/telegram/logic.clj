(ns betbot.telegram.logic
  "Describes how bot should handle incomming messages"
  (:require [taoensso.timbre :as log]
            [betbot.dao.models :as m]
            [cheshire.core :as json]
            [korma.core :as k]
            [clojure.string :as str]
            [betbot.dao.bets :as bets]
            [betbot.dao.users :as users]
            [betbot.dao.events :as events]
            [clojure.core.match :refer [match]]
            [betbot.telegram.api :as api]))

(def userid 3)

; Assume we have a valid command with following signature `/cmd val mod`
(defn parse-command
  "Parses command and returns hashmap"
  [text]
  (let [command (first (str/split text #" "))
        value (second (str/split text #" "))
        modifier (nth (str/split text #" ") 2)]
  {:command command
   :event-id value
   :outcome modifier}))


(defn check-user
  "Finds or creates user"
  [{:keys [id] :as user}]
  (let [exists (k/select m/users
                         (k/where {:telegram_id id}))]
    (if (empty? exists)
      (users/create user)
      (log/debug "User already exists"))))


(defn update-handler
  "Parses the message from user, and generates the response"
  [update]
  (log/debug "Got update from bot:\n"
             (json/generate-string update {:pretty true}))
  (let [chat (-> update :message :chat)
        text (-> update :message :text)
        telegram_id (-> update :message :from :id)
        ;; for some bizarro reason k/select retruns everything but the id
        sql (str "SELECT * FROM users WHERE telegram_id = ?;")
        user-id (:id (into {} (k/exec-raw [sql [telegram_id]] :results)))
        command (:command (parse-command text))
        event-id (Integer/parseInt (:event-id (parse-command text)))
        outcome (Integer/parseInt (:outcome (parse-command text)))]
    (match [command]
           ["/events"] (api/send-message (:id chat)
                          (events/get-hot-events))
           ["yo"] (doseq []
                    (if (= 1 (first (bets/create user-id event-id outcome)))
                      (api/send-message (:id chat) "Bet accepted! 😎")
                      (api/send-message (:id chat) "Woops.. something went wrong 😱")))
           :else (api/send-message (:id chat) "I dont know this command 🙈"))))

