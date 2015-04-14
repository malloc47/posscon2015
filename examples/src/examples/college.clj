(ns examples.college
  (:require [clara.rules :refer :all]
            [clara.tools.viz :as viz]
            [clara.tools.inspect :as inspect])
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

(def sample-facts
  #{(->Applicant "Phil" 22)
    (->Parents 50000 false)
    (->Grade :gpa 3.1)
    (->Grade :sat 1650)})

(def sample-names
  ["James" "Robert" "John" "Michael" "David" "William" "Richard" "Thomas" "Charles" "Gary" "Larry" "Ronald" "Joseph" "Donald" "Kenneth" "Steven" "Dennis" "Paul" "Stephen" "George" "Daniel" "Edward" "Mark" "Jerry" "Gregory" "Bruce" "Roger" "Douglas" "Frank" "Terry" "Raymond" "Timothy" "Lawrence" "Gerald" "Wayne" "Anthony" "Peter" "Patrick" "Danny" "Walter" "Alan" "Willie" "Jeffrey" "Carl" "Harold" "Arthur" "Henry" "Jack" "Dale" "Johnny" "Roy" "Ralph" "Joe" "Albert" "Jimmy" "Billy" "Eugene" "Glenn" "Stanley" "Harry" "Samuel" "Howard" "Phillip" "Bobby" "Christopher" "Louis" "Andrew" "Russell" "Craig" "Randall" "Allen" "Kevin" "Barry" "Frederick" "Ronnie" "Leonard" "Keith" "Brian" "Randy" "Ernest" "Scott" "Fred" "Steve" "Martin" "Francis" "Melvin" "Rodney" "Eddie" "Norman" "Lee" "Earl" "Marvin" "Tommy" "Clarence" "Alfred" "Curtis" "Eric" "Theodore" "Clifford" "Linda" "Mary" "Patricia" "Barbara" "Susan" "Nancy" "Deborah" "Sandra" "Carol" "Kathleen" "Sharon" "Karen" "Donna" "Brenda" "Margaret" "Diane" "Pamela" "Janet" "Shirley" "Carolyn" "Judith" "Janice" "Cynthia" "Elizabeth" "Judy" "Betty" "Joyce" "Christine" "Cheryl" "Gloria" "Beverly" "Martha" "Bonnie" "Catherine" "Dorothy" "Rebecca" "Marilyn" "Kathy" "Jane" "Joan" "Peggy" "Gail" "Virginia" "Connie" "Ann" "Kathryn" "Diana" "Jean" "Ruth" "Helen" "Frances" "Wanda" "Phyllis" "Paula" "Jacqueline" "Rita" "Alice" "Katherine" "Debra" "Elaine" "Vicki" "Sherry" "Laura" "Jo" "Theresa" "Ellen" "Joanne" "Marsha" "Rose" "Sheila" "Suzanne" "Marie" "Maria" "Doris" "Cathy" "Lynn" "Marcia" "Sally" "Darlene" "Charlotte" "Teresa" "Denise" "Lois" "Anne" "Constance" "Evelyn" "Glenda" "Sarah" "Maureen" "Dianne" "Eileen" "Irene" "Anna" "Victoria" "Jeanne" "Roberta" "Sylvia" "Joann" "Anita" "Sue"])

(defn fact-generator
  []
  #{(->Applicant (rand-nth sample-names) (+ 16 (rand-int 24)))
    (->Parents (rand-int 2000000) (rand-nth [true false]))
    (->Grade :gpa (rand 4.0))
    (->Grade :sat (+ 600 (rand-int 1800)))
    (->Grade :act (+ 1 (rand-int 35)))})

(defn run-rules-silently
  [fact-sets]
  (let [session (mk-session 'examples.college :cache false)]
    (time
     (doseq [facts fact-sets]
       (let [session (-> session
                         (insert-all facts)
                         (fire-rules))])))))

(defn run-rules
  [fact-sets]
  (let [base-session (mk-session 'examples.college :cache false)]
    (doseq [facts fact-sets]
      (time
       (let [session (-> base-session
                         (insert-all facts)
                         (fire-rules))]
         (prn facts)
         (-> session
             (query get-acceptance) (->> (map  :?accept))
             count pos? (->> (str "Accepted: ")) println)
         (-> session
             (query get-aid) first :?aid :amount (or 0)
             (->> (str "Aid     : ")) println)
         #_(inspect/explain-activations session)))
      (println))))

(defn -main
  []
  (run-rules (repeatedly 10 fact-generator)))
