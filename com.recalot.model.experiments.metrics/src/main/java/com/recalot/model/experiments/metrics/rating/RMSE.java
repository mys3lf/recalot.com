// Copyright (C) 2016 Matthäus Schmedding
//
// This file is part of recalot.com.
//
// recalot.com is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// recalot.com is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with recalot.com. If not, see <http://www.gnu.org/licenses/>.

package com.recalot.model.experiments.metrics.rating;

import com.recalot.common.interfaces.model.experiment.RatingMetric;

/**
 * Implements root-mean-square error (RMSE)
 *
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class RMSE extends RatingMetric {

    private int count;
    private double sum;

    public RMSE() {
        count = 0;
        sum = 0;
    }

    @Override
    public void addRating(double actual, double retrieved) {
        count++;
        sum += Math.pow(Math.abs(actual -  retrieved), 2);
    }

    @Override
    public double getResult() {
        return count > 0 ? Math.sqrt(sum / count) : Double.MAX_VALUE;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
