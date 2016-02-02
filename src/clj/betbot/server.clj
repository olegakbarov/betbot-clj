(ns betbot.server
  (:require [taoensso.timbre :as log]
            [ring.adapter.jetty :refer [run-jetty]]
            [environ.core :refer [env]]

            [betbot.handler :refer [app]])
  (:gen-class))

(def required-keys [])

(def port (Integer/parseInt (env :port "3000")))

(defn require-env! [vars]
  "Checks that all variables with provided names are present in env"
  (let [missing (remove #(env %) vars)]
    (when (seq missing)
      (doseq [var missing] (log/error "Variable" var "was not provided"))
      (System/exit 0))))

(defn -main [& args]
  (require-env! required-keys)
  (run-jetty app {:port port :join? false}))
