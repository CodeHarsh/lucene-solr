package org.apache.solr.handler.component;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.PluginInfo;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * Created by harshvardhan.s on 19/11/15.
 */
public class RestrictiveSearchHandler extends SearchHandler {
    static final String CARDINALITY_CHECK = "cardinality-check";
    static final String CARDINALITY_CHECK_ZK_SERVER = "zk-server";
    static final String CARDINALITY_CHECK_COLLECTION = "collection";
    static final String CARDINALITY_THRESHOLD = "cardinality-threshold";

    //Default solr server to localhost
    static final String CARDINALITY_CHECK_ZK_SERVER_DEFAULT = "localhost:9983";

    //Default solr collection to gettingstarted
    static final String CARDINALITY_CHECK_COLLECTION_DEFAULT = "gettingstarted";

    //Default cardinality threshold to 100
    static final int CARDINALITY_THRESHOLD_DEFAULT = 100;

    RestrictiveComponentAttributes restrictiveComponentAttributes = null;


    protected static Logger log = LoggerFactory.getLogger(RestrictiveSearchHandler.class);

    @Override
    public void init(PluginInfo info) {
        super.init(info);
        Object obj = info.initArgs.get(CARDINALITY_CHECK);
        SolrParams cardinalityCheck ;
        if (obj != null && obj instanceof NamedList) {
            cardinalityCheck = SolrParams.toSolrParams((NamedList)obj);
            String cardinalityCheckSolrServer = cardinalityCheck.get(CARDINALITY_CHECK_ZK_SERVER, CARDINALITY_CHECK_ZK_SERVER_DEFAULT);
            String cardinalityCheckCollectionName = cardinalityCheck.get(CARDINALITY_CHECK_COLLECTION, CARDINALITY_CHECK_COLLECTION_DEFAULT);
            Long cardinalityThreshold = cardinalityCheck.getLong(CARDINALITY_THRESHOLD, CARDINALITY_THRESHOLD_DEFAULT);

            restrictiveComponentAttributes = new RestrictiveComponentAttributes(cardinalityCheckSolrServer,
                    cardinalityCheckCollectionName,cardinalityThreshold);
        }
    }

    @Override
    public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception
    {
         try {
             List<SearchComponent> components  = getComponents();
             for( SearchComponent component : components ) {
                 if ( component instanceof RestrictiveSearchComponent ){
                     RestrictiveSearchComponent restrictiveSearchComponent = (RestrictiveSearchComponent) component;
                     restrictiveSearchComponent.validate(req.getOriginalParams(),restrictiveComponentAttributes);
                 }
             }
             super.handleRequestBody(req,rsp);

        } catch (RestrictedQueryException ex) {
            log.debug(ex.getMessage() ,ex);
            throw new SolrException(SolrException.ErrorCode.SERVER_ERROR, new Exception(ex.getMessage()));
        }
    }

}
