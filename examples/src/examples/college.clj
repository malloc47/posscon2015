(ns examples.college
  (:require [clara.rules :refer :all]
            [clara.tools.viz :refer [show-logic!] :as viz]
            [clojure.pprint :refer [pprint]])
  (:gen-class))

(defrecord Applicant [name age])
(defrecord Grade [type score])
(defrecord Parents [income alumni?])

(defrecord Accept [])
(defrecord Aid [amount])

(defrule good-enough-grades
  [Grade (= type :gpa) (> score 3.0)]
  [:or
   [Grade (= type :sat) (> score 1500)]
   [Grade (= type :act) (> score 20)]]
  =>
  (insert! (->Accept)))

(defrule rich-parents
  [Parents (> income 2000000)]
  =>
  (insert! (->Accept)))

(defrule aid-package
  [Accept]
  [Applicant (< age 26)]
  [Parents (< income 60000)]
  =>
  (insert! (->Aid 20000)))

(defquery get-acceptance
  []
  [?accept <- Accept])

(defquery get-aid
  []
  [?aid <- Aid])

(def facts
  [(->Applicant "Phil" 22)
   (->Parents 50000 false)
   (->Grade :gpa 3.1)
   (->Grade :sat 1650)])

(defn -main
  []
  (let [session
        (-> (mk-session 'examples.college :cache false)
            (insert-all facts)
            (fire-rules))]
    (-> session
        (query get-acceptance) (->> (map  :?accept))
        count pos? (->> (str "Accepted: ")) println)
    (-> session
        (query get-aid) first :?aid :amount (or 0)
        (->> (str "Aid     : ")) println)))
