package com.parabole.rda.platform.graphdb;

import javax.inject.Singleton;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Inject;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import play.Logger;

@Singleton
public class OctopusIdMapper {

    @Inject
    protected Octopus octopus;

    private final BiMap<Integer, String> URI_ID_MAPPING = HashBiMap.create();
    private boolean initialized = false;

    private void load() {
        final OrientGraphNoTx graphDbNoTx = octopus.getGraphConnectionNoTx();
        try {
            graphDbNoTx.getVertices().forEach((final Vertex vertex) -> {
                final Integer id = octopus.getId(vertex);
                final String value = octopus.getURI(vertex);
                Logger.info(id + "=" + value);
                if (!URI_ID_MAPPING.containsValue(value)) {
                    URI_ID_MAPPING.put(id, value);
                } else {
                    Logger.info("Value is DUPLICATE =" + value);
                }
            });
            initialized = true;
        } finally {
            octopus.closeGraphConnection(graphDbNoTx);
        }
    }

    public Integer getId(final String uri) {
        if (!initialized) {
            load();
        }
        return URI_ID_MAPPING.inverse().get(uri);
    }

    public String getURI(final Integer id) {
        if (!initialized) {
            load();
        }
        return URI_ID_MAPPING.get(id);
    }
}
