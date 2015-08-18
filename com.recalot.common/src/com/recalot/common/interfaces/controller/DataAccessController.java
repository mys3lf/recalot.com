package com.recalot.common.interfaces.controller;

/**
 * @author Matthaeus.schmedding
 */
public interface DataAccessController extends Controller {

    public enum DataAccessRequestAction implements RequestAction {
        GetData(0),
        GetUser (1),
        GetUsers(2),
        UpdateUser(3),
        CreateUser(4),
        GetItems(5),
        GetItem(6),
        UpdateItem(7),
        CreateItem(8),
        GetInteractions(9),
        GetInteraction(10),
        GetSources(11),
        CreateSource(12),
        UpdateSource(13),
        GetSource(14),
        DeleteSource(15),
        AddInteraction(16),
        GetDataSourceBuilder(17),
        GetRelation(18),
        GetRelations(19),
        CreateRelation(20),
        UpdateRelation(21);

        private final int value;

        @Override
        public int getValue() {
            return this.value;
        }

        DataAccessRequestAction(int value) {
            this.value = value;
        }
    }
}
