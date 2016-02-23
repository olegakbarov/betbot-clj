(ns betbot.dao.users
  "Describes how to operate with bets objects"
  (:require [betbot.dao.models :as m]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.coerce :refer [to-sql-time]]
            [korma.core :as k]
            [cheshire.core :as json]
            [taoensso.timbre :as log]))

(defn create
  "Creates new user in database"
  [user]
  (let [user-gen {:created_at (t/now)
                  :telegram_id (:id user)}
        new-user (-> user-gen
                     (merge user)
                     (dissoc :id)) ;; we have our own id's
        result (k/insert m/users (k/values new-user))]
    result))


(defn find-one
  "Find one user by id"
  [id]
  (let [result (k/select m/users
                 (k/where {:id (Integer/parseInt id)}))]
    result))


(defn find-by-telegram-id
  "Find user by Telegram id"
  [id]
  (k/select m/users (k/where {:telegram_id id})))


(defn find-or-create
  "Finds or creates user"
  [{:keys [id] :as user}]
  (let [exists (k/select m/users
                         (k/where {:telegram_id id}))]
    (if (empty? exists)
      (create user)
      (log/debug "User already exists"))))
