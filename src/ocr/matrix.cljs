(ns ocr.matrix
  (:require [clojure.core.matrix :as m]
            [ocr.protocols :as p]))

(defn new-pos [row* col*]
  (reify
    p/OcrPos
    (row [_] row*)
    (col [_] col*)))

(defn new-pattern [w* h*]
  (reify
    p/OcrPattern
    (width [_] w*)
    (height [_] h*)))

(defn view-at* [mat ocr-pos ocr-pattern]
  (m/submatrix mat (p/row ocr-pos) (p/width ocr-pattern) (p/col ocr-pos) (p/height ocr-pattern)))

(defn new-pattern-seq* [scan-mat ocr-pattern]  
  (let [[height* width*] (p/shape scan-mat)]
    (loop [seq* '()
           patterns-count (count (partition (p/width ocr-pattern) (range width*)))]
      (if (pos? patterns-count)
        (let [ocr-pos (->> (* (dec patterns-count) (p/width ocr-pattern))
                           (new-pos 0))
              pattern-value (->> (p/view-at scan-mat ocr-pos ocr-pattern)
                                 ;; following for human readable
                                 (mapv #(apply str %)))]
          (recur (conj seq* pattern-value) (dec patterns-count)))
        seq*))))

(defn new-scan-matrix [ocr-reader]
  (let [mat (m/matrix (p/read ocr-reader))]
    (reify
      p/MatrixScan
      (view-at [_ ocr-pos ocr-pattern]
        (view-at* mat ocr-pos ocr-pattern))
      (shape [_]
        (m/shape mat))
      (pattern-seq [this ocr-pattern]
        (new-pattern-seq* this ocr-pattern)))))
