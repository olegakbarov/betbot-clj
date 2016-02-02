(ns betbot.handlers
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-frame.core :refer [register-handler dispatch]]
            [taoensso.timbre :as log :include-macros true]
            [cljs.core.async :refer [<!]]
            [cljs-http.client :as http]

            [betbot.utils.helpers :refer [read-json]]
            [betbot.database :as db]))

(register-handler :initialize-db
  (fn [_ _] db/default-db))

(register-handler :set-active-page
  (fn [db [_ active-page]] (assoc db :active-page active-page)))

(register-handler :sign-out
  (fn [db _] (db/sign-out db)))