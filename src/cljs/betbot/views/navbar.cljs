(ns betbot.views.navbar
  (:require [re-frame.core :refer [subscribe dispatch]]
            [betbot.routes :refer [url-for]]))

(defn navbar []
  (let [profile (subscribe [:profile])]
    (fn []
      [:div.ui.attached.stackable.menu
       {:style {:margin-bottom "2em"}}
       [:div.ui.container
        [:a.item {:href (url-for :home-page)}
         [:i.octicon.octicon-broadcast {:style {:margin-right 5}}]
         "BetBot"]

        [:div.right.menu
         [:a.ui.item {:href (url-for :about-page)}
          "About"]

         (if @profile
           [:a.ui.item {:href (url-for :profile-page)}
            "Profile"])

         (if @profile
           [:a.ui.item {:href (url-for :home) :on-click #(dispatch [:sign-out])}
            "Sign Out"]
           [:a.ui.item {:href "/api/auth"}
            "Sign In"])]]])))