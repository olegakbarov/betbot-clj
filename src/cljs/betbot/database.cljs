(ns betbot.database
  (:require [taoensso.timbre :as log]
  
            [betbot.constants :as constants]
            [betbot.utils.localstorage :as storage]
            [betbot.utils.helpers :refer [read-json]]))

(defn init-user
  "Loads user from localStorage if present"
  []
  (let [string (storage/get-item constants/localstorage-profile-key)
        result (read-json string)]
    result))

(defn sign-out
  "Removes current authorization and returns new DB instance"
  [db]
  (storage/remove-item! constants/localstorage-profile-key)
  (dissoc db :profile))

(def default-db
  {:name "re-frame"
   :active-page :home-page
   :profile (init-user)})

