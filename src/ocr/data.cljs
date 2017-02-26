(ns ocr.data
  (:require [ocr.protocols :as p]
            [cljs.nodejs :as nodejs]
            [clojure.string :as str]
            [cljs.test :refer-macros [deftest is testing run-tests] :as t]))

(nodejs/enable-util-print!)

(def dictionary-raw-data
" _     _  _     _  _  _  _  _ 
| |  | _| _||_||_ |_   ||_||_|
|_|  ||_  _|  | _||_|  ||_| _|")

(defn ocr-new-lines-data [seed]

  (mapv (comp vec seq) (str/split-lines seed)))

(defn new-ocr-reader [seed]
  (reify
    p/OcrReader
    (read [_]
      (ocr-new-lines-data seed))))




(def n-0 " _  _  _  _  _  _  _  _  _ 
| || || || || || || || || |
|_||_||_||_||_||_||_||_||_|
")
(def n-1 "                           
  |  |  |  |  |  |  |  |  |
  |  |  |  |  |  |  |  |  |
")

(def n-2 " _  _  _  _  _  _  _  _  _ 
 _| _| _| _| _| _| _| _| _|
|_ |_ |_ |_ |_ |_ |_ |_ |_ 
")

(def n-c "    _  _     _  _  _  _  _ 
  | _| _||_||_ |_   ||_||_|
  ||_  _|  | _||_|  ||_| _|")

(def ill-1 "    _  _  _  _  _  _     _ 
|_||_|| || ||_   |  |  | _ 
  | _||_||_||_|  |  |  | _|
")

(def ill-2 "    _  _     _  _  _  _  _ 
  | _| _||_| _ |_   ||_||_|
  ||_  _|  | _||_|  ||_| _ 
")

(t/deftest integrity-data
  (let [shape [27 27 27]
        check-fun #(map count (ocr-new-lines-data %))]
    (t/testing (str "final vector size: " shape)
      (doseq [raw [n-1 n-2 n-c ill-1 ill-2]]
        (t/is (= shape (check-fun raw)) raw)))))

(t/run-tests)


