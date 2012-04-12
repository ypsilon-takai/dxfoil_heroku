(ns dxfoil_heroku.core
  (:use [compojure.core :only [defroutes GET POST]])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.adapter.jetty :as ring]
            [ring.middleware.multipart-params.temp-file :as tmp-file]
            [hiccup.form :as form] 
            [hiccup.page :as page]
            [clojure.java.io :as io]
            [skkime_reg_maker_heroku.readwrite :as rw]))

(defn index []
  (page/html5
   [:head
    [:title "dxFoil - web"]]
   [:body
    [:div {:id "instruction"}
     [:b "Please input parameters."]]
    [:form {:action "/upload" :method "POST" :enctype "multipart/form-data"}
     [:table ]
     (form/label "upllabel" "Airfoil dat file: ")
     (form/file-upload "airfoil")
     (form/label "upllabel" "Airfoil dat file: ")
     (form/file-upload "airfoil")
     (form/label "upllabel" "Airfoil dat file: ")
     (form/file-upload "airfoil")
     (form/label "upllabel" "Airfoil dat file: ")
     (form/file-upload "airfoil")
     [:br]
     (form/submit-button "UPLOAD")]]))

(defn result-page [input-file]
  (let [input (slurp input-file)
        output (doall (rw/encode input-file))]
    (page/html5
     [:head
      [:title "SKKIME roma-kana registry entry maker."]]
     [:body
      [:div {:id "input" :name "input"}
       (form/text-area "input" input)
       (form/text-area "output" (apply str  output))]])))

(defroutes routes
  (GET "/" [] (index))
  (POST "/upload" {params :params}
        (result-page (:tempfile (:upload params))))
  (route/resources "/")
  (route/files "/")
  (route/not-found "NOT FOUND"))

(def app
  (handler/site  routes))

;; for on the fly
;; (use 'ring.util.serve)
;; (serve app)
;; (stop-server)

(defn -main []
  (ring/run-jetty  app {:port 8080 :join? false}  ))
