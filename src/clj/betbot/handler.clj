(ns betbot.handler
  "Root handler for HTTP server"
  (:require [environ.core :refer [env]]
            [taoensso.timbre :as log]
            [compojure.core :refer [GET POST PUT DELETE defroutes context]]
            [compojure.route :refer [not-found resources]]

            [ring.util.response :refer [redirect]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [prone.middleware :refer [wrap-exceptions]]

            [betbot.util.clj-time-cheshire]                 ; adds support for DateTime serialization
            [betbot.dao.events :as events]
            [betbot.layout :as layout]
            [betbot.telegram.logic :as telegram-logic]))

(defroutes routes
  (GET "/" [] layout/reagent-page)

  (context "/api" []
    (POST "/telegram/:token/" {update :body} (telegram-logic/update-handler update))

    (context "/events" []
      (GET "/" {query :params}
        (let [result (events/search query)]
          (if (empty? result)
             {:status 404
              :body "No eventh with this id"}
             {:status 200
              :body result})))

      (POST "/" {event-param :body}
         (let [result (events/create event-param)]
           (if (empty? result)
             {:status 500
              :body "Can't create event"}
             {:status 201
              :body result})))

      (context "/:id" [id]
        (GET "/" []
           (let [result (events/find-one id)]
              (if (empty? result)
               {:status 404
                :body "Can't find the event"}
               {:status 200
                :body result})))

        (PUT "/" {event :body}
           (let [result (events/update-one id event)]
             (if (not= result 1)
               {:status 500
                :body "Bad request"}
               {:status 200
                :body (str "Succesfully updated sting with id " id)})))

        (DELETE "/" []
           (let [result (events/delete id)]
             (if (not= result 1)
                {:status 500
                 :body "Bad request"}
                {:status 200
                 :body (str "Succesfully deleted event with id " id)}))))))

  ; catch-all handler to allow client-side routing
  (GET "/*" [] layout/reagent-page))

(def app
  ;; #'routes expands to (var routes) so that when we reload our code,
  ;; the server is forced to re-resolve the symbol in the var
  ;; rather than having its own copy. When the root binding
  ;; changes, the server picks it up without having to restart.
  (let [defaults (assoc-in site-defaults [:security :anti-forgery] false)
        handler (-> #'routes
                    (wrap-json-response)
                    (wrap-json-body {:keywords? true :bigdecimals? true})
                    (wrap-defaults defaults))]
    (if (env :dev)
      (-> handler
          wrap-exceptions
          wrap-reload)
      handler)))
