(ns ocr.protocols)

(defprotocol OcrReader
  (read [_]))

(defprotocol OcrPattern
  (width [_])
  (height [_]))

(defprotocol OcrPos
  (row [_])
  (col [_]))

(defprotocol MatrixScan
  (view-at [_ ocr-pos ocr-patter])
  (shape [_]
    "matrix shape")
  (pattern-seq [_ ocr-patter]))
