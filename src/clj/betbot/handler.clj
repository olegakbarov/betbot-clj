(ns betbot.handler
  "Root handler for HTTP server"
  (:require [environ.core :refer [env]]
            [compojure.core :refer [GET POST PUT DELETE defroutes context]]
            [compojure.route :refer [not-found resources]]

            [ring.util.response :refer [redirect]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [prone.middleware :refer [wrap-exceptions]]

            [betbot.util.clj-time-cheshire]                 ; adds support for DateTime serialization
            [betbot.dao.events :as events]
            [betbot.layout :as layout]))

(defroutes routes
  (GET "/" [] layout/reagent-page)

  (context "/api" []
    (context "/events" []
      (GET "/" {query :params} (events/search query))
      (POST "/" {event-param :body} (events/create event-param))

      (context "/:id" [id]
        (GET "/" [] (events/find-one id))
        (PUT "/" {event :body} (events/update-one id event))
        (DELETE "/" [] (events/delete id)))))

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
