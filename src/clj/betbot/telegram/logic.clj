(ns betbot.telegram.logic
  "Describes how bot should handle incomming messages"
  (:require [taoensso.timbre :as log]))

(defn callback [update]
  (log/debug "Got update from bot:" update))

