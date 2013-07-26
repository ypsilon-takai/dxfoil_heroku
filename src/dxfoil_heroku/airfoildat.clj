(ns dxfoil-heroku.airfoildat
  (:require [clojure.java.io :as io]
            [clojure.string :as st]))

;; Airfoil dat file reader
(defn- read-datfile 
  "Read airfoil dat data ande returns airfoil map data.
   It's not countains upper nor lower surface data yet."
  [datfile-name]
  (with-open [rdr (io/reader datfile-name)]
    (reduce (fn [foil-data line]
              (let [blank (re-matches #"^\s*$" line)
                    [_ x y :as datline] (re-matches #"^ *(\d+\.\d+) +(\d+\.\d+) *$" line)]
                (cond blank
                      ,,foil-data
                      datline
                      ,,(update-in foil-data [:points] conj [x y])
                      :t
                      ,,(assoc foil-data :airfoilinfo line))))
            {:airfoilinfo "unknown" :points []}
            (line-seq rdr))))


(defn- slice-into-two 
  "Take hall foil data and slice it into upper surface and lower
  surface datas."
  [foil-data]
  (reduce (fn [{prev :prev top :top bottom :bottom :as m} [x1 y1 :as point]]
            (let [x1val (Double/parseDouble x1)]
              (if (< x1val prev)
                {:prev x1val
                 :top (conj top point)
                 :bottom [point]}
                {:prev x1val
                 :top top
                 :bottom (conj bottom point)})))
          {:prev 1 :top [] :bottom nil}
          foil-data))

;; D:/userdata/q3197c/Desktop/testfoil.dat

(defn read-airfoil-data
  "Read airfoil dat file and returns map which includes:
   - Airfoil information if provided
   - Hole airfoil points data as :points
   - Upper surface points data as :upper
   - Lowre surface points data as :lower"
  [airfoli-file-name]
  (let [filedata (read-datfile airfoli-file-name)
        {:keys [top bottom]} (slice-into-two (:points filedata))]
    (assoc filedata
      :upper top
      :lower bottom)))


(defn write-airfoil-data
  "Not created yet. May be write proper dat data."
  [{:keys [:airfoilinfo :points]}]
  nil)
