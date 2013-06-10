(defproject dxfoil_heroku "0.1.0" 
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.postgresql/postgresql "9.2-1003-jdbc4"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [ring/ring-core "1.1.8"]
                 [ring/ring-jetty-adapter "1.1.8"]
                 [compojure "1.1.5"]]
  :web-content "resources"
  :profiles {:dev
             {:dependencies
              [[clj-stacktrace "0.2.4"]]}}
  :repositories {"sonatype-oss-public"
                 "https://oss.sonatype.org/content/groups/public/"}
  :description "Template for wing foam core maker on Heroku.")
