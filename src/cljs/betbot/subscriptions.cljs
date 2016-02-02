(ns betbot.subscriptions
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-sub]]))

(register-sub :active-page
  (fn [db _] (reaction (:active-page @db))))

(register-sub :profile
  (fn [db _] (reaction (:profile @db))))

(register-sub :is-authorized
  (fn [db _] (reaction (boolean (:profile @db)))))