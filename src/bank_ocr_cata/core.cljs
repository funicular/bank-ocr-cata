(ns bank-ocr-cata.core
  (:require [cljs.nodejs :as nodejs]
            [ocr.api :as ocr-api]
            [ocr.coercer :as c]
            [bank-ocr-cata.mocks.data :as md]
            [clojure.string :as str]))

(nodejs/enable-util-print!)

(defn -main []
  (do
    (println "Hello bank-ocr-cata kata!")
    (println "------------")
    (println "User story 1. "   "write a program that can take this file and parse it into actual account numbers.")
    (println "== 0123456789  =>"  (= "0123456789"
                                     (let [d (ocr-api/new-dictionary md/dictionary-raw-data)] 
                                       (ocr-api/translate md/dictionary-raw-data d))))
    (println "------------")
    (println "User story 2. "   "validate that the numbers you read are in fact valid account numbers.
 A valid account number has a valid checksum.")
    (println "VALID => 457508000: " (ocr-api/checksum? 457508000))
    (println "INVALID => 457508000: " (not (ocr-api/checksum? 664371495)))

    (println "------------")
    (println "User story 3. "   "write out a file of your findings, one for each input file")

    (let [d (ocr-api/new-dictionary md/dictionary-raw-data)] 
      (println "000000000" (= "000000000" (ocr-api/translate md/n-0 d)))
      (println "111111111 ERR" (= "111111111 ERR" (ocr-api/translate md/n-1 d)))
      (println "222222222 ERR" (= "222222222 ERR" (ocr-api/translate md/n-2 d)))
      (println "123456789" (= "123456789" (ocr-api/translate md/n-c d)))
      (println "49006771? ILL" (= "49006771? ILL" (ocr-api/translate md/ill-1 d)))
      (println "1234?678? ILL" (= "1234?678? ILL" (ocr-api/translate md/ill-2 d)))
      (println "664371495 ERR" (= "664371495 ERR" (-> 664371495
                                                      (c/num->pattern d)
                                                      (ocr-api/translate d)))))))

(set! *main-cli-fn* -main)
