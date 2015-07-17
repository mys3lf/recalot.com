package de.oforge.osgi.repo.builder;

import org.osgi.service.indexer.*;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Created by matthaeus.schmedding on 16.04.2015.
 */
public class MyTestAnalyser implements ResourceAnalyzer {
    @Override
    public void analyzeResource(Resource resource, List<Capability> capabilities, List<Requirement> requirements) throws Exception {
        Manifest mf = resource.getManifest();

        Attributes attr  = mf.getMainAttributes();


        if(attr != null && attr.size() > 0){

            String license =   attr.getValue("Bundle-License");
            String docUrl =   attr.getValue("Bundle-DocURL");
            String description =   attr.getValue("Bundle-Description");
            String category = attr.getValue("Bundle-Category");
            String symbolic = attr.getValue("Bundle-SymbolicName");
            String name = attr.getValue("Bundle-Name");
            String version = attr.getValue("Bundle-Version");

            Builder builder = new Builder().setNamespace("Bundle-Information");
            if(license != null){
                builder.addAttribute("Bundle-License", license);
            }
            if(docUrl != null){
                builder.addAttribute("Bundle-DocURL", docUrl);
            }
            if(description != null){
                builder.addAttribute("Bundle-Description", description);
            }
            if(category != null){
                builder.addAttribute("Bundle-Category", category);
            }
            if(symbolic != null){
                builder.addAttribute("Bundle-SymbolicName", symbolic);
            }
            if(name != null){
                builder.addAttribute("Bundle-Name", name);
            }
            if(version != null){
                builder.addAttribute("Bundle-Version", version);
            }

            capabilities.add(builder.buildCapability());
        }
    }
}
