(ns ocr.coercer
  (:require [clojure.string :as str]
            [clojure.set :as set]))
(defn col->num [col]
  (int (apply str col)))

(defn col->str [col]
  (apply str col))
(defn num->col [num]
  (map int ((comp seq str) num)))


(defn num->pattern [num dict]
  (->> num
       num->col 
       (map (set/map-invert dict))
       reverse
       (reduce (fn [c  [t m b]]
                 (-> c
                     (update-in [0] conj t)
                     (update-in [1] conj m)
                     (update-in [2] conj b)
                     )) [] )
       (map #(apply str %))
       (str/join "\n")))
