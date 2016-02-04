(ns betbot.entities
  (:use korma.core
        betbot.db))

(declare users events bets)

(defentity users
  (pk :id)
  (table :users)
  (has-many bets)
  (entity-fields :email :role))

(defentity events
  (pk :id)
  (table :events)
  (has-many bets)
  (entity-fields :title :start :end :category :subcategory))

(defentity bets
  (pk :id)
  (table :bets)
  (has-many users)
  (has-one events)
  (entity-fields :result :value))

;; TODO
; (defentity auth-tokens
;   (pk :id)
;   (table :auth_tokens)
;   (belongs-to users {:fk :user_id}))