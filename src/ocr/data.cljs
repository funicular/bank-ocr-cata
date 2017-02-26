(ns ocr.data
  (:require [ocr.protocols :as p]
            [cljs.nodejs :as nodejs]
            [clojure.string :as str]
            [cljs.test :refer-macros [deftest is testing run-tests] :as t]
            ))

(nodejs/enable-util-print!)

(defn ocr-new-lines-data [seed]

  (mapv (comp vec seq) (str/split-lines seed)))

(defn data-consistency-checker [parsed-data]
  (let [line-chars-count (map count parsed-data)]
    (when-not (or (= [27 27 27] line-chars-count)
                  (= [31 31 31] line-chars-count)))
    (throw 
     (ex-info "data doesn't conform schema" 
              {:causes (line "each line should contain 27 chars or 31 chars, current parsed data has " line-chars-count)}))))

(defn new-ocr-reader [seed]
  (reify
    p/OcrReader
    (read [_]
      (data-consistency-checker
       (ocr-new-lines-data seed)))))



