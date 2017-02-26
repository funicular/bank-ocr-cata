(ns ocr.api
  (:require
   [ocr.protocols :as p]
   [ocr.matrix :as m]
   [ocr.reader :as r]
   [ocr.coercer :as c]))

(defn checksum? [col*]
  (let [col* (or (and (seq? col*) col*)
                (c/num->col col*))]
    (-> (reduce (fn [c [idx v]]
                  (+ c (* v idx)))
                0 (map vector (map inc (range )) (reverse col*)))
        (mod 11)
        zero?)))

(defn translate [raw-data dict]
  (let [seq* (p/pattern-seq (m/new-scan-matrix (r/new-ocr-reader raw-data))
                            (m/new-pattern 3 3))
        res (map #(get dict % "?") seq*)
        illegible? (some (partial = "?") res)
        invalid? (not (checksum? res))]
    (str (c/col->str res) (if illegible? " ILL"
                              (if invalid?  " ERR")))))

(defn new-dictionary [raw-data]
  (let [seq* (p/pattern-seq (m/new-scan-matrix  (r/new-ocr-reader raw-data))
                            (m/new-pattern 3 3))
        digits-dictionary (zipmap seq* (range))]
    digits-dictionary))

(defn display
  "just for human eyes :- "
  [pattern]
  (println "•   •")
  (doseq [row pattern]
    (println (str " " row " ")))
  (println "•   •")
  pattern)
