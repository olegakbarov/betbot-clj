(ns betbot.server
  "Responsible for starting application from command line"
  (:require [taoensso.timbre :as log]
            [ring.adapter.jetty :refer [run-jetty]]
            [environ.core :refer [env]]

            [betbot.handler :refer [app]]
            [betbot.telegram.polling :as telegram-polling]
            [betbot.scraper.core :as scraper]
            [betbot.telegram.api :as telegram-api])
  (:gen-class))

(def required-keys [:host
                    :database-url
                    :telegram-token])

(def port (Integer/parseInt (env :port "3000")))

(defn require-env! [vars]
  "Checks that all variables with provided names are present in env"
  (let [missing (remove #(env %) vars)]
    (when (seq missing)
      (doseq [var missing] (log/error "Variable" var "was not provided"))
      (System/exit 0))))

(defn -main [& args]
  (require-env! required-keys)
  (if (env :dev)
    (telegram-polling/start!)
    (telegram-api/set-webhook (str (env :host) "/api/telegram/" (env :telegram-token))))
  (scraper/launch)
  (run-jetty app {:port port :join? false}))
