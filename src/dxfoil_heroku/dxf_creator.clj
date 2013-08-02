(ns dxfoil-heroku.dxf-creator
  (:require [dxfoil-heroku.dxflib :as dxflib]))

;; draiwing coordinate
(def ^:dynamic drawing-coordinate
  {:upper-template-ypos 0
   :lower-template-ypos 60
   :airfoil-fig-ypos 120})

;; template infomation
(defn template-params
  "Default template parameters"
  ([]
     {:upper {:frontstage-height 15
              :frontstage-length 40
              :rearstage-height 15
              :rearstage-length 40}
      :lower {:frontstage-height 15
              :frontstage-length 40
              :rearstage-height 15
              :rearstage-length 40}
      :airfoil {:height 15
                :chord 100
                :hinge-pos 0.7}}))


(defn template-info
  "Returns default template information map"
  ([]
     {:owner "annonimous"
      :create-date (.format (java.text.SimpleDateFormat. "yyyy/MM/dd") (java.util.Date.))
      :filename "template"
      :filename-extention ".dxf"
      :airfoil-name "Unknown"
      :params (template-params)
      :airfoil-data nil}))


;; comment strings
(def comment-strings
  ["Created by dxFoil(j)."
   "Copyright by TAKAI, Yosiyuki. 2013."
   "dxFoil(j) is free software under the terms of the GNU General Public License."
   "See http://www.gnu.org/licenses/gpl.txt for license an waranty."])


;; utils
(defn convert-to-3d [points]
  (map #(conj % "0.0") points))

(defn format-str [dbl]
  (format "%7.6f" dbl))


;; point transformation
(defmulti scale-and-move (fn [[x _ _] _ _ _] (class x)))

(defmethod scale-and-move String [[x y z :as point] scale x-delta y-delta]
  (vector (format-str (+ x-delta (* scale (Double/parseDouble x))))
          (format-str (+ y-delta (* scale (Double/parseDouble y))))
          z))

(defmethod scale-and-move Number [[x y z :as point] scale x-delta y-delta]
  (vector (format-str (double (+ x-delta (* scale x))))
          (format-str (double (+ y-delta (* scale y))))
          (format-str (double z))))

(defn transform-data [points scale x-delta y-delta]
  (map #(scale-and-move % scale x-delta y-delta) points))




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; drawing creater
(defn create-drawing [items]
  (reduce #(dxflib/addItem %1 :entities %2)
          (dxflib/Drawing comment-strings)
          items))


;; airfoil data
(defn airfoil-part [x-offset y-offset points chord-length]
  (let [plot-data (transform-data (convert-to-3d points) chord-length x-offset y-offset)]
    (merge (dxflib/Polyline)
           {:points plot-data
            :flag 0 ;open
            :color 1
            })))

;; template base
(defn base-part [origin-x origin-y
                 {:keys [frontstage-height frontstage-length
                         rearstage-height rearstage-length]}
                 chord-length]
  (let [new-line (fn [point1 point2]
                   (merge (dxflib/Line)
                          {:points (transform-data [point1 point2]
                                                   1
                                                   origin-x origin-y)
                           :color 1}))]
    [(new-line [0, 0, 0] [0, frontstage-height, 0]) ;front line
     (new-line [0, frontstage-height, 0] [frontstage-length, frontstage-height, 0]) ;front stage
     (new-line [0, 0, 0] [(+ frontstage-length chord-length rearstage-length), 0, 0]) ; base line
     (new-line [(+ frontstage-length chord-length rearstage-length), 0, 0]
               [(+ frontstage-length chord-length rearstage-length), rearstage-height, 0]) ; rear line
     (new-line [(+ frontstage-length chord-length rearstage-length), rearstage-height, 0]
               [(+ frontstage-length chord-length), rearstage-height, 0]) ; rear stage
   ]))

;; marker
(defn marker-part [origin-x origin-y
                   front-marker-x
                   rear-marker-x
                   hinge-marker-x]
  (let [new-line (fn [point1 point2]
                   (merge (dxflib/Line)
                          {:points (transform-data [point1 point2]
                                                   1
                                                   origin-x origin-y)
                           :color 1}))]
    [(new-line [front-marker-x, 0, 0] [front-marker-x, 10, 0])
     (new-line [rear-marker-x, 0, 0] [rear-marker-x, 10, 0])
     (new-line [hinge-marker-x, 0, 0] [hinge-marker-x, 10, 0])]))

;; create templete
(defn create-template [template-info]
  (let [airfoil-upper (airfoil-part (get-in template-info [:params :upper :frontstage-length])
                                    (+ (:upper-template-ypos drawing-coordinate)
                                       (get-in template-info [:params :airfoil :height]))
                                    (get-in template-info [:airfoil-data :upper])
                                    (get-in template-info [:params :airfoil :chord]))
        airfoil-lower (airfoil-part (get-in template-info [:params :lower :frontstage-length])
                                    (+ (:lower-template-ypos drawing-coordinate)
                                       (get-in template-info [:params :airfoil :height]))
                                    (get-in template-info [:airfoil-data :upper])
                                    (get-in template-info [:params :airfoil :chord]))
        template-base-upper (base-part 0
                                       (:upper-template-ypos drawing-coordinate)
                                       (get-in template-info [:params :upper])
                                       (get-in template-info [:params :airfoil :chord]))
        template-base-lower (base-part 0
                                       (:lower-template-ypos drawing-coordinate)
                                       (get-in template-info [:params :lower])
                                       (get-in template-info [:params :airfoil :chord]))]
    (-> (concat (conj template-base-upper airfoil-upper)
                (conj template-base-lower airfoil-lower))
        (create-drawing ,,)
        (dxflib/generate ,,))))




;;(def ti (merge (template-info) {:airfoil-data
;;(dxfoil-heroku.airfoildat/read-airfoil-data
;;"D:\\userdata\\q3197c\\Desktop\\ag455ct-00r.dat")}))

(spit "D:\\userdata\\q3197c\\Desktop\\ag455ct-00r.dat" (create-template ti) )
