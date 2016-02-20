(ns betbot.telegram.logic
  "Describes how bot should handle incomming messages"
  (:require [taoensso.timbre :as log]
            [korma.core :as k]
            [cheshire.core :as json]
            [betbot.dao.models :as m]
            [betbot.dao.events :as e]
            [betbot.dao.users :as u]
            [betbot.telegram.api :as api]))

(defn check-user
  "Finds or creates user"
  [{:keys [id] :as user}]
  (let [exists (k/select m/users
                         (k/where {:telegram_id id}))]
    (if (empty? exists)
      (u/create user)
      (log/debug "User already exists"))))

(defn update-handler [update]
  (log/debug "Got update from bot:\n"
             (json/generate-string update {:pretty true}))
  (let [chat (-> update :message :chat)
        text (-> update :message :text)
        user (-> update :message :from)]
    ;; create user unless one already exists
    (check-user user)
    ;; TODO | get-hot-events is just a placeholder
    (api/send-message (:id chat) (e/get-hot-events))))

