(ns betbot.telegram.polling
  "Declares ways co communicate with Telegram Bot API"
  (:require [environ.core :refer [env]]
            [clojure.core.async :as a :refer [>!! <! go chan close! thread]]
            [clj-http.client :as http]
            [taoensso.timbre :as log]))

(def base-url "https://api.telegram.org/bot")

;; this holds messages from Telegram
(def message-chan (atom nil))

;; this controls if loops are rolling
(def running (atom false))

(defn callback [message]
  (log/debug "Got message from channel:" message))

(defn start!
  "Starts long-polling process"
  []
  (reset! message-chan (a/chan))
  (reset! running true)

  ;; Start infinite loop inside go-routine
  ;; that will pull messages from channel
  (go (loop []
        (callback (<! @message-chan))
        (while @running (recur))))

  ;; Start thread with polling process
  ;; that will populate channel
  (thread (loop [offset 0]
            (let [url (str base-url (env :telegram-token) "/getUpdates")
                  query {:timeout 1 :offset offset}
                  resp (http/get url {:as :json :query-params query})
                  updates (-> resp :body :result)
                  new-offset (if (empty? updates)
                               offset
                               (-> updates last :update_id inc))]
              (doseq [update updates] (>!! @message-chan update))
              (if @running (recur new-offset))))))

(defn stop!
  "Stops everything"
  []
  (reset! running false)
  (close! @message-chan))


