package com.recalot.demos.wallpaper.controller;


import com.recalot.common.GenericServiceListener;
import com.recalot.common.Helper;
import com.recalot.common.communication.*;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.exceptions.NotReadyException;
import com.recalot.common.interfaces.controller.Controller;
import com.recalot.common.interfaces.controller.RequestAction;
import com.recalot.common.interfaces.model.data.DataAccess;
import com.recalot.common.interfaces.model.data.DataInformation;
import com.recalot.common.interfaces.model.data.DataSource;
import com.recalot.common.interfaces.template.DataTemplate;
import flexjson.JSONDeserializer;
import org.osgi.framework.BundleContext;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;


/**
 * @author matthaeus.schmedding
 */
public class DataAccessController implements Controller, Closeable {

    private final BundleContext context;
    private final GenericServiceListener<DataAccess> dataAccess;
    private final GenericServiceListener<DataTemplate> dataTemplates;
    private final HashMap<String, HashMap<String, List<String>>> categories;
    private final WallpaperTemplate wallpapertemplate;

    public DataAccessController(BundleContext context) {
        this.context = context;
        this.dataAccess = new GenericServiceListener<>(context, DataAccess.class.getName());
        this.wallpapertemplate = new WallpaperTemplate();
        this.dataTemplates = new GenericServiceListener(context, DataTemplate.class.getName());
        this.categories = new HashMap<>();
        this.context.addServiceListener(dataAccess);
        this.context.addServiceListener(dataTemplates);
    }

    @Override
    public TemplateResult process(RequestAction action, String templateKey, Map<String, String> param) throws BaseException {
        TemplateResult result = null;
        DataTemplate template = null;

        try {
            template = dataTemplates.getInstance(templateKey);
            DataAccess access = dataAccess.getFirstInstance();

            DataSource dataSource = getDataSource(access, param.get(Helper.Keys.SourceId));

            if(dataSource.getState() != DataInformation.DataState.READY) throw new NotReadyException("The datasource %s is not yet ready.", dataSource.getId());

            if(!categories.containsKey(dataSource.getId())) {
                initializeCategories(dataSource, param);
            }

            switch ((WallpaperAccessAction) action) {
                case GetData: {
                    result = getData(dataSource, template, param);
                    break;
                }
                case GetCategories: {
                    result = getCategories(dataSource, template, param);
                    break;
                }
            }
        } catch (BaseException ex) {
            if (template != null) result = template.transform(ex);
            ex.printStackTrace();

        } catch (Exception ex) {
            if (template != null) result = template.transform(new BaseException(ex.getMessage()));
            ex.printStackTrace();
        }

        return result;
    }

    private TemplateResult getCategories(DataSource source, DataTemplate template, Map<String, String> param) {
        return this.wallpapertemplate.transform(categories.get(source.getId()).keySet());
    }

    private TemplateResult getData(DataSource source, DataTemplate template, Map<String, String> param) throws BaseException {
        Item[] items = new Item[0];
        String cat = param.get("cat");
        if(cat != null && !cat.isEmpty() && categories.get(source.getId()).containsKey(cat)) {
            List<String> itemIds = categories.get(source.getId()).get(cat);
            String[] itemWithPaging = Helper.applyPaging(itemIds.toArray(new String[itemIds.size()]), param);
            items = new Item[itemWithPaging.length];

            int i = 0;
            for(String itemId : itemWithPaging){
                items[i++] = source.getItem(itemId);
            }
        } else {
            source.getItems();
            items = Helper.applyPaging(items, param);
        }

        return template.transform(items);
    }

    private void initializeCategories(DataSource dataSource, Map<String, String> param) throws BaseException {
        if(!this.categories.containsKey(dataSource.getId())) {
            synchronized (this.categories) {
                if (!this.categories.containsKey(dataSource.getId())) {
                    HashMap<String, List<String>> categoryEntries = new HashMap<>();

                    Item[] items = dataSource.getItems();

                    for (Item item : items) {
                        String cats = item.getValue("content");
                        HashMap map = new JSONDeserializer<HashMap>().deserialize(cats);
                        if (map.containsKey("content")) {
                            HashMap cMap = (HashMap) map.get("content");
                            if (cMap.containsKey("Categories")) {
                                ArrayList categories = (ArrayList) cMap.get("Categories");
                                for (Object cat : categories) {
                                    if (!categoryEntries.containsKey(cat)) {
                                        categoryEntries.put((String) cat, new ArrayList<>());
                                    }

                                    categoryEntries.get(cat).add(item.getId());
                                }
                            }
                        }
                    }

                    this.categories.put(dataSource.getId(), categoryEntries);
                }
            }
        }
    }


    private DataSource getDataSource(DataAccess access, String sourceId) throws BaseException {
        return access.getDataSource(sourceId);
    }

    @Override
    public void close() throws IOException {
        if (dataAccess != null) {
            this.context.removeServiceListener(dataAccess);
        }

        if (dataTemplates != null) {
            this.context.removeServiceListener(dataTemplates);
        }
    }


    public enum WallpaperAccessAction implements RequestAction {
        GetData(0),
        GetCategories (1);

        private final int value;

        @Override
        public int getValue() {
            return this.value;
        }

        WallpaperAccessAction(int value) {
            this.value = value;
        }
    }
}
