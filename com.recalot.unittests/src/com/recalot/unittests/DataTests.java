package com.recalot.unittests;

import com.recalot.common.communication.Interaction;
import com.recalot.common.communication.Item;
import com.recalot.common.communication.Message;
import com.recalot.common.communication.User;
import com.recalot.unittests.helper.WebRequest;
import com.recalot.unittests.helper.WebResponse;
import flexjson.JSONDeserializer;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by matthaeus.schmedding on 24.04.2015.
 */
public class DataTests extends TestsBase {
    private String Path = "data/";
    private String TrackingPath = "track/";
    private String UsersPath = "users/";
    private String ItemsPath = "items/";
    private String InteractionsPath = "interactions/";

    @Test
    public void getUsers() {
        WebResponse response = WebRequest.execute(HOST + Path + SourcesPath + SourceId + PathSeparator  + UsersPath);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);

        List<User> users = new JSONDeserializer<List<User>>().deserialize(response.getBody());

        assertNotNull(users);
        assertNotEquals(users.size(), 0);
    }

    @Test
    public void getItems() {
        WebResponse response = WebRequest.execute(HOST + Path + SourcesPath + SourceId + PathSeparator  + ItemsPath);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);

        List<Item> items = new JSONDeserializer<List<Item>>().deserialize(response.getBody());

        assertNotNull(items);
        assertNotEquals(items.size(), 0);
    }

    @Test
    public void getAllInteractions() {
        WebResponse response = WebRequest.execute(HOST + Path + SourcesPath + SourceId + PathSeparator + InteractionsPath );
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);

        List<Interaction> interactions = new JSONDeserializer<List<Interaction>>().deserialize(response.getBody());

        assertNotNull(interactions);
        assertNotEquals(interactions.size(), 0);
    }

    @Test
    public void getUser() {
        WebResponse response = WebRequest.execute(HOST + Path + SourcesPath + SourceId + PathSeparator  + UsersPath + 1);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);

        HashMap user = new JSONDeserializer<HashMap>().deserialize(response.getBody());

        assertNotNull(user);
        assertEquals(user.get("id"), "1");
    }

    @Test
    public void getItem() {
        WebResponse response = WebRequest.execute(HOST + Path + SourcesPath + SourceId + PathSeparator  + ItemsPath + 1);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);

        HashMap item = new JSONDeserializer<HashMap>().deserialize(response.getBody());

        assertNotNull(item);
        assertEquals(item.get("id"), "1");
    }

    @Test
    public void getInteractionForUser() {
        WebResponse response = WebRequest.execute(HOST + Path + SourcesPath + SourceId + PathSeparator  + UsersPath + 1 + PathSeparator + InteractionsPath);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);

        List<Interaction> interactions = new JSONDeserializer<List<Interaction>>().deserialize(response.getBody());

        assertNotNull(interactions);

        assertNotEquals(interactions.size(), 0);
    }

    @Test
    public void createUser() throws UnsupportedEncodingException {
        Map<String, String> params = new Hashtable<>();
        params.put("gender", "male");

        WebResponse response = WebRequest.execute(WebRequest.HTTPMethod.PUT, HOST + Path + SourcesPath + SourceId + PathSeparator  + UsersPath,  params);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);
        HashMap user = new JSONDeserializer<HashMap>().deserialize(response.getBody());

        assertNotNull(user);

    }

    @Test
    public void createItem() throws UnsupportedEncodingException {
        Map<String, String> params = new Hashtable<>();
        params.put("type", "website");

        WebResponse response = WebRequest.execute(WebRequest.HTTPMethod.PUT, HOST + Path + SourcesPath + SourceId + PathSeparator  + ItemsPath, params);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);

        HashMap item = new JSONDeserializer<HashMap>().deserialize(response.getBody());
        assertNotNull(item);
        assertNotNull(item.get("id"));
      //  assertEquals(item.getValue("type"), "website");
    }

    @Test
    public void updateUser() throws UnsupportedEncodingException {
        WebResponse response2 = WebRequest.execute(HOST + Path + SourcesPath + SourceId + PathSeparator  + UsersPath + 1);
        assertNotNull(response2);
        assertEquals(response2.getContentType(), JsonMimeType);
        assertNotNull(response2.getBody());
        assertEquals(response2.getResponseCode(), 200);

        Map user = new JSONDeserializer<Map>().deserialize(response2.getBody());

        assertNotNull(user);
        assertEquals(user.get("id"), "1");

        String a = "a";
        String b = "b";

        String value = (String)((Map)user.get("content")).get("test");
        String newValue = null;

        if(value != null && value.equals("a")) {
            newValue = b;
        } else {
            newValue = a;
        }


        Map<String, String> params = new Hashtable<>();
        params.put("test", newValue);

        WebResponse response = WebRequest.execute(WebRequest.HTTPMethod.PUT, HOST + Path + SourcesPath + SourceId + PathSeparator  + UsersPath + user.get("id"),  params);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);
        HashMap newUser = new JSONDeserializer<HashMap>().deserialize(response.getBody());

        assertNotNull(newUser);
        assertNotEquals(newUser.get("id"), 0);
        assertNotNull((String)((Map)newUser.get("content")).get("test"));
        assertNotEquals((String)((Map)newUser.get("content")).get("test"), value);
    }

    @Test
    public void updateItem() throws UnsupportedEncodingException {
        WebResponse response = WebRequest.execute(HOST + Path + SourcesPath + SourceId + PathSeparator  + ItemsPath + 1);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);

        HashMap item = new JSONDeserializer<HashMap>().deserialize(response.getBody());

        assertNotNull(item);
        assertEquals(item.get("id"), "1");


        String a = "a";
        String b = "b";

        String value = (String)((Map)item.get("content")).get("test");
        String newValue = null;

        if(value != null && value.equals("a")) {
            newValue = b;
        } else {
            newValue = a;
        }

        System.out.println("newValue:" + newValue);
        Map<String, String> params = new Hashtable<>();
        params.put("test", newValue);


        WebResponse response2 = WebRequest.execute(WebRequest.HTTPMethod.PUT, HOST + Path + SourcesPath + SourceId + PathSeparator  + ItemsPath + 1, params);
        assertNotNull(response2);
        assertEquals(response2.getContentType(), JsonMimeType);
        assertNotNull(response2.getBody());
        assertEquals(response2.getResponseCode(), 200);

        HashMap message = new JSONDeserializer<HashMap>().deserialize(response2.getBody());
        assertNotNull(message);


        WebResponse response3 = WebRequest.execute(HOST + Path + SourcesPath + SourceId + PathSeparator  + ItemsPath + 1);
        assertNotNull(response3);
        assertEquals(response3.getContentType(), JsonMimeType);
        assertNotNull(response3.getBody());
        assertEquals(response3.getResponseCode(), 200);

        HashMap newItem = new JSONDeserializer<HashMap>().deserialize(response3.getBody());

        assertNotEquals(newItem.get("id"), 1);
        assertNotEquals(((Map)newItem.get("content")).get("test"), value);
    }

    @Test
    public void createInteraction() throws UnsupportedEncodingException {
        Map<String, String> params = new Hashtable<>();
        params.put("type", "view");
        params.put("value", "1");

        WebResponse response = WebRequest.execute(WebRequest.HTTPMethod.POST, HOST + TrackingPath + SourcesPath + SourceId + PathSeparator  + UsersPath + 1 + PathSeparator + ItemsPath + 1, params);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);


        WebResponse response2 = WebRequest.execute(WebRequest.HTTPMethod.GET, HOST + Path + SourcesPath + SourceId + PathSeparator  + UsersPath + 1 + PathSeparator + ItemsPath + 1 + PathSeparator + InteractionsPath, params);
        assertNotNull(response2);
        assertEquals(response2.getContentType(), JsonMimeType);
        assertNotNull(response2.getBody());
        assertEquals(response2.getResponseCode(), 200);

        ArrayList interaction = new JSONDeserializer<ArrayList>().deserialize(response2.getBody());
        assertNotNull(interaction);

       assertNotEquals(interaction.size(), 0);
    //    assertEquals(interaction.get("type"), "view");
     //   assertEquals(interaction.get("value"), "1");
    }

    /*
    @Test
    public void deleteUser() {
        Map<String, String> params = new Hashtable<>();

        WebResponse response = WebRequest.execute(WebRequest.HTTPMethod.PUT, HOST + Path + SourcesPath + SourceId + PathSeparator  + UsersPath,  params);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);
        HashMap user = new JSONDeserializer<HashMap>().deserialize(response.getBody());

        assertNotNull(user);

        WebResponse response2 = WebRequest.execute(WebRequest.HTTPMethod.DELETE, HOST + Path + SourcesPath + SourceId + PathSeparator  + UsersPath + user.get("id"),  params);
        assertNotNull(response2);
        assertEquals(response2.getContentType(), JsonMimeType);
        assertNotNull(response2.getBody());
        assertEquals(response2.getResponseCode(), 200);

        Message message = new JSONDeserializer<Message>().deserialize(response.getBody());
        assertNotNull(message);
        assertEquals(message.getStatus(), Message.Status.INFO);
    }

    @Test
    public void deleteItem() {
        Map<String, String> params = new Hashtable<>();

        WebResponse response = WebRequest.execute(WebRequest.HTTPMethod.PUT, HOST + Path + SourcesPath + SourceId + PathSeparator  + ItemsPath,  params);
        assertNotNull(response);
        assertEquals(response.getContentType(), JsonMimeType);
        assertNotNull(response.getBody());
        assertEquals(response.getResponseCode(), 200);
        HashMap item = new JSONDeserializer<HashMap>().deserialize(response.getBody());

        assertNotNull(item);

        WebResponse response2 = WebRequest.execute(WebRequest.HTTPMethod.DELETE, HOST + Path + SourcesPath + SourceId + PathSeparator  + ItemsPath + item.get("id"),  params);
        assertNotNull(response2);
        assertEquals(response2.getContentType(), JsonMimeType);
        assertNotNull(response2.getBody());
        assertEquals(response2.getResponseCode(), 200);

        Message message = new JSONDeserializer<Message>().deserialize(response.getBody());
        assertNotNull(message);
        assertEquals(message.getStatus(), Message.Status.INFO);
    }
    */
}
