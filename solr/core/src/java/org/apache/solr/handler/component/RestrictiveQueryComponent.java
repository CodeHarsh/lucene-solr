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


import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by harshvardhan.s on 24/11/15.
 */
public class RestrictiveQueryComponent extends QueryComponent implements RestrictiveSearchComponent  {
    protected static Logger log = LoggerFactory.getLogger(RestrictiveQueryComponent.class);
    @Override
    public Boolean validate(SolrParams request, RestrictiveComponentAttributes restrictiveComponentAttributes) throws RestrictedQueryException {

        if(request.get(CommonParams.SORT) != null) {
            throw new RestrictedQueryException("Sorting not allowed.");
        }

        if (matchPattern("(%20| )OR(%20| )",request.get(CommonParams.Q,""))) {
            throw new RestrictedQueryException("'OR'-conjunction is not allowed in query due to high performance penalty in scoring over a large data-set." +
                    " Please discuss your usecase with log-svc-maintainers@flipkart.com if you really need it.");
        }

        if (matchPattern("(%3A|:).?(%3F|\\*).",request.get(CommonParams.Q,""))) {

            String fieldName = request.get(CommonParams.Q);
            fieldName = fieldName.substring(0,fieldName.indexOf(":"));
            if(fieldName != null && checkCardinality(request, restrictiveComponentAttributes,fieldName) == false ){
                throw new RestrictedQueryException("Wildcards on high cardinality fields are not allowed.");
            }
        }

        return true;
    }
}
