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
import com.recalot.demos.wallpaper.model.Category;
import com.recalot.demos.wallpaper.model.Paging;
import flexjson.JSONDeserializer;
import org.osgi.framework.BundleContext;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author matthaeus.schmedding
 */
public class DataAccessController implements Controller, Closeable {

    private final BundleContext context;
    private final GenericServiceListener<DataAccess> dataAccess;
    private final GenericServiceListener<DataTemplate> dataTemplates;
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Category>> categories;
    private final WallpaperTemplate wallpapertemplate;
    private Item[] allItems;

    public DataAccessController(BundleContext context) {
        this.context = context;
        this.dataAccess = new GenericServiceListener<>(context, DataAccess.class.getName());
        this.wallpapertemplate = new WallpaperTemplate();
        this.dataTemplates = new GenericServiceListener(context, DataTemplate.class.getName());
        this.categories = new ConcurrentHashMap<>();
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

            if (dataSource.getState() != DataInformation.DataState.READY)
                throw new NotReadyException("The datasource %s is not yet ready.", dataSource.getId());

            if (!categories.containsKey(dataSource.getId())) {
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
                case UpdateCategories: {
                    result = updateCategories(dataSource, template, param);
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
        return this.wallpapertemplate.transform(categories.get(source.getId()).values());
    }

    private TemplateResult getData(DataSource source, DataTemplate template, Map<String, String> param) throws BaseException {
        Item[] items = new Item[0];
        String cat = param.get("cat");

        Paging result = new Paging<Item>();

        int page = 1;
        int pageSize = Helper.Keys.PageSize;

        String pageString = param.get(Helper.Keys.Page);
        if (Helper.isIntegerRegex(pageString)) {
            page = Integer.parseInt(pageString);
        }


        if (param.containsKey(Helper.Keys.PageSizeKey)) {
            String pageSizeString = param.get(Helper.Keys.PageSizeKey);
            if (Helper.isIntegerRegex(pageSizeString)) {
                pageSize = Integer.parseInt(pageSizeString);
            }
        }

        result.setPageSize(pageSize);
        result.setPage(page);

        param.put(Helper.Keys.Page, "" + page);
        param.put(Helper.Keys.PageSizeKey, "" + pageSize);

        if (cat != null && !cat.isEmpty() && categories.get(source.getId()).containsKey(cat)) {
            String catString = cat.toLowerCase().trim();
            List<String> itemIds = categories.get(source.getId()).get(catString).getItems();

            result.setCount(itemIds.size());

            String[] itemWithPaging = Helper.applyPaging(itemIds.toArray(new String[itemIds.size()]), param);
            items = new Item[itemWithPaging.length];

            int i = 0;
            for (String itemId : itemWithPaging) {
                items[i++] = source.getItem(itemId);
            }

        } else {

            result.setCount(allItems.length);
            items = allItems;
        }

        items = Helper.applyPaging(items, param);
        result.setItems(items);

        return this.wallpapertemplate.transform(result);
    }

    private void initializeCategories(DataSource dataSource, Map<String, String> param) throws BaseException {
        if (!this.categories.containsKey(dataSource.getId())) {
            synchronized (this.categories) {
                if (!this.categories.containsKey(dataSource.getId())) {

                    this.categories.put(dataSource.getId(), createCategoriesMap(dataSource));
                }
            }
        }
    }


    private TemplateResult updateCategories(DataSource dataSource, DataTemplate template, Map<String, String> param) throws BaseException {

        if (this.categories.containsKey(dataSource.getId())) {
            synchronized (this.categories) {
                if (this.categories.containsKey(dataSource.getId())) {
                    this.categories.put(dataSource.getId(), createCategoriesMap(dataSource));
                    return template.transform(new Message("Update performed", "Categories successfull updated", Message.Status.INFO));
                }
            }
        }

        return template.transform(new Message("Nope", "Nope", Message.Status.INFO));
    }

    private ConcurrentHashMap<String, Category> createCategoriesMap(DataSource dataSource) throws BaseException {
        ConcurrentHashMap<String, Category> categoryEntries = new ConcurrentHashMap<>();

        Item[] allItems = dataSource.getItems();
        ArrayList<Item> items = new ArrayList<>(Arrays.asList(allItems));

        Collections.sort(items, new ItemCompare());
        //Collections.reverse(items);

        for (Item item : items) {
            String cats = item.getValue("categories");

            if (cats != null && !cats.isEmpty()) {

                String[] split = cats.split(",");

                for (String cat : split) {
                    String catString = cat.toLowerCase().trim();

                    if (!categoryEntries.containsKey(catString)) {
                        categoryEntries.put(catString, new Category(catString, "" + item.getValue("src")));
                    }

                    categoryEntries.get(catString).addItem(item.getId());
                }
            }
        }

        this.allItems = items.toArray(new Item[items.size()]);

        return categoryEntries;
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
        GetCategories(1),
        UpdateCategories(2);

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
