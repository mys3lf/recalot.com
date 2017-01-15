// Copyright (C) 2016 Matthäus Schmedding
//
// This file is part of recalot.com.
//
// recalot.com is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// recalot.com is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with recalot.com. If not, see <http://www.gnu.org/licenses/>.


package com.recalot.model.rec.librec;

import com.recalot.common.builder.Initiator;
import com.recalot.common.builder.RecommenderBuilder;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.context.Context;
import com.recalot.common.exceptions.BaseException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Register the recommender builder services. This class is called automatically when the bundle is activated.
 *
 * @author Matthaeus.schmedding
 */
public class Activator implements BundleActivator, Initiator {

    private List<RecommenderBuilder> recommenders;
    private List<Context> contexts;

    /**
     * Implements BundleActivator.start(). Prints
     * a message and adds itself to the bundle context as a service
     * listener.
     *
     * @param context the framework context for the bundle.
     */
    public void start(BundleContext context) {
        registerRecommenders(context);
        registerContext(context);
    }

    private void registerContext(BundleContext context) {
        contexts = new ArrayList<>();

    }

    private void registerRecommenders(BundleContext context) {
        recommenders = new ArrayList<>();

        HashMap<String, String> map = new HashMap<>();

        map.put("librec-ConstantGuess","librec.baseline.ConstantGuess");
        map.put("librec-GlobalAverage","librec.baseline.GlobalAverage");
        map.put("librec-ItemAverage","librec.baseline.ItemAverage");
        map.put("librec-ItemCluster","librec.baseline.ItemCluster");
        map.put("librec-MostPopular","librec.baseline.MostPopular");
        map.put("librec-RandomGuess","librec.baseline.RandomGuess");
        map.put("librec-UserAverage","librec.baseline.UserAverage");
        map.put("librec-UserCluster","librec.baseline.UserCluster");

        map.put("librec-AR","librec.ext.AR");
        map.put("librec-Hybrid","librec.ext.Hybrid");
        map.put("librec-NMF","librec.ext.NMF");
        map.put("librec-PD","librec.ext.PD");
        map.put("librec-PRankD","librec.ext.PRankD");
        map.put("librec-SlopeOne","librec.ext.SlopeOne");

        map.put("librec-AoBPR","librec.ranking.AoBPR");
        map.put("librec-BHfree","librec.ranking.BHfree");
        map.put("librec-BPR","librec.ranking.BPR");
        map.put("librec-BUCM","librec.ranking.BUCM");
        map.put("librec-CLiMF","librec.ranking.CLiMF");
        map.put("librec-FISMauc","librec.ranking.FISMauc");
        map.put("librec-FISMrmse","librec.ranking.FISMrmse");
        map.put("librec-GBPR","librec.ranking.GBPR");
        map.put("librec-ItemBigram","librec.ranking.ItemBigram");
        map.put("librec-LDA","librec.ranking.LDA");
        map.put("librec-LRMF","librec.ranking.LRMF");
        map.put("librec-RankALS","librec.ranking.RankALS");
        map.put("librec-RankSGD","librec.ranking.RankSGD");
        map.put("librec-SBPR","librec.ranking.SBPR");
        map.put("librec-SLIM","librec.ranking.SLIM");
        map.put("librec-WBPR","librec.ranking.WBPR");
        map.put("librec-WRMF","librec.ranking.WRMF");
        map.put("librec-BPMF","librec.rating.BPMF");
        map.put("librec-BiasedMF","librec.rating.BiasedMF");
        map.put("librec-CPTF","librec.rating.CPTF");
        map.put("librec-GPLSA","librec.rating.GPLSA");
        map.put("librec-ItemKNN","librec.rating.ItemKNN");
        map.put("librec-LDCC","librec.rating.LDCC");
        map.put("librec-PMF","librec.rating.PMF");
        map.put("librec-RSTE","librec.rating.RSTE");
        map.put("librec-RfRec","librec.rating.RfRec");
        map.put("librec-SVDPlusPlus","librec.rating.SVDPlusPlus");
        map.put("librec-SoRec","librec.rating.SoRec");
        map.put("librec-SoReg","librec.rating.SoReg");
        map.put("librec-SocialMF","librec.rating.SocialMF");
        map.put("librec-TimeSVD","librec.rating.TimeSVD");
        map.put("librec-TrustMF","librec.rating.TrustMF");
        map.put("librec-TrustSVD","librec.rating.TrustSVD");
        map.put("librec-URP","librec.rating.URP");
        map.put("librec-UserKNN","librec.rating.UserKNN");

        for(String key: map.keySet()) {
            try {
                RecommenderBuilder builder = new RecommenderBuilder(this, GenericLibRecRecommender.class.getName(), key, "");
                builder.setConfiguration(new ConfigurationItem("className", ConfigurationItem.ConfigurationItemType.String, map.get(key), ConfigurationItem.ConfigurationItemRequirementType.Hidden, ""));

                ConfigurationItem[] items = GenericLibRecRecommender.getConfigurationItems(map.get(key));
                for (ConfigurationItem item : items) {
                    builder.setConfiguration(item);
                }

                recommenders.add(builder);
            } catch (BaseException e) {
                e.printStackTrace();
            }
        }


        for (RecommenderBuilder c : recommenders) {
            context.registerService(RecommenderBuilder.class.getName(), c, null);
        }
    }

    /**
     * Implements BundleActivator.stop(). Prints
     * a message and removes itself from the bundle context as a
     * service listener.
     *
     * @param context the framework context for the bundle.
     */
    public void stop(BundleContext context) throws Exception {
        if (recommenders != null) {
            for (RecommenderBuilder c : recommenders) {
                c.close();
            }

            recommenders = null;
        }
        if (contexts != null) {
            for (Context c : contexts) {
                c.close();
            }

            contexts = null;
        }
    }

    @Override
    public Object createInstance(String className) {
        try {
            Class c = Class.forName(className);
            return c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}