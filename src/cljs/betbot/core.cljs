(ns ^:figwheel-no-load betbot.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [taoensso.timbre :as log :include-macros true]
              [betbot.config :as config]
              [betbot.views :as views]
              [betbot.routes :as routes]
              [betbot.handlers]
              [betbot.subscriptions]))

(when config/debug?
  (log/debug "Running app in dev mode"))

(defn mount-root []
  (reagent/render [views/root-component]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db])
  (routes/init-routes)
  (mount-root))

(init)