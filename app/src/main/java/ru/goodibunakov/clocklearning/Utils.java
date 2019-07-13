/*
 * Copyright (C) 2014 TripAdvisor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author ksarmalkar 7/28/2014
 */

package ru.goodibunakov.clocklearning;

import static java.lang.Math.abs;

/**
 * Created by ksarmalkar on 4/30/14.
 */
class Utils {

    private static boolean shouldMoveClockwise(int oldValue, int newValue) {
        int dist = abs(newValue - oldValue);
        int direction = oldValue < newValue ? 1 : -1;
        direction = dist < CircularClockSeekBar.TOTAL_DEGREES_INT / 2 ? direction : -direction;
        return direction == 1;
    }

    private static int getDistanceTo(int oldValue, int newValue) {
        int totalDegrees = CircularClockSeekBar.TOTAL_DEGREES_INT;
        boolean isClockWise = shouldMoveClockwise(oldValue, newValue);
        int dist = (newValue - oldValue) % totalDegrees;

        if (isClockWise) {
            if (abs(dist) > totalDegrees / 2) {
                dist = (totalDegrees - oldValue) + newValue;
            } else {
                dist = newValue - oldValue;
            }
        } else {
            if (abs(dist) > totalDegrees / 2) {
                dist = totalDegrees - (newValue - oldValue);
                dist = -dist;
            } else {
                dist = newValue - oldValue;
            }
        }
        return dist;
    }

    static int getDelta(int oldDegrees, int newDegrees) {
        if ((oldDegrees == CircularClockSeekBar.TOTAL_DEGREES_INT && newDegrees == 0) || (newDegrees == CircularClockSeekBar.TOTAL_DEGREES_INT && oldDegrees == 0)
                || (oldDegrees == 0 && newDegrees == 0)) {
            // dont worry about delta for this condition as this basically means they are same.
            // we have this granular values when user touches/scrolls
            return 0;
        }
        return getDistanceTo(oldDegrees, newDegrees);
    }
}
