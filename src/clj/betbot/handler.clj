(ns betbot.handler
  (:require [environ.core :refer [env]]
            [compojure.core :refer [GET defroutes context]]
            [compojure.route :refer [not-found resources]]

            [ring.util.response :refer [redirect]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.json :refer [wrap-json-response]]
            [prone.middleware :refer [wrap-exceptions]]

            [betbot.constants :as constants]
            [betbot.layout :as layout]))


(defroutes routes
  (GET "/"      [] layout/reagent-page)

  (context "/api" []
    (GET "/ping" [] {:status 200 :body "pong"}))

  ; catch-all handler to allow client-side routing
  (GET "/*" [] layout/reagent-page))

(def app
  ;; #'routes expands to (var routes) so that when we reload our code,
  ;; the server is forced to re-resolve the symbol in the var
  ;; rather than having its own copy. When the root binding
  ;; changes, the server picks it up without having to restart.
  (let [handler (-> #'routes
                    (wrap-json-response)
                    (wrap-defaults site-defaults))]
    (if (env :dev)
      (-> handler
          wrap-exceptions
          wrap-reload)
      handler)))
