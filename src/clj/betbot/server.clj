(ns betbot.server
  "Responsible for starting application from command line"
  (:gen-class)
  (:require [clojure.java.io :as io]
            [taoensso.timbre :as log]
            [ring.adapter.jetty :refer [run-jetty]]
            [omniconf.core :as cfg]

            [betbot.handler :refer [app]]
            [betbot.dao.models :as models]
            [betbot.telegram.polling :as telegram-polling]))

(cfg/define {:dev {:description "Environment mode"
                   :type :boolean
                   :default false}
             :port {:description "HTTP port"
                    :type :number
                    :default 3000}
             :host {:description "Where service is deployed"
                    :type :string
                    :required true}
             :database-url {:description "URL to connect to Postgres DB"
                            :type :string
                            :default "postgres://localhost:5432/betbot"
                            :secret true}
             :telegram-token {:description "Token to connect to Telegram API"
                              :type :string
                              :required true
                              :secret true}})

(defn init []
  (cfg/verify :quit-on-error true)
  (models/init-db)
  (telegram-polling/start!))

(defn ring-init []
  (let [local-config "dev-config.edn"]
    (if (.exists (io/as-file local-config))
      (cfg/populate-from-file local-config)
      (log/warn "Can't find local dev configuration file" local-config))
    (init)))

(defn -main [& args]
  (cfg/populate-from-env)
  (cfg/populate-from-cmd args)
  (init)
  (log/info "Starting server")
  (run-jetty app {:port (cfg/get :port) :join? false}))
