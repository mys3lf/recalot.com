package com.recalot.demos.wallpaper.controller;

import com.recalot.common.communication.TemplateResult;
import com.recalot.demos.wallpaper.model.Category;
import com.recalot.demos.wallpaper.model.Paging;
import flexjson.JSONSerializer;
import flexjson.transformer.IterableTransformer;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Set;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class WallpaperTemplate {
    protected static String MimeType = "application/json";
    protected static Charset charset = StandardCharsets.UTF_8;


    public TemplateResult transform(Set<String> strings) {
        String result =  new JSONSerializer().transform(new IterableTransformer(), "Iterable.class").exclude("class", "*.class").serialize(strings);
        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    public TemplateResult transform(Collection<Category> values) {
        String result =  new JSONSerializer().transform(new IterableTransformer(), "Iterable.class").exclude("class", "*.class").serialize(values);
        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }

    public TemplateResult transform(Paging paging) {
        String result =  new JSONSerializer().transform(new IterableTransformer(), "Iterable.class").exclude("class", "*.class").include("items", "items.*").serialize(paging);
        return new TemplateResult(200, MimeType, new ByteArrayInputStream(result.getBytes(charset)), charset);
    }
}
