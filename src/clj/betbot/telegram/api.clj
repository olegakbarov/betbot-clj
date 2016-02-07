(ns betbot.telegram.api
  (:require [taoensso.timbre :as log]
            [environ.core :refer [env]]
            [clj-http.client :as http]))

(def base-url "https://api.telegram.org/bot")

(def token (env :telegram-token))

(defn get-updates
  "Receive updates from Bot via long-polling endpoint"
  [{:keys [limit offset timeout]}]
  (let [url (str base-url token "/getUpdates")
        query {:timeout (or timeout 1)
               :offset  (or offset 0)
               :limit   (or limit 100)}
        resp (http/get url {:as :json :query-params query})]
    (-> resp :body :result)))