/**
 * Copyright 2013 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidtransfuse.gen.proxy;

public class MockDelegate implements MockInterface, SecondMockInteface {

    private boolean executed = false;
    private String valueOne;
    private String passThroughValue;
    private int secondValue = 0;

    @Override
    public void execute() {
        executed = true;
    }

    @Override
    public String getValue() {
        return VirtualProxyGeneratorTest.TEST_VALUE;
    }

    @Override
    public void setValue(String value) {
        valueOne = value;
    }

    @Override
    public void setValue(int value) {
        this.secondValue = value;
    }

    @Override
    public String passThroughValue(String input) {
        passThroughValue = input;
        return VirtualProxyGeneratorTest.TEST_VALUE;
    }

    public boolean primitiveCall() {
        return true;
    }

    public boolean validate(String inputOne, String inputTwo, int secondValue) {
        return executed && inputOne.equals(valueOne) && inputTwo.equals(passThroughValue) && this.secondValue == secondValue;
    }
}