(defproject dxfoil_heroku "0.1.0"
  :description "Template for wing foam core maker on Heroku."
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [postgresql/postgresql "9.1-901.jdbc4"]
                 [org.clojure/java.jdbc "0.1.1"]
                 [ring/ring-jetty-adapter "1.0.1"]
                 [compojure "1.0.1"]
                 [hiccup "1.0.0-beta1"]]
  :dev-dependencies [[clj-stacktrace "0.2.4"]
                     [ring-serve "0.1.2"]]
  :repositories  {"sonatype-oss-public"
                  "https://oss.sonatype.org/content/groups/public/"})
