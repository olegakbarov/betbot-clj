(ns betbot.handler
  "Root handler for HTTP server"
  (:require [environ.core :refer [env]]
            [compojure.core :refer [GET POST PUT DELETE defroutes context]]
            [compojure.route :refer [not-found resources]]

            [ring.util.response :refer [redirect]]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [prone.middleware :refer [wrap-exceptions]]

            [betbot.dao.events :as events]
            [betbot.layout :as layout]))

(defroutes routes
  (GET "/" [] layout/reagent-page)

  (context "/api" []
    (GET "/events" {query :params} (events/search query))
    (POST "/events" {event-param :body} (events/create event-param))

    (context "/:id" [id]
      (GET "/" [] (events/find-one id))
      (PUT "/" {event :body} (events/update id event))
      (DELETE "/" [] (events/delete id))))

  ; catch-all handler to allow client-side routing
  (GET "/*" [] layout/reagent-page))

(def app
  ;; #'routes expands to (var routes) so that when we reload our code,
  ;; the server is forced to re-resolve the symbol in the var
  ;; rather than having its own copy. When the root binding
  ;; changes, the server picks it up without having to restart.
  (let [handler (-> #'routes
                    (wrap-json-response)
                    (wrap-json-body {:keywords? true :bigdecimals? true})
                    (wrap-defaults api-defaults))]
    (if (env :dev)
      (-> handler
          wrap-exceptions
          wrap-reload)
      handler)))
