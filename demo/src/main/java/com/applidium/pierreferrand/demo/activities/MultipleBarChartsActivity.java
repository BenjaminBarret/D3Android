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

package com.applidium.pierreferrand.demo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.applidium.pierreferrand.d3library.D3View;
import com.applidium.pierreferrand.d3library.axes.AxisOrientation;
import com.applidium.pierreferrand.d3library.axes.D3Axis;
import com.applidium.pierreferrand.d3library.axes.D3FloatFunction;
import com.applidium.pierreferrand.d3library.axes.HorizontalAlignment;
import com.applidium.pierreferrand.d3library.barchart.D3StackBarChart;
import com.applidium.pierreferrand.d3library.mappers.D3FloatDataMapperFunction;
import com.applidium.pierreferrand.d3library.scale.D3Converter;
import com.applidium.pierreferrand.d3library.scale.D3LabelFunction;
import com.applidium.pierreferrand.demo.R;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MultipleBarChartsActivity extends Activity {
    private static final int MAXIMUM_SALES = 500;
    private static final int FRUIT_NUMBER = 3;
    private static final int DEFAULT_DATA_NUMBER = 4;

    @BindView(R.id.d3view) D3View view;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity);
        ButterKnife.bind(this);

        final D3Axis<Integer> salesAxis =
            new D3Axis<>(AxisOrientation.RIGHT, Integer.class)
                .domain(new Integer[]{0, FRUIT_NUMBER * MAXIMUM_SALES});

        final D3Axis<DateTime> timeAxis =
            new D3Axis<DateTime>(AxisOrientation.BOTTOM)
                .domain(new DateTime[]{new DateTime(), new DateTime().plusDays(5)})
                .ticks(DEFAULT_DATA_NUMBER + 2)
                .converter(getDateTimeConverter())
                .onScrollAction(null)
                .onPinchAction(null)
                .labelFunction(new D3LabelFunction<DateTime>() {
                    String format = "dd/MM/yyyy";

                    @Override public String getLabel(DateTime object) {
                        return object.toString(format);
                    }
                })
                .legendHorizontalAlignment(HorizontalAlignment.CENTER);

        List<D3FloatDataMapperFunction<Sales>> heights = new ArrayList<>();
        heights.add(
            new D3FloatDataMapperFunction<Sales>() {
                @Override public float compute(Sales object, int position, Sales[] data) {
                    return salesAxis.scale().value(0) - salesAxis.scale().value(object.bananas);
                }
            });
        heights.add(
            new D3FloatDataMapperFunction<Sales>() {
                @Override public float compute(Sales object, int position, Sales[] data) {
                    return salesAxis.scale().value(0) - salesAxis.scale().value(object.apples);
                }
            });
        heights.add(
            new D3FloatDataMapperFunction<Sales>() {
                @Override public float compute(Sales object, int position, Sales[] data) {
                    return salesAxis.scale().value(0) -
                        salesAxis.scale().value(object.strawberries);
                }
            });

        Sales[] sales = buildSales(DEFAULT_DATA_NUMBER);
        D3StackBarChart<Sales> stackBarChart =
            new D3StackBarChart<>(sales, FRUIT_NUMBER)
                .x(new D3FloatDataMapperFunction<Sales>() {
                    @Override public float compute(Sales object, int position, Sales[] data) {
                        return timeAxis.scale().value(object.date);
                    }
                })
                .y(new D3FloatDataMapperFunction<Sales>() {
                    @Override public float compute(Sales object, int position, Sales[] data) {
                        return salesAxis.scale().value(0);
                    }
                })
                .dataWidth(new D3FloatFunction() {
                    @Override public float getFloat() {
                        float[] range = timeAxis.range();
                        return (range[1] - range[0]) / (1.25F * timeAxis.ticks());
                    }
                })
                .colors(new int[][]{
                    new int[]{0xFFFFFF00},
                    new int[]{0xFF99CC00},
                    new int[]{0xFFD20E07}
                })
                .setClipRect(
                    new D3FloatFunction() {
                        @Override public float getFloat() {
                            return 0.05f * view.getWidth();
                        }
                    },
                    new D3FloatFunction() {
                        @Override public float getFloat() {
                            return 0F;
                        }
                    },
                    new D3FloatFunction() {
                        @Override public float getFloat() {
                            return 0.95f * view.getWidth();
                        }
                    },
                    new D3FloatFunction() {
                        @Override public float getFloat() {
                            return view.getHeight() * 0.95f;
                        }
                    }
                )
                .dataHeight(heights);
        view.add(stackBarChart);
        view.add(salesAxis);
        view.add(timeAxis);
    }

    @NonNull private D3Converter<DateTime> getDateTimeConverter() {
        return new D3Converter<DateTime>() {
            @Override public float convert(DateTime toConvert) {
                return toConvert.getMillis();
            }

            @Override public DateTime invert(float toInvert) {
                return new DateTime().withMillis((long) toInvert);
            }
        };
    }

    @NonNull private Sales[] buildSales(int size) {
        Sales[] result = new Sales[size];
        for (int i = 0; i < size; i++) {
            result[i] = new Sales(
                new DateTime().plusDays(i + 1),
                (int) (Math.random() * MAXIMUM_SALES),
                (int) (Math.random() * MAXIMUM_SALES),
                (int) (Math.random() * MAXIMUM_SALES)
            );
        }
        return result;
    }

    static class Sales {
        @NonNull final DateTime date;
        final int apples;
        final int bananas;
        final int strawberries;

        private Sales(
            @NonNull DateTime date, int soldApples, int soldBananas, int soldStrawberries
        ) {
            this.date = date;
            apples = soldApples;
            bananas = soldBananas;
            strawberries = soldStrawberries;
        }
    }

    @Override protected void onResume() {
        super.onResume();
        view.onResume();
    }

    @Override protected void onPause() {
        super.onPause();
        view.onPause();
    }
}
