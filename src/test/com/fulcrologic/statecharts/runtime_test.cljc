(ns com.fulcrologic.statecharts.runtime-test
  (:require
    [com.fulcrologic.statecharts.simple :as simple]
    [com.fulcrologic.statecharts.runtime :as rt]
    [com.fulcrologic.statecharts.chart :as chart]
    [com.fulcrologic.statecharts.elements :as ele :refer [on-entry state transition]]
    [com.fulcrologic.statecharts.testing :as testing]
    [fulcro-spec.core :refer [=> assertions specification]]))

(specification
  "current-configuration"
  (let [chart (chart/statechart
                {:initial :s2}
                (state {:id :uber}
                  (state {:id :s1}
                    (on-entry {})
                    (transition {:event :ev1, :target :s2}))
                  (state {:id :s2})))
        {:keys [env] :as test-env} (testing/new-testing-env {:statechart chart
                                                             :session-id :session} {})]
    (testing/start! test-env)
    (assertions
      "finds the set of live states for a statechart session"
      (rt/current-configuration env :session) => #{:uber :s2})))

(specification
  "session-data"
  (let [chart (chart/statechart {:initial :s2}
                (ele/data-model {:expr {:x {:y 1}}})
                (state {:id :uber}
                  (state {:id :s1}
                    (on-entry {})
                    (transition {:event :ev1, :target :s2}))
                  (state {:id :s2})))
        {:keys [env] :as test-env} (testing/new-testing-env {:statechart chart
                                                             :session-id :session} {})]
    (testing/start! test-env)
    (assertions
      "can return the full data model value"
      (rt/session-data env :session) => {:x {:y 1}}
      "can return the data at a path"
      (rt/session-data env :session [:x :y]) => 1)))
