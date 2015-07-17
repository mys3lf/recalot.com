package com.recalot.controller.data;


import com.recalot.common.GenericServiceListener;
import com.recalot.common.Helper;
import com.recalot.common.builder.DataSourceBuilder;
import com.recalot.common.communication.*;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.exceptions.NotReadyException;
import com.recalot.common.interfaces.controller.RequestAction;
import com.recalot.common.interfaces.model.data.DataAccess;
import com.recalot.common.interfaces.model.data.DataInformation;
import com.recalot.common.interfaces.model.data.DataSource;
import com.recalot.common.interfaces.template.DataTemplate;
import org.osgi.framework.BundleContext;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author matthaeus.schmedding
 */
public class DataAccessController implements com.recalot.common.interfaces.controller.DataAccessController, Closeable {

    private final BundleContext context;
    private final GenericServiceListener<DataAccess> dataAccess;
    private final GenericServiceListener<DataTemplate> dataTemplates;


    public DataAccessController(BundleContext context) {
        this.context = context;
        this.dataAccess = new GenericServiceListener<>(context, DataAccess.class.getName());
        this.dataTemplates = new GenericServiceListener(context, DataTemplate.class.getName());

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

            switch ((DataAccessRequestAction) action) {
                case GetData: {
                    result = getData(access, template, param);
                    break;
                }
                case GetUser: {
                    result = getUser(access, template, param);
                    break;
                }
                case GetUsers: {
                    result = getUsers(access, template, param);
                    break;
                }
                case UpdateUser: {
                    result = updateUser(access, template, param);
                    break;
                }
                case CreateUser: {
                    result = createUser(access, template, param);
                    break;
                }
                case GetItems: {
                    result = getItems(access, template, param);
                    break;
                }
                case GetItem: {
                    result = getItem(access, template, param);
                    break;
                }
                case UpdateItem: {
                    result = updateItem(access, template, param);
                    break;
                }
                case CreateItem: {
                    result = createItem(access, template, param);
                    break;
                }
                case GetInteractions: {
                    result = getInteractions(access, template, param);
                    break;
                }
                case GetInteraction: {
                    result = getInteraction(access, template, param);
                    break;
                }
                case AddInteraction: {
                    result = addInteraction(access, template, param);
                    break;
                }
                case GetSources: {
                    result = getSources(access, template, param);
                    break;
                }
                case CreateSource: {
                    result = createSource(access, template, param);
                    break;
                }
                case UpdateSource: {
                    result = updateSource(access, template, param);
                    break;
                }
                case GetSource: {
                    result = getSource(access, template, param);
                    break;
                }
                case GetDataSourceBuilder: {
                    result = getDataSourceBuilder(access, template, param);
                    break;
                }
                case DeleteSource: {
                    result = deleteSource(access, template, param);
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

    private TemplateResult getDataSourceBuilder(DataAccess access, DataTemplate template, Map<String, String> param) throws BaseException {
        String id = param.get(Helper.Keys.SourceId);
        DataSourceBuilder builder = access.getDataSourceBuilder(id);

        return template.transform(builder);
    }

    private TemplateResult getData(DataAccess access, DataTemplate template, Map<String, String> param) throws BaseException {
        DataSource dataSource = getDataSource(access, param.get(Helper.Keys.SourceId));
        if(dataSource.getState() != DataInformation.DataState.READY) throw new NotReadyException("The datasource %s is not yet ready.", dataSource.getId());

        DataSet data = dataSource.getDataSet();
        return template.transform(data);
    }

    private TemplateResult getUser(DataAccess access, DataTemplate template, Map<String, String> param) throws BaseException {
        DataSource dataSource = getDataSource(access, param.get(Helper.Keys.SourceId));
        if(dataSource.getState() != DataInformation.DataState.READY) throw new NotReadyException("The datasource %s is not yet ready.", dataSource.getId());

        String userId = param.get(Helper.Keys.UserId);
        User user = dataSource.getUser(userId);
        return template.transform(user);

    }

    private TemplateResult getUsers(DataAccess access, DataTemplate template, Map<String, String> param) throws BaseException {
        DataSource dataSource = getDataSource(access, param.get(Helper.Keys.SourceId));
        if(dataSource.getState() != DataInformation.DataState.READY) throw new NotReadyException("The datasource %s is not yet ready.", dataSource.getId());

        User[] users = dataSource.getUsers();

        users = Helper.applyPaging(users, param);

        return template.transform(users);
    }

    private TemplateResult updateUser(DataAccess access, DataTemplate template, Map<String, String> param) throws BaseException {
        DataSource dataSource = getDataSource(access, param.get(Helper.Keys.SourceId));
        if(dataSource.getState() != DataInformation.DataState.READY) throw new NotReadyException("The datasource %s is not yet ready.", dataSource.getId());

        String userId = param.get(Helper.Keys.UserId);

        User user = dataSource.updateUser(userId, param);
        return template.transform(user);
    }

    private TemplateResult createUser(DataAccess access, DataTemplate template, Map<String, String> param) throws BaseException {
        DataSource dataSource = getDataSource(access, param.get(Helper.Keys.SourceId));
        if(dataSource.getState() != DataInformation.DataState.READY) throw new NotReadyException("The datasource %s is not yet ready.", dataSource.getId());

        User user = dataSource.createUser(param);
        return template.transform(user);

    }

    private TemplateResult getItems(DataAccess access, DataTemplate template, Map<String, String> param) throws BaseException {
        DataSource dataSource = getDataSource(access, param.get(Helper.Keys.SourceId));
        if(dataSource.getState() != DataInformation.DataState.READY) throw new NotReadyException("The datasource %s is not yet ready.", dataSource.getId());

        Item[] items = dataSource.getItems();

        items = Helper.applyPaging(items, param);

        return template.transform(items);
    }

    private TemplateResult getItem(DataAccess access, DataTemplate template, Map<String, String> param) throws BaseException {
        DataSource dataSource = getDataSource(access, param.get(Helper.Keys.SourceId));
        if(dataSource.getState() != DataInformation.DataState.READY) throw new NotReadyException("The datasource %s is not yet ready.", dataSource.getId());

        Item item = dataSource.getItem(param.get(Helper.Keys.ItemId));
        return template.transform(item);
    }

    private TemplateResult updateItem(DataAccess access, DataTemplate template, Map<String, String> param) throws BaseException {
        DataSource dataSource = getDataSource(access, param.get(Helper.Keys.SourceId));
        if(dataSource.getState() != DataInformation.DataState.READY) throw new NotReadyException("The datasource %s is not yet ready.", dataSource.getId());

        Item item = dataSource.updateItem(param.get(Helper.Keys.ItemId), param);
        return template.transform(item);
    }

    private TemplateResult createItem(DataAccess access, DataTemplate template, Map<String, String> param) throws BaseException {
        DataSource dataSource = getDataSource(access, param.get(Helper.Keys.SourceId));
        if(dataSource.getState() != DataInformation.DataState.READY) throw new NotReadyException("The datasource %s is not yet ready.", dataSource.getId());

        Item item = dataSource.createItem(param);
        return template.transform(item);
    }

    private TemplateResult addInteraction(DataAccess access, DataTemplate template, Map<String, String> param) throws BaseException {
        DataSource dataSource = getDataSource(access, param.get(Helper.Keys.SourceId));

        if(dataSource.getState() != DataInformation.DataState.READY) throw new NotReadyException("The datasource %s is not yet ready.", dataSource.getId());

        String itemId = param.get(Helper.Keys.ItemId);
        String userId = param.get(Helper.Keys.UserId);
        String type = param.get(Helper.Keys.Type);
        String value = param.get(Helper.Keys.Value);

        Message message = dataSource.addInteraction(itemId, userId, new Date(), type, value, param);
        return template.transform(message);

    }

    private TemplateResult getInteractions(DataAccess access, DataTemplate template, Map<String, String> param) throws BaseException {
        DataSource dataSource = getDataSource(access, param.get(Helper.Keys.SourceId));
        if(dataSource.getState() != DataInformation.DataState.READY) throw new NotReadyException("The datasource %s is not yet ready.", dataSource.getId());

        String userId = param.get(Helper.Keys.UserId);
        Interaction[] interaction;

        if(userId != null) {
            interaction = dataSource.getInteractions(userId);
        } else {
            interaction = dataSource.getInteractions();
        }

        interaction = Helper.applyPaging(interaction, param);

        return template.transform(interaction);
    }

    private TemplateResult getInteraction(DataAccess access, DataTemplate template, Map<String, String> param) throws BaseException {
        DataSource dataSource = getDataSource(access, param.get(Helper.Keys.SourceId));
        if(dataSource.getState() != DataInformation.DataState.READY) throw new NotReadyException("The datasource %s is not yet ready.", dataSource.getId());

        String itemId = param.get(Helper.Keys.ItemId);
        String userId = param.get(Helper.Keys.UserId);

        Interaction[] interaction = dataSource.getInteractions(itemId, userId);

        interaction = Helper.applyPaging(interaction, param);

        return template.transform(interaction);
    }

    private TemplateResult getSources(DataAccess access, DataTemplate template, Map<String, String> param) throws BaseException {
        List<DataInformation> sources = access.getDataInformations();

        if (param.containsKey(Helper.Keys.State)) {
            DataInformation.DataState state = DataInformation.DataState.valueOf(param.get(Helper.Keys.State));
            List<DataInformation> temp = new ArrayList<>();
            for (DataInformation info : sources) {
                if (info.getState() == state) {
                    temp.add(info);
                }
            }

            return template.transform(temp);
        }

        return template.transform(sources);
    }

    private TemplateResult createSource(DataAccess access, DataTemplate template, Map<String, String> param) throws BaseException {
        DataSource source = access.createDataSource(param);
        return template.transform(source);
    }

    private TemplateResult updateSource(DataAccess access, DataTemplate template, Map<String, String> param) throws BaseException {
        String id = param.get(Helper.Keys.SourceId);
        Message message = access.updateDataSource(id, param);
        return template.transform(message);
    }

    private TemplateResult getSource(DataAccess access, DataTemplate template, Map<String, String> param) throws BaseException {
        String id = param.get(Helper.Keys.SourceId);
        DataSource source = access.getDataSource(id);

        return template.transform(source);
    }

    private TemplateResult deleteSource(DataAccess access, DataTemplate template, Map<String, String> param) throws BaseException {
        String id = param.get(Helper.Keys.SourceId);
        Message message = access.deleteDataSource(id);
        return template.transform(message);
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
}
