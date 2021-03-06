(ns betbot.utils.helpers
  (:require [clojure.string :as string]
            [goog.dom :as dom]))

(defn read-json
  "Parses json string as clojure object"
  [string]
  (let [obj    (.parse js/JSON string)
        result (js->clj obj :keywordize-keys true)]
    result))

(defn write-json
  "Writes map as json string"
  [value]
  (let [obj    (clj->js value)
        result (.stringify js/JSON obj)]
    result))