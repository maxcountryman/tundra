(require '[figwheel-sidecar.repl :as r]
         '[figwheel-sidecar.repl-api :as ra]
         '[tundra.server :as server])

(ra/start-figwheel!
 {:figwheel-options {:ring-handler server/dev-handler}
  :build-ids ["dev"]
  :all-builds
  [{:id "dev"
    :figwheel true
    :source-paths ["src/clj" "src/cljs"]
    :compiler {:main 'tundra.core
               :asset-path "js"
               :output-to "resources/public/js/main.js"
               :output-dir "resources/public/js"
               :verbose true}}]})

(ra/cljs-repl)
