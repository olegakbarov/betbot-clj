(ns betbot.models.events
  (:use korma.core)
  (:require [betbot.entities :as e]))

(defn create [event]
  (insert e/events
    (values event)))

(defn find-all []
  (select e/events))