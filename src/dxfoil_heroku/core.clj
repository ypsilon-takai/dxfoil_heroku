(ns dxfoil-heroku.core
  (:use [compojure.core :only [defroutes GET POST]])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.adapter.jetty :as ring]
            [ring.util.response :as resp]
            [ring.middleware.multipart-params.temp-file :as tmp-file]
            ))

(defroutes routes
  (GET "/" [] (resp/redirect "/dxfoil.html"))
  (GET "/upload/:filename" [filename]
       (println filename))
  (POST "/senddata" {params :params :as all}
        (do (println params)
            (str "<html><body><textarea>somefilename.dxf</textarea></body></html>")
            ;;(str "<a href=\"here\"> here </a>")
            ))
  (route/resources "/")
  (route/files "/")
  (route/not-found "NOT FOUND"))

(def app
  (handler/site  routes))

;; for on the fly
;; (use 'ring.adapter.jetty)
;; (ring/run-jetty app {:port 8080})

(defn -main []
  (ring/run-jetty  app {:port 8080 :join? false}  ))
