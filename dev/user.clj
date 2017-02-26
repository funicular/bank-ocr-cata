(ns user
  (:use [figwheel-sidecar.repl-api] ))

(defn dev []
  (start-figwheel!)
  (println "Type in a new terminal: \"node target/server_dev/bank_ocr_cata.js\" " )
  (println "... and then type in the repl: \"(cljs-repl)\" " ))

(println "To start development type: (dev)")
