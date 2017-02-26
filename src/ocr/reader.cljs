(ns ocr.reader
  (:require [ocr.protocols :as p]
            [clojure.string :as str]))

(defn ocr-new-lines-data [seed]
  (mapv (comp vec seq) (str/split-lines seed)))

(defn data-consistency-checker [parsed-data]
  (let [line-chars-count (mapv count parsed-data)]
    (when-not (or (= [27 27 27] line-chars-count)
                  (= [30 30 30] line-chars-count))
      (throw 
       (ex-info "data doesn't conform schema" 
                {:causes (str "each line should contain 27 chars or 30 chars, current parsed data has " line-chars-count)})))
    parsed-data))

(defn new-ocr-reader [seed]
  (reify
    p/OcrReader
    (read [_]
      (data-consistency-checker
       (ocr-new-lines-data seed)))))
