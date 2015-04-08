(defproject rules-ex "0.1.0-SNAPSHOT"
  :description "rules engine example for POSSCON 2015"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.toomuchcode/clara-rules "0.8.7"]]
  :main ^:skip-aot rules-ex.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
