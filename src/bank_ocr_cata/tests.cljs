(ns bank-ocr-cata.tests
  (:require [cljs.nodejs :as nodejs]
            [clojure.core.matrix :as m]
            [ocr.api :as ocr-api]
            [ocr.protocols :as p]
            [clojure.core.matrix :as m]
            [ocr.matrix :as ocr-m]
            [ocr.reader :as r]
            [bank-ocr-cata.mocks.data :as md]
            [clojure.string :as str]
            [cljs.test  :as t]))

;; TODO Replace by is-thown?
(t/deftest data-integrity-test
  (let [shape [27 27 27]
        check-fun #(map count (r/ocr-new-lines-data %))]
    (t/testing (str "final vector size: " shape)
      (doseq [raw [md/n-1 md/n-2 md/n-c md/ill-1 md/ill-2]]
        (t/is (= shape (check-fun raw)) raw)))))

(t/deftest matrix-tests
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
               (-> (ocr-m/view-at*  seed (ocr-m/new-pos 1 1) (ocr-m/new-pattern 2 2))
                   m/coerce)))
      (let [seed [[0 0]
                  [1 1]]]
        (t/is (= [[1]]
                 (-> (ocr-m/view-at*  seed (ocr-m/new-pos 1 1) (ocr-m/new-pattern 1 1))
                     m/coerce))))))

  (t/testing "new-pattern-seq"
    (let [scan-matrix (ocr-m/new-scan-matrix (r/new-ocr-reader md/dictionary-raw-data))
          mat-seq (ocr-m/new-pattern-seq* scan-matrix (ocr-m/new-pattern 3 3))
          ]
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
                " _|"]))
      (doseq [digit mat-seq]
        (ocr-api/display digit)))))


(t/run-tests)

