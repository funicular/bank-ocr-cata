(ns bank-ocr-cata.core
  (:require [cljs.nodejs :as nodejs]
            [ocr.protocols :as p]
            [ocr.matrix :as m]
            [ocr.data :as d]
            [clojure.string :as str]
            [cljs.test  :as t]))

(nodejs/enable-util-print!)

(defn -main []
  (println "Hello world!"))

(set! *main-cli-fn* -main)

(defn display
   "just for human eyes :- "
   [pattern]
   (println "•   •")
   (doseq [r pattern]
     (println (str " " r " ")))
   (println "•   •"))

(defn read* [seed width height]
  (let [scan-matrix (m/new-scan-matrix (d/new-ocr-reader seed))
        ocr-pattern (m/new-pattern width height)]
    (m/new-pattern-seq scan-matrix ocr-pattern)))

(defn digits-dictionary [seq*]
  (zipmap seq* (range)))

(defn translate [seq* dict]
  (map #(get dict % "?") seq*))

(defn parse-digits [raw-data dict]
  (let [seq* (read* raw-data 3 3)]  
    (translate seq* dict)))

(defn new-dictionary [raw-data ]
  (let [seq* (read* raw-data 3 3)]
    (digits-dictionary seq*)))


(t/deftest check-reading
  (t/testing "with dictionary data"
    (let [mat-seq (read* d/dictionary-raw-data 3 3)] 
      (let [n-1 ["   "
                 "  |"
                 "  |"]]
        (t/testing "1 => "(display n-1)
                   (t/is (= (nth mat-seq 1) n-1))))
      (let [n-2 [" _ "
                 " _|"
                 "|_ "]]
        (t/testing "2 => "(display n-2)
                   (t/is (= (nth mat-seq 2) n-2))))
      (let [n-9 [" _ "
                 "|_|"
                 " _|"]]
        (t/testing "9 => "(display n-9)
                   (t/is (= (nth mat-seq 9) n-9))))))
  (t/testing "parse-digits"
    (let [dict (new-dictionary d/dictionary-raw-data)]
      (t/is (= '(0 0 0 0 0 0 0 0 0) (parse-digits d/n-0 dict)))
      (t/is (= '(1 1 1 1 1 1 1 1 1) (parse-digits d/n-1 dict)))
      (t/is (= '(2 2 2 2 2 2 2 2 2) (parse-digits d/n-2 dict)))
      (t/is (= '(1 2 3 4 5 6 7 8 9) (parse-digits d/n-c dict)))
      (t/is (= '(4 9 0 0 6 7 7 1 "?") (parse-digits d/ill-1 dict)))
      (t/is (= '(1 2 3 4 "?" 6 7 8 "?") (parse-digits d/ill-2 dict))))))

;; checksum
;;account number:  3  4  5  8  8  2  8  6  5
;;position names:  d9 d8 d7 d6 d5 d4 d3 d2 d1
;;checksum calculation: (d1+2*d2+3*d3 +..+9*d9) mod 11 = 0


(t/run-tests)
