(ns betbot.views.pages
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reagent.ratom :refer-macros [reaction]]))

(defn welcome []
  [:div.ui.vertical.masthead.center.aligned.segment
   {:style {:min-height "500px"}}
   [:div.ui.test.container
    [:h1.header {:style {:margin-top "2em"
                         :font-size "3em"}}
     "Welcome to BetBot"]
    [:h3 "Telegram bot for betting"]
    [:a.ui.huge.primary.button {:href "/api/auth"}
     "Place your wager"
     [:i.icon.right.arrow]]]])

(defn home []
  (let [authorized (subscribe [:is-authorized])]
    (fn []
      (if @authorized
        [welcome] ;; display something different for authorized users
        [welcome]))))

(defn about []
  [:div.ui.text.container
   [:h2 "About BetBot"]
   [:p "This is simple Telegram bot for making bets"]])

(defn active-page []
  (let [active-page (subscribe [:active-page])]
    (fn []
      [:div.ui.container
       (case @active-page
         :home-page    [home]
         :about-page   [about]

         ;; default is like 404
         [:div [:h1 "404?!"]])])))