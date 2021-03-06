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
package org.androidtransfuse.analysis;

import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.annotations.Data;
import org.androidtransfuse.annotations.Intent;
import org.androidtransfuse.annotations.IntentFilter;
import org.androidtransfuse.annotations.IntentFilters;
import org.androidtransfuse.model.manifest.Action;
import org.androidtransfuse.model.manifest.Category;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

import static org.androidtransfuse.util.AnnotationUtil.checkBlank;
import static org.androidtransfuse.util.AnnotationUtil.checkDefault;

/**
 * Factory to build the IntentFilter metadata from an annotated class.
 *
 * @author John Ericksen
 */
public class IntentFilterFactory {

    private final Provider<org.androidtransfuse.model.manifest.IntentFilter> intentFilterProvider;
    private final Provider<Action> actionProvider;
    private final Provider<Category> categoryProvider;
    private final Provider<org.androidtransfuse.model.manifest.Data> dataProvider;

    @Inject
    public IntentFilterFactory(Provider<org.androidtransfuse.model.manifest.IntentFilter> intentFilterProvider, Provider<Action> actionProvider, Provider<Category> categoryProvider, Provider<org.androidtransfuse.model.manifest.Data> dataProvider) {
        this.intentFilterProvider = intentFilterProvider;
        this.actionProvider = actionProvider;
        this.categoryProvider = categoryProvider;
        this.dataProvider = dataProvider;
    }

    public List<org.androidtransfuse.model.manifest.IntentFilter> buildIntentFilters(ASTType astType) {

        IntentFilters intentFilters = astType.getAnnotation(IntentFilters.class);
        IntentFilter intentFilter = astType.getAnnotation(IntentFilter.class);
        Intent intent = astType.getAnnotation(Intent.class);
        Data data = astType.getAnnotation(Data.class);

        List<org.androidtransfuse.model.manifest.IntentFilter> convertedIntentFilters = new ArrayList<org.androidtransfuse.model.manifest.IntentFilter>();

        if(intentFilters != null){
            for (IntentFilter filter : intentFilters.value()) {
                convertedIntentFilters.add(convertIntentFilter(filter));
            }
        }

        org.androidtransfuse.model.manifest.IntentFilter resultIntentFilter = null;
        if (intentFilter != null) {
            resultIntentFilter = convertIntentFilter(intentFilter);
            convertedIntentFilters.add(resultIntentFilter);
        }
        if (intent != null) {
            if (resultIntentFilter == null) {
                resultIntentFilter = intentFilterProvider.get();
                convertedIntentFilters.add(resultIntentFilter);
            }

            addIntent(intent, resultIntentFilter);
        }
        if(data != null){
            if(resultIntentFilter == null){
                resultIntentFilter = intentFilterProvider.get();
                convertedIntentFilters.add(resultIntentFilter);
            }
            addData(data, resultIntentFilter);
        }

        return convertedIntentFilters;
    }

    private org.androidtransfuse.model.manifest.IntentFilter convertIntentFilter(IntentFilter intentFilter){
        org.androidtransfuse.model.manifest.IntentFilter resultIntentFilter = intentFilterProvider.get();

        resultIntentFilter.setIcon(checkBlank(intentFilter.icon()));
        resultIntentFilter.setLabel(checkBlank(intentFilter.label()));
        resultIntentFilter.setPriority(checkDefault(intentFilter.priority(), -1));

        for (Intent intentAnnotation : intentFilter.value()) {
            addIntent(intentAnnotation, resultIntentFilter);
        }

        for (Data dataAnnotation : intentFilter.data()){
            addData(dataAnnotation, resultIntentFilter);
        }

        return resultIntentFilter;
    }

    private void addIntent(Intent intentAnnotation, org.androidtransfuse.model.manifest.IntentFilter intentFilter) {
        switch (intentAnnotation.type()) {
            case ACTION:
                Action action = actionProvider.get();
                action.setName(intentAnnotation.name());
                intentFilter.getActions().add(action);
                break;
            case CATEGORY:
                Category category = categoryProvider.get();
                category.setName(intentAnnotation.name());
                intentFilter.getCategories().add(category);
                break;
            default:
                //noop
                break;
        }
    }

    private void addData(Data dataAnnotation, org.androidtransfuse.model.manifest.IntentFilter intentFilter) {
        org.androidtransfuse.model.manifest.Data data = dataProvider.get();

        data.setHost(checkBlank(dataAnnotation.host()));
        data.setPath(checkBlank(dataAnnotation.path()));
        Integer port = checkDefault(dataAnnotation.port(), -1);
        data.setPort(port == null? null : port.toString());
        data.setMimeType(checkBlank(dataAnnotation.mimeType()));
        data.setPathPattern(checkBlank(dataAnnotation.pathPattern()));
        data.setPathPrefix(checkBlank(dataAnnotation.pathPrefix()));
        data.setScheme(checkBlank(dataAnnotation.scheme()));

        intentFilter.getData().add(data);
    }
}
