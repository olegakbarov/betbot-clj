(ns betbot.handler
  (:require [environ.core :refer [env]]
            [compojure.core :refer [GET POST PUT DELETE defroutes context]]
            [compojure.route :refer [not-found resources]]

            [ring.util.response :refer [redirect]]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.json :refer [wrap-json-response]]
            [prone.middleware :refer [wrap-exceptions]]

            [betbot.models.events :as events]

            [betbot.constants :as constants]
            [betbot.layout :as layout]))

(defn get-all [_]
  {:status 200
   :body {:results (events/find-all)}})

;; TODO this retuns <h1>Invalid anti-forgery token</h1>
;; Dunno how to fix
(defn create-event [{event :body}]
  (let [new-event (events/create event)]
    {:status 200
     :body { :id new-event }}))

(defn get-event [id]
   ;; go to db and get one event
  )

(defn update-event [id]
   ;; go to db and update event
  )

(defn delete-event [id]
   ;; go to db and delete event
  )

(defroutes routes
  (GET "/" [] layout/reagent-page)

  (context "/api" []
    (GET "/events" [] get-all)
    (POST "/events" []  create-event)

    (context "/:id" [id]
      (GET "/" [] get-event)
      (PUT "/" [] update-event)
      (DELETE "/" [] delete-event)))

  ; catch-all handler to allow client-side routing
  (GET "/*" [] layout/reagent-page))

(def app
  ;; #'routes expands to (var routes) so that when we reload our code,
  ;; the server is forced to re-resolve the symbol in the var
  ;; rather than having its own copy. When the root binding
  ;; changes, the server picks it up without having to restart.
  (let [handler (-> #'routes
                    (wrap-json-response)
                    (wrap-defaults api-defaults))]
    (if (env :dev)
      (-> handler
          wrap-exceptions
          wrap-reload)
      handler)))
