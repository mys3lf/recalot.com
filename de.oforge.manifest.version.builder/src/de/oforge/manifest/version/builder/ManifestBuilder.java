package de.oforge.manifest.version.builder;

import java.io.*;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;


/**
 * Created by matthaeus.schmedding on 10.04.2015.
 */
public class ManifestBuilder {
    public static void main(String[] args) {
        String path = null;
        if (args.length > 0) {
            path = args[0];
        } else {
            path = "C:\\Privat\\3_Uni\\5_Workspaces\\recalot.com";
        }

        List<Boolean> change = Arrays.asList(false, false, false, true);
        Map<String, String> defaultValues = new HashMap<>();
        defaultValues.put("Bundle-DocURL", "http://recalot.com/doc");
        defaultValues.put("Bundle-License", "http://recalot.com/license.txt");

        if (path != null) {
            List<File> manifests = findAllManifestFiles(path);

            for (File manifest : manifests) {
                modifyManifest(manifest, change, defaultValues);
                // manifest.delete();
            }
        }
    }

    private static void modifyManifest(File manifest, List<Boolean> change, Map<String, String> defaultValues) {

        List<String> lines = new ArrayList<>();
        String line = null;
        String bV = "Bundle-Version".intern();
        String eP = "Export-Package".intern();

        try {

            Manifest mf = new Manifest(new FileInputStream(manifest));
            Attributes mainAttributes = mf.getMainAttributes();

            String version = mainAttributes.getValue(bV);

            if (version == null) {
                mainAttributes.putValue(bV, "0.0.0.1");
            } else {
                String split[] = version.split("\\.");
                for (int i = 0; i < split.length; i++) {
                    if (change.size() > i && change.get(i)) {
                        try {
                            int n = Integer.parseInt(split[i]);
                            split[i] = "" + (++n);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            split[i] = "1";
                        }
                    }
                }

                mainAttributes.putValue(bV, String.join(".", split));
            }


            String packages = mainAttributes.getValue(eP);

            version = mainAttributes.getValue(bV);
            String packageVersion = version.substring(0, version.lastIndexOf("."));
            if(packages != null){
                String[] packagesSplit = packages.split(",");


                for(int i = 0; i <packagesSplit.length; i++){
                    if(packagesSplit[i].contains(";")) {
                        packagesSplit[i] = (packagesSplit[i].substring(0, packagesSplit[i].indexOf(";"))) + ";version=\"" + packageVersion +"\"";
                    } else {
                        packagesSplit[i] += ";version=\"" + packageVersion + "\"";
                    }
                }

                mainAttributes.putValue(eP, String.join(",", packagesSplit));
            }

            for(String key : defaultValues.keySet()){
                mainAttributes.putValue(key, defaultValues.get(key));
            }

            mf.write(new FileOutputStream(manifest));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static List<File> findAllManifestFiles(String path) {
        File dir = new File(path);
        List<File> files = new ArrayList<>();
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    files.addAll(findAllManifestFiles(file.getAbsolutePath()));
                } else if (file.getName().toLowerCase().equals("manifest.mf")) {
                    files.add(file);
                }
            }
        }

        return files;
    }
}
