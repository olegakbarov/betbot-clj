(ns betbot.views
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [subscribe dispatch]]

            [betbot.routes :refer [url-for]]
            [betbot.views.navbar :refer [navbar]]
            [betbot.views.pages :refer [active-page]]))

(defn root-component []
  [:div
   [navbar]
   [active-page]])
