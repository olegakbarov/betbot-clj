(ns betbot.telegram.logic
  "Describes how bot should handle incomming messages"
  (:require [taoensso.timbre :as log]
            [cheshire.core :as json]
            [betbot.telegram.api :as api]))

(defn update-handler [update]
  (log/debug "Got update from bot:\n"
             (json/generate-string update {:pretty true}))
  (let [chat (-> update :message :chat)
        text (-> update :message :text)]
    (api/send-message (:id chat) "\uD83D\uDC26")))

