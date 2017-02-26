(ns ocr.matrix
  (:require [clojure.core.matrix :as m]
            [cljs.nodejs :as nodejs]
            [ocr.protocols :as p]
            [ocr.data :as d]
            [clojure.string :as str]
            [cljs.test :refer-macros [deftest is testing run-tests] :as t]))
(nodejs/enable-util-print!)

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

(defn coerce [mat]
  (m/coerce mat))

(defn- view-at* [mat ocr-pos ocr-pattern]
  (m/submatrix mat (p/row ocr-pos) (p/width ocr-pattern) (p/col ocr-pos) (p/height ocr-pattern)))

(defn new-scan-matrix [ocr-reader]
  (let [mat (m/matrix (p/read ocr-reader))]
    (reify
      p/MatrixScan
      (view-at [_ ocr-pos ocr-pattern]
        (view-at* mat ocr-pos ocr-pattern))
      (shape [_]
        (m/shape mat)))))

(defn- pattern-seq [scan-mat ocr-pattern]
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

(defn new-pattern-seq [scan-mat ocr-pattern]  
  (pattern-seq scan-mat ocr-pattern))

(t/deftest check-matrix
  (t/testing "checking mikera funs"
    (t/is (= [4 3] (m/shape [[0 0 0]
                             [0 0 0]
                             [0 0 0]
                             [0 0 0]]))))

  (t/testing "view-at* fun"
    (let [seed (m/matrix [[0 0 0]
                          [1 1 1]
                          [2 2 2]
                          [3 3 3]])]

      (t/is (= [[1 1]
                [2 2]]
               (-> (view-at*  seed (new-pos 1 1) (new-pattern 2 2))
                   coerce)))
      (let [seed [[0 0]
                  [1 1]]]
        (t/is (= [[1]]
                 (-> (view-at*  seed (new-pos 1 1) (new-pattern 1 1))
                     coerce))))))

  (t/testing "new-pattern-seq*"
    (let [scan-matrix (new-scan-matrix (d/new-ocr-reader md/dictionary-raw-data))
          mat-seq (new-pattern-seq scan-matrix (new-pattern 3 3))]
      (t/is (= (first mat-seq)
               [" _ "
                "| |"
                "|_|"]))
      (t/is (= (second mat-seq)
               ["   "
                "  |"
                "  |"]))
      (t/is (= (last mat-seq)
               [" _ "
                "|_|"
                " _|"])))))

(t/run-tests)
