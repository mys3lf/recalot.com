package com.recalot.common.interfaces.controller;

/**
 * @author Matthaeus.schmedding
 */
public interface ExperimentsController extends Controller {
    public enum ExperimentsRequestAction implements RequestAction {
        GetExperiments (0),
        GetExperiment (1),
        CreateExperiment (2),
        DeleteExperiment (3),
        GetMetrics (4),
        GetMetric (5),
        GetSplitters (6),
        GetSplitter (7),
        GetExperimentConfiguration(8);

        private final int value;

        @Override
        public int getValue() {
            return this.value;
        }

        ExperimentsRequestAction(int value) {
            this.value = value;
        }
    }
}
