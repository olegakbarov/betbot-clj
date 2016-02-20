(ns betbot.telegram.logic
  "Describes how bot should handle incomming messages"
  (:require [taoensso.timbre :as log]
            [cheshire.core :as json]
            [betbot.dao.bets :as bets]
            [clojure.core.match :refer [match]]
            [betbot.telegram.api :as api]))

(defn update-handler [update]
  (log/debug "Got update from bot:\n"
             (json/generate-string update {:pretty true}))
  (let [chat (-> update :message :chat)
        text (-> update :message :text)]
    ;; This handles 'command-recognition'
    (match [text]
           ["/events"] (log/debug "TODO!")
           ["/create"] (log/debug "TODO!")
           ["yo"] (api/send-message (:id chat)
                    (bets/create 3 111 2))
           :else (api/send-message (:id chat) "I don\'t know this command 🙈"))))

