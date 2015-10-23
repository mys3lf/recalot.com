// Copyright (C) 2015 Matthäus Schmedding
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

package com.recalot.common.builder;

/**
 * Interface which is necessary for the initialization of instance in the different Builder classes
 * Every bundle has to initialize its classes. Otherwise this bundle would need all dependencies, which is impossible.
 *
 * @author Matthäus Schmedding (info@recalot.com)
 */
public interface Initiator {
    /**
     * Initialize an object with of the given class name
     * @param className class name of the instance that should be initialized
     * @return initialized object
     */
    public Object createInstance(String className);
}
