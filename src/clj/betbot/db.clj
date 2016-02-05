(ns betbot.db
  (:use korma.db)
  (:require [environ.core :refer [env]]))

(defdb db (postgres {:db (get env :betbot-db "betbot")
                     :user (get env :betbot-db-user "betbot")
                     :password (get env :betbot-db-pass "")
                     :host (get env :betbot-db-host "localhost")
                     :port (get env :betbot-db-port 5432)}))