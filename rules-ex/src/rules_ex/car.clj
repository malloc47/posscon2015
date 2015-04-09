(ns rules-ex.car
  (:require [clara.rules :refer :all]
            [clojure.pprint :refer [pprint]])
  (:gen-class))

(defrecord Customer [name age])
(defrecord Car [year make model color])
(defrecord Policy [liability collision])

(def facts
  [(->Customer "Michael" 40 )
   (->Car 1971 "Buick" "Riviera" "red")])

(defrule basic-coverage
  [Customer (> age 16)]
  =>
  (insert! (->Policy 20000 0)))

(defrule mid-range-coverage
  [Customer (> age 25)]
  =>
  (insert! (->Policy 50000 50000)))

(defrule premium-coverage
  [Customer (> age 25)]
  [Car (> year 1980)]
  =>
  (insert! (->Policy 100000 100000)))

(defquery get-policies
  []
  [?policies <- Policy])

(defn -main
  [& args]
  (-> (mk-session 'rules-ex.car :cache false)
      (insert-all facts)
      (fire-rules)
      (query get-policies)
      (->> (map  :?policies))))
