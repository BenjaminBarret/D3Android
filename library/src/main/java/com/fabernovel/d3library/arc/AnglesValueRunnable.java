/*
 * Copyright 2017, Fabernovel Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fabernovel.d3library.arc;

import android.support.annotation.NonNull;

import com.fabernovel.d3library.threading.ThreadPool;
import com.fabernovel.d3library.threading.ValueRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class AnglesValueRunnable<T> extends ValueRunnable<Angles> {
    private static final String DATA_ERROR = "Data should not be null.";
    private static final String SUM_WEIGHT_ERROR = "Sum of weight must be different from 0";
    private static final float CIRCLE_ANGLE = 360F;
    private final FirstHalfAnglesRunnable firstHalfAnglesRunnable;
    private final LastHalfAnglesRunnable lastHalfAnglesRunnable;

    @NonNull private final D3Arc<T> arc;
    @NonNull private final List<Callable<Object>> tasks;

    public AnglesValueRunnable(@NonNull D3Arc<T> arc) {
        this.arc = arc;
        tasks = new ArrayList<>();
        firstHalfAnglesRunnable = new FirstHalfAnglesRunnable();
        lastHalfAnglesRunnable = new LastHalfAnglesRunnable();
    }

    void setDataLength(int length) {
        value = new Angles(length);
    }

    @Override protected void computeValue() {
        value = computeStartAngle();
    }

    private Angles computeStartAngle() {
        if (arc.data == null) {
            throw new IllegalStateException(DATA_ERROR);
        }
        final float[] computedWeights = arc.weights();
        float totalWeight = 0F;
        for (int i = 0; i < arc.data.length; i++) {
            totalWeight += computedWeights[i];
        }
        if (totalWeight == 0F) {
            throw new IllegalStateException(SUM_WEIGHT_ERROR);
        }

        tasks.clear();
        firstHalfAnglesRunnable.setParameters(computedWeights, value, totalWeight);
        tasks.add(Executors.callable(firstHalfAnglesRunnable));
        lastHalfAnglesRunnable.setParameters(computedWeights, value, totalWeight);
        tasks.add(Executors.callable(lastHalfAnglesRunnable));
        ThreadPool.executeOnSecondaryPool(tasks);
        return value;
    }

    private class FirstHalfAnglesRunnable implements Runnable {
        private float[] computedWeights;
        private Angles angles;
        private float totalWeight;

        private void setParameters(
            float[] computedWeights, Angles angles, float totalWeight
        ) {
            this.computedWeights = computedWeights;
            this.angles = angles;
            this.totalWeight = totalWeight;
        }

        @Override public void run() {
            if (arc.data == null) {
                throw new IllegalStateException(DATA_ERROR);
            }

            angles.startAngles[0] = (arc.startAngle.getFloat()) % CIRCLE_ANGLE;
            if (angles.startAngles[0] < 0.0F) {
                angles.startAngles[0] += CIRCLE_ANGLE;
            }
            angles.drawAngles[0] = (CIRCLE_ANGLE - arc.data.length * arc.padAngle)
                * computedWeights[0] / totalWeight;
            for (int i = 1; i < arc.data.length / 2; i++) {
                angles.startAngles[i] = (angles.startAngles[i - 1] + angles.drawAngles[i - 1]
                    + arc.padAngle) % CIRCLE_ANGLE;
                if (angles.startAngles[i] < 0.0F) {
                    angles.startAngles[i] += CIRCLE_ANGLE;
                }
                angles.drawAngles[i] = (CIRCLE_ANGLE - computedWeights.length * arc.padAngle) *
                    computedWeights[i] / totalWeight;
            }
        }
    }

    private class LastHalfAnglesRunnable implements Runnable {
        private float[] computedWeights;
        private Angles angles;
        private float totalWeight;

        private void setParameters(
            float[] computedWeights, Angles angles, float totalWeight
        ) {
            this.computedWeights = computedWeights;
            this.angles = angles;
            this.totalWeight = totalWeight;
        }

        @Override public void run() {
            if (arc.data == null) {
                throw new IllegalStateException(DATA_ERROR);
            }

            angles.drawAngles[arc.data.length - 1] = (CIRCLE_ANGLE - arc.data.length * arc
                .padAngle)
                * computedWeights[arc.data.length - 1] / totalWeight;
            angles.startAngles[arc.data.length - 1] = (arc.startAngle.getFloat() - arc
                .padAngle - angles.drawAngles[arc.data.length - 1]) % CIRCLE_ANGLE;
            if (angles.startAngles[arc.data.length - 1] < 0.0F) {
                angles.startAngles[arc.data.length - 1] += CIRCLE_ANGLE;
            }
            for (int i = arc.data.length - 2; i >= arc.data.length / 2; i--) {
                angles.drawAngles[i] = (CIRCLE_ANGLE - computedWeights.length * arc.padAngle) *
                    computedWeights[i] / totalWeight;

                angles.startAngles[i] = (angles.startAngles[i + 1] - angles.drawAngles[i]
                    - arc.padAngle) % CIRCLE_ANGLE;
                if (angles.startAngles[i] < 0.0F) {
                    angles.startAngles[i] += CIRCLE_ANGLE;
                }
            }
        }
    }
}
