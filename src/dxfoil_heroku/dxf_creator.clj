(ns dxfoil-heroku.dxf-creator
  (:require [dxfoil-heroku.dxflib :as dxflib]))




(def header-strings
  ["  999"
   "Created by dxFoil(j)."
   "  999"
   "Copyright by TAKAI, Yosiyuki. 2013."
   "  999"
   "dxFoil(j) is free software under the terms of the GNU General Public License."
   "  999"
   "See http://www.gnu.org/licenses/gpl.txt for license an waranty."
   "  0"
   "SECTION"
   "  2"
   "ENTITIES"
   ])

(def footer-strings
  ["  0"
   "ENDSEC"
   "  0"
   "EOF"])

(defn- points-to-line [point-list]
  (letfn [oneline (fn [p1 p2]
                    )]))


(defn create-dxf-data [datfilename airfoilname]
  (let [point-list (read-datfile datfilename)]
    (flatten header-string
             (points-to-line point-list)
             footer-string)))
