(ns ocr.protocols
  (:refer-clojure :exclude [seq]))

(defprotocol OcrReader
  (read [_]))

(defprotocol OcrPattern
  (width [_])
  (height [_]))

(defprotocol OcrPos
  (row [_])
  (col [_]))

(defprotocol MatrixScan
  (view-at [_ ocr-pos ocr-patter]
    "position is a vector containing [x y] coords in root matrix 
     shape is the shape to slice or crop in root matrix")
  (shape [_])
  )

(defprotocol PaternSeq
  (seq [_ ocr-pattern]))

